package co.edu.uco.mihorario.infrastructure.adapters.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.uco.mihorario.infrastructure.adapters.persistence.entity.ShiftEntityJPA;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ShiftJpaRepository extends JpaRepository<ShiftEntityJPA, UUID> {
}
