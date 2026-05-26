package co.edu.uco.mihorario.infrastructure.adapters.persistence;

import co.edu.uco.mihorario.domain.model.Shift;
import co.edu.uco.mihorario.domain.model.valueobject.TimeRange;
import co.edu.uco.mihorario.domain.ports.out.ShiftRepository;
import co.edu.uco.mihorario.infrastructure.adapters.persistence.repository.ShiftJpaRepository;
import co.edu.uco.mihorario.infrastructure.adapters.persistence.entity.ShiftEntityJPA; // 👈 Tu entidad real
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class ShiftRepositoryAdapter implements ShiftRepository {

    private final ShiftJpaRepository shiftJpaRepository;

    public ShiftRepositoryAdapter(ShiftJpaRepository shiftJpaRepository) {
        this.shiftJpaRepository = shiftJpaRepository;
    }

    @Override
    public void save(Shift shift) {
        // Mapeamos el objeto inmutable de dominio a la entidad JPA que me acabas de mostrar
        ShiftEntityJPA jpaEntity = new ShiftEntityJPA(
            shift.getId() != null ? shift.getId() : UUID.randomUUID(),
            shift.getEmployeeId(),
            shift.getLaborId(),
            shift.getDate(),
            shift.getTimeRange().startTime(), // Extrayendo del Value Object TimeRange
            shift.getTimeRange().endTime(),   // Extrayendo del Value Object TimeRange
            shift.isActive(),
            shift.getObservation()
        );

        // Guardamos en PostgreSQL
        this.shiftJpaRepository.save(jpaEntity);
    }
   

    @Override
    public void delete(UUID id) {
        if (this.shiftJpaRepository.existsById(id)) {
            this.shiftJpaRepository.deleteById(id);
        }
    }

    @Override
    public Optional<Shift> findById(UUID id) {
        return this.shiftJpaRepository.findById(id).map(this::toShift);
    }

    @Override
    public List<Shift> findByEmployeeId(UUID employeeId) {
        return this.shiftJpaRepository.findByEmployeeId(employeeId).stream()
                .map(this::toShift)
                .collect(Collectors.toList());
    }

    @Override
    public List<Shift> findByDate(LocalDate date) {
        return this.shiftJpaRepository.findByDate(date).stream()
                .map(this::toShift)
                .collect(Collectors.toList());
    }

    private Shift toShift(ShiftEntityJPA entity) {
        return new Shift(
                entity.getId(),
                entity.getEmployeeId(),
                entity.getLaborId(),
                entity.getDate(),
                new TimeRange(entity.getStartTime(), entity.getEndTime()),
                entity.getActive(),
                entity.getObservation()
        );
    }

    @Override
    public List<Shift> findAll() {
        return this.shiftJpaRepository.findAll().stream()
                .map(this::toShift)
                .collect(Collectors.toList());
    }

}