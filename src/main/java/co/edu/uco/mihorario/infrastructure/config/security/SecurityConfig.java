package co.edu.uco.mihorario.infrastructure.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Activa la protección de métodos individuales con @PreAuthorize
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 🌐 1. Habilitamos la configuración de CORS antes de evaluar las rutas
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Desactivamos CSRF porque las APIs REST usan tokens (JWT), no cookies
            .csrf(csrf -> csrf.disable())
            
            // Configuración de las reglas de los endpoints
            .authorizeHttpRequests(auth -> auth
                // El Swagger y la documentación son públicos
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                
                // Exigimos roles específicos para los turnos
                .requestMatchers(HttpMethod.POST, "/api/v1/shifts/**").hasRole("COORDINADOR")
                .requestMatchers(HttpMethod.GET, "/api/v1/shifts/**").hasAnyRole("COORDINADOR", "EMPLEADO")
                
                // Cualquier otra petición debe estar autenticada
                .anyRequest().authenticated()
            );

        return http.build();
    }

    // 🛡️ 2. Definición del componente que le da permisos al Frontend de saltarse el bloqueo
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Permite que peticiones de cualquier origen (como tu index.html local) se conecten
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