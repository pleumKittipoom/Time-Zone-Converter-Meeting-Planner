package app;

import java.time.*;
import java.util.*;

public final class MeetingPlanner {

    public static final class Slot {
        public final ZonedDateTime startUtc;
        public final Duration duration;
        public final Map<City, ZonedDateTime> localTimes;

        public Slot(ZonedDateTime startUtc, Duration duration, Map<City, ZonedDateTime> localTimes) {
            this.startUtc = startUtc;
            this.duration = duration;
            this.localTimes = localTimes;
        }
    }

    public List<Slot> plan(Collection<City> participants,
                           LocalDate startDate,
                           Duration duration,
                           Duration step,
                           int days,
                           int maxResults) {

        List<Slot> results = new ArrayList<>();
        if (participants.isEmpty()) return results;

        ZonedDateTime cursorUtc = ZonedDateTime.of(startDate, LocalTime.MIDNIGHT, ZoneOffset.UTC);
        ZonedDateTime endUtc = cursorUtc.plusDays(days);

        while (cursorUtc.plus(duration).isBefore(endUtc) && results.size() < maxResults) {
            boolean ok = true;
            Map<City, ZonedDateTime> map = new LinkedHashMap<>();

            for (City c : participants) {
                ZonedDateTime localStart = cursorUtc.withZoneSameInstant(c.zone());
                ZonedDateTime localEnd   = localStart.plus(duration);

                LocalDate localDay = localStart.toLocalDate();
                WorkHours wh = c.work();

                ZonedDateTime workStartZ = ZonedDateTime.of(localDay, wh.start(), c.zone());
                ZonedDateTime workEndZ   = ZonedDateTime.of(localDay, wh.end(),   c.zone());

                // รองรับช่วงเวลางานข้ามคืน (เช่น 22:00–06:00)
                if (!workEndZ.isAfter(workStartZ)) {
                    workEndZ = workEndZ.plusDays(1);
                }

                boolean inside = !localStart.isBefore(workStartZ) && !localEnd.isAfter(workEndZ);
                if (!inside) { ok = false; break; }

                map.put(c, localStart);
            }

            if (ok) results.add(new Slot(cursorUtc, duration, map));
            cursorUtc = cursorUtc.plus(step);
        }
        return results;
    }
}
