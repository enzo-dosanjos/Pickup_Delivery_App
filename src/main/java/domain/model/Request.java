package domain.model;

import java.time.Duration;
import java.util.UUID;

/**
 * Represents a request for a pickup and delivery operation.
 */
public class Request {
    /** The unique identifier for the request. */
    private final long id;

    /** The intersection ID where the pickup occurs. */
    private final long pickupIntersectionId;

    /** The duration of the pickup operation. */
    private final Duration pickupDuration;

    /** The intersection ID where the delivery occurs. */
    private final long deliveryIntersectionId;

    /** The duration of the delivery operation. */
    private final Duration deliveryDuration;

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

    /**
     * Generates a unique identifier for the request.
     *
     * @return a unique long value
     */
    public static long generateId() {
        return UUID.randomUUID().getMostSignificantBits();
    }

    /**
     * Retrieves the unique identifier of the request.
     *
     * @return the request ID
     */
    public long getId() {
        return id;
    }

    /**
     * Retrieves the intersection ID for the pickup.
     *
     * @return the pickup intersection ID
     */
    public long getPickupIntersectionId() {
        return pickupIntersectionId;
    }

    /**
     * Retrieves the duration of the pickup operation.
     *
     * @return the pickup duration
     */
    public Duration getPickupDuration() {
        return pickupDuration;
    }

    /**
     * Retrieves the intersection ID for the delivery.
     *
     * @return the delivery intersection ID
     */
    public long getDeliveryIntersectionId() {
        return deliveryIntersectionId;
    }

    /**
     * Retrieves the duration of the delivery operation.
     *
     * @return the delivery duration
     */
    public Duration getDeliveryDuration() {
        return deliveryDuration;
    }

    /**
     * Returns a string representation of the request, including its details.
     *
     * @return a string describing the request
     */
    public String toString() {
        return "Request ID: " + id +
               ", Pickup at: " + pickupIntersectionId + " (Duration: " + pickupDuration.toMinutes() + " mins)" +
               ", Delivery at: " + deliveryIntersectionId + " (Duration: " + deliveryDuration.toMinutes() + " mins)";
    }
}