package domain.service;

import domain.model.PickupDelivery;
import domain.model.Request;
import org.springframework.stereotype.Service;
import persistence.XMLParsers;
import persistence.XMLWriters;


@Service
public class RequestService {

    private PickupDelivery pickupDelivery;

    public RequestService() {
        pickupDelivery = new PickupDelivery();
    }

    public void addRequest(long courierId, Request request) {
        pickupDelivery.addRequestToCourier(courierId, request);
    }

    public void deleteRequest(long courierId, long requestId) {
        pickupDelivery.removeRequestFromCourier(courierId, requestId);
    }

    public void loadRequests(String filepath, long courierId) {
        XMLParsers.parseRequests(filepath, courierId, pickupDelivery);
    }

    public PickupDelivery getPickupDelivery() {
        return pickupDelivery;
    }
}
