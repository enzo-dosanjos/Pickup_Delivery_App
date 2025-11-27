package domain.service;

import domain.model.PickupDelivery;
import persistence.XMLParsers;


public class RequestService {

    private PickupDelivery pickupDelivery;

    public RequestService() {
    }

    public void loadRequests(String filepath) {
        this.pickupDelivery = XMLParsers.parseRequests(filepath);
    }

    public PickupDelivery getPickupDelivery() {
        return pickupDelivery;
    }
}
