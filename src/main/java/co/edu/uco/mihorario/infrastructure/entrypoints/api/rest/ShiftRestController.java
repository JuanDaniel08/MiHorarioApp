package co.edu.uco.mihorario.infrastructure.entrypoints.api.rest;

import co.edu.uco.mihorario.application.dto.request.AddShiftRequestDTO;
import co.edu.uco.mihorario.application.usecase.input.AddShiftUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/shifts") // Ruta base del endpoint para los turnos
public class ShiftRestController {

    // Dependemos del puerto de entrada (la interfaz del caso de uso), no de la implementación
    private final AddShiftUseCase addShiftUseCase;

    // Inyección de dependencias por constructor de Spring
    public ShiftRestController(AddShiftUseCase addShiftUseCase) {
        this.addShiftUseCase = addShiftUseCase;
    }

    @PostMapping
    public ResponseEntity<Void> createShift(@RequestBody AddShiftRequestDTO requestDto) {
        // Enviamos el DTO directamente al caso de uso de la capa de aplicación
        addShiftUseCase.execute(requestDto);
        
        // Retornamos un estado HTTP 201 Created si todo el proceso fue exitoso
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}