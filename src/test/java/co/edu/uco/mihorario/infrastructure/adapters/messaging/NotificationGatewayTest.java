package co.edu.uco.mihorario.infrastructure.adapters.messaging;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class NotificationGatewayTest {

    @Autowired
    private NotificationGatewayAdapter notificationGatewayAdapter;

    @Test
    public void whenContextLoads_thenNotificationAdapterIsConfigured() {
        assertNotNull(notificationGatewayAdapter);
    }
}
