package domain.model;

import java.time.LocalDateTime;

/**
 * Represents a stop in a delivery or pickup tour.
 * Each stop is characterized by its type, request ID, intersection ID, arrival time, and departure time.
 */
public class TourStop {
    /** The type of the stop (e.g., PICKUP, DELIVERY, etc.). */
    private final StopType type;

    /** The unique identifier for the request associated with the stop. */
    private final long requestID;

    /** The ID of the intersection where the stop is located. */
    private final long intersectionId;

    /** The time when the courier arrives at the stop. */
    private LocalDateTime arrivalTime;

    /** The time when the courier departs from the stop. */
    private LocalDateTime departureTime;

    /**
     * Constructs a new TourStop with the specified details.
     *
     * @param type the type of the stop
     * @param requestID the unique identifier for the request
     * @param intersectionId the ID of the intersection
     * @param arrivalTime the arrival time at the stop
     * @param departureTime the departure time from the stop
     */
    public TourStop(StopType type, long requestID, long intersectionId, LocalDateTime arrivalTime, LocalDateTime departureTime) {
        this.type = type;
        this.requestID = requestID;
        this.intersectionId = intersectionId;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
    }

    /**
     * Retrieves the type of the stop.
     *
     * @return the type of the stop
     */
    public StopType getType() {
        return type;
    }

    /**
     * Retrieves the request ID associated with the stop.
     *
     * @return the request ID
     */
    public long getRequestID() {
        return requestID;
    }

    /**
     * Retrieves the intersection ID of the stop.
     *
     * @return the intersection ID
     */
    public long getIntersectionId() {
        return intersectionId;
    }

    /**
     * Retrieves the arrival time at the stop.
     *
     * @return the arrival time
     */
    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    /**
     * Retrieves the departure time from the stop.
     *
     * @return the departure time
     */
    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    /**
     * Updates the arrival time at the stop.
     *
     * @param arrivalTime the new arrival time
     */
    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    /**
     * Updates the departure time from the stop.
     *
     * @param departureTime the new departure time
     */
    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    /**
     * Returns a string representation of the TourStop, including its type, request ID, intersection ID, arrival time, and departure time.
     *
     * @return a string representation of the TourStop
     */
    public String toString() {
        return "TourStop [type=" + type + ", requestID=" + requestID + ", intersectionId=" + intersectionId
                + ", arrivalTime=" + arrivalTime + ", departureTime=" + departureTime + "]";
    }
}