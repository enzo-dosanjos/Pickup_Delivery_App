package domain.model;

import java.time.Duration;

public class Courier {
    private final long id;
    private final String name;
    private final Duration shiftDuration;
    private AvailabilityStatus availabilityStatus;

    public Courier(long id, String name, Duration shiftDuration) {
        this.id = id;
        this.name = name;
        this.shiftDuration = shiftDuration;
        this.availabilityStatus = AvailabilityStatus.AVAILABLE;
    }

    public Boolean isAvailable() {
        return availabilityStatus == AvailabilityStatus.AVAILABLE;
    }

    public Boolean isBusy() {
        return availabilityStatus == AvailabilityStatus.BUSY;
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

    public AvailabilityStatus getAvailabilityStatus() {
        return availabilityStatus;
    }

    public String toString() {
        return "Courier{id=" + id + ", name='" + name + "', shiftDuration=" + shiftDuration.toMinutes() + "min "+ ", availabilityStatus=" + availabilityStatus + "}";
    }
}
