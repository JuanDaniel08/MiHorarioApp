package co.edu.uco.mihorario.infrastructure.entrypoints.api.rest;

import co.edu.uco.mihorario.application.dto.request.AddShiftRequestDTO;
import co.edu.uco.mihorario.application.usecase.input.AddShiftUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/shifts") // Ruta base del endpoint para los turnos
@Tag(name = "Shift", description = "API para gestionar los turnos de los empleados")
public class ShiftRestController {

    // Dependemos del puerto de entrada (la interfaz del caso de uso), no de la implementación
    private final AddShiftUseCase addShiftUseCase;

    // Inyección de dependencias por constructor de Spring
    public ShiftRestController(AddShiftUseCase addShiftUseCase) {
        this.addShiftUseCase = addShiftUseCase;
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo turno", description = "Crea un nuevo turno para un empleado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Turno creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "403", description = "Prohibido"),
        @ApiResponse(responseCode = "404", description = "No encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> createShift(@RequestBody AddShiftRequestDTO requestDto) {
        // Enviamos el DTO directamente al caso de uso de la capa de aplicación
        addShiftUseCase.execute(requestDto);
        
        // Retornamos un estado HTTP 201 Created si todo el proceso fue exitoso
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}