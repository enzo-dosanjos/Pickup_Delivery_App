package domain.model;

/**
 * Represents the type of a stop in a delivery or pickup operation.
 * Each stop type is associated with a unique integer value.
 */
public enum StopType {
    /** Represents a pickup stop with a type value of 1. */
    PICKUP(1),

    /** Represents a delivery stop with a type value of 2. */
    DELIVERY(2),

    /** Represents an intermediate stop with a type value of 3. */
    INTERMEDIATE(3),

    /** Represents a warehouse stop with a type value of 0. */
    WAREHOUSE(0);

    /** The integer value associated with the stop type. */
    final int type;

    /**
     * Constructs a StopType with the specified integer value.
     *
     * @param type the integer value representing the stop type
     */
    StopType(int type) {
        this.type = type;
    }

    /**
     * Retrieves the integer value associated with the stop type.
     *
     * @return the integer value of the stop type
     */
    public int getType() {
        return type;
    }
}