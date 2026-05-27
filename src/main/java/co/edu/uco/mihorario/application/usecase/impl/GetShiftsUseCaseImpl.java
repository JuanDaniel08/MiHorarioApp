package co.edu.uco.mihorario.application.usecase.impl;

import co.edu.uco.mihorario.application.dto.response.ShiftResponseDTO;
import co.edu.uco.mihorario.application.usecase.input.GetShiftsUseCase;
import co.edu.uco.mihorario.domain.ports.out.ShiftRepository;

import java.util.List;
import java.util.stream.Collectors;

public class GetShiftsUseCaseImpl implements GetShiftsUseCase {

    private final ShiftRepository shiftRepository;

    public GetShiftsUseCaseImpl(ShiftRepository shiftRepository) {
        this.shiftRepository = shiftRepository;
    }

    @Override
    public List<ShiftResponseDTO> execute() {
        return shiftRepository.findAll().stream()
                .map(shift -> new ShiftResponseDTO(
                        shift.getId(),
                        shift.getEmployeeId(),
                        shift.getLaborId(),
                        shift.getDate(),
                        shift.getTimeRange().startTime(),
                        shift.getTimeRange().endTime(),
                        shift.isActive(),
                        shift.getObservation()
                ))
                .collect(Collectors.toList());
    }
}
