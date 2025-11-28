package domain.service;

import domain.model.Courier;
import domain.model.Tour;

import java.time.Duration;
import java.util.HashMap;
import java.util.TreeMap;

public class TourService {
    private int numCouriers;
    private Courier[] couriers;
    private TreeMap<Long,Tour> tours;
    private TreeMap<Long, HashMap<Long, Long>> requestsOrder;

    private static final double COURIER_SPEED_KMH = 15.0;

    public TourService() {
        this.numCouriers = 1;
        this.couriers = new Courier[numCouriers];
        this.tours = new TreeMap<>();
        this.requestsOrder = new TreeMap<>();
    }

    public void setTourForCourier(long courierId, Tour tour) {
        tours.put(courierId, tour);
    }

    public boolean addCourier(Courier courier) {
        for (int i = 0; i < couriers.length; i++) {
            if (couriers[i] == null) {
                couriers[i] = courier;
                return true;
            }
        }

        return false;
    }

    public Duration computeDuration(double distance) {
        if (distance <= 0.0) {
            return Duration.ZERO;
        }

        double hours = distance / COURIER_SPEED_KMH;
        long seconds = Math.round(hours * 3600.0);
        return Duration.ofSeconds(seconds);
    }

    public boolean updateNumCouriers(int numCouriers)
    // Updates the number of couriers, clears the couriers array and resizes it accordingly
    {
        if (numCouriers < 0) {
            return false;
        }

        this.numCouriers = numCouriers;
        this.couriers = new Courier[numCouriers];

        return true;
    }

    public void updateRequestOrder(long requestBeforeId, long requestAfterId, long courierId)
    // Adds a constraint that requestBeforeId must be served before requestAfterId by the specified courier
    {
        HashMap<Long, Long> constraints =
                requestsOrder.computeIfAbsent(courierId, id -> new HashMap<>());
        constraints.put(requestBeforeId, requestAfterId);
    }

    public Courier[] getCouriers() {
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
}
