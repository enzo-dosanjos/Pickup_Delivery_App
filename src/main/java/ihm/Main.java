package ihm;

import domain.model.GrapheComplet;
import domain.model.Map;
import domain.model.PickupDelivery;
import domain.model.Request;
import persistence.XMLParsers;
import domain.service.DijkstraService;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        // Parse XML files to import the map and tour stops
        Map map = XMLParsers.parseMap("src/main/resources/grandPlan.xml");
        PickupDelivery pickupDelivery = XMLParsers.parseRequests("src/main/resources/requests.xml");

        // Initialize graph and 2D-array of predecessors to use Dijkstra
        int nbStops = 2 * pickupDelivery.getRequestsPerCourier().get(1L).size();
        int actual = 0;
        long[] stops = new long[nbStops];

        for (long requestId: pickupDelivery.getRequestsPerCourier().get(1L)) {
            Request request = pickupDelivery.getRequests().get(requestId);
            stops[actual++] = request.getPickupIntersectionId();
            stops[actual++] = request.getDeliveryIntersectionId();
        }

        GrapheComplet graph = new GrapheComplet(stops, nbStops);
        long[][] predecessors = new long[nbStops][nbStops];
        for (int i = 0; i < nbStops; i++) Arrays.fill(predecessors[i], -1);

        // Compute the shortest paths using the Dijkstra algorithm
        DijkstraService.computeShortestPath(map, graph, predecessors);

        // Run the SOP algorithm (asymmetrical TSP with precedence constraints) on the graph
    }
}