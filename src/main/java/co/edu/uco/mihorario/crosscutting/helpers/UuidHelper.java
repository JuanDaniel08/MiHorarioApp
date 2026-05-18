package co.edu.uco.mihorario.domain.helpers;

import java.util.UUID;

public class UuidHelper {

    private UuidHelper() {
    }

    public static UUID generateUuid() {
        return UUID.randomUUID();
    }

    public static UUID convertStringToUuid(String uuid) {
        return UUID.fromString(uuid);
    }

    public static String generateString() {
        return UUID.randomUUID().toString();
    }

    public static UUID nullSafeId(UUID uuid) {
        return uuid == null ? UUID.randomUUID() : uuid;
    }
}
