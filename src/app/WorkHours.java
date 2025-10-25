package app;

import java.time.LocalTime;

public record WorkHours(LocalTime start, LocalTime end) {
    @Override public String toString() {
        return "[" + start + "–" + end + "]";
    }
}
