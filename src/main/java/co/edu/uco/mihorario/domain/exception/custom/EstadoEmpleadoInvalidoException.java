package co.edu.uco.mihorario.domain.exception.custom;

import co.edu.uco.mihorario.domain.exception.DomainException;

public final class EstadoEmpleadoInvalidoException extends DomainException {

    public EstadoEmpleadoInvalidoException(String descripcionInvalida) {
        super(String.format("El estado '%s' no es válido para un empleado del sistema.", descripcionInvalida));
    }
}
