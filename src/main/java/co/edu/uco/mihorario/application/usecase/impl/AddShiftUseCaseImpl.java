package co.edu.uco.mihorario.application.usecase.impl;


import co.edu.uco.mihorario.application.dto.request.AddShiftRequestDTO;
import co.edu.uco.mihorario.application.usecase.input.AddShiftUseCase;
import co.edu.uco.mihorario.domain.model.Shift;
import co.edu.uco.mihorario.domain.model.valueobject.TimeRange;
import co.edu.uco.mihorario.domain.ports.out.ShiftRepository;
import co.edu.uco.mihorario.crosscutting.parameter.ParameterCatalogService;
import co.edu.uco.mihorario.crosscutting.exception.BusinessException;

public class AddShiftUseCaseImpl implements AddShiftUseCase {

    private final ShiftRepository shiftRepository;
    private final ParameterCatalogService parameterCatalogService;

    public AddShiftUseCaseImpl(ShiftRepository shiftRepository, ParameterCatalogService parameterCatalogService) {
        this.shiftRepository = shiftRepository;
        this.parameterCatalogService = parameterCatalogService;
    }

    @Override
    public void execute(AddShiftRequestDTO dto) {

        if (dto == null) {
            // "ERR-103" -> Catálogo de mensajes para datos nulos
            throw new BusinessException("ERR-103"); 
        }

        // 1. Consultamos en caliente los parámetros de horas desde Redis (con fallbacks seguros)
        int maxHours = parameterCatalogService.getIntParameter("SHIFT_MAX_HOURS", 9);
        int minHours = parameterCatalogService.getIntParameter("SHIFT_MIN_HOURS", 8);

        // 2. Calculamos la duración usando los datos que vienen en el DTO
        long shiftHours = java.time.Duration.between(dto.startTime(), dto.endTime()).toHours();

        // 3. Evaluamos la regla de negocio dinámica
        if (shiftHours < minHours || shiftHours > maxHours) {
            // "ERR-101" -> Catálogo de mensajes para duración inválida
            throw new BusinessException("ERR-101");
        }

        // 4. Si pasa los parámetros, construimos el dominio de forma normal
        TimeRange timeRange = new TimeRange(dto.startTime(), dto.endTime());

        Shift newShift = new Shift(
            null, 
            dto.employeeId(), 
            dto.laborId(), 
            dto.date(), 
            timeRange, 
            dto.active(), 
            dto.observation()
        );

        // 5. Guardamos en el puerto de salida
        this.shiftRepository.save(newShift);
    }
}