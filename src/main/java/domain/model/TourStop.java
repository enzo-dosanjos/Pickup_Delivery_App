package domain.model;

import java.time.LocalDateTime;

public class TourStop {
    StopType type;
    long requestID;
    long intersectionId;
    LocalDateTime  arrivalTime;
    LocalDateTime  departureTime;

}
