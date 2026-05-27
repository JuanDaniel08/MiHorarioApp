package co.edu.uco.mihorario.domain.model.valueobject;

import java.time.LocalTime;

public record TimeRange(LocalTime startTime, LocalTime endTime) {

    // Constructor compacto para la validación del negocio
    public TimeRange {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("El rango de tiempo no puede ser nulo.");
        }

        // Regla de negocio: La hora de inicio debe ser estrictamente antes de la hora de fin
        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            throw new IllegalArgumentException("La hora de inicio debe ser estrictamente antes de la hora de fin.");
        }
    }

    // Lógica de dominio: Verifica si este rango de tiempo se cruza (solapa) con otro
    // Esencial para evitar conflictos de horarios en los turnos de limpieza del parque
    public boolean overlapsWith(TimeRange other) {
        if (other == null) {
            return false;
        }
        // Fórmula matemática de intervalos: (InicioA < FinB) Y (FinA > InicioB)
        return this.startTime.isBefore(other.endTime()) && this.endTime.isAfter(other.startTime());
    }

    // Lógica de dominio: Calcula la duración total del turno en minutos
    public long getDurationInMinutes() {
        return java.time.temporal.ChronoUnit.MINUTES.between(startTime, endTime);
    }
}