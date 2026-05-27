package co.edu.uco.mihorario.crosscutting.helpers;

import java.time.LocalTime;
import java.util.Objects;

public final class TimeHelper {

    private TimeHelper() {
        throw new UnsupportedOperationException("Utilities class cannot be instantiated");
    }

    public static LocalTime getDefaultTime(LocalTime time) {
        return Objects.isNull(time) ? LocalTime.now() : time;
    }

    public static LocalTime validateEntryTime(LocalTime time) {
        if (Objects.isNull(time)) {
            throw new IllegalArgumentException("Entry time cannot be null");
        }
        return time;
    }

    public static LocalTime validateExitTime(LocalTime time) {
        if (Objects.isNull(time)) {
            throw new IllegalArgumentException("Exit time cannot be null");
        }
        return time;
    }

    public static boolean isAfter(LocalTime time1, LocalTime time2) {
        return time1.isAfter(time2);
    }
}
