package co.edu.uco.mihorario.domain.ports.out;

public interface NotificationGateway {
    void sendShiftNotification(String destinationEmail, String workerName, String laborDetail, String shiftDate);
}
