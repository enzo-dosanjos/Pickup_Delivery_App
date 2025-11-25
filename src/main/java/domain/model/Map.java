package domain.model;

import java.util.HashMap;
import java.util.TreeMap;

public class Map {
    private final TreeMap<Long, Intersection> intersections;
    private final HashMap<Long, RoadSegment[]> adjencyList;

    public Map() {
        this.intersections = new TreeMap<>();
        this.adjencyList = new HashMap<>();
    }

    public boolean addIntersection(Intersection intersection) {
        if (intersections.containsKey((long)intersection.getId())) {
            return false;
        }

        intersections.put((long)intersection.getId(), intersection);

        return true;
    }

    public boolean addRoadSegment(Long intersectionId, RoadSegment roadSegment) {
        if (!intersections.containsKey(intersectionId)) {
            return false;
        }

        RoadSegment[] segments = adjencyList.get(intersectionId);
        if (segments == null) {
            segments = new RoadSegment[] { roadSegment };
        } else {
            RoadSegment[] newSegments = new RoadSegment[segments.length + 1];
            System.arraycopy(segments, 0, newSegments, 0, segments.length);
            newSegments[segments.length] = roadSegment;
            segments = newSegments;
        }

        adjencyList.put(intersectionId, segments);

        return true;
    }

    public RoadSegment getRoadSegment(Long startId, Long endId) {
        RoadSegment[] segments = adjencyList.get(startId);
        if (segments != null) {
            for (RoadSegment segment : segments) {
                if (segment.getEndId() == endId) {
                    return segment;
                }
            }
        }

        return null;
    }

    public TreeMap<Long, Intersection> getIntersections() {
        return intersections;
    }

    public HashMap<Long, RoadSegment[]> getAdjencyList() {
        return adjencyList;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Map:\n");
        sb.append("Intersections:\n");
        for (Intersection intersection : intersections.values()) {
            sb.append(intersection).append("\n");
        }
        sb.append("Adjacency List:\n");
        for (Long id : adjencyList.keySet()) {
            sb.append("Intersection ").append(id).append(": ");
            for (RoadSegment segment : adjencyList.get(id)) {
                sb.append(segment).append(", ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
