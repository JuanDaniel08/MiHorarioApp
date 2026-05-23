package co.edu.uco.mihorario.infrastructure.adapters.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "turno") // Mapeo físico a la tabla de PostgreSQL
public class ShiftEntityJPA {

    @Id
    private UUID id;

    @Column(name = "id_empleado", nullable = false)
    private UUID employeeId;

    @Column(name = "id_labor", nullable = false)
    private UUID laborId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime startTime;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private Boolean active;

    private String observation;

    // Constructor vacío obligatorio
    public ShiftEntityJPA() {
    }

    // Constructor completo actualizado con el nuevo nombre de la clase
    public ShiftEntityJPA(UUID id, UUID employeeId, UUID laborId, LocalDate date, LocalTime startTime, LocalTime endTime, Boolean active, String observation) {
        this.id = id;
        this.employeeId = employeeId;
        this.laborId = laborId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.active = active;
        this.observation = observation;
    }

    // Getters y Setters estándar...
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getEmployeeId() { return employeeId; }
    public void setEmployeeId(UUID employeeId) { this.employeeId = employeeId; }

    public UUID getLaborId() { return laborId; }
    public void setLaborId(UUID laborId) { this.laborId = laborId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public String getObservation() { return observation; }
    public void setObservation(String observation) { this.observation = observation; }
}