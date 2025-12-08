package domain.model;

import java.time.Duration;

public class Courier {
    private final long id;
    private final String name;
    private final Duration shiftDuration;

    public Courier(long id, String name, Duration shiftDuration) {
        this.id = id;
        this.name = name;
        this.shiftDuration = shiftDuration;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Duration getShiftDuration() {
        return shiftDuration;
    }

    public String toString() {
        return "Courier{id=" + id + ", name='" + name + "', shiftDuration=" + shiftDuration.toMinutes() + "min}";
    }
}
