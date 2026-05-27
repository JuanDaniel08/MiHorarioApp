package co.edu.uco.mihorario.infrastructure.config;

import co.edu.uco.mihorario.application.usecase.impl.AddShiftUseCaseImpl;
import co.edu.uco.mihorario.application.usecase.input.AddShiftUseCase;
import co.edu.uco.mihorario.application.usecase.impl.GetShiftsUseCaseImpl;
import co.edu.uco.mihorario.application.usecase.input.GetShiftsUseCase;
import co.edu.uco.mihorario.application.usecase.impl.DeleteShiftUseCaseImpl;
import co.edu.uco.mihorario.application.usecase.input.DeleteShiftUseCase;
import co.edu.uco.mihorario.crosscutting.parameter.ParameterCatalogService;
import co.edu.uco.mihorario.domain.ports.out.ShiftRepository;
import co.edu.uco.mihorario.infrastructure.adapters.messaging.NotificationGatewayAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShiftConfig {

    @Bean
    public AddShiftUseCase addShiftUseCase(
            ShiftRepository shiftRepository,
            ParameterCatalogService parameterCatalogService,
            NotificationGatewayAdapter notificationGatewayAdapter // 🚀 Inyectamos el nuevo adapter
    ) {
        // 🔄 Le pasamos los 3 parámetros requeridos al constructor
        return new AddShiftUseCaseImpl(shiftRepository, parameterCatalogService, notificationGatewayAdapter);
    }

    @Bean
    public GetShiftsUseCase getShiftsUseCase(ShiftRepository shiftRepository) {
        return new GetShiftsUseCaseImpl(shiftRepository);
    }

    @Bean
    public DeleteShiftUseCase deleteShiftUseCase(ShiftRepository shiftRepository) {
        return new DeleteShiftUseCaseImpl(shiftRepository);
    }
}