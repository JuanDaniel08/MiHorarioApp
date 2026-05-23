package co.edu.uco.mihorario.application.usecase.impl;


import co.edu.uco.mihorario.application.dto.request.AddShiftRequestDTO;
import co.edu.uco.mihorario.application.usecase.input.AddShiftUseCase;
import co.edu.uco.mihorario.domain.model.Shift;
import co.edu.uco.mihorario.domain.model.valueobject.TimeRange;
import co.edu.uco.mihorario.domain.ports.out.ShiftRepository;

public class AddShiftUseCaseImpl implements AddShiftUseCase {

    private final ShiftRepository shiftRepository;

    public AddShiftUseCaseImpl(ShiftRepository shiftRepository) {
        this.shiftRepository = shiftRepository;
    }

    @Override
    public void execute(AddShiftRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("El objeto de datos de solicitud de turno no puede ser nulo.");
        }

        // Creamos el rango de tiempo del dominio
        TimeRange timeRange = new TimeRange(dto.startTime(), dto.endTime());

        // Construimos el Shift con las reglas de tu dominio
        Shift newShift = new Shift(
            null, 
            dto.employeeId(), 
            dto.laborId(), 
            dto.date(), 
            timeRange, 
            dto.active(), 
            dto.observation()
        );

        // Persistimos a través de tu puerto de salida
        shiftRepository.save(newShift);
    }
}