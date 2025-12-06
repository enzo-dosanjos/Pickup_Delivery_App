package domain.service;

import domain.model.*;
import domain.model.dijkstra.DijkstraTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

@Service
public class PlanningService {
    private final RequestService requestService;
    private final TourService tourService;
    private MapService mapService;

    @Autowired
    public PlanningService(RequestService requestService, TourService tourService, MapService mapService) {
        this.requestService = requestService;
        this.tourService = tourService;

        this.mapService = mapService;
    }

    public void recomputeTourForCourier(long courierId) {
        if (!requestService.getPickupDelivery().getRequestsPerCourier().containsKey(courierId)) {
            throw new IllegalArgumentException("Courier ID " + courierId + " not found in requests.");
        }
        
        // Create a local copy to avoid concurrency issues
        PickupDelivery pickupDelivery = new PickupDelivery(requestService.getPickupDelivery());
        TreeMap<Long, Request> requests = pickupDelivery.getRequests();
        Long[] requestIdsForCourier = pickupDelivery.getRequestsPerCourier().get(courierId);

        // 1. Build list of stops (warehouse + pickups + deliveries)
        int nbStops = 2 * requestIdsForCourier.length + 1;
        long[] stops = new long[nbStops];

        int idx = 0;
        stops[idx++] = pickupDelivery.getWarehouseAdressId();

        for (long reqId : requestIdsForCourier) {
            Request r = requests.get(reqId);
            stops[idx++] = r.getPickupIntersectionId();
            stops[idx++] = r.getDeliveryIntersectionId();
        }

        // 2. build graph
        GrapheComplet graph = new GrapheComplet(stops, nbStops);

        // 3. Distances with Dijkstra
        DijkstraTable dijkstraTable = new DijkstraTable();  // todo: store in DijkstraService
        DijkstraService dijkstraService = new DijkstraService(mapService.getMap(), graph);
        dijkstraService.computeShortestPath(dijkstraTable);

        // 4.Precedences
        HashMap<Integer, Set<Integer>> precs = new HashMap<>();

        int requestIndex = 0;
        for (long reqId : requestIdsForCourier) {
            int pickupIndex = 1 + requestIndex * 2;
            int deliveryIndex = pickupIndex + 1;

            precs.put(deliveryIndex, Set.of(pickupIndex));
            requestIndex++;
        }

        TSP1 tsp = new TSP1();
        tsp.setPrecedences(precs);

        // 5. Service times
        double[] serviceTimes = new double[dijkstraService.getGraph().getNbSommets()];
        Arrays.fill(serviceTimes, 0); // warehouse = 0

        requestIndex = 0;
        for (long reqId : requestIdsForCourier) {
            Request r = requests.get(reqId);

            int pickupIndex = 1 + requestIndex * 2;
            int deliveryIndex = pickupIndex + 1;

            serviceTimes[pickupIndex]   = r.getPickupDuration().getSeconds();   // Pickup duration
            serviceTimes[deliveryIndex] = r.getDeliveryDuration().getSeconds(); // Delivery duration

            requestIndex++;
        }

        tsp.setServiceTimes(serviceTimes);

        // 6. execute TSP (SOP)
        tsp.chercheSolution(30000, dijkstraService.getGraph());

        if (tsp.getMeilleureCoutSolution() == Integer.MAX_VALUE) {
            throw new RuntimeException("TSP algorithm did not find a solution for courier " + courierId);
        }

        // 7. result
        double[] serviceTimesUsed = tsp.getServiceTimes();

        double currentTime = 0.0;  // time

        int numVertices = dijkstraService.getGraph().getNbSommets();
        for (int i = 0; i < numVertices; i++) {
            int node = tsp.getSolution(i);

            if (i > 0) {
                int prev = tsp.getSolution(i - 1);
                currentTime += dijkstraService.getGraph().getCout(prev, node);
            }

            // Arrival
            double arrival = currentTime;

            // Service time
            double service = serviceTimesUsed[node];
            double departure = arrival + service;

            currentTime = departure;
        }

        // 8. Convert graph to tour
        Integer[] sol = new Integer[graph.getNbSommets()];
        for (int i = 0; i < sol.length; i++)
            sol[i] = tsp.getSolution(i);

        Long[] vertices = Arrays.stream(graph.getSommets()).boxed().toArray(Long[]::new);

        LocalDateTime start = LocalDateTime.now();

        Tour tour = tourService.convertGraphToTour(
                pickupDelivery, start, courierId, sol, vertices, graph.getCout()
        );

        // 9. add roads to tour
        tour = tourService.addRoadsToTour(tour, dijkstraTable, mapService.getMap());

        tourService.setTourForCourier(courierId, tour);
    }

    public boolean courierExists(long courierId) {
        return tourService.getCouriers().stream().anyMatch(courier -> courier.getId() == courierId);
    }
}
