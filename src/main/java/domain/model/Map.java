package domain.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Represents a map containing intersections and road segments.
 */
public class Map {
    /** A map of intersections, keyed by their unique identifiers. */
    private final TreeMap<Long, Intersection> intersections;

    /** An adjacency list representing road segments starting from each intersection. */
    private final HashMap<Long, RoadSegment[]> adjencyList;

    /**
     * Constructs an empty map with no intersections or road segments.
     */
    public Map() {
        this.intersections = new TreeMap<>();
        this.adjencyList = new HashMap<>();
    }

    /**
     * Adds an intersection to the map.
     *
     * @param intersection the intersection to add
     * @return true if the intersection was added, false if it already exists
     */
    public boolean addIntersection(Intersection intersection) {
        if (intersections.containsKey(intersection.getId())) {
            return false;
        }

        intersections.put(intersection.getId(), intersection);

        return true;
    }

    /**
     * Adds a road segment to the map.
     *
     * @param startIntersectionId the ID of the starting intersection
     * @param roadSegment the road segment to add
     * @return true if the road segment was added, false otherwise
     */
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

    /**
     * Retrieves a road segment between two intersections.
     *
     * @param startId the ID of the starting intersection
     * @param endId the ID of the ending intersection
     * @return the road segment if it exists, or null otherwise
     */
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

    /**
     * Retrieves all road segments that match a given name (partial or full).
     *
     * @param name the name or partial name of the road segments to search for
     * @return an ArrayList of matching road segments
     */
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

    /**
     * Retrieves the intersections in the map.
     *
     * @return a TreeMap of intersections
     */
    public TreeMap<Long, Intersection> getIntersections() {
        return intersections;
    }

    /**
     * Retrieves the adjacency list of road segments.
     *
     * @return a HashMap representing the adjacency list
     */
    public HashMap<Long, RoadSegment[]> getAdjencyList() {
        return adjencyList;
    }

    /**
     * Returns a string representation of the map.
     *
     * @return a string describing the intersections and adjacency list
     */
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
