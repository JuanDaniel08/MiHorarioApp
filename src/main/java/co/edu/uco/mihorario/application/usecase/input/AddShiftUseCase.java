package co.edu.uco.mihorario.application.usecase.input;

import co.edu.uco.mihorario.application.dto.request.AddShiftRequestDTO;

public interface AddShiftUseCase {
    void execute(AddShiftRequestDTO dto);
}
