package domain;

import domain.model.RoadSegment;
import domain.utils.DurationUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DurationUtilTest {
    @Test
    void checkComputeDurationReturnsZeroWhenDistanceIsZeroOrNegative() {
        RoadSegment roadSegment = new RoadSegment("", 0.0, 0L, 0L);
        double d0 = DurationUtil.computeDuration(roadSegment);
        roadSegment = new RoadSegment("", -5.0, 0L, 1L);
        double dNeg = DurationUtil.computeDuration(roadSegment);

        assertEquals(0.0, d0);
        assertEquals(0.0, dNeg);
    }

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
}
