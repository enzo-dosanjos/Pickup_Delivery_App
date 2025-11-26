package domain.service;

import domain.model.Graphe;
import domain.model.GrapheComplet;
import domain.model.Intersection;
import domain.model.Map;
import domain.model.RoadSegment;
import domain.model.PickupDelivery;
import domain.model.Request;

import java.util.Arrays;

public class DijkstraService {
    private static double compareTo(Map map, int startId, int endId) {
    // Not yet implemented
        return 0;
    }

    public static void computeShortestPath(Map map, GrapheComplet g, long[][] predecesseurs) {
    // Calculate the shortest paths between all intersections that need to be visited using Dijkstra's algorithm
        long[][] etatNoeuds = new long[g.getNbSommets()][g.getNbSommets()];
        for (int i = 0; i < g.getNbSommets(); i++) { Arrays.fill(etatNoeuds[i], -1); }

        for (long i: g.getSommets()) {
            Dijkstra(map, g, i, predecesseurs, etatNoeuds);
        }
    }

    private static void Dijkstra(Map map, GrapheComplet grapheComplet, long start, long[][] predecesseurs, long[][] etatNoeuds) {
        // Calculate the shortest paths using Dijkstra's algorithm

    }
}