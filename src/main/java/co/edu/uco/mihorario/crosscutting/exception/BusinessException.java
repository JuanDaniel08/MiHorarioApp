package co.edu.uco.mihorario.crosscutting.exception;

public class BusinessException extends RuntimeException {
    
    private final String errorCode;

    public BusinessException(String errorCode) {
        super(errorCode); // Pasamos el código como el mensaje base temporal
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}