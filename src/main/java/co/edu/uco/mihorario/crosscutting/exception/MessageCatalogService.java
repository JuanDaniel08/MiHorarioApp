package co.edu.uco.mihorario.crosscutting.exception;

public interface MessageCatalogService {
    // Recibe el código (ej: "ERR-101") y retorna el mensaje dinámico desde Redis
    String getMessage(String errorCode);
}