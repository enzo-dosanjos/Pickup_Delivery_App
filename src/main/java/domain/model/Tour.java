package domain.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a delivery tour for a courier, including stops, road segments, total distance, and duration.
 */
public class Tour {
    /** The ID of the courier assigned to the tour. */
    private final long courierId;

    /** The list of stops included in the tour. */
    private ArrayList<TourStop> stops;

    /** The list of road segments taken during the tour. */
    private ArrayList<RoadSegment> roadSegmentsTaken;

    /** The total distance covered during the tour, in kilometers. */
    private double totalDistance;

    /** The start time of the tour. */
    private LocalDateTime startTime;

    /** The total duration of the tour. */
    private Duration totalDuration;

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
     * @param distance the distance to be added, in kilometers
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

    /**
     * Retrieves the ID of the courier assigned to the tour.
     *
     * @return the courier ID
     */
    public long getCourierId() {
        return courierId;
    }

    /**
     * Retrieves the list of stops included in the tour.
     *
     * @return the list of stops
     */
    public List<TourStop> getStops() {
        return stops;
    }

    /**
     * Retrieves the total distance covered during the tour.
     *
     * @return the total distance, in kilometers
     */
    public double getTotalDistance() {
        return totalDistance;
    }

    /**
     * Retrieves the start time of the tour.
     *
     * @return the start time
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Retrieves the total duration of the tour.
     *
     * @return the total duration
     */
    public Duration getTotalDuration() {
        return totalDuration;
    }

    /**
     * Retrieves the list of road segments taken during the tour.
     *
     * @return the list of road segments
     */
    public ArrayList<RoadSegment> getRoadSegmentsTaken() {
        return roadSegmentsTaken;
    }

    /**
     * Retrieves a stop from the tour by its intersection ID.
     *
     * @param intersectionId the intersection ID of the stop to retrieve
     * @return the TourStop with the specified intersection ID, or null if not found
     */
    public TourStop getStopByIntersectionId(long intersectionId) {
    for (TourStop stop : stops) {
        if (stop.getIntersectionId() == intersectionId) {
            return stop;
        }
    }

    return null;
}

    /**
     * Returns a string representation of the tour, including courier ID, stops, total distance, and duration.
     *
     * @return a string describing the tour
     */
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
