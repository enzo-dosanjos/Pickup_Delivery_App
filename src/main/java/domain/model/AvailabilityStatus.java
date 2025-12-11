package domain.model;

/**
 * Describes the current availability of a courier.
 * Values are persisted as integers to align with existing DTOs and XML exports.
 */
public enum AvailabilityStatus {
    /** Courier can receive additional requests. */
    AVAILABLE(0),
    /** Courier is currently occupied (tour close to or at capacity). */
    BUSY(1);

    final int type;

    AvailabilityStatus(int type) {
        this.type = type;
    }

    /**
     * Integer code used when serialising the status.
     * @return numeric representation of the status.
     */
    public int getType() {
        return type;
    }

}
