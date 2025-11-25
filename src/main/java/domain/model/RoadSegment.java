package domain.model;

public class RoadSegment {
    private final String name;
    private final double length; // in meters
    private final long startId;
    private final long endId;

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
