package domain.utils;

import domain.model.RoadSegment;

public class DurationUtil {
    private static final double COURIER_SPEED_KMH = 15.0;

    public static double computeDuration(RoadSegment roadSegment) {
        return (roadSegment.getLength() / (1000.0 * COURIER_SPEED_KMH))*60;
    }
}
