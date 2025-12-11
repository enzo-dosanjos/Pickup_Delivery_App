package domain.model;

/**
 * Enumeration representing the availability status of an entity.
 */
public enum AvailabilityStatus {
    /** The entity is available. */
    AVAILABLE(0),
    /** The entity is busy. */
    BUSY(1);

    final int type; // The integer type associated with the availability status.

    /**
     * Constructs an AvailabilityStatus with the specified type.
     *
     * @param type the integer type associated with the availability status
     */
    AvailabilityStatus(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

}
