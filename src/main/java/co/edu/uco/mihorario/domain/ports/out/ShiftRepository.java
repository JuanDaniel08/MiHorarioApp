package co.edu.uco.mihorario.domain.ports.out;

import co.edu.uco.mihorario.domain.model.Shift;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShiftRepository {

    // Guarda un nuevo turno o actualiza uno existente en el sistema
    void save(Shift shift);

    // Busca un turno específico por su identificador único
    Optional<Shift> findById(UUID id);

    // Busca todos los turnos asignados a un empleado específico
    List<Shift> findByEmployeeId(UUID employeeId);

    // Busca todos los turnos programados para una fecha específica en el parque
    List<Shift> findByDate(LocalDate date);

    // Elimina un turno del sistema utilizando su ID
    void delete(UUID id);
}