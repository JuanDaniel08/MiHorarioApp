package co.edu.uco.mihorario.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("MiHorarioApp API - Gestion de turnos")
                .version("1.0.0")
                .description("API REST principal para gestionar y programar los turnos de los empleados, optimizar las zonas de aparcamiento y garantizar el cumplimiento de las normas y restricciones laborales.")
                .contact(new Contact()
                    .name("Systems Engineering Team")
                    .email("support@mihorario.com")));
    }
}
