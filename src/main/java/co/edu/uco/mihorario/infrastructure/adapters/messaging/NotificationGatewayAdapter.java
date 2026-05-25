package co.edu.uco.mihorario.infrastructure.adapters.messaging;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class NotificationGatewayAdapter {

    private static final Logger log = LoggerFactory.getLogger(NotificationGatewayAdapter.class);

    // Inyectamos los secretos desde el application.properties (Punto 11)
    @Value("${sendgrid.api.key}")
    private String apiKey;

    @Value("${sendgrid.from.email}")
    private String fromEmail;

    // 🚀 PUNTO 8: Integración Real con el Notification Gateway Externo
    public void sendShiftNotification(String employeeEmail, String employeeName, String zoneName, String startDate) {
        
        // 1. Simulación de lectura del Catálogo de Propiedades (Punto 11)
        String subject = "Asignación de Nuevo Turno - MiHorarioApp";
        String bodyTemplate = "Hola %s, se te ha asignado un nuevo turno de limpieza en la zona %s. Tu jornada inicia el %s. ¡Revisa tu app!";
        String finalMessage = String.format(bodyTemplate, employeeName, zoneName, startDate);

        // 2. Construcción del correo electrónico estructurado según el SDK externo
        Email from = new Email(fromEmail);
        Email to = new Email(employeeEmail); // Correo del empleado real
        Content content = new Content("text/plain", finalMessage);
        Mail mail = new Mail(from, subject, to, content);

        // 3. Cliente HTTP de SendGrid para disparar la petición al exterior
        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();

        try {
            log.info("[NOTIFICATION GATEWAY] Desplegando petición externa hacia los servidores de SendGrid...");
            
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            
            // Se ejecuta la llamada de red externa
            Response response = sg.api(request);

            // Verificamos si el proveedor aceptó el envío (Códigos 200, 202)
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                log.info("[NOTIFICATION GATEWAY] ¡Correo enviado con éxito! Proveedor Externo Status: {}", response.getStatusCode());
            } else {
                log.warn("[NOTIFICATION GATEWAY] SendGrid rechazó el correo. Status: {} | Body: {}", response.getStatusCode(), response.getBody());
            }

        } catch (IOException ex) {
            // Manejo de resiliencia si el servicio externo se cae
            log.error("[NOTIFICATION GATEWAY] Error crítico de conectividad con el proveedor de notificaciones.", ex);
        }
    }
}