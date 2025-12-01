package domain.utils;

import domain.model.RoadSegment;

public class DurationUtil {
    private static final double COURIER_SPEED_KMH = 15.0;

    public static double computeDuration(RoadSegment roadSegment) {
        if (roadSegment == null || roadSegment.getLength() <= 0) { return 0.0; }
        return (roadSegment.getLength() / (1000.0 * COURIER_SPEED_KMH))*60;
    }
}
