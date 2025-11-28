package domain.service;

import domain.model.dijkstra.*;
import domain.model.GrapheComplet;
import domain.model.Map;
import domain.model.RoadSegment;

import java.util.HashMap;
import java.util.PriorityQueue;

public class DijkstraService {
    private Map map;
    private GrapheComplet g;

    public void computeShortestPath() {
    // Calculate the shortest paths between all intersections that need to be visited using Dijkstra's algorithm

        DijkstraTable dijkstraTable = new DijkstraTable();
        for (Long row : map.getIntersections().keySet()) {
            for (Long col : map.getIntersections().keySet()) {
                double distance;
                if (row.equals(col)) {
                    distance = 0L;
                } else {
                    distance = Double.MAX_VALUE;
                }
                long predecessor = -1;
                boolean visited = false;
                dijkstraTable.put(row, col, distance, predecessor, visited);
            }
        }


        for (int i = 0; i < g.getNbSommets(); i++) {
            Dijkstra(i, dijkstraTable);
        }
    }

    private void Dijkstra(int start, DijkstraTable dijkstraTable) {
        // Calculate the shortest paths using Dijkstra's algorithm
        this.g.setCout(start, start, 0L);
        long[] sommets = this.g.getSommets();
        HashMap<Long, RoadSegment[]> adjencyList = map.getAdjencyList();
        PriorityQueue<Node> pq = new PriorityQueue<>();
        pq.add(new Node(sommets[start], 0));
        while (!pq.isEmpty()) {
            Node currentNode = pq.poll();
            long currentVertex = currentNode.getVertex();
            CellInfo currentCell = dijkstraTable.get(sommets[start], currentVertex);
            if (currentCell == null) { System.out.println(sommets[start]); System.out.println(currentVertex); System.out.println(g); }
            if (currentCell.isVisited()) continue;
            currentCell.setVisited(true);
            RoadSegment[] neighbors = adjencyList.get(currentVertex);
            if (neighbors != null) {
                for (RoadSegment segment : neighbors) {
                    long neighborVertex = segment.getEndId();
                    CellInfo neighborCell = dijkstraTable.get(sommets[start], neighborVertex);
                    if (neighborCell != null && !neighborCell.isVisited()) {
                        double newDist = currentCell.getDistance() + segment.getLength();
                        if (newDist < neighborCell.getDistance()) {
                            neighborCell.setDistance(newDist);
                            neighborCell.setPredecessor(currentVertex);
                            pq.add(new Node(neighborVertex, newDist));
                            dijkstraTable.put(sommets[start], neighborVertex, neighborCell);
                        }
                    }
                }
            }
            // Test pour savoir si le sommet est dans grapheComplet
            for (int j = 0; j < this.g.getNbSommets(); j++) {
                if (sommets[j] == currentVertex) {
                    this.g.setCout(start, j, currentNode.getDistance());
                }
            }
        }
    }

    public DijkstraService(Map map, GrapheComplet g) {
        this.map = map;
        this.g = g;
    }
}