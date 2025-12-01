package domain.service;

import domain.model.Courier;
import domain.model.Tour;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import domain.model.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
                requestId = 0;
                arrivalTime = tour.getStartTime();
                first = false;
            }
            else
            {
                var result = pickupDelivery.findRequestByIntersectionId(intersectionId);
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

    public Tour addRoadsToTour(Tour tour){
        return tour;
    }
}
