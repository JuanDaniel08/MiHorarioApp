package co.edu.uco.mihorario.infrastructure.config.security;

import co.edu.uco.mihorario.infrastructure.adapters.persistence.entity.EmployeeEntityJPA;
import co.edu.uco.mihorario.infrastructure.adapters.persistence.repository.EmployeeJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.UUID;

public class KeycloakUserSyncConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private static final Logger log = LoggerFactory.getLogger(KeycloakUserSyncConverter.class);
    private final JwtAuthenticationConverter defaultConverter;
    private final EmployeeJpaRepository employeeJpaRepository;

    public KeycloakUserSyncConverter(JwtAuthenticationConverter defaultConverter, EmployeeJpaRepository employeeJpaRepository) {
        this.defaultConverter = defaultConverter;
        this.employeeJpaRepository = employeeJpaRepository;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        AbstractAuthenticationToken authenticationToken = defaultConverter.convert(jwt);
        syncUserToDatabase(jwt);
        return authenticationToken;
    }

    private void syncUserToDatabase(Jwt jwt) {
        try {
            String sub = jwt.getClaimAsString("sub");
            if (sub == null || sub.trim().isEmpty()) {
                return;
            }

            UUID id;
            try {
                id = UUID.fromString(sub);
            } catch (IllegalArgumentException e) {
                log.warn("El claim 'sub' del JWT ({}) no es un UUID válido. Omitiendo sincronización.", sub);
                return;
            }

            // Si ya existe por ID, no es necesario recrearlo
            if (employeeJpaRepository.existsById(id)) {
                return;
            }

            String email = jwt.getClaimAsString("email");
            if (email == null || email.trim().isEmpty()) {
                String username = jwt.getClaimAsString("preferred_username");
                email = (username != null ? username : sub) + "@uco.edu.co";
            }

            // Evitar conflictos con correos ya existentes
            if (employeeJpaRepository.findByEmail(email).isPresent()) {
                log.warn("Ya existe un empleado registrado con el email {}. Omitiendo sincronización.", email);
                return;
            }

            String name = jwt.getClaimAsString("given_name");
            String lastName = jwt.getClaimAsString("family_name");

            if (name == null || name.trim().isEmpty()) {
                name = jwt.getClaimAsString("name");
                if (name == null || name.trim().isEmpty()) {
                    name = jwt.getClaimAsString("preferred_username");
                    if (name == null || name.trim().isEmpty()) {
                        name = "Keycloak User";
                    }
                }
            }

            if (lastName == null || lastName.trim().isEmpty()) {
                lastName = "";
            }

            String identification = jwt.getClaimAsString("preferred_username");
            if (identification == null) {
                identification = "N/A";
            }

            EmployeeEntityJPA newEmployee = new EmployeeEntityJPA(
                    id,
                    name,
                    lastName,
                    identification,
                    "N/A", // Teléfono por defecto
                    email,
                    true // Activo por defecto
            );

            employeeJpaRepository.save(newEmployee);
            log.info("Usuario sincronizado con éxito desde Keycloak en base de datos: {} ({})", email, id);

        } catch (Exception e) {
            log.error("Fallo no crítico al sincronizar usuario de Keycloak con base de datos: {}", e.getMessage(), e);
        }
    }
}
