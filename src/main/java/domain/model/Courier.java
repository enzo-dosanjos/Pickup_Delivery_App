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

    /**
     * Gets the unique identifier of the courier.
     *
     * @return the courier's ID (long)
     */
    public long getId() {
        return id;
    }

    /**
     * Gets the name of the courier.
     *
     * @return the courier's name (String)
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the duration of the courier's shift.
     *
     * @return the courier's shift duration (Duration)
     */
    public Duration getShiftDuration() {
        return shiftDuration;
    }

    /**
     * Returns a string representation of the courier.
     *
     * @return a string containing the courier's ID, name, and shift duration
     */
    public String toString() {
        return "Courier{id=" + id + ", name='" + name + "', shiftDuration=" + shiftDuration.toMinutes() + "min}";
    }
}
