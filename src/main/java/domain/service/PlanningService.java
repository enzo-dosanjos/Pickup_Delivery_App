package domain.service;

import domain.model.*;
import domain.model.dijkstra.DijkstraTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

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
        ArrayList<Long> requestIdsForCourier = pickupDelivery.getRequestsPerCourier().get(courierId);
        Courier courier = courierInCharge(courierId);
        Duration shiftDuration = courier.getShiftDuration();

        //----------------------------------------------------------------

        System.out.println("========================================");
        System.out.println("Computing tour for Courier #" + courierId);
        System.out.println("Requests: " + requestIdsForCourier.size());
        System.out.println("Shift Duration: " + formatDuration(shiftDuration.toSeconds()));
        System.out.println("========================================");

        //----------------------------------------------------------------

        // 1. Build list of stops (warehouse + pickups + deliveries)
        int nbStops = 2 * requestIdsForCourier.size() + 1;
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

            serviceTimes[pickupIndex]   = r.getPickupDuration().toSeconds();   // Pickup duration
            serviceTimes[deliveryIndex] = r.getDeliveryDuration().toSeconds(); // Delivery duration

            requestIndex++;
        }

        tsp.setServiceTimes(serviceTimes);

        // Set shift duration constraint
        tsp.setMaxDuration(shiftDuration.toSeconds());

        // 6. execute TSP (SOP)
        System.out.println("Running TSP algorithm...");
        long tspStartTime = System.currentTimeMillis();


        
        int timeLimit;
        long NO_IMPROVEMENT_TIMEOUT;
        if (nbStops <= 10) {
            timeLimit = 7500; // 10s not many stops
            NO_IMPROVEMENT_TIMEOUT = 2000;
        } else if (nbStops <= 15) {
            timeLimit = 20000; // 30s
            NO_IMPROVEMENT_TIMEOUT = 3000;
        } else {
            timeLimit = 45000; // 45s many stops
            NO_IMPROVEMENT_TIMEOUT = 7000;
        }
        tsp.setNO_IMPROVEMENT_TIMEOUT(NO_IMPROVEMENT_TIMEOUT);

        tsp.chercheSolution(timeLimit, dijkstraService.getGraph());

        long tspEndTime = System.currentTimeMillis();
        long tspExecutionTime = tspEndTime - tspStartTime;

        System.out.println("TSP execution time: " + tspExecutionTime + " ms");

        if (tsp.getCoutMeilleureSolution() == Integer.MAX_VALUE) {
            System.err.println("No solution found!");
            throw new RuntimeException("TSP algorithm did not find a solution for courier " + courierId);}

        double tourDuration = tsp.getCoutMeilleureSolution();
        
        System.out.println("----------------------------------------");
        System.out.println("Solution found!");
        System.out.println(
            "Tour duration: " + formatDuration(tourDuration)
        );
        System.out.println(
            "Shift duration: " + formatDuration(shiftDuration.toSeconds())
        );
        
        // Check if solution exceeds shift duration
        if (tourDuration > shiftDuration.toSeconds()) {
            double exceedSeconds = tourDuration - shiftDuration.toSeconds();
            System.err.println("⚠️  WARNING: Tour exceeds shift duration!");
            System.err.println(
                "Exceeds by: " + formatDuration(exceedSeconds)
            );
        } else {
            double remainingSeconds = shiftDuration.toSeconds() - tourDuration;
            System.out.println(
                "✓ Within shift duration (remaining: " + 
                formatDuration(remainingSeconds) + ")"
            );
        }
        
        // Print TSP solution
        System.out.println("----------------------------------------");
        System.out.println("Tour sequence:");
        printTSPSolution(
            tsp, 
            stops, 
            dijkstraService.getGraph(), 
            serviceTimes
        );
        System.out.println("========================================");




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

    public Courier courierInCharge(long courierId) {
        return tourService.getCouriers().stream()
            .filter(courier -> courier.getId() == courierId)
            .findFirst()
            .orElse(null);
    }


    private void printTSPSolution(
        TSP1 tsp, 
        long[] stops, 
        Graphe graph, 
        double[] serviceTimes
    ) {
        double cumulativeTime = 0.0;
        
        for (int i = 0; i < graph.getNbSommets(); i++) {
            int nodeIndex = tsp.getSolution(i);
            long stopAddress = stops[nodeIndex];
            
            String stopType;
            if (i == 0) {
                stopType = "Depot";
            } else if (nodeIndex % 2 == 1) {
                stopType = "Pickup";
            } else {
                stopType = "Delivery";
            }
            
            double travelTime = 0.0;
            if (i > 0) {
                int prevIndex = tsp.getSolution(i - 1);
                travelTime = graph.getCout(prevIndex, nodeIndex);
                cumulativeTime += travelTime;
            }
            
            double serviceTime = serviceTimes[nodeIndex];
            
            System.out.printf(
                "%2d. [%s] Stop #%d (Address: %d) | " +
                "Travel: %s | Service: %s | Cumulative: %s%n",
                i,
                stopType,
                nodeIndex,
                stopAddress,
                formatDuration(travelTime),
                formatDuration(serviceTime),
                formatDuration(cumulativeTime)
            );
            
            cumulativeTime += serviceTime;
        }
        
        // Return to depot
        int lastIndex = tsp.getSolution(graph.getNbSommets() - 1);
        double returnCost = graph.getCout(lastIndex, 0);
        cumulativeTime += returnCost;
        
        System.out.printf(
            "    Return to Depot | Travel: %s | Total: %s%n",
            formatDuration(returnCost),
            formatDuration(cumulativeTime)
        );
    }

    private String formatDuration(double seconds) {
        long hours = (long) (seconds / 3600);
        long minutes = (long) ((seconds % 3600) / 60);
        long secs = (long) (seconds % 60);
        
        if (hours > 0) {
            return String.format("%dh %02dm %02ds", hours, minutes, secs);
        } else if (minutes > 0) {
            return String.format("%dm %02ds", minutes, secs);
        } else {
            return String.format("%ds", secs);
        }
    }
}
