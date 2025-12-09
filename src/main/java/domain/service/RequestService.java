package domain.service;

import domain.model.PickupDelivery;
import domain.model.Request;
import org.springframework.stereotype.Service;
import persistence.XMLParsers;
import persistence.XMLWriters;

@Service
public class RequestService {

    private final PickupDelivery pickupDelivery;
    private final XMLWriters xmlWriters;

    public RequestService() {
        this.pickupDelivery = new PickupDelivery();
        this.xmlWriters = new XMLWriters();
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

    public void saveRequests(String filepath) {
        xmlWriters.writeRequests(pickupDelivery, filepath);
    }

    public PickupDelivery getPickupDelivery() {
        return pickupDelivery;
    }

    public Request getRequestById(long requestId) {
        return pickupDelivery.findRequestById(requestId);
    }

    public void setWarehouseAddress(long warehouseId) {
        pickupDelivery.setWarehouseAdressId(warehouseId);
    }
}
