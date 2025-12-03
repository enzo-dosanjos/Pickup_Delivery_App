package domain.service;

import domain.model.PickupDelivery;
import domain.model.Request;
import persistence.XMLParsers;

/**
 * Service class for managing requests and their association with couriers.
 * Provides functionality to add requests, load requests from an XML file,
 * and retrieve the current state of the PickupDelivery object.
 */
public class RequestService {

    /** The PickupDelivery object that manages requests and their associations. */
    private PickupDelivery pickupDelivery;

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

    /**
     * Loads requests from an XML file and populates the PickupDelivery object.
     *
     * @param filepath the path to the XML file containing the requests
     */
    public void loadRequests(String filepath) {
        XMLParsers.parseRequests(filepath, pickupDelivery);
    }

    /**
     * Retrieves the current PickupDelivery object.
     *
     * @return the PickupDelivery object
     */
    public PickupDelivery getPickupDelivery() {
        return pickupDelivery;
    }
}