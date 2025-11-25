package domain.service;

import domain.model.Graphe;
import domain.model.GrapheComplet;
import domain.model.Intersection;
import domain.model.Map;
import domain.model.RoadSegment;
import domain.model.PickupDelivery;
import domain.model.Request;



public class DijkstraService {
    public double compareTo(Map map, int startId, int endId) {
    // Not yet implemented
        return 0;
    }

    public pair<GrapheComplet, long[][]> computeShortestPath(Map map, long[] requests) {
    // Calculate the shortest paths between all intersections that need to be visited using Dijkstra's algorithm
        GrapheComplet g = grapheComplet(requests);
        predecesseurs = new long[map.getIntersections.size()][map.getIntersections.size()];


        return null;
    }

    public void Dijkstra(Map map,GrapheComplet grapheComplet, int start, long[][] predecesseurs) {
        // Calculate the shortest paths using Dijkstra's algorithm



    }
}