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

@Service
public class TourService {
    private int numCouriers;
    private ArrayList<Courier> couriers;
    private TreeMap<Long,Tour> tours;
    private TreeMap<Long, HashMap<Long, Long>> requestsOrder;

    public TourService() {
        this.numCouriers = 0;
        this.couriers = new ArrayList<>();
        this.tours = new TreeMap<>();
        this.requestsOrder = new TreeMap<>();
    }

    public void setTourForCourier(long courierId, Tour tour) {
        tours.put(courierId, tour);
    }

    public boolean addCourier(Courier courier) {
        boolean added = couriers.add(courier);
        if (added) { numCouriers++; }
        return added;
    }

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

    public void loadCouriers(String filepath) {
        ArrayList<Courier> couriersToAdd = XMLParsers.parseCouriers(filepath);

        for (Courier courier : couriersToAdd) {
            boolean added = couriers.add(courier);
            if (added) { numCouriers++; }
        }
    }

    public boolean updateRequestOrder(long requestBeforeId, long requestAfterId, long courierId)
    // Adds a constraint that requestBeforeId must be served before requestAfterId by the specified courier
    {
        boolean courierExists = false;
        for (Courier courier : couriers) {
            if (courier.getId() == courierId) {
                courierExists = true;
            }
        }
        if (!courierExists) { return false; }

        HashMap<Long, Long> constraints =
                requestsOrder.computeIfAbsent(courierId, id -> new HashMap<>());
        constraints.put(requestBeforeId, requestAfterId);

        return true;
    }

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

        for(Integer i : solution)
        {
            duration = Duration.ZERO;
            intersectionId = vertices[i];


            if(first)
            {
                stopType = StopType.WAREHOUSE;
                requestId = -1;
                arrivalTime = tour.getStartTime();
                first = false;
            }
            else
            {
                Entry<Request, StopType> result = pickupDelivery.findRequestByIntersectionId(intersectionId);

                if (result == null) {
                    // This should not happen if the solution is valid
                    throw new IllegalArgumentException("No request found for intersection ID: " + intersectionId);
                }

                request = result.getKey();
                stopType = result.getValue();
                arrivalTime =  tour.getStartTime().plus(tour.getTotalDuration());

                if (stopType == StopType.PICKUP)
                {
                    duration = request.getPickupDuration();
                }

                else if(stopType == StopType.DELIVERY)
                {
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
        //add commute time between last stop and warehouse
        minutes = costs[solution[solution.length - 1]][solution[0]];
        long wholeMinutes = (long) minutes;
        long seconds = Math.round((minutes - wholeMinutes) * 60);
        commuteDuration = Duration.ofMinutes(wholeMinutes).plusSeconds(seconds);
        tour.updateTotalDuration(commuteDuration);
        return tour;
    }

    public Tour addRoadsToTour(Tour tour, DijkstraTable table, Map map){
        List<TourStop> stops = tour.getStops();
        List<Long> reverseIntersectPath = new ArrayList<>();

        long targetIntersectionId;
        long sourceIntersectionId;
        long currentIntersectionId;

        CellInfo info;
        RoadSegment road;

        //add all the visited intersections in order to a list
        for (int i = stops.size(); i > 0; i--) {
            if(i == stops.size()){
                targetIntersectionId = stops.get(0).getIntersectionId();
            }
            else{
                targetIntersectionId = stops.get(i).getIntersectionId();
            }
            sourceIntersectionId = stops.get(i-1).getIntersectionId();
            currentIntersectionId = targetIntersectionId;
            while (currentIntersectionId != sourceIntersectionId) {
                reverseIntersectPath.add(currentIntersectionId);
                info = table.get(sourceIntersectionId, currentIntersectionId);
                currentIntersectionId = info.getPredecessor();
            }
        }

        reverseIntersectPath.add(stops.get(0).getIntersectionId());
        Collections.reverse(reverseIntersectPath);

        for (int i = 0; i < reverseIntersectPath.size()-1; i++){
            road = map.getRoadSegment(reverseIntersectPath.get(i), reverseIntersectPath.get(i+1));
            tour.addRoadSegment(road); }

        return tour;
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

    public TreeMap<Long, HashMap<Long, Long>> getRequestOrder() {
        return requestsOrder;
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
