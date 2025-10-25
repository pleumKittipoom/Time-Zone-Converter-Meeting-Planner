package app;

import java.time.ZoneId;
import java.util.Objects;

public final class City {
    private final String code;
    private final String name;
    private final ZoneId zone;
    private final WorkHours work;

    public City(String code, String name, ZoneId zone, WorkHours work) {
        this.code = Objects.requireNonNull(code);
        this.name = Objects.requireNonNull(name);
        this.zone = Objects.requireNonNull(zone);
        this.work = Objects.requireNonNull(work);
    }

    public String code() { return code; }
    public String name() { return name; }
    public ZoneId zone() { return zone; }
    public WorkHours work() { return work; }

    @Override public String toString() {
        return code + " - " + name + " (" + zone + ") " + work;
    }
}
