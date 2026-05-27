package co.edu.uco.mihorario.infrastructure.adapters.messaging;

import co.edu.uco.mihorario.domain.ports.out.NotificationGateway;
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
@org.springframework.context.annotation.PropertySource("classpath:notifications.properties")
public class NotificationGatewayAdapter implements NotificationGateway {

    private static final Logger log = LoggerFactory.getLogger(NotificationGatewayAdapter.class);

    @Value("${sendgrid.api.key}")
    private String apiKey;

    @Value("${sendgrid.from.email}")
    private String fromEmail;

    @Value("${template.shift.assigned.subject}")
    private String subject;

    @Value("${template.shift.assigned.body}")
    private String bodyTemplate;

    @Override
    public void sendShiftNotification(String employeeEmail, String employeeName, String zoneName, String startDate) {

        String finalMessage = String.format(bodyTemplate, employeeName, zoneName, startDate);

        Email from = new Email(fromEmail);
        Email to = new Email(employeeEmail);
        Content content = new Content("text/plain", finalMessage);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();

        try {
            log.info("[NOTIFICATION GATEWAY] Desplegando petición externa hacia SendGrid...");

            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                log.info("[NOTIFICATION GATEWAY] ¡Correo enviado con éxito! Status: {}", response.getStatusCode());
            } else {
                log.warn("[NOTIFICATION GATEWAY] SendGrid rechazó el correo. Status: {}", response.getStatusCode());
            }

        } catch (IOException ex) {
            log.error("[NOTIFICATION GATEWAY] Error de conectividad con el proveedor externo.", ex);
        }
    }
}