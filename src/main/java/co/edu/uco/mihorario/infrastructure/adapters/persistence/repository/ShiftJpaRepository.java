package co.edu.uco.mihorario.infrastructure.adapters.persistence.repository;

import co.edu.uco.mihorario.infrastructure.adapters.persistence.entity.ShiftEntityJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ShiftJpaRepository extends JpaRepository<ShiftEntityJPA, UUID> {
    // Spring Data JPA arma la query SQL automáticamente gracias a este nombre:
    List<ShiftEntityJPA> findByDate(LocalDate date);

    List<ShiftEntityJPA> findByEmployeeId(UUID employeeId);
}