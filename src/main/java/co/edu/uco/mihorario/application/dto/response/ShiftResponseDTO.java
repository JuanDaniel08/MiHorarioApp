package co.edu.uco.mihorario.application.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ShiftResponseDTO(
    UUID id,
    UUID employeeId,
    UUID laborId,
    LocalDate date,
    LocalTime startTime,
    LocalTime endTime,
    Boolean active,
    String observation
) {}
