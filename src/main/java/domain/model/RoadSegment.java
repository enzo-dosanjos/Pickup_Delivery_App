package domain.model;

/**
 * Represents a road segment with a name, length, and start and end intersection IDs.
 */
public class RoadSegment {

    private final String name; // The name of the road segment.


    private final double length; // The length of the road segment in meters.


    private final long startId; // The intersection ID where the road segment starts.


    private final long endId; // The intersection ID where the road segment ends.

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


    public String getName() {
        return name;
    }


    public double getLength() {
        return length;
    }


    public long getStartId() {
        return startId;
    }


    public long getEndId() {
        return endId;
    }


    public String toString() {
        return "RoadSegment{name='" + name + "', length=" + length +
               ", startId=" + startId + ", endId=" + endId + "}";
    }
}