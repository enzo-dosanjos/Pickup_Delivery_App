package domain.service;

import domain.model.*;
import domain.model.dijkstra.DijkstraTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Service class for planning and managing tours for couriers.
 * Provides functionality to recompute tours based on requests and map data.
 */
@Service
public class PlanningService {

    private final RequestService requestService; // Services for handling requests and tours.


    private final TourService tourService; // Service for handling tours.


    private MapService mapService; // Service for handling map data.

    /** Constructs a new PlanningService with the specified services.
     *
     * @param requestService the service for handling requests
     * @param tourService the service for handling tours
     * @param mapService the service for handling map data
     */
    @Autowired
    public PlanningService(RequestService requestService, TourService tourService, MapService mapService) {
        this.requestService = requestService;
        this.tourService = tourService;
        this.mapService = mapService;
    }

    /**
     * Recomputes the tour for a specific courier based on their requests.
     *
     * @param courierId the ID of the courier whose tour is to be recomputed
     * @throws IllegalArgumentException if the courier ID is not found in requests
     * @throws RuntimeException if the TSP algorithm does not find a solution
     */
    public void recomputeTourForCourier(long courierId) {
        if (!requestService.getPickupDeliveryPerCourier().containsKey(courierId)) {
            throw new IllegalArgumentException("Courier ID " + courierId + " not found in requests.");
        }

        // Create a local copy to avoid concurrency issues
        PickupDelivery pickupDelivery = new PickupDelivery(requestService.getPickupDeliveryForCourier(courierId));
        ArrayList<Request> requests = pickupDelivery.getRequests();


        if (!tourService.getPrecedencesByCourier().containsKey(courierId)) {
            tourService.initPrecedences(courierId, requests);
        }
        // 1. Generate TSP precedences and stops

        java.util.Map.Entry<long[], HashMap<Integer, Set<Integer>>> result = tourService.generateTspPrecedences(requests, pickupDelivery.getWarehouseAddressId(), courierId);
        long[] stops = result.getKey();
        HashMap<Integer, Set<Integer>> tspPrecedences = result.getValue();

        // 2. build graph
        GrapheComplet graph = new GrapheComplet(stops, stops.length);

        // 3. Distances with Dijkstra
        DijkstraTable dijkstraTable = new DijkstraTable();  // todo: store in DijkstraService
        DijkstraService dijkstraService = new DijkstraService(mapService.getMap(), graph);
        dijkstraService.computeShortestPath(dijkstraTable);

        // 4.Precedences
        TSP1 tsp = new TSP1();
        tsp.setPrecedences(tspPrecedences);


        // 5. Service times
        double[] serviceTimes = new double[dijkstraService.getGraph().getNbSommets()];
        Arrays.fill(serviceTimes, 0); // warehouse = 0

        int requestIndex = 0;
        for (Request req : requests) {
            int pickupIndex = 1 + requestIndex * 2;
            int deliveryIndex = pickupIndex + 1;

            serviceTimes[pickupIndex]   = req.getPickupDuration().toSeconds();   // Pickup duration
            serviceTimes[deliveryIndex] = req.getDeliveryDuration().toSeconds(); // Delivery duration

            requestIndex++;
        }

        tsp.setServiceTimes(serviceTimes);

        // 6. execute TSP (SOP)
        tsp.chercheSolution(30000, dijkstraService.getGraph());

        if (tsp.getCoutMeilleureSolution() == Integer.MAX_VALUE) {
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

        // 10. Set courier's availability status to BUSY or AVAILABLE depending on tour duration
        ArrayList<Courier> couriers = tourService.getCouriers();
        int i;
        for(i = 0; i < couriers.size(); i++) {
            if (couriers.get(i).getId() == courierId) {
                break;
            }
        }

        if (i >= couriers.size()) {
            return;
        } else if (couriers.get(i).getShiftDuration().minus(tour.getTotalDuration()).toMinutes() < 30) {
            tourService.getCouriers().get(i).setAvailabilityStatus(AvailabilityStatus.BUSY);
        } else {
            tourService.getCouriers().get(i).setAvailabilityStatus(AvailabilityStatus.AVAILABLE);
        };
    }

    /**
     * Checks if a courier with the specified ID exists.
     *
     * @param courierId the ID of the courier to check
     * @return true if the courier exists, false otherwise
     */
    public boolean courierExists(long courierId) {
        return tourService.getCouriers().stream().anyMatch(courier -> courier.getId() == courierId);
    }

    /**
     * Updates the precedence constraints for a courier by adding a new request.
     *
     * @param courierId The ID of the courier.
     * @param newRequest The new request to be added.
     */
    public void updatePrecedences(long courierId, Request newRequest) {
        long puIntersectionId, delIntersectionId;

        if (!tourService.getPrecedencesByCourier().containsKey(courierId)) {
            tourService.initPrecedences(courierId, requestService.getPickupDeliveryForCourier(courierId).getRequests());
        }

        HashMap<String, Set<String>> precs = tourService.getPrecedencesByCourier().get(courierId);

        puIntersectionId = newRequest.getPickupIntersectionId();
        delIntersectionId = newRequest.getDeliveryIntersectionId();
        precs.computeIfAbsent(tourService.parseParams(newRequest.getId(), delIntersectionId, 'd'), k -> new HashSet<>()).add(tourService.parseParams(newRequest.getId(), puIntersectionId, 'p'));
    }

    /**
     * Deletes the precedence constraints for a specific request of a courier.
     *
     * @param courierId The ID of the courier.
     * @param requestId The ID of the request whose precedences need to be removed.
     */
    public void deletePrecedences(long courierId, long requestId) {
        HashMap<String, Set<String>> precs = tourService.getPrecedencesByCourier().get(courierId);

        // Remove keys starting with the request ID
        precs.keySet().removeIf(key -> key.startsWith(String.valueOf(requestId)));

        // Remove values starting with the request ID
        precs.values().forEach(set -> set.removeIf(value -> value.startsWith(String.valueOf(requestId))));

        // Clean up empty entries
        precs.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }
}
