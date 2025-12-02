package domain.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Tour {
    long courrierId;
    ArrayList<TourStop> stops;
    ArrayList<RoadSegment> roadSegmentsTaken;
    double totalDistance;
    LocalDateTime startTime;
    Duration totalDuration;

    public Tour(long courrierId, LocalDateTime startTime) {
        this.courrierId = courrierId;
        this.startTime = startTime;
        stops = new ArrayList<>();
        roadSegmentsTaken = new ArrayList<>();
        totalDistance = 0.0;
        totalDuration = Duration.ZERO;
    }

    public void addStop (TourStop stop){
        this.stops.add(stop);
    }

    public void addRoadSegment (RoadSegment roadSegment){
        roadSegmentsTaken.add(roadSegment);
        updateTotalDistance(roadSegment.getLength());
    }

    public void updateTotalDistance(double distance) {
        totalDistance += distance;
    }

    public void updateTotalDuration(Duration duration) {
        totalDuration = totalDuration.plus(duration);
    }

    public long getCourrierId() {
        return courrierId;
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

    public ArrayList<RoadSegment> getRoadSegmentsTaken() {
        return roadSegmentsTaken;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Tour for Courrier ID: ").append(courrierId).append("\n");
        sb.append("Stops:\n");
        for (TourStop stop : stops) {
            sb.append(" - ").append(stop.toString()).append("\n");
        }
        sb.append("Total Distance: ").append(totalDistance).append(" km\n");
        sb.append("Total Duration: ").append(totalDuration.toMinutes()).append(" minutes\n");
        return sb.toString();
    }
}
