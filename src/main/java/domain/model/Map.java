package domain.model;

import java.util.ArrayList;
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
        if (intersections.containsKey(intersection.getId())) {
            return false;
        }

        intersections.put(intersection.getId(), intersection);

        return true;
    }

    public boolean addRoadSegment(Long startIntersectionId, RoadSegment roadSegment) {
        if (!intersections.containsKey(startIntersectionId)) {
            return false;
        }

        if (roadSegment.getStartId() != startIntersectionId) {
            return false;
        }

        if (!intersections.containsKey(roadSegment.getEndId())) {
            return false;
        }

        RoadSegment[] segments = adjencyList.get(startIntersectionId);
        if (segments == null) {
            segments = new RoadSegment[] { roadSegment };
        } else {
            RoadSegment[] newSegments = new RoadSegment[segments.length + 1];
            System.arraycopy(segments, 0, newSegments, 0, segments.length);
            newSegments[segments.length] = roadSegment;
            segments = newSegments;
        }

        adjencyList.put(startIntersectionId, segments);

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

    public ArrayList<RoadSegment> getRoadSegmentByName(String name) {
        // Iterate through all road segments in the adjacency list to find segments with the given partial or full name
        ArrayList<RoadSegment> roadSegments = new ArrayList<>();
        for (RoadSegment[] segments : adjencyList.values()) {
            for (RoadSegment segment : segments) {
                if (segment.getName().contains(name)) {
                    roadSegments.add(segment);
                }
            }
        }

        return roadSegments;
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
