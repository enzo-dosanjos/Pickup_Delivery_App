package domain.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Represents a map containing intersections and road segments.
 */
public class Map {

    private final TreeMap<Long, Intersection> intersections; // A map of intersections, where the key is the intersection ID and the value is the Intersection object.


    private final HashMap<Long, RoadSegment[]> adjacencyList; // An adjacency list representing road segments starting from each intersection.

    /**
     * Constructs an empty map with no intersections or road segments.
     */
    public Map() {
        this.intersections = new TreeMap<>();
        this.adjacencyList = new HashMap<>();
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

        RoadSegment[] segments = adjacencyList.get(startIntersectionId);
        if (segments == null) {
            segments = new RoadSegment[] { roadSegment };
        } else {
            RoadSegment[] newSegments = new RoadSegment[segments.length + 1];
            System.arraycopy(segments, 0, newSegments, 0, segments.length);
            newSegments[segments.length] = roadSegment;
            segments = newSegments;
        }

        adjacencyList.put(startIntersectionId, segments);

        return true;
    }


    /**
     * Retrieves a road segment from start to end if it exists.
     * @param startId origin intersection id.
     * @param endId destination intersection id.
     * @return matching road segment or {@code null} if none exists.
     */
    public RoadSegment getRoadSegment(Long startId, Long endId) {
        RoadSegment[] segments = adjacencyList.get(startId);
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
     * Finds all segments whose name contains the given substring.
     * @param name partial or full road name to search for.
     * @return list of matching road segments (possibly empty).
     */
    public ArrayList<RoadSegment> getRoadSegmentByName(String name) {
        // Iterate through all road segments in the adjacency list to find segments with the given partial or full name
        ArrayList<RoadSegment> roadSegments = new ArrayList<>();
        if (name == null) {
            return roadSegments;
        }

        String needle = name.toLowerCase();
        for (RoadSegment[] segments : adjacencyList.values()) {
            for (RoadSegment segment : segments) {
                if (segment.getName().toLowerCase().contains(needle)) {
                    roadSegments.add(segment);
                }
            }
        }

        return roadSegments;
    }


    /**
     * Immutable view of intersections keyed by id.
     * @return all intersections on the map.
     */
    public TreeMap<Long, Intersection> getIntersections() {
        return intersections;
    }


    /**
     * Adjacency list keyed by origin intersection id.
     * @return mapping of origin to outgoing road segments.
     */
    public HashMap<Long, RoadSegment[]> getAdjacencyList() {
        return adjacencyList;
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Map:\n");
        sb.append("Intersections:\n");
        for (Intersection intersection : intersections.values()) {
            sb.append(intersection).append("\n");
        }
        sb.append("Adjacency List:\n");
        for (Long id : adjacencyList.keySet()) {
            sb.append("Intersection ").append(id).append(": ");
            for (RoadSegment segment : adjacencyList.get(id)) {
                sb.append(segment).append(", ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
