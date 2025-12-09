package domain.utils;

import domain.model.RoadSegment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the {@link DurationUtil} class.
 * Verifies the correctness of the duration computation based on road segment length and courier speed.
 */
public class DurationUtilTest {

    /**
     * Tests that the {@code computeDuration} method returns 0.0 when the road segment length is zero or negative.
     */
    @Test
    void checkComputeDurationReturnsZeroWhenDistanceIsZeroOrNegative() {
        RoadSegment roadSegment = new RoadSegment("", 0.0, 0L, 0L);
        double d0 = DurationUtil.computeDuration(roadSegment);
        roadSegment = new RoadSegment("", -5.0, 0L, 1L);
        double dNeg = DurationUtil.computeDuration(roadSegment);

        assertEquals(0.0, d0);
        assertEquals(0.0, dNeg);
    }

    /**
     * Tests that the {@code computeDuration} method calculates the duration correctly
     * based on a courier speed of 15 km/h.
     */
    @Test
    void checkComputeDurationUsesSpeedOf15KmPerHour() {
        // 15 km at 15 km/h -> 1 hour
        RoadSegment roadSegment = new RoadSegment("", 15000.0, 0L, 1L);
        double d15 = DurationUtil.computeDuration(roadSegment);
        assertEquals(60.0, d15);

        // 7.5 km at 15 km/h -> 0.5 h
        roadSegment = new RoadSegment("", 7500.0, 0L, 1L);
        double d75 = DurationUtil.computeDuration(roadSegment);
        assertEquals(30.0, d75);
    }

    /**
     * Tests that the {@code computeDuration} method returns 0.0 when the road segment is null.
     */
    @Test
    void checkComputeDurationReturnsZeroWhenRoadSegmentIsNull() {
        double duration = DurationUtil.computeDuration(null);
        assertEquals(0.0, duration);
    }

    /**
     * Tests that the {@code computeDuration} method calculates the duration correctly
     * for a very small road segment length.
     */
    @Test
    void checkComputeDurationForVerySmallDistance() {
        RoadSegment roadSegment = new RoadSegment("", 1.0, 0L, 1L);
        double duration = DurationUtil.computeDuration(roadSegment);
        assertEquals(0.004, duration, 0.001);
    }

    /**
     * Tests that the {@code computeDuration} method calculates the duration correctly
     * for a very large road segment length.
     */
    @Test
    void checkComputeDurationForVeryLargeDistance() {
        RoadSegment roadSegment = new RoadSegment("", 900_000.0, 0L, 1L);
        double duration = DurationUtil.computeDuration(roadSegment);
        assertEquals(3600.0, duration);
    }
}