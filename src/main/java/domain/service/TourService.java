package domain.service;

import domain.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import persistence.XMLWriters;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.Vector;

@Service
public class TourService {
    private final MapService mapService;
    private final RequestService requestService;
    private final XMLWriters xmlWriters;

    // Fields from the class diagram
    private long[] couriers; // list of courier ids
    private int numCouriers;
    private GrapheComplet graph; // Placeholder, needs actual initialization when TSP is integrated
    private TreeMap<Long, Tour> tours; // key: courier id
    private TreeMap<Long, Vector<Entry<Long, Long>>> requestOrder; // key: courier id, value: pair of ids of stop that should be before the other

    @Autowired
    public TourService(MapService mapService, RequestService requestService, XMLWriters xmlWriters) {
        this.mapService = mapService;
        this.requestService = requestService;
        this.xmlWriters = xmlWriters;
        this.tours = new TreeMap<>();
        this.requestOrder = new TreeMap<>();
        // Initialize other fields as needed, perhaps with default values or through methods
        this.numCouriers = 0; // Default to 0 couriers
        this.couriers = new long[0]; // Empty array initially
    }

    /**
     * Computes tours for all couriers based on currently loaded requests and map.
     */
    public void computeAllTours() {
        // Placeholder for tour computation logic
        System.out.println("Computing all tours...");
        // This method would typically iterate through couriers, get their requests,
        // compute shortest paths (using DijkstraService), and then solve the TSP
        // for each courier's route.
        // The results would be stored in the 'tours' TreeMap.

        // Example placeholder: create a dummy tour for a courier
        // Need to ensure requestService.getPickupDelivery() is not null
        // and that couriers array is populated before attempting to assign.
        if (numCouriers > 0 && requestService.getPickupDelivery() != null) {
            for (long courierId : couriers) {
                List<Request> courierRequests = requestService.getRequestsForCourier(courierId);
                if (!courierRequests.isEmpty()) {
                    List<TourStop> stops = new Vector<>();
                    for (Request req : courierRequests) {
                        // Dummy stops for pickup and delivery
                        stops.add(new TourStop(StopType.PICKUP, req.getId(), req.getPickupIntersectionId(), 0L, 0L));
                        stops.add(new TourStop(StopType.DELIVERY, req.getId(), req.getDeliveryIntersectionId(), 0L, 0L));
                    }
                    Tour dummyTour = new Tour(courierId, stops, 0, 0); // distance and duration are 0 for dummy
                    tours.put(courierId, dummyTour);
                }
            }
        } else {
             System.out.println("No couriers defined or no requests loaded to compute tours for.");
        }
    }

    /**
     * Computes a tour for a specific courier.
     * @param id The ID of the courier.
     * @return The computed Tour object.
     */
    public Tour computeTourForCourier(long id) {
        System.out.println("Computing tour for courier: " + id);
        // Implement specific tour computation for one courier
        return tours.get(id); // Return existing or compute and return
    }

    /**
     * Saves all computed tours to an XML file.
     * @param filepath The path to the file where tours will be saved.
     */
    public void saveTours(String filepath) {
        System.out.println("Saving tours to: " + filepath);
        xmlWriters.writeTours(tours.values(), filepath); // Assuming XMLWriters has a writeTours method
    }

    /**
     * Computes the distance between two intersection IDs.
     * @param startId The starting intersection ID.
     * @param endId The ending intersection ID.
     * @return The computed distance.
     */
    public double computeDistance(long startId, long endId) {
        System.out.println("Computing distance between " + startId + " and " + endId);
        // This would involve using MapService and potentially DijkstraService
        return 0.0; // Placeholder
    }

    /**
     * Computes the duration between two intersection IDs.
     * @param startId The starting intersection ID.
     * @param endId The ending Intersection ID.
     * @return The computed duration.
     */
    public Duration computeDuration(long startId, long endId) {
        System.out.println("Computing duration between " + startId + " and " + endId);
        // This would involve using MapService and potentially DijkstraService
        return Duration.ZERO; // Placeholder
    }

    /**
     * Assigns requests to couriers.
     */
    public void assignRequestsToCouriers() {
        System.out.println("Assigning requests to couriers...");
        // This method would distribute the loaded requests among the available couriers.
        // It's a complex planning problem.
    }

    /**
     * Updates the number of couriers available for tour calculation.
     * Also updates the 'couriers' array with dummy IDs or real ones if available.
     * @param numCouriers The new number of couriers.
     */
    public void updateNumCouriers(int numCouriers) {
        this.numCouriers = numCouriers;
        this.couriers = new long[numCouriers];
        for (int i = 0; i < numCouriers; i++) {
            this.couriers[i] = i + 1; // Assign simple sequential IDs for now
        }
        System.out.println("Updated number of couriers to: " + numCouriers);
    }

    /**
     * Updates the order of requests for a specific courier, indicating one request should be before another.
     * @param requestBeforeId The ID of the request that should occur earlier.
     * @param requestAfterId The ID of the request that should occur later.
     * @param courierId The ID of the courier.
     */
    public void updateRequestOrder(long requestBeforeId, long requestAfterId, long courierId) {
        System.out.println("Updating request order for courier " + courierId + ": " + requestBeforeId + " before " + requestAfterId);
        requestOrder.computeIfAbsent(courierId, k -> new Vector<>()).add(new Entry<Long, Long>() {
            @Override
            public Long getKey() {
                return requestBeforeId;
            }

            @Override
            public Long getValue() {
                return requestAfterId;
            }

            @Override
            public Long setValue(Long value) {
                throw new UnsupportedOperationException("Not supported.");
            }
        });
    }

    /**
     * Returns the TreeMap of tours, keyed by courier ID.
     * @return A TreeMap containing all computed tours.
     */
    public TreeMap<Long, Tour> getTours() {
        return tours;
    }

    // Placeholder for other dependencies as per class diagram
    // For example, if TSP/TemplateTSP are used internally, they would be instantiated here or passed in.
}
