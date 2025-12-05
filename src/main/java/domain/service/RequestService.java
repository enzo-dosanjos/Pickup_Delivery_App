package domain.service;

import domain.model.PickupDelivery;
import domain.model.Request;
import org.springframework.stereotype.Service;
import persistence.XMLParsers;


@Service
public class RequestService {

    private PickupDelivery pickupDelivery;

    public RequestService() {
        pickupDelivery = new PickupDelivery();
    }

    public void addRequest(long courierId, Request request) {
        pickupDelivery.addRequestToCourier(courierId, request);
    }

    public void deleteRequest(long requestId, long courierId) {
        pickupDelivery.removeRequestFromCourier(requestId, courierId);
    }

    public void loadRequests(String filepath) {
        XMLParsers.parseRequests(filepath, pickupDelivery);
    }

    public PickupDelivery getPickupDelivery() {
        return pickupDelivery;
    }
}
