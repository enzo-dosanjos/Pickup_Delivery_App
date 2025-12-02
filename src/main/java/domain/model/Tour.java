package domain.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Tour {
    private final long courierId;
    private ArrayList<TourStop> stops;
    private ArrayList<RoadSegment> roadSegmentsTaken;
    private double totalDistance;
    private LocalDateTime startTime;
    private Duration totalDuration;

    public Tour(long courierId, LocalDateTime startTime) {
        this.courierId = courierId;
        this.startTime = startTime;
        stops = new ArrayList<>();
        roadSegmentsTaken = new ArrayList<>();
        totalDistance = 0.0;
        totalDuration = Duration.ZERO;
    }

    public void addStop (TourStop stop){
        this.stops.add(stop);
    }

    public void addRoadSegment (RoadSegment roadSegment, Duration duration){
        roadSegmentsTaken.add(roadSegment);
        updateTotalDistance(roadSegment.getLength());
        updateTotalDuration(duration);
    }

    public void updateTotalDistance(double distance) {
        totalDistance += distance;
    }

    public void updateTotalDuration(Duration duration) {
        totalDuration = totalDuration.plus(duration);
    }

    public long getCourierId() {
        return courierId;
    }

    public List<TourStop> getStops() {
        return stops;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getTotalDuration() {
        return totalDuration;
    }

    public List<RoadSegment> getRoadSegmentsTaken() {
        return roadSegmentsTaken;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Tour for Courier ID: ").append(courierId).append("\n");
        sb.append("Stops:\n");
        for (TourStop stop : stops) {
            sb.append(" - ").append(stop.toString()).append("\n");
        }
        sb.append("Total Distance: ").append(totalDistance).append(" km\n");
        sb.append("Total Duration: ").append(totalDuration.toMinutes()).append(" minutes\n");
        return sb.toString();
    }
}
