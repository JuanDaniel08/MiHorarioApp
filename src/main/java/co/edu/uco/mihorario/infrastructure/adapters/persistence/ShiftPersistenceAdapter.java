package co.edu.uco.mihorario.infrastructure.adapters.persistence;

import co.edu.uco.mihorario.domain.model.Shift;
import co.edu.uco.mihorario.domain.model.valueobject.TimeRange;
import co.edu.uco.mihorario.domain.ports.out.ShiftRepository;
import co.edu.uco.mihorario.infrastructure.adapters.persistence.entity.ShiftEntityJPA;
import co.edu.uco.mihorario.infrastructure.adapters.persistence.repository.ShiftJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class ShiftPersistenceAdapter implements ShiftRepository {

    private final ShiftJpaRepository shiftJpaRepository;

    public ShiftPersistenceAdapter(ShiftJpaRepository shiftJpaRepository) {
        this.shiftJpaRepository = shiftJpaRepository;
    }

    @Override
    public void save(Shift shift) {
        ShiftEntityJPA entity = toEntity(shift);
        shiftJpaRepository.save(entity);
    }

    @Override
    public Optional<Shift> findById(UUID id) {
        return shiftJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Shift> findByEmployeeId(UUID employeeId) {
        return shiftJpaRepository.findByEmployeeId(employeeId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Shift> findByDate(LocalDate date) {
        return shiftJpaRepository.findByDate(date).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Shift> findAll() {
        return shiftJpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        shiftJpaRepository.deleteById(id);
    }

    private ShiftEntityJPA toEntity(Shift shift) {
        return new ShiftEntityJPA(
                shift.getId(),
                shift.getEmployeeId(),
                shift.getLaborId(),
                shift.getDate(),
                shift.getTimeRange().startTime(),
                shift.getTimeRange().endTime(),
                shift.isActive(),
                shift.getObservation()
        );
    }

    private Shift toDomain(ShiftEntityJPA entity) {
        TimeRange timeRange = new TimeRange(entity.getStartTime(), entity.getEndTime());
        return new Shift(
                entity.getId(),
                entity.getEmployeeId(),
                entity.getLaborId(),
                entity.getDate(),
                timeRange,
                entity.getActive(),
                entity.getObservation()
        );
    }
}
