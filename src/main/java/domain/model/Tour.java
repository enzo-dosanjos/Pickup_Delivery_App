package domain.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a delivery tour for a courier, including stops, road segments, total distance, and duration.
 */
public class Tour {

    private final long courierId; // The ID of the courier assigned to the tour.


    private ArrayList<TourStop> stops; // The list of stops included in the tour.


    private ArrayList<RoadSegment> roadSegmentsTaken; // The list of road segments taken during the tour.


    private double totalDistance; // The total distance covered during the tour, in meters.


    private LocalDateTime startTime; // The start time of the tour.


    private Duration totalDuration; // The total duration of the tour.

    /**
     * Constructs a new Tour for the specified courier, starting at the given time.
     *
     * @param courierId the ID of the courier assigned to the tour
     * @param startTime the start time of the tour
     */
    public Tour(long courierId, LocalDateTime startTime) {
        this.courierId = courierId;
        this.startTime = startTime;
        stops = new ArrayList<>();
        roadSegmentsTaken = new ArrayList<>();
        totalDistance = 0.0;
        totalDuration = Duration.ZERO;
    }

    /**
     * Adds a stop to the tour.
     *
     * @param stop the stop to be added
     */
    public void addStop(TourStop stop) {
        this.stops.add(stop);
    }

    /**
     * Adds a road segment to the tour and updates the total distance and duration.
     *
     * @param roadSegment the road segment to be added
     */
    public void addRoadSegment(RoadSegment roadSegment) {
        roadSegmentsTaken.add(roadSegment);
        updateTotalDistance(roadSegment.getLength());
    }

    /**
     * Updates the total distance of the tour by adding the specified distance.
     *
     * @param distance the distance to be added, in meters
     */
    public void updateTotalDistance(double distance) {
        totalDistance += distance;
    }

    /**
     * Updates the total duration of the tour by adding the specified duration.
     *
     * @param duration the duration to be added
     */
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


    public ArrayList<RoadSegment> getRoadSegmentsTaken() {
        return roadSegmentsTaken;
    }


    public TourStop getStopByIntersectionId(long intersectionId) {
    for (TourStop stop : stops) {
        if (stop.getIntersectionId() == intersectionId) {
            return stop;
        }
    }

    return null;
}


    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Tour for Courier ID: ").append(courierId).append("\n");
        sb.append("Stops:\n");
        for (TourStop stop : stops) {
            sb.append(" - ").append(stop.toString()).append("\n");
        }
        sb.append("Total Distance: ").append(totalDistance).append(" m\n");
        sb.append("Total Duration: ").append(totalDuration.toMinutes()).append(" minutes\n");
        return sb.toString();
    }
}
