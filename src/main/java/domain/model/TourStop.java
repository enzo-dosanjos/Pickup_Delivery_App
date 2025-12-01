package domain.model;

import java.time.LocalDateTime;

public class TourStop {
    private final StopType type;
    private final long requestID;
    private final long intersectionId;
    private LocalDateTime  arrivalTime;
    private LocalDateTime  departureTime;

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
