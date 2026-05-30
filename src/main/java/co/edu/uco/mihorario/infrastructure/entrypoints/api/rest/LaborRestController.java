package co.edu.uco.mihorario.infrastructure.entrypoints.api.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/labors")
public class LaborRestController {

    public record LaborDTO(
            UUID id,
            String name,
            String description
    ) {}

    private static final List<LaborDTO> LABORS = List.of(
            new LaborDTO(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"), "Mantenimiento Mecánico", "Revisión preventiva de equipos y atracciones"),
            new LaborDTO(UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"), "Servicio al Cliente", "Atención en taquilla y orientación al visitante"),
            new LaborDTO(UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc"), "Limpieza y Desinfección", "Aseo general de áreas comunes y baños"),
            new LaborDTO(UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd"), "Supervisión de Seguridad", "Vigilancia y control de accesos en el parque")
    );

    @GetMapping
    public ResponseEntity<List<LaborDTO>> getLabors() {
        return new ResponseEntity<>(LABORS, HttpStatus.OK);
    }
}
