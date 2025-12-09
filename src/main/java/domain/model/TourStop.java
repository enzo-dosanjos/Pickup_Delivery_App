package domain.model;

import java.time.LocalDateTime;

/**
 * Represents a stop in a pickup and delivery tour.
 * Each stop is characterized by its type, request ID, intersection ID, arrival time, and departure time.
 */
public class TourStop {

    private final StopType type; // The type of the stop (e.g., PICKUP, DELIVERY, etc.).


    private final long requestID; // The unique identifier for the request associated with the stop.


    private final long intersectionId; // The ID of the intersection where the stop is located.


    private LocalDateTime arrivalTime; // The time when the courier arrives at the stop.


    private LocalDateTime departureTime; // The time when the courier departs from the stop.

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


    public StopType getType() {
        return type;
    }


    public long getRequestID() {
        return requestID;
    }


    public long getIntersectionId() {
        return intersectionId;
    }


    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }


    public LocalDateTime getDepartureTime() {
        return departureTime;
    }


    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }


    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }


    public String toString() {
        return "TourStop [type=" + type + ", requestID=" + requestID + ", intersectionId=" + intersectionId
                + ", arrivalTime=" + arrivalTime + ", departureTime=" + departureTime + "]";
    }
}