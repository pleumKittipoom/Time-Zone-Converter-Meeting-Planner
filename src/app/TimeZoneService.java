package app;

import java.time.*;

public final class TimeZoneService {
    public ZonedDateTime convert(LocalDateTime localDateTime, City from, City to) {
        ZonedDateTime zFrom = ZonedDateTime.of(localDateTime, from.zone());
        return zFrom.withZoneSameInstant(to.zone());
    }
}
