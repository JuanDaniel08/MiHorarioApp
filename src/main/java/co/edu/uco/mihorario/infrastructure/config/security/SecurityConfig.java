package co.edu.uco.mihorario.infrastructure.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri:}")
    private String issuerUri;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 🌐 1. Habilitamos la configuración de CORS antes de evaluar las rutas
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Desactivamos CSRF porque las APIs REST usan tokens (JWT), no cookies
                .csrf(csrf -> csrf.disable())

                // Registramos el filtro JWT personalizado para validar los tokens de rol
                // (mock/desarrollo local)
                .addFilterBefore(new JwtAuthenticationFilter(),
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        // 🔐 2. Habilitamos el soporte para validar tokens JWT federados (Identity
        // Provider) en la nube si hay un emisor configurado
        if (issuerUri != null && !issuerUri.trim().isEmpty() && !issuerUri.contains("dev-placeholder")) {
            http.oauth2ResourceServer(oauth2 -> oauth2
                    .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        }

        // Configuración de las reglas de los endpoints
        http.authorizeHttpRequests(auth -> auth
                // El Swagger, archivos estáticos, Actuator y la documentación son públicos
                .requestMatchers(
                        "/",
                        "/index.html",
                        "/css/**",
                        "/js/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/actuator/**")
                .permitAll()

                // Permite consulta pública de empleados y labores para iniciar sesión y llenar
                // desplegables
                .requestMatchers(HttpMethod.GET, "/api/v1/employees", "/api/v1/employees/**", "/api/v1/labors", "/api/v1/labors/**").permitAll()

                // 🔒 Las restricciones por rol vuelven a estar activas en primer orden:
                .requestMatchers(HttpMethod.POST, "/api/v1/shifts", "/api/v1/shifts/**").hasRole("COORDINADOR")
                .requestMatchers(HttpMethod.GET, "/api/v1/shifts", "/api/v1/shifts/**").hasAnyRole("COORDINADOR", "EMPLEADO")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/shifts", "/api/v1/shifts/**").hasRole("COORDINADOR")

                // Cualquier otra petición debe estar autenticada
                .anyRequest().authenticated());

        return http.build();
    }

    // Converter para mapear los roles de Keycloak
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        return converter;
    }

    // Clase interna para extraer los roles de Keycloak
    private static class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
        private final JwtGrantedAuthoritiesConverter defaultAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

        @Override
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            // Obtenemos los roles por defecto (scopes)
            Collection<GrantedAuthority> authorities = defaultAuthoritiesConverter.convert(jwt);

            // Extraemos los roles de realm_access.roles en Keycloak
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess == null || realmAccess.isEmpty()) {
                return authorities;
            }

            @SuppressWarnings("unchecked")
            Collection<String> roles = (Collection<String>) realmAccess.get("roles");
            if (roles == null || roles.isEmpty()) {
                return authorities;
            }

            // Convertimos los roles a SimpleGrantedAuthority agregando el prefijo "ROLE_"
            List<SimpleGrantedAuthority> keycloakAuthorities = roles.stream()
                    .map(roleName -> new SimpleGrantedAuthority("ROLE_" + roleName))
                    .collect(Collectors.toList());

            // Combinamos las autoridades de Keycloak con las por defecto
            return Stream.concat(authorities.stream(), keycloakAuthorities.stream())
                    .collect(Collectors.toList());
        }
    }

    // 🛡️ 3. Definición del componente que le da permisos al Frontend de saltarse
    // el bloqueo
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Permite que peticiones de cualquier origen (como tu index.html local) se
        // conecten
        configuration.setAllowedOrigins(List.of("*"));

        // Métodos HTTP permitidos en la arquitectura
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Cabeceras personalizadas que viajan desde tu formulario HTML
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Captcha-Token", "Accept-Language"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}