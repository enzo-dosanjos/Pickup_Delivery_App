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
import persistence.XMLWriters;

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
     * Empties the couriers array, then loads couriers from an XML file and adds them to the service.
     *
     * @param filepath the path to the XML file containing courier data
     */
    public void loadCouriers(String filepath) {
        couriers.clear();
        ArrayList<Courier> couriersToAdd = XMLParsers.parseCouriers(filepath);

        for (Courier courier : couriersToAdd) {
            boolean added = couriers.add(courier);
            if (added) { numCouriers++; }
        }
    }


    /**
     * Converts a TSP solution into a Tour object.
     *
     * @param pickupDelivery the PickupDelivery object containing requests
     * @param courierId the ID of the courier
     * @param solution the TSP solution as an array of vertex indices
     * @param vertices the list of vertex strings
     * @param costs the cost matrix representing travel times between vertices
     * @return the constructed Tour object
     */
    public Tour convertGraphToTour(PickupDelivery pickupDelivery, long courierId, Integer[] solution, List<String> vertices, double[][] costs) {
        long intersectionId;
        Integer previousTourStop = null;

        Request request;
        long requestId;

        StopType stopType;
        TourStop tourStop;
        LocalDateTime arrivalTime, departureTime;
        Duration duration;

        Tour tour = new Tour(courierId, pickupDelivery.getDepartureTime());
        double minutes = 0.0;
        boolean first = true;
        Duration commuteDuration = Duration.ZERO;

        for (Integer i : solution) {
            duration = Duration.ZERO;
            intersectionId = extractIntersectionId(vertices.get(i));

            if (first) {
                stopType = StopType.WAREHOUSE;
                requestId = -1;
                arrivalTime = tour.getStartTime();
                first = false;
            } else {
                request = pickupDelivery.findRequestById(extractRequestId(vertices.get(i)));
                stopType = extractStopType(vertices.get(i));
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
     * @param requests The list of requests assigned to the courier.
     */
    public void initPrecedences(long courierId, ArrayList<Request> requests) {
        HashMap<String, Set<String>> precs = new HashMap<>();
        long delIntersectionId;
        long puIntersectionId;
        for (Request request : requests) {
            puIntersectionId = request.getPickupIntersectionId();
            delIntersectionId = request.getDeliveryIntersectionId();
            precs.computeIfAbsent(parseParams(request.getId(), delIntersectionId, 'd'), k -> new HashSet<>()).add(parseParams(request.getId(), puIntersectionId, 'p'));
        }
        precedencesByCourier.put(courierId, precs);
    }

    /**
     * Generates TSP precedence constraints for a courier based on their requests.
     *
     * @param requests The list of request IDs for the courier.
     * @param warehouseAddressId The address of the warehouse.
     * @param courierId The ID of the courier.
     * @return A map entry containing intersection IDs and precedence constraints.
     */
    public java.util.Map.Entry<List<String>, HashMap<Integer, Set<Integer>>> generateTspPrecedences(
            ArrayList<Request> requests,
            long warehouseAddressId,
            long courierId) {

        HashMap<String, Set<String>> precs = precedencesByCourier.get(courierId);

        List<String> vertices = new ArrayList<>(requests.size() * 2 + 1);
        vertices.add(parseParams(-1, warehouseAddressId, 'w'));

        for (Request request : requests) {
            vertices.add(parseParams(request.getId(), request.getPickupIntersectionId(), 'p'));
            vertices.add(parseParams(request.getId(), request.getDeliveryIntersectionId(), 'd'));
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


        return java.util.Map.entry(vertices, tspPrecs);
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

    /**
     * Extracts the request ID from a vertex string.
     *
     * @param vertix The vertex string.
     * @return The extracted request ID.
     */
    public long extractRequestId(String vertix) {
        long requestId;
        String[] parts = vertix.split("/");
        requestId = Long.parseLong(parts[0]);
        return requestId;
    }

    /**
     * Extracts the intersection ID from a vertex string.
     *
     * @param vertix The vertex string.
     * @return The extracted intersection ID.
     */
    public long extractIntersectionId(String vertix) {
        long intersectionId;
        String[] parts = vertix.split("/");
        intersectionId = Long.parseLong(parts[1]);
        return intersectionId;
    }

    /**
     * Extracts the stop type from a vertex string.
     *
     * @param vertix The vertex string.
     * @return The extracted stop type.
     */
    public StopType extractStopType(String vertix) {
        StopType stopType;
        char typeChar;
        String[] parts = vertix.split("/");
        typeChar = parts[2].charAt(0);
        if (typeChar != 'p' && typeChar != 'd' && typeChar != 'w') {
            throw new IllegalArgumentException(
                    "Incorrect StopType."
            );
        }
        if (typeChar == 'p') {
            stopType = StopType.PICKUP;
        } else if (typeChar == 'd') {
            stopType = StopType.DELIVERY;
        } else {
            stopType = StopType.WAREHOUSE;
        }
        return stopType;
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

    /**
     * Exports the tour of the given courier to an XML file.
     *
     * @param courierId the courier whose tour must be exported
     * @param filePath the destination XML file path
     * @throws Exception if writing fails
     */
    public void exportTour(long courierId, String filePath) throws Exception {
        Tour tour = tours.get(courierId);
        if (tour == null) {
            throw new IllegalArgumentException("No tour found for courier " + courierId);
        }

        XMLWriters.writeTour(tour, filePath);
    }
}
