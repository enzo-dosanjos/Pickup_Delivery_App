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

    private final TreeMap<Long, HashMap<String, Set<String>>> precsByCourier;

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

        this.precsByCourier = new TreeMap<>();
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
        if (!requestService.getPickupDelivery().getRequestsPerCourier().containsKey(courierId)) {
            throw new IllegalArgumentException("Courier ID " + courierId + " not found in requests.");
        }

        // Create a local copy to avoid concurrency issues
        PickupDelivery pickupDelivery = new PickupDelivery(requestService.getPickupDelivery());
        TreeMap<Long, Request> requests = pickupDelivery.getRequests();
        ArrayList<Long> requestIdsForCourier = pickupDelivery.getRequestsPerCourier().get(courierId);


        if (!precsByCourier.containsKey(courierId)) {
            initPrecedences(courierId, requestIdsForCourier, pickupDelivery);
        }

        /*
        // 1. Build list of stops (warehouse + pickups + deliveries)
        int nbStops = 2 * requestIdsForCourier.size() + 1;
        long[] stops = new long[nbStops];

        int idx = 0;
        stops[idx++] = pickupDelivery.getWarehouseAddressId();

        for (long reqId : requestIdsForCourier) {
            Request r = requests.get(reqId);
            stops[idx++] = r.getPickupIntersectionId();
            stops[idx++] = r.getDeliveryIntersectionId();
        }
        */

        java.util.Map.Entry<long[], HashMap<Integer, Set<Integer>>> result = generateTspPrecedences(requestIdsForCourier, courierId, pickupDelivery);
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
        /*
        int requestIndex = 0;

        /*
        if (tsp.getPrecedences() == null){
            HashMap<Integer, Set<Integer>> precs = new HashMap<>();
            for (long reqId : requestIdsForCourier) {
                int pickupIndex = 1 + requestIndex * 2;
                int deliveryIndex = pickupIndex + 1;

                precs.put(deliveryIndex, Set.of(pickupIndex));
                requestIndex++;
            }
            tsp.setPrecedences(precs);
        }
        */
        tsp.setPrecedences(tspPrecedences);


        // 5. Service times
        double[] serviceTimes = new double[dijkstraService.getGraph().getNbSommets()];
        Arrays.fill(serviceTimes, 0); // warehouse = 0

        int requestIndex = 0;
        for (long reqId : requestIdsForCourier) {
            Request r = requests.get(reqId);

            int pickupIndex = 1 + requestIndex * 2;
            int deliveryIndex = pickupIndex + 1;

            serviceTimes[pickupIndex]   = r.getPickupDuration().toSeconds();   // Pickup duration
            serviceTimes[deliveryIndex] = r.getDeliveryDuration().toSeconds(); // Delivery duration

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

    public void addConstraint(PickupDelivery pickupDelivery, long courierId, long precRequestId, long precIntersectionId, long followingRequestId, long followingIntersectionId) {
        HashMap<String, Set<String>> precs = precsByCourier.get(courierId);
        Request precRequest, followRequest;
        char precType, followingType;

        precRequest = pickupDelivery.findRequestById(precRequestId);
        followRequest = pickupDelivery.findRequestById(followingRequestId);

        if(precRequest.getPickupIntersectionId() == precIntersectionId){
            precType = 'p';

        } else {
            precType = 'd';
        }

        if(followRequest.getPickupIntersectionId() == followingIntersectionId){
            followingType = 'p';

        } else {
            followingType = 'd';
        }
        precs.computeIfAbsent(parseParams(followingRequestId, followingIntersectionId, followingType), k -> new HashSet<>()).add(parseParams(precRequestId, precIntersectionId, precType));
        precsByCourier.put(courierId, precs);
        recomputeTourForCourier(courierId);
    }

    public void initPrecedences(long courierId, ArrayList<Long> requestsId, PickupDelivery pickupDelivery) {
        HashMap<String, Set<String>> precs = new HashMap<>();
        Request request;
        long delIntersectionId;
        long puIntersectionId;
        for (long requestId : requestsId) {
            request = pickupDelivery.findRequestById(requestId);
            puIntersectionId = request.getPickupIntersectionId();
            delIntersectionId = request.getDeliveryIntersectionId();
            precs.computeIfAbsent(parseParams(requestId, delIntersectionId, 'd'), k -> new HashSet<>()).add(parseParams(requestId, puIntersectionId, 'p'));
            //precs.put(parseParams(requestId, delIntersectionId, 'd'), Set.of(parseParams(requestId, puIntersectionId, 'p')));
        }
        precsByCourier.put(courierId, precs);
    }

    public java.util.Map.Entry<long[], HashMap<Integer, Set<Integer>>> generateTspPrecedences(
            ArrayList<Long> requestIdsForCourier,
            long courierId,
            PickupDelivery pickupDelivery) {

        // Récupère les précédences pour ce courier
        HashMap<String, Set<String>> precs = precsByCourier.get(courierId);

        Request request;

        // Utilisation d'une ArrayList
        List<String> vertices = new ArrayList<>(requestIdsForCourier.size() * 2 +1);
        vertices.add(parseParams(-1, pickupDelivery.getWarehouseAdressId(), 'w'));

        for (long requestId : requestIdsForCourier) {
            request = pickupDelivery.findRequestById(requestId);
            vertices.add(parseParams(requestId, request.getPickupIntersectionId(), 'p'));
            vertices.add(parseParams(requestId, request.getDeliveryIntersectionId(), 'd'));
        }

        HashMap<Integer, Set<Integer>> tspPrecs = new HashMap<>();

        // Parcours de la liste des vertices
        for (int i = 0; i < vertices.size(); i++) {
            Set<String> precVertices = precs.get(vertices.get(i));
            if (precVertices != null) {
                for (String precVertix : precVertices) {
                    int precVertixIndex = vertices.indexOf(precVertix);
                    tspPrecs.computeIfAbsent(i, k -> new HashSet<>())
                            .add(precVertixIndex);
                }
            }
        }
        long[] verticesIntersectionIds = extractIntersectionIds(vertices);

        // Retourne la paire : List<String> et HashMap<Integer, Set<Integer>>
        return java.util.Map.entry(verticesIntersectionIds, tspPrecs);
    }

    public String parseParams(long requestId, long intersectionID, char type) {
        return requestId + "-" + intersectionID + "-" + type;
    }

    public long[] extractIntersectionIds(List<String> vertices) {
        long[] intersectionIds = new long[vertices.size()];

        for (int i = 0; i < vertices.size(); i++) {
            String v = vertices.get(i);
            String[] parts = v.split("-");
            intersectionIds[i] = Long.parseLong(parts[1]);
        }

        return intersectionIds;
    }

    public static long parseRequest(String s) {
        String[] parts = s.split("-");
        return Long.parseLong(parts[0]);
    }

    public static long parseIntersection(String s) {
        String[] parts = s.split("-");
        return Long.parseLong(parts[1]);
    }

    public static char parseType(String s) {
        String[] parts = s.split("-");
        return parts[2].charAt(0);
    }
}
