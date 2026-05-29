package co.edu.uco.mihorario.infrastructure.adapters.messaging;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NotificationGatewayTest {

    @Autowired
    private NotificationGatewayAdapter notificationGatewayAdapter;

    @Test
    void whenContextLoads_thenNotificationAdapterIsConfigured() {
        assertNotNull(notificationGatewayAdapter);
    }

    @Test
    void testSendEmailIntegration() {
        notificationGatewayAdapter.sendShiftNotification(
                "jrodriguezgiraldo8@gmail.com",
                "Juan Rodríguez",
                "Zona Norte",
                "2026-05-27 08:00"
        );
    }
}
