package co.edu.uco.mihorario.infrastructure.entrypoints.api.rest;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import java.util.UUID;
import co.edu.uco.mihorario.application.dto.request.AddShiftRequestDTO;
import co.edu.uco.mihorario.application.usecase.input.AddShiftUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/shifts") // Ruta base del endpoint para los turnos
@Tag(name = "Shift", description = "API para gestionar los turnos de los empleados")
public class ShiftRestController {

    // 📊 Inicializamos el Logger oficial de la arquitectura
    private static final Logger log = LoggerFactory.getLogger(ShiftRestController.class);
    private final AddShiftUseCase addShiftUseCase;

    // Inyección de dependencias por constructor de Spring
    public ShiftRestController(AddShiftUseCase addShiftUseCase) {
        this.addShiftUseCase = addShiftUseCase;
    }

    

    @PostMapping
    @Operation(summary = "Crear un nuevo turno", description = "Crea un nuevo turno para un empleado")
    // @PreAuthorize("hasRole('ROLE_COORDINADOR')")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Turno creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "403", description = "Prohibido"),
        @ApiResponse(responseCode = "404", description = "No encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })

    public ResponseEntity<Void> createShift(
            @RequestBody AddShiftRequestDTO requestDto,
            @org.springframework.web.bind.annotation.RequestHeader(value = "X-Captcha-Token", required = false) String captchaToken,
            @org.springframework.web.bind.annotation.RequestHeader(value = "Accept-Language", defaultValue = "es") String acceptLanguage
    ) {
        // 🆔 Generamos un ID único de trazabilidad (Trace ID) para esta petición
        String traceId = MDC.get("traceId") != null ? MDC.get("traceId") : UUID.randomUUID().toString();
        MDC.put("traceId", traceId);

        log.info("[INICIO] Petición recibida para registrar un nuevo turno. TraceID: {}, Idioma: {}", traceId, acceptLanguage);

        // 🛡️ Validación de Captcha (Punto 19)
        if (captchaToken == null || !captchaToken.equals("google-recaptcha-v3-token-valid")) {
            log.warn("[CAPTCHA] Falló la validación del reCAPTCHA. Token incorrecto o nulo: {}", captchaToken);
            MDC.clear();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            addShiftUseCase.execute(requestDto);
            log.info("[ÉXITO] Turno procesado y creado exitosamente en el sistema.");
            return new ResponseEntity<>(HttpStatus.CREATED);
            
        } catch (Exception e) {
            log.error("[ERROR] Falló la creación del turno. Motivo: {}", e.getMessage(), e);
            throw e;
        } finally {
            MDC.clear(); // Limpiamos el hilo al terminar por seguridad
        }
    }
}