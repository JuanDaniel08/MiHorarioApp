package co.edu.uco.mihorario.infrastructure.config;

import co.edu.uco.mihorario.application.usecase.impl.AddShiftUseCaseImpl;
import co.edu.uco.mihorario.application.usecase.input.AddShiftUseCase;
import co.edu.uco.mihorario.crosscutting.parameter.ParameterCatalogService;
import co.edu.uco.mihorario.domain.ports.out.ShiftRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShiftConfig {

    @Bean
    public AddShiftUseCase addShiftUseCase(ShiftRepository shiftRepository, ParameterCatalogService parameterCatalogService) {
        // Le inyectamos tanto el repositorio JPA como el adaptador de parámetros de Redis
        return new AddShiftUseCaseImpl(shiftRepository, parameterCatalogService);
    }
}