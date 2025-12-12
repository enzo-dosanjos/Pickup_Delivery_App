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

    /** Represents an intermediate stop with a type value of 3.
     * An intermediate stop is not a real stop, but rather an intersection by witch
     * the courier will pass between two real stops (Pickup, Delivery or Warehouse)*/
    INTERMEDIATE(3),

    /** Represents the warehouse stop with a type value of 0. */
    WAREHOUSE(0);


    final int type; // The integer value associated with the stop type.

    /**
     * Constructs a StopType with the specified integer value.
     *
     * @param type the integer value representing the stop type
     */
    StopType(int type) {
        this.type = type;
    }


    public int getType() {
        return type;
    }
}