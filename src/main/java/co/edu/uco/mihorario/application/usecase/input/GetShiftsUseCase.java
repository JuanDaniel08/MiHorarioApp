package co.edu.uco.mihorario.application.usecase.input;

import co.edu.uco.mihorario.application.dto.response.ShiftResponseDTO;
import java.util.List;

public interface GetShiftsUseCase {
    List<ShiftResponseDTO> execute();
}
