package domain.model;

public class Request {
    private final long id;
    private final long pickupIntersectionId;
    private final long pickupDuration;
    private final long deliveryIntersectionId;
    private final long deliveryDuration;

    public Request(long id, long pickupIntersectionId, long pickupDuration, long deliveryIntersectionId, long deliveryDuration) {
        this.id = id;
        this.pickupIntersectionId = pickupIntersectionId;
        this.pickupDuration = pickupDuration;
        this.deliveryIntersectionId = deliveryIntersectionId;
        this.deliveryDuration = deliveryDuration;
    }

    public long getId() {
        return id;
    }

    public long getPickupIntersectionId() {
        return pickupIntersectionId;
    }

    public long getPickupDuration() {
        return pickupDuration;
    }

    public long getDeliveryIntersectionId() {
        return deliveryIntersectionId;
    }

    public long getDeliveryDuration() {
        return deliveryDuration;
    }
}
