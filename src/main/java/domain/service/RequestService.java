package domain.service;

import domain.model.PickupDelivery;
import domain.model.Request;
import org.springframework.stereotype.Service;
import persistence.XMLParsers;


/**
 * Service class for managing requests and their association with couriers.
 * Provides functionality to add requests, load requests from an XML file,
 * and retrieve the current state of the PickupDelivery object.
 */
@Service
public class RequestService {


    private PickupDelivery pickupDelivery; // The PickupDelivery object that manages requests and their associations.

    /**
     * Constructs a new RequestService and initializes the PickupDelivery object.
     */
    public RequestService() {
        pickupDelivery = new PickupDelivery();
    }

    /**
     * Adds a request to a specific courier.
     *
     * @param courierId the ID of the courier to whom the request will be added
     * @param request the request to be added
     */
    public void addRequest(long courierId, Request request) {
        pickupDelivery.addRequestToCourier(courierId, request);
    }

    public void deleteRequest(long courierId, long requestId) {
        pickupDelivery.removeRequestFromCourier(courierId, requestId);
    }

    /**
     * Loads requests from an XML file and populates the PickupDelivery object.
     *
     * @param filepath the path to the XML file containing the requests
     * @param courierId the ID of the courier for whom the requests are being loaded
     * @return true if the requests were successfully loaded, false otherwise
     */
    public boolean loadRequests(String filepath, long courierId) {
        return XMLParsers.parseRequests(filepath, courierId, pickupDelivery);
    }


    public PickupDelivery getPickupDelivery() {
        return pickupDelivery;
    }
}
