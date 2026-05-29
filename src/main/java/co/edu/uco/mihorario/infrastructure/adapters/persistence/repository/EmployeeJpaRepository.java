package co.edu.uco.mihorario.infrastructure.adapters.persistence.repository;

import co.edu.uco.mihorario.infrastructure.adapters.persistence.entity.EmployeeEntityJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeJpaRepository extends JpaRepository<EmployeeEntityJPA, UUID> {
    Optional<EmployeeEntityJPA> findByEmail(String email);
}
