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

    private final Map map; // The map containing intersections and road segments.

    private DijkstraTable dijkstraTable; // The Dijkstra table to store shortest path information.

    /**
     * Computes the shortest paths between all intersections that need to be visited.
     * If some have already been computed, they are reused.
     *
     * @param stops an array of intersection IDs representing the stops to be visited
     * @return a complete graph with the shortest path costs between the specified stops
     */
    public GrapheComplet computeShortestPath(long[] stops) {
        // Initialize the graph with the values in the DijkstraTable
        GrapheComplet g = new GrapheComplet(stops, stops.length);
        for (int i = 0; i < stops.length; i++) {
            if (dijkstraTable.get(stops[i], stops[i]).isVisited()) {
                for (int j = 0; j < stops.length; j++) {
                    CellInfo cell = dijkstraTable.get(stops[i], stops[j]);
                    if (cell != null) {
                        g.setCout(i, j, cell.getDuration());
                    }
                }
            }
        }


        // Compute the shortest paths for each warehouse/pickup/delivery stop of this tour
        for (int i = 0; i < g.getNbSommets(); i++) {
            if (g.getCout(i,i) != Double.MAX_VALUE) continue; // already computed
            g = dijkstra(i, g);
        }

        return g;
    }

    /**
     * Executes Dijkstra's algorithm to calculate the shortest paths from a starting vertex.
     *
     * @param start the index of the starting vertex
     * @param g     the complete graph to update with the shortest paths costs for the specified start vertex
     * @return the updated complete graph with the shortest paths costs from the start vertex
     */
    private GrapheComplet dijkstra(int start, GrapheComplet g) {
        long[] sommets = g.getSommets();
        HashMap<Long, RoadSegment[]> adjacencyList = map.getAdjacencyList();
        PriorityQueue<Node> pq = new PriorityQueue<>();
        pq.add(new Node(sommets[start], 0));

        while (!pq.isEmpty()) {
            Node currentNode = pq.poll();
            long currentVertex = currentNode.getVertex();
            CellInfo currentCell = dijkstraTable.get(sommets[start], currentVertex);

            if (currentCell == null || currentCell.isVisited()) continue;

            currentCell.setVisited(true);
            RoadSegment[] neighbors = adjacencyList.get(currentVertex);

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
            for (int j = 0; j < g.getNbSommets(); j++) {
                for (int i = 0; i< g.getNbSommets(); i++) {
                    if (sommets[j] == currentVertex && sommets[i] == sommets[start]) {
                        g.setCout(i, j, currentNode.getDuration());
                    }
                }
            }
        }
        return g;
    }

    /**
     * Constructs a new DijkstraService with the specified map and creates a DijkstraTable with default values.
     *
     * @param map the map containing intersections and road segments
     */
    public DijkstraService(Map map) {
        this.map = map;
        this.dijkstraTable = new DijkstraTable();
        // Initialize the Dijkstra table with default values
        for (Long row : this.map.getIntersections().keySet()) {
            for (Long col : this.map.getIntersections().keySet()) {
                double duration = row.equals(col) ? 0L : Double.MAX_VALUE;
                long predecessor = -1;
                boolean visited = false;
                dijkstraTable.put(row, col, duration, predecessor, visited);
            }
        }
    }


    public DijkstraTable getDijkstraTable() {
        return dijkstraTable;
    }
}