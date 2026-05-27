package co.edu.uco.mihorario.domain.model;

import java.time.LocalDate;
import java.util.UUID;
import co.edu.uco.mihorario.domain.model.valueobject.TimeRange;
import co.edu.uco.mihorario.crosscutting.helpers.UuidHelper;

public class Shift {
    // Todos los atributos son final para garantizar la inmutabilidad
    private final UUID id;
    private final UUID employeeId;
    private final UUID laborId;
    private final LocalDate date;
    private final TimeRange timeRange; // Encapsula startTime y endTime con sus validaciones
    private final Boolean active; // Cambiado 'state' por 'active' para ser más semántico
    private final String observation;

    public Shift(UUID id, UUID employeeId, UUID laborId, LocalDate date, TimeRange timeRange, Boolean active, String observation) {
        // Validación de nulidad general obligatoria en el dominio
        if (employeeId == null || laborId == null || date == null || timeRange == null || active == null) {
            throw new IllegalArgumentException("Los campos requeridos no pueden ser nulos.");
        }

        // Corrección del ID: Si no viene un ID externo, el crosscutting genera uno nuevo
        this.id = (id == null) ? UuidHelper.generateUuid() : id;
        
        this.employeeId = employeeId;
        this.laborId = laborId;
        this.date = date;
        this.timeRange = timeRange;
        this.active = active;
        // Si la observación es nula, le asignamos un texto vacío por defecto
        this.observation = (observation == null) ? "" : observation.trim();
    }

    // Solo dejamos Getters limpios. Cero Setters.
    public UUID getId() {
        return id;
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public UUID getLaborId() {
        return laborId;
    }

    public LocalDate getDate() {
        return date;
    }

    public TimeRange getTimeRange() {
        return timeRange;
    }

    public Boolean isActive() {
        return active;
    }

    public String getObservation() {
        return observation;
    }
}