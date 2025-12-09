package domain.model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Tour} class.
 */
public class TourTest {

    /**
     * Tests the constructor and getter methods of the {@link Tour} class.
     * Verifies that the fields are correctly initialized.
     */
    @Test
    void checkConstructorAndGetters() {
        long courierId = 1L; // Unique identifier for the courier
        LocalDateTime startTime = LocalDateTime.now(); // Start time of the tour
        Tour tour = new Tour(courierId, startTime);
        // Assert that the courier ID and start time match the expected values
        assertEquals(courierId, tour.getCourierId());
        assertEquals(startTime, tour.getStartTime());
        // Assert that the stops list is initialized as empty
        assertTrue(tour.getStops().isEmpty());
        // Assert that the total distance is initialized to zero
        assertEquals(0.0, tour.getTotalDistance());
        // Assert that the total duration is initialized to zero
        assertEquals(Duration.ZERO, tour.getTotalDuration());
    }

    /**
     * Verifies that adding a stop to the tour updates the list of stops.
     */
    @Test
    void addingStopUpdatesStopsList() {
        Tour tour = new Tour(1L, LocalDateTime.now());
        TourStop stop = new TourStop(StopType.DELIVERY, 100L, 50L, LocalDateTime.now(), LocalDateTime.now().plusMinutes(10));

        tour.addStop(stop);

        assertEquals(1, tour.getStops().size());
        assertTrue(tour.getStops().contains(stop));
    }

    /**
     * Verifies that adding multiple stops to the tour updates the list of stops correctly.
     */
    @Test
    void addingMultipleStopsUpdatesStopsList() {
        Tour tour = new Tour(1L, LocalDateTime.now());
        TourStop stop1 = new TourStop(StopType.PICKUP, 101L, 51L, LocalDateTime.now(), LocalDateTime.now().plusMinutes(5));
        TourStop stop2 = new TourStop(StopType.DELIVERY, 102L, 52L, LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusMinutes(20));

        tour.addStop(stop1);
        tour.addStop(stop2);

        assertEquals(2, tour.getStops().size());
        assertTrue(tour.getStops().contains(stop1));
        assertTrue(tour.getStops().contains(stop2));
    }

    /**
     * Verifies that adding a road segment with zero distance does not update the total distance.
     */
    @Test
    void addingRoadSegmentWithZeroDistanceDoesNotUpdateTotalDistance() {
        Tour tour = new Tour(1L, LocalDateTime.now());
        RoadSegment segment = new RoadSegment("Zero Distance Segment", 0.0, 1L, 2L);

        tour.addRoadSegment(segment);

        assertEquals(0.0, tour.getTotalDistance());
    }


    /**
     * Verifies that the total duration remains zero when no road segments are added.
     */
    @Test
    void totalDurationRemainsZeroWhenNoRoadSegmentsAdded() {
        Tour tour = new Tour(1L, LocalDateTime.now());

        assertEquals(Duration.ZERO, tour.getTotalDuration());
    }

    /**
     * Verifies that retrieving a stop by intersection ID returns the correct stop.
     */
    @Test
    void getStopByIntersectionIdReturnsCorrectStop() {
        Tour tour = new Tour(1L, LocalDateTime.now());
        TourStop stop1 = new TourStop(StopType.PICKUP, 101L, 51L, LocalDateTime.now(), LocalDateTime.now().plusMinutes(5));
        TourStop stop2 = new TourStop(StopType.DELIVERY, 102L, 52L, LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusMinutes(20));

        tour.addStop(stop1);
        tour.addStop(stop2);

        TourStop retrievedStop = tour.getStopByIntersectionId(52L);
        assertNotNull(retrievedStop);
        assertEquals(stop2, retrievedStop);
    }

    /**
     * Verifies that the toString method handles an empty tour correctly.
     */
    @Test
    void toStringHandlesEmptyTour() {
        Tour tour = new Tour(1L, LocalDateTime.now());

        String result = tour.toString();

        assertTrue(result.contains("Courier ID: 1"));
        assertTrue(result.contains("Stops:"));
        assertTrue(result.contains("Total Distance: 0.0 m"));
        assertTrue(result.contains("Total Duration: 0 minutes"));
    }
}
