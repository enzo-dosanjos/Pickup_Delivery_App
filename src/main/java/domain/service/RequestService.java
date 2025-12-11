package domain.service;

import domain.model.PickupDelivery;
import domain.model.Request;
import org.springframework.stereotype.Service;
import persistence.XMLParsers;
import persistence.XMLWriters;

import java.util.ArrayList;
import java.util.TreeMap;


/**
 * Service class for managing requests and their association with couriers.
 * Provides functionality to add requests, load requests from an XML file,
 * and retrieve the current state of the PickupDelivery object.
 */
@Service
public class RequestService {

    private TreeMap<Long, PickupDelivery> pickupDeliveryPerCourier; // A map of courier IDs to a the pickup delivery associated with that courier.

    /**
     * Constructs a new RequestService and initializes the PickupDelivery object.
     */
    public RequestService() {
        pickupDeliveryPerCourier = new TreeMap<>();
    }

    /**
     * Adds a request to a specific courier.
     *
     * @param courierId the ID of the courier to whom the request will be added
     * @param request the request to be added
     */
    public void addRequest(long courierId, Request request) {
        if (!pickupDeliveryPerCourier.containsKey(courierId)) {
            pickupDeliveryPerCourier.put(courierId, new PickupDelivery());
        }

        pickupDeliveryPerCourier.get(courierId).addRequest(request);
    }

    /**
     * Deletes a request from a specific courier.
     *
     * @param courierId the ID of the courier from whom the request will be deleted
     * @param requestId the ID of the request to be deleted
     */
    public void deleteRequest(long courierId, long requestId) {
        pickupDeliveryPerCourier.get(courierId).removeRequest(requestId);
    }

    /**
     * Loads requests from an XML file and populates the PickupDelivery object.
     *
     * @param filepath the path to the XML file containing the requests
     * @param courierId the ID of the courier for whom the requests are being loaded
     * @return true if the requests were successfully loaded, false otherwise
     */
    public boolean loadRequests(String filepath, long courierId) {
        if (!pickupDeliveryPerCourier.containsKey(courierId)) {
            pickupDeliveryPerCourier.put(courierId, new PickupDelivery());
        }

        return XMLParsers.parseRequests(filepath, pickupDeliveryPerCourier.get(courierId));
    }

    /**
     * Saves the requests of a specific courier to an XML file.
     *
     * @param filepath the path to the XML file where the requests will be saved
     * @param courierId the ID of the courier whose requests are being saved
     */
    public void saveRequests(String filepath, long courierId) {
        XMLWriters.writeRequests(pickupDeliveryPerCourier.get(courierId), filepath);
    }

    /**
     * Retrieves a list of all warehouse IDs associated with the requests of a specific courier.
     *
     * @return an ArrayList of warehouse IDs
     */
    public ArrayList<Long> getAllWarehouseIds() {
        ArrayList<Long> warehouseIds = new ArrayList<>();

        for (PickupDelivery pickupDelivery : pickupDeliveryPerCourier.values()) {
            warehouseIds.add(pickupDelivery.getWarehouseAddressId());
        }

        return warehouseIds;
    }

    public PickupDelivery getPickupDeliveryForCourier(long courierId) {
        if (!pickupDeliveryPerCourier.containsKey(courierId)) {
            pickupDeliveryPerCourier.put(courierId, new PickupDelivery());
        }

        return pickupDeliveryPerCourier.get(courierId);
    }

    public TreeMap<Long, PickupDelivery> getPickupDeliveryPerCourier() {
        return pickupDeliveryPerCourier;
    }

    public Request getRequestById(long requestId, long courierId) {
        return pickupDeliveryPerCourier.get(courierId).findRequestById(requestId);
    }

    public void setWarehouseAddress(long warehouseId,long courierId) {
        if (!pickupDeliveryPerCourier.containsKey(courierId)) {
            pickupDeliveryPerCourier.put(courierId, new PickupDelivery());
        }

        pickupDeliveryPerCourier.get(courierId).setWarehouseAddressId(warehouseId);
    }
}
