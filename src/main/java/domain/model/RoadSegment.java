package domain.model;

/**
 * Represents a road segment with a name, length, and start and end intersection IDs.
 */
public class RoadSegment {
    /** The name of the road segment. */
    private final String name;

    /** The length of the road segment in meters. */
    private final double length;

    /** The intersection ID where the road segment starts. */
    private final long startId;

    /** The intersection ID where the road segment ends. */
    private final long endId;

    /**
     * Constructs a new RoadSegment with the specified name, length, start ID, and end ID.
     *
     * @param name the name of the road segment
     * @param length the length of the road segment in meters
     * @param startId the intersection ID where the road segment starts
     * @param endId the intersection ID where the road segment ends
     */
    public RoadSegment(String name, double length, long startId, long endId) {
        this.name = name;
        this.length = length;
        this.startId = startId;
        this.endId = endId;
    }

    /**
     * Retrieves the name of the road segment.
     *
     * @return the name of the road segment
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the length of the road segment in meters.
     *
     * @return the length of the road segment
     */
    public double getLength() {
        return length;
    }

    /**
     * Retrieves the intersection ID where the road segment starts.
     *
     * @return the start intersection ID
     */
    public long getStartId() {
        return startId;
    }

    /**
     * Retrieves the intersection ID where the road segment ends.
     *
     * @return the end intersection ID
     */
    public long getEndId() {
        return endId;
    }

    /**
     * Returns a string representation of the road segment, including its name, length, start ID, and end ID.
     *
     * @return a string describing the road segment
     */
    public String toString() {
        return "RoadSegment{name='" + name + "', length=" + length +
               ", startId=" + startId + ", endId=" + endId + "}";
    }
}