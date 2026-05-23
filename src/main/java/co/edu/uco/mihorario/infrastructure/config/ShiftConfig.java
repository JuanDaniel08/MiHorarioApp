package co.edu.uco.mihorario.infrastructure.config;

import co.edu.uco.mihorario.application.usecase.impl.AddShiftUseCaseImpl;
import co.edu.uco.mihorario.application.usecase.input.AddShiftUseCase;
import co.edu.uco.mihorario.domain.ports.out.ShiftRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // Le indica a Spring que esta clase contiene definiciones de Beans
public class ShiftConfig {

    /**
     * Registramos el caso de uso manualmente en el contenedor de Spring.
     * Spring buscará automáticamente un componente que implemente 'ShiftRepository'
     * (que en nuestro caso es el 'ShiftPersistenceAdapter') y se lo inyectará.
     */
    @Bean
    public AddShiftUseCase addShiftUseCase(ShiftRepository shiftRepository) {
        return new AddShiftUseCaseImpl(shiftRepository);
    }
}