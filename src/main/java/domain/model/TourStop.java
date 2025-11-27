package domain.model;

import java.time.LocalDateTime;

public class TourStop {
    private StopType type;
    private long requestID;
    private long intersectionId;
    private long arrivalTime;
    private long departureTime;

    public TourStop(StopType type, long requestID, long intersectionId, long arrivalTime, long departureTime) {
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

    public long getArrivalTime() {
        return arrivalTime;
    }

    public long getDepartureTime() {
        return departureTime;
    }
}
