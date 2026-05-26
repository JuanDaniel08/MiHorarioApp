package co.edu.uco.mihorario.application.usecase.impl;

import co.edu.uco.mihorario.application.dto.request.AddShiftRequestDTO;
import co.edu.uco.mihorario.application.usecase.input.AddShiftUseCase;
import co.edu.uco.mihorario.domain.model.Shift;
import co.edu.uco.mihorario.domain.model.valueobject.TimeRange;
import co.edu.uco.mihorario.domain.ports.out.ShiftRepository;
// 🚀 Cambiamos el Adapter de infraestructura por el Puerto del dominio
import co.edu.uco.mihorario.domain.ports.out.NotificationGateway;
import co.edu.uco.mihorario.crosscutting.parameter.ParameterCatalogService;
import co.edu.uco.mihorario.crosscutting.exception.BusinessException;

public class AddShiftUseCaseImpl implements AddShiftUseCase {

    private final ShiftRepository shiftRepository;
    private final ParameterCatalogService parameterCatalogService;
    private final NotificationGateway notificationGateway; // 🔒 Ahora es una interfaz pura del dominio

    public AddShiftUseCaseImpl(ShiftRepository shiftRepository, 
                               ParameterCatalogService parameterCatalogService,
                               NotificationGateway notificationGateway) {
        this.shiftRepository = shiftRepository;
        this.parameterCatalogService = parameterCatalogService;
        this.notificationGateway = notificationGateway;
    }

    @Override
    public void execute(AddShiftRequestDTO dto) {
        if (dto == null) { throw new BusinessException("ERR-103"); }

        int maxHours = parameterCatalogService.getIntParameter("SHIFT_MAX_HOURS", 9);
        int minHours = parameterCatalogService.getIntParameter("SHIFT_MIN_HOURS", 8);
        long shiftHours = java.time.Duration.between(dto.startTime(), dto.endTime()).toHours();

        if (shiftHours < minHours || shiftHours > maxHours) {
            throw new BusinessException("ERR-101");
        }

        TimeRange timeRange = new TimeRange(dto.startTime(), dto.endTime());
        Shift newShift = new Shift(null, dto.employeeId(), dto.laborId(), dto.date(), timeRange, dto.active(), dto.observation());

        // 1. Persistencia en base de datos
        this.shiftRepository.save(newShift);

        // 2. ✉️ Envío dinámico de notificaciones mediante el puerto abstracto
        // Nota: Si el objeto Shift aún no resuelve el email, puedes quemarlo temporalmente aquí o sacarlo del flujo de negocio.
        String correoDestino = "jrodriguezgiraldo8@gmail.com"; 
        
        String nombreTrabajador = "Empleado ID: " + dto.employeeId().toString().substring(0, 8);
        String detalleLabor = "Labor ID: " + dto.laborId().toString().substring(0, 8);
        String fechaTurno = dto.date().toString();

        this.notificationGateway.sendShiftNotification(correoDestino, nombreTrabajador, detalleLabor, fechaTurno);
    }
}