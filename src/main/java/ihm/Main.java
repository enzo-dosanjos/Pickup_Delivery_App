package ihm;

import domain.model.GrapheComplet;
import domain.model.Map;
import domain.model.PickupDelivery;
import domain.model.Request;
import domain.service.TSP1;
import persistence.XMLParsers;
import domain.service.DijkstraService;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        // Parse XML files to import the map and tour stops
        Map map = XMLParsers.parseMap("src/main/resources/grandPlan.xml");
        PickupDelivery pickupDelivery = XMLParsers.parseRequests("src/main/resources/requests.xml");

        // Initialize graph and 2D-array of predecessors to use Dijkstra
        int nbStops = 2 * pickupDelivery.getRequestsPerCourier().get(1L).size() + 1;
        int actual = 0;
        long[] stops = new long[nbStops];

        stops[actual++] = pickupDelivery.getWarehouseAddress();
        for (long requestId: pickupDelivery.getRequestsPerCourier().get(1L)) {
            Request request = pickupDelivery.getRequests().get(requestId);
            stops[actual++] = request.getPickupIntersectionId();
            stops[actual++] = request.getDeliveryIntersectionId();
        }

        GrapheComplet graph = new GrapheComplet(stops, nbStops);

        // Compute the shortest paths using the Dijkstra algorithm
        DijkstraService dijkstraService = new DijkstraService(map, graph);
        dijkstraService.computeShortestPath();

        // Run the SOP algorithm (asymmetrical TSP with precedence constraints) on the graph
        TSP1 TSPService = new TSP1();
        TSPService.chercheSolution(30000, graph);

        System.out.println("Tour duration: " + TSPService.getCoutSolution());
        for (int i = 0; i < nbStops; i++) {
            System.out.println(map.getIntersections().get(graph.getSommets()[TSPService.getSolution(i)]));
        }
    }
}