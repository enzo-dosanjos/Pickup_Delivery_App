package domain.model;

import java.time.Duration;
import java.util.UUID;

public class Request {
    private final long id;
    private final long pickupIntersectionId;
    private final Duration pickupDuration;
    private final long deliveryIntersectionId;
    private final Duration deliveryDuration;

    public Request(long pickupIntersectionId, Duration pickupDuration, long deliveryIntersectionId, Duration deliveryDuration) {
        this.id = generateId();
        this.pickupIntersectionId = pickupIntersectionId;
        this.pickupDuration = pickupDuration;
        this.deliveryIntersectionId = deliveryIntersectionId;
        this.deliveryDuration = deliveryDuration;
    }

    public static long generateId() {
        return UUID.randomUUID().getMostSignificantBits();
    }

    public long getId() {
        return id;
    }

    public long getPickupIntersectionId() {
        return pickupIntersectionId;
    }

    public Duration getPickupDuration() {
        return pickupDuration;
    }

    public long getDeliveryIntersectionId() {
        return deliveryIntersectionId;
    }

    public Duration getDeliveryDuration() {
        return deliveryDuration;
    }

    public String toString() {
        return "Request ID: " + id +
               ", Pickup at: " + pickupIntersectionId + " (Duration: " + pickupDuration.toMinutes() + " mins)" +
               ", Delivery at: " + deliveryIntersectionId + " (Duration: " + deliveryDuration.toMinutes() + " mins)";
    }
}
