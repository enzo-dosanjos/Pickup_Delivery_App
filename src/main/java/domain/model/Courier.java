package domain.model;

import java.time.Duration;

/**
 * Represents a courier with an ID, name, and shift duration.
 */
public class Courier {
    private final long id; // Unique identifier for the courier
    private final String name; // Name of the courier
    private final Duration shiftDuration; // Duration of the courier's shift

    /**
     * Constructs a new Courier instance.
     *
     * @param id            the unique identifier of the courier (long)
     * @param name          the name of the courier (String)
     * @param shiftDuration the duration of the courier's shift (Duration)
     */
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
