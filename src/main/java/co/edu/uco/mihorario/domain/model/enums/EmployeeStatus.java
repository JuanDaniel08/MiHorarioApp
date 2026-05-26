package co.edu.uco.mihorario.domain.model.enums;

import co.edu.uco.mihorario.domain.exception.custom.EstadoEmpleadoInvalidoException;

public enum EmployeeStatus {
    ACTIVE("Active"),
    VACATION("Vacation"),
    LEAVE("Leave"),
    INCAPACITY("Incapacity"),
    OTHER("Other");

    private final String description;

    private EmployeeStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static EmployeeStatus fromDescription(String description) {
        for (EmployeeStatus status : values()) {
            if (status.description.equalsIgnoreCase(description)) {
                return status;
            }
        }
        throw new EstadoEmpleadoInvalidoException(description);
    }
}