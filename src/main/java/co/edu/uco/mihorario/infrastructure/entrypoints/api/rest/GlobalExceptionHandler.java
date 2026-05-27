package co.edu.uco.mihorario.infrastructure.entrypoints.api.rest;

import co.edu.uco.mihorario.crosscutting.exception.BusinessException;
import co.edu.uco.mihorario.crosscutting.exception.MessageCatalogService;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Locale;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;
    private final MessageCatalogService messageCatalogService;

    public GlobalExceptionHandler(MessageSource messageSource, MessageCatalogService messageCatalogService) {
        this.messageSource = messageSource;
        this.messageCatalogService = messageCatalogService;
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, String>> handleBusinessException(
            BusinessException ex,
            Locale locale
    ) {
        String errorCode = ex.getErrorCode();
        String translatedMessage;

        try {
            // Intenta traducir con los archivos locales de I18N (Punto 20)
            translatedMessage = messageSource.getMessage(errorCode, null, locale);
        } catch (NoSuchMessageException e) {
            // Fallback al catálogo de mensajes en Redis (Punto 9)
            translatedMessage = messageCatalogService.getMessage(errorCode);
        }

        return new ResponseEntity<>(
                Map.of("code", errorCode, "message", translatedMessage),
                HttpStatus.BAD_REQUEST
        );
    }
}
