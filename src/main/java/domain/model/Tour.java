package domain.model;

import java.util.ArrayList;
import java.util.List;

public class Tour {
    private long courierId;
    private List<TourStop> stops;
    private long totalDistance;
    private long totalDuration;

    public Tour(long courierId, List<TourStop> stops, long totalDistance, long totalDuration) {
        this.courierId = courierId;
        this.stops = stops;
        this.totalDistance = totalDistance;
        this.totalDuration = totalDuration;
    }

    public long getCourierId() {
        return courierId;
    }

    public List<TourStop> getStops() {
        return stops;
    }

    public long getTotalDistance() {
        return totalDistance;
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    public void addStop (TourStop stop){
        this.stops.add(stop);
    }
}
