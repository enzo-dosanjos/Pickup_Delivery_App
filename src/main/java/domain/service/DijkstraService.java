package domain.service;

import domain.model.dijkstra.*;
import domain.model.GrapheComplet;
import domain.model.Map;
import domain.model.RoadSegment;
import domain.utils.DurationUtil;

import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * Service class for calculating the shortest paths using Dijkstra's algorithm.
 */
public class DijkstraService {
    /** The map containing intersections and road segments. */
    private final Map map;

    /** The complete graph representation used for storing shortest path costs. */
    private GrapheComplet g;

    /**
     * Computes the shortest paths between all intersections that need to be visited.
     *
     * @param dijkstraTable the table used to store the shortest path information
     */
    public void computeShortestPath(DijkstraTable dijkstraTable) {
        // Initialize the Dijkstra table with default values
        for (Long row : map.getIntersections().keySet()) {
            for (Long col : map.getIntersections().keySet()) {
                double duration = row.equals(col) ? 0L : Double.MAX_VALUE;
                long predecessor = -1;
                boolean visited = false;
                dijkstraTable.put(row, col, duration, predecessor, visited);
            }
        }

        // Compute shortest paths for each vertex in the graph
        for (int i = 0; i < g.getNbSommets(); i++) {
            Dijkstra(i, dijkstraTable);
        }
    }

    /**
     * Executes Dijkstra's algorithm to calculate the shortest paths from a starting vertex.
     *
     * @param start the index of the starting vertex
     * @param dijkstraTable the table used to store the shortest path information
     */
    private void Dijkstra(int start, DijkstraTable dijkstraTable) {
        this.g.setCout(start, start, 0L);
        long[] sommets = this.g.getSommets();
        HashMap<Long, RoadSegment[]> adjencyList = map.getAdjencyList();
        PriorityQueue<Node> pq = new PriorityQueue<>();
        pq.add(new Node(sommets[start], 0));

        while (!pq.isEmpty()) {
            Node currentNode = pq.poll();
            long currentVertex = currentNode.getVertex();
            CellInfo currentCell = dijkstraTable.get(sommets[start], currentVertex);

            if (currentCell == null || currentCell.isVisited()) continue;

            currentCell.setVisited(true);
            RoadSegment[] neighbors = adjencyList.get(currentVertex);

            if (neighbors != null) {
                for (RoadSegment segment : neighbors) {
                    long neighborVertex = segment.getEndId();
                    CellInfo neighborCell = dijkstraTable.get(sommets[start], neighborVertex);

                    if (neighborCell != null && !neighborCell.isVisited()) {
                        double newDur = currentCell.getDuration() + DurationUtil.computeDuration(segment);

                        if (newDur < neighborCell.getDuration()) {
                            neighborCell.setDuration(newDur);
                            neighborCell.setPredecessor(currentVertex);
                            pq.add(new Node(neighborVertex, newDur));
                            dijkstraTable.put(sommets[start], neighborVertex, neighborCell);
                        }
                    }
                }
            }

            // Update the cost in the complete graph if the vertex is part of it
            for (int j = 0; j < this.g.getNbSommets(); j++) {
                if (sommets[j] == currentVertex) {
                    this.g.setCout(start, j, currentNode.getDuration());
                }
            }
        }
    }

    /**
     * Constructs a new DijkstraService with the specified map and complete graph.
     *
     * @param map the map containing intersections and road segments
     * @param g the complete graph representation
     */
    public DijkstraService(Map map, GrapheComplet g) {
        this.map = map;
        this.g = g;
    }

    /**
     * Retrieves the complete graph representation.
     *
     * @return the complete graph
     */
    public GrapheComplet getGraph() {
        return g;
    }
}