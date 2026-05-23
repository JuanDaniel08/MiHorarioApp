package co.edu.uco.mihorario.domain.exception;

public abstract class DomainException extends RuntimeException {

    protected DomainException(String mensaje) {
        super(mensaje);
    }

    protected DomainException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
