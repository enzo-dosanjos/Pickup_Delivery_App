package domain.utils;

import domain.model.RoadSegment;

/**
 * Utility class for calculating durations based on road segments and courier speed.
 */
public class DurationUtil {
    // Constant representing the courier's speed in kilometers per hour.
    private static final double COURIER_SPEED_KMH = 15.0;

    /**
     * Computes the duration (in minutes) required to traverse a given road segment
     * based on the courier's speed.
     *
     * @param roadSegment the road segment for which the duration is to be calculated
     * @return the duration in minutes, or 0.0 if the road segment is null or has a non-positive length
     */
    public static double computeDuration(RoadSegment roadSegment) {
        if (roadSegment == null || roadSegment.getLength() <= 0) { return 0.0; }
        return (roadSegment.getLength() / (1000.0 * COURIER_SPEED_KMH)) * 60;
    }
}