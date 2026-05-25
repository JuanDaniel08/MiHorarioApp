package co.edu.uco.mihorario.application.usecase.impl;

import co.edu.uco.mihorario.application.usecase.input.DeleteShiftUseCase;
import co.edu.uco.mihorario.domain.ports.out.ShiftRepository;

import java.util.UUID;

public class DeleteShiftUseCaseImpl implements DeleteShiftUseCase {

    private final ShiftRepository shiftRepository;

    public DeleteShiftUseCaseImpl(ShiftRepository shiftRepository) {
        this.shiftRepository = shiftRepository;
    }

    @Override
    public void execute(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del turno no puede ser nulo.");
        }
        shiftRepository.delete(id);
    }
}
