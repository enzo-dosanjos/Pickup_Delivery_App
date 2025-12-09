package domain.model;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Represents a request for a pickup and delivery operation.
 */
public class Request {

    private final long id; // The unique identifier for the request.

    private final long pickupIntersectionId; // The intersection ID where the pickup occurs.

    private final Duration pickupDuration; // The duration of the pickup operation.

    private final long deliveryIntersectionId; // The intersection ID where the delivery occurs.

    private final Duration deliveryDuration; // The duration of the delivery operation.

    /**
     * Constructs a new Request with the specified pickup and delivery details.
     *
     * @param pickupIntersectionId the intersection ID for the pickup
     * @param pickupDuration the duration of the pickup operation
     * @param deliveryIntersectionId the intersection ID for the delivery
     * @param deliveryDuration the duration of the delivery operation
     */
    public Request(long pickupIntersectionId, Duration pickupDuration, long deliveryIntersectionId, Duration deliveryDuration) {
        this.id = generateId();
        this.pickupIntersectionId = pickupIntersectionId;
        this.pickupDuration = pickupDuration;
        this.deliveryIntersectionId = deliveryIntersectionId;
        this.deliveryDuration = deliveryDuration;
    }

    private static final AtomicLong ID_GENERATOR = new AtomicLong(1);

    /**
     * Generates a unique identifier for the request.
     *
     * @return a unique long value
     */
    public static long generateId() {
        // Use a monotonically increasing counter to stay within JS safe integer range
        return ID_GENERATOR.getAndIncrement();
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
