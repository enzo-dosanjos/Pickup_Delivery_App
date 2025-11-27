package domain.model;

public class Request {
    private final long id;
    private final long pickupIntersectionId;
    private final int pickupDuration;
    private final long deliveryIntersectionId;
    private final int deliveryDuration;

    public Request(long id, long pickupIntersectionId, int pickupDuration, long deliveryIntersectionId, int deliveryDuration) {
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

    public int getPickupDuration() {
        return pickupDuration;
    }

    public long getDeliveryIntersectionId() {
        return deliveryIntersectionId;
    }

    public int getDeliveryDuration() {
        return deliveryDuration;
    }
}
