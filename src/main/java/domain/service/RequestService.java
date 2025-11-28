package domain.service;

import domain.model.PickupDelivery;
import domain.model.Request;
import persistence.XMLParsers;


public class RequestService {

    private PickupDelivery pickupDelivery;

    public RequestService() {
        pickupDelivery = new PickupDelivery();
    }

    public void addRequest(long courierId, Request request) {
        pickupDelivery.addRequestToCourier(courierId, request);
    }

    public void loadRequests(String filepath) {
        XMLParsers.parseRequests(filepath, pickupDelivery);
    }

    public PickupDelivery getPickupDelivery() {
        return pickupDelivery;
    }
}
