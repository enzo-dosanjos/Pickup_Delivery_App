package domain.service;

import domain.model.Courier;
import domain.model.Map;
import domain.model.Tour;

import java.util.*;
import java.util.Map.Entry;

import domain.model.*;
import domain.model.dijkstra.CellInfo;
import domain.model.dijkstra.DijkstraTable;
import org.springframework.stereotype.Service;
import persistence.XMLParsers;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Service class for managing tours and couriers.
 * Provides functionality to manage couriers, create tours, and handle requests.
 */
@Service
public class TourService {

    private int numCouriers; // Number of couriers managed by the service.


    private ArrayList<Courier> couriers; // List of couriers managed by the service.


    private TreeMap<Long, Tour> tours; //  Map of tours associated with each courier ID.


    private TreeMap<Long, HashMap<String, Set<String>>> precedencesByCourier; // Map of precedence constraints for each courier.


    /** Initializes a new instance of the TourService class. */
    public TourService() {
        this.numCouriers = 0;
        this.couriers = new ArrayList<>();
        this.tours = new TreeMap<>();
        this.precedencesByCourier = new TreeMap<>();
    }


    public void setTourForCourier(long courierId, Tour tour) {
        tours.put(courierId, tour);
    }

    /**
     * Adds a courier to the service.
     *
     * @param courier the courier to be added
     * @return true if the courier was added successfully, false otherwise
     */
    public boolean addCourier(Courier courier) {
        boolean added = couriers.add(courier);
        if (added) { numCouriers++; }
        return added;
    }

    /**
     * Removes a courier from the service by ID.
     *
     * @param courierId the ID of the courier to be removed
     * @return true if the courier was removed successfully, false otherwise
     */
    public boolean removeCourier(long courierId) {
        for (int i = 0; i < couriers.size(); i++) {
            if (couriers.get(i).getId() == courierId) {
                couriers.remove(i);
                numCouriers--;
                return true;
            }
        }

        return false;
    }

    /**
     * Loads couriers from an XML file and adds them to the service.
     *
     * @param filepath the path to the XML file containing courier data
     */
    public void loadCouriers(String filepath) {
        ArrayList<Courier> couriersToAdd = XMLParsers.parseCouriers(filepath);

        for (Courier courier : couriersToAdd) {
            boolean added = couriers.add(courier);
            if (added) { numCouriers++; }
        }
    }


    /**
     * Generates a tour for a specific courier by using the solution calculated by TemplateTSP
     * for the graph made by DijkstraService.
     *
     * @param pickupDelivery the pickup and delivery data
     * @param startTime the start time of the tour
     * @param courierId the ID of the courier
     * @param solution the solution array representing the order of stops
     * @param vertices the array of vertices in the graph
     * @param costs the cost matrix for the graph
     * @return the generated tour
     */
    public Tour convertGraphToTour(PickupDelivery pickupDelivery, LocalDateTime startTime, long courierId, Integer[] solution, Long[] vertices, double[][] costs) {
        long intersectionId;
        Integer previousTourStop = null;

        Request request;
        long requestId;

        StopType stopType;
        TourStop tourStop;
        LocalDateTime arrivalTime, departureTime;
        Duration duration;

        Tour tour = new Tour(courierId, startTime);
        double minutes = 0.0;
        boolean first = true;
        Duration commuteDuration = Duration.ZERO;

        for (Integer i : solution) {
            duration = Duration.ZERO;
            intersectionId = vertices[i];

            if (first) {
                stopType = StopType.WAREHOUSE;
                requestId = -1;
                arrivalTime = tour.getStartTime();
                first = false;
            } else {
                Entry<Request, StopType> result = pickupDelivery.findRequestByIntersectionId(intersectionId);

                if (result == null) {
                    // This should not happen if the solution is valid
                    throw new IllegalArgumentException("No request found for intersection ID: " + intersectionId);
                }

                request = result.getKey();
                stopType = result.getValue();
                arrivalTime = tour.getStartTime().plus(tour.getTotalDuration());

                if (stopType == StopType.PICKUP) {
                    duration = request.getPickupDuration();
                } else if (stopType == StopType.DELIVERY) {
                    duration = request.getDeliveryDuration();
                }
                minutes = costs[previousTourStop][i];
                long wholeMinutes = (long) minutes;
                long seconds = Math.round((minutes - wholeMinutes) * 60);
                commuteDuration = Duration.ofMinutes(wholeMinutes).plusSeconds(seconds);
                arrivalTime = arrivalTime.plus(commuteDuration);
                requestId = request.getId();
            }
            departureTime = arrivalTime.plus(duration);
            tourStop = new TourStop(stopType, requestId, intersectionId, arrivalTime, departureTime);
            tour.addStop(tourStop);
            tour.updateTotalDuration(duration.plus(commuteDuration));
            previousTourStop = i;
        }

        // Add commute time between last stop and warehouse
        minutes = costs[solution[solution.length - 1]][solution[0]];
        long wholeMinutes = (long) minutes;
        long seconds = Math.round((minutes - wholeMinutes) * 60);
        commuteDuration = Duration.ofMinutes(wholeMinutes).plusSeconds(seconds);
        tour.updateTotalDuration(commuteDuration);
        return tour;
    }

    /**
     * Adds road segments to a tour based on the Dijkstra table and map data.
     * For each pair of consecutive stops in the tour, it retrieves the intermediary intersections
     * from the Dijkstra table and adds the corresponding road segments to the tour.
     *
     * @param tour the tour to which road segments will be added
     * @param table the Dijkstra table containing shortest path information
     * @param map the map containing road segment data
     * @return the updated tour with road segments added
     */
    public Tour addRoadsToTour(Tour tour, DijkstraTable table, Map map) {
        List<TourStop> stops = tour.getStops();
        List<Long> reverseIntersectPath = new ArrayList<>();

        long targetIntersectionId;
        long sourceIntersectionId;
        long currentIntersectionId;

        CellInfo info;
        RoadSegment road;

        // Add all the visited intersections in order to a list
        for (int i = stops.size(); i > 0; i--) {
            if (i == stops.size()) {
                targetIntersectionId = stops.get(0).getIntersectionId();
            } else {
                targetIntersectionId = stops.get(i).getIntersectionId();
            }
            sourceIntersectionId = stops.get(i - 1).getIntersectionId();
            currentIntersectionId = targetIntersectionId;
            while (currentIntersectionId != sourceIntersectionId) {
                reverseIntersectPath.add(currentIntersectionId);
                info = table.get(sourceIntersectionId, currentIntersectionId);
                currentIntersectionId = info.getPredecessor();
            }
        }

        reverseIntersectPath.add(stops.get(0).getIntersectionId());
        Collections.reverse(reverseIntersectPath);

        for (int i = 0; i < reverseIntersectPath.size() - 1; i++) {
            road = map.getRoadSegment(reverseIntersectPath.get(i), reverseIntersectPath.get(i + 1));
            tour.addRoadSegment(road);
        }

        return tour;
    }

    /**
     * Updates the stop order for a courier's tour and adds precedence constraints.
     *
     * @param courierId The ID of the courier.
     * @param beforeStopIndex The current index of the stop that must come before the other in tour.
     * @param afterStopIndex The current index of the stop that must come after the other in tour.
     */
    public void updateStopOrder(long courierId, Integer beforeStopIndex, Integer afterStopIndex) {

        if(beforeStopIndex < 0 || afterStopIndex < 0 || beforeStopIndex >= tours.get(courierId).getStops().size() || afterStopIndex >= tours.get(courierId).getStops().size()) {
            throw new IllegalArgumentException(
                    "Stop indices are out of bounds."
            );
        }

        HashMap<String, Set<String>> precs = precedencesByCourier.get(courierId);
        TourStop beforeStop, followStop;
        Tour tour;
        char prevType, followingType;

        tour = tours.get(courierId);
        beforeStop = tour.getStops().get(beforeStopIndex);
        followStop = tour.getStops().get(afterStopIndex);

        // Checking if the stops are not the warehouse
        if (beforeStop.getRequestID() == -1 || followStop.getRequestID() == -1) {
            throw new IllegalArgumentException(
                    "Impossible to create precedence with warehouse."
            );
        }

        // Checking if the stops come from different requests
        if (beforeStop.getRequestID() == followStop.getRequestID()) {
            throw new IllegalArgumentException(
                    "Impossible to create precedence between 2 stops belonging to the same request (requestId = "
                            + beforeStop.getRequestID() + ")."
            );
        }

        // Find types
        prevType = (beforeStop.getType() == StopType.PICKUP) ? 'p' : 'd';
        followingType = (followStop.getType() == StopType.PICKUP) ? 'p' : 'd';

        String prevParse = parseParams(beforeStop.getRequestID(), beforeStop.getIntersectionId(), prevType);
        String followParse = parseParams(followStop.getRequestID(), followStop.getIntersectionId(), followingType);

        if ((precs.containsKey(prevParse) && precs.get(prevParse).contains(followParse)) || (precs.containsKey(followParse) && precs.get(followParse).contains(prevParse))) {
            throw new IllegalArgumentException(
                    "Impossible to create precedence between 2 stops already linked by precedence : "
                            + beforeStopIndex + " and " + afterStopIndex
            );
        }
        // Add precedence
        precs.computeIfAbsent(
                        followParse,
                        k -> new HashSet<>())
                .add(prevParse);

        precedencesByCourier.put(courierId, precs);
    }

    /**
     * Initializes precedence constraints for a courier based on their requests.
     *
     * @param courierId The ID of the courier.
     * @param requestsId The list of request IDs.
     * @param pickupDelivery The pickup and delivery data.
     */
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
        }
        precedencesByCourier.put(courierId, precs);
    }

    /**
     * Generates TSP precedences for a courier based on their requests.
     *
     * @param requestIdsForCourier The list of request IDs for the courier.
     * @param courierId The ID of the courier.
     * @param pickupDelivery The pickup and delivery data.
     * @return A map entry containing intersection IDs and precedence constraints.
     */
    public java.util.Map.Entry<long[], HashMap<Integer, Set<Integer>>> generateTspPrecedences(
            ArrayList<Long> requestIdsForCourier,
            long courierId,
            PickupDelivery pickupDelivery) {

        HashMap<String, Set<String>> precs = precedencesByCourier.get(courierId);

        Request request;

        List<String> vertices = new ArrayList<>(requestIdsForCourier.size() * 2 + 1);
        vertices.add(parseParams(-1, pickupDelivery.getWarehouseAddressId(), 'w'));

        for (long requestId : requestIdsForCourier) {
            request = pickupDelivery.findRequestById(requestId);
            vertices.add(parseParams(requestId, request.getPickupIntersectionId(), 'p'));
            vertices.add(parseParams(requestId, request.getDeliveryIntersectionId(), 'd'));
        }

        HashMap<Integer, Set<Integer>> tspPrecs = new HashMap<>();

        for (int i = 0; i < vertices.size(); i++) {
            Set<String> prevVertices = precs.get(vertices.get(i));
            if (prevVertices != null) {
                for (String prevVertix : prevVertices) {
                    int prevVertixIndex = vertices.indexOf(prevVertix);
                    tspPrecs.computeIfAbsent(i, k -> new HashSet<>())
                            .add(prevVertixIndex);
                }
            }
        }
        long[] verticesIntersectionIds = extractIntersectionIds(vertices);

        return java.util.Map.entry(verticesIntersectionIds, tspPrecs);
    }

    /**
     * Parses parameters into a string representation.
     *
     * @param requestId The request ID.
     * @param intersectionID The intersection ID.
     * @param type The type of stop ('p' for pickup, 'd' for delivery, 'w' for warehouse).
     * @return The parsed string representation.
     */
    public String parseParams(long requestId, long intersectionID, char type) {
        return requestId + "/" + intersectionID + "/" + type;
    }

    /**
     * Extracts intersection IDs from a list of vertex strings.
     *
     * @param vertices The list of vertex strings.
     * @return An array of intersection IDs.
     */
    public long[] extractIntersectionIds(List<String> vertices) {
        long[] intersectionIds = new long[vertices.size()];

        for (int i = 0; i < vertices.size(); i++) {
            String v = vertices.get(i);
            String[] parts = v.split("/");
            intersectionIds[i] = Long.parseLong(parts[1]);
        }

        return intersectionIds;
    }

    public ArrayList<Courier> getCouriers() {
        return couriers;
    }


    public int getNumCouriers() {
        return numCouriers;
    }


    public TreeMap<Long, Tour> getTours() {
        return tours;
    }

    public TreeMap<Long, HashMap<String, Set<String>>> getPrecedencesByCourier() {
        return precedencesByCourier;
    }

    public ArrayList<Courier> getAvailableCouriers() {
        ArrayList<Courier> availableCouriers = new ArrayList<>();
        for (Courier courier : couriers) {
            if (courier.isAvailable()) {
                availableCouriers.add(courier);
            }
        }
        return availableCouriers;
    }
}
