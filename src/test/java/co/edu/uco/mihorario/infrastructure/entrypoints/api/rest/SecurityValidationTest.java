package co.edu.uco.mihorario.infrastructure.entrypoints.api.rest;

import co.edu.uco.mihorario.application.usecase.input.AddShiftUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class SecurityValidationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private AddShiftUseCase addShiftUseCase;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    void whenPostShiftWithoutToken_thenReturns403Or401() throws Exception {
        String payload = """
            {
                "employeeId": "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d",
                "laborId": "f8e7d6c5-b4a3-2f1e-0d9c-8b7a6f5e4d3c",
                "date": "2026-05-25",
                "startTime": "2026-05-25T06:00:00",
                "endTime": "2026-05-25T14:00:00",
                "active": true,
                "observation": "Test observation"
            }
            """;

        mockMvc.perform(post("/api/v1/shifts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isUnauthorized()); // Spring Security returns 401 when not authenticated
    }

    @Test
    @WithMockUser(username = "empleado", roles = {"EMPLEADO"})
    void whenPostShiftWithInvalidRole_thenReturns403() throws Exception {
        String payload = """
            {
                "employeeId": "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d",
                "laborId": "f8e7d6c5-b4a3-2f1e-0d9c-8b7a6f5e4d3c",
                "date": "2026-05-25",
                "startTime": "2026-05-25T06:00:00",
                "endTime": "2026-05-25T14:00:00",
                "active": true,
                "observation": "Test observation"
            }
            """;

        mockMvc.perform(post("/api/v1/shifts")
                .header("X-Captcha-Token", "google-recaptcha-v3-token-valid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "coordinador", roles = {"COORDINADOR"})
    void whenPostShiftWithoutCaptcha_thenReturns400() throws Exception {
        String payload = """
            {
                "employeeId": "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d",
                "laborId": "f8e7d6c5-b4a3-2f1e-0d9c-8b7a6f5e4d3c",
                "date": "2026-05-25",
                "startTime": "2026-05-25T06:00:00",
                "endTime": "2026-05-25T14:00:00",
                "active": true,
                "observation": "Test observation"
            }
            """;

        mockMvc.perform(post("/api/v1/shifts")
                // Missing captcha
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "coordinador", roles = {"COORDINADOR"})
    void whenPostShiftWithValidRoleAndCaptcha_thenReturns201() throws Exception {
        String payload = """
            {
                "employeeId": "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d",
                "laborId": "f8e7d6c5-b4a3-2f1e-0d9c-8b7a6f5e4d3c",
                "date": "2026-05-25",
                "startTime": "2026-05-25T06:00:00",
                "endTime": "2026-05-25T14:00:00",
                "active": true,
                "observation": "Test observation"
            }
            """;

        mockMvc.perform(post("/api/v1/shifts")
                .header("X-Captcha-Token", "google-recaptcha-v3-token-valid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isCreated());
    }
}
