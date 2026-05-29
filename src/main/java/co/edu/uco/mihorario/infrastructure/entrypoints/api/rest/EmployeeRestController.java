package co.edu.uco.mihorario.infrastructure.entrypoints.api.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employees")
@CrossOrigin(origins = "*")
public class EmployeeRestController {

    public record EmployeeDTO(
            UUID id,
            String name,
            String lastName,
            String identification,
            String phone,
            String email,
            boolean active) {
    }

    private static final List<EmployeeDTO> EMPLOYEES = List.of(
            new EmployeeDTO(UUID.fromString("11111111-1111-1111-1111-111111111111"), "Juan Daniel", "Gómez Restrepo",
                    "10203040", "3123456789", "juan.dan@uco.edu.co", true),
            new EmployeeDTO(UUID.fromString("22222222-2222-2222-2222-222222222222"), "Camila", "Herrera Muñoz",
                    "10506070", "3159876543", "camila.herrera@uco.edu.co", true),
            new EmployeeDTO(UUID.fromString("33333333-3333-3333-3333-333333333333"), "Carlos Mario", "Pérez López",
                    "10809010", "3201234567", "carlos.perez@uco.edu.co", true),
            new EmployeeDTO(UUID.fromString("44444444-4444-4444-4444-444444444444"), "Daniel", "Rodriguez", "10908070",
                    "3104567890", "daniel@uco.edu.co", true));

    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getEmployees() {
        return new ResponseEntity<>(EMPLOYEES, HttpStatus.OK);
    }
}
