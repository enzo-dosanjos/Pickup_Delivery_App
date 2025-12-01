package domain.service;

import domain.model.Courier;
import domain.model.Tour;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

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
}
