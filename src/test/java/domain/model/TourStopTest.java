package domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link TourStop} class.
 */
public class TourStopTest {

    /**
     * Verifies that the arrival time can be updated correctly.
     */
    @Test
    void updatingArrivalTimeUpdatesValue() {
        TourStop stop = new TourStop(StopType.PICKUP, 101L, 51L, LocalDateTime.now(), LocalDateTime.now().plusMinutes(5));
        LocalDateTime newArrivalTime = LocalDateTime.now().plusMinutes(10);

        stop.setArrivalTime(newArrivalTime);

        assertEquals(newArrivalTime, stop.getArrivalTime());
    }

    /**
     * Verifies that the departure time can be updated correctly.
     */
    @Test
    void updatingDepartureTimeUpdatesValue() {
        TourStop stop = new TourStop(StopType.DELIVERY, 102L, 52L, LocalDateTime.now(), LocalDateTime.now().plusMinutes(5));
        LocalDateTime newDepartureTime = LocalDateTime.now().plusMinutes(15);

        stop.setDepartureTime(newDepartureTime);

        assertEquals(newDepartureTime, stop.getDepartureTime());
    }

    /**
     * Verifies that the toString method includes all relevant details of the TourStop.
     */
    @Test
    void toStringIncludesAllDetails() {
        LocalDateTime arrivalTime = LocalDateTime.now();
        LocalDateTime departureTime = arrivalTime.plusMinutes(5);
        TourStop stop = new TourStop(StopType.PICKUP, 101L, 51L, arrivalTime, departureTime);

        String result = stop.toString();

        assertTrue(result.contains("type=PICKUP"));
        assertTrue(result.contains("requestID=101"));
        assertTrue(result.contains("intersectionId=51"));
        assertTrue(result.contains("arrivalTime=" + arrivalTime));
        assertTrue(result.contains("departureTime=" + departureTime));
    }

}