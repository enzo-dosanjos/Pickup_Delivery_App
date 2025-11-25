package domain.model;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Tour {
    long courrierId;
    List<TourStop> stops = new ArrayList<>();
    double totalDistance;
    Duration totalDuration;

    public Tour(long courrierId) {
        this.courrierId = courrierId;
    }
    public void addStop (TourStop stop){
        this.stops.add(stop);
    }

}
