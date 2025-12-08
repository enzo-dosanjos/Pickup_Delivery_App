package domain.model;

import java.util.*;
import java.util.Map;

import static domain.model.StopType.*;


/**
 * Represents a system for managing pickup and delivery requests.
 */
public class PickupDelivery {

    /** A map of requests, keyed by their unique identifiers. */
    private TreeMap<Long, Request> requests;

    /** A map of courier IDs to their associated request IDs. */
    private TreeMap<Long, ArrayList<Long>> requestsPerCourier;

    /** The ID of the warehouse address. */
    private long warehouseAdressId;

    /**
     * Constructs an empty PickupDelivery system.
     */
    public PickupDelivery() {
        requests = new TreeMap<>();
        requestsPerCourier = new TreeMap<>();
        warehouseAdressId = -1;
    }

    /**
     * Constructs a PickupDelivery by copying another instance.
     *
     * @param other the PickupDelivery instance to copy
     */
    public PickupDelivery(PickupDelivery other) {
        this.requests = new TreeMap<>(other.requests);
        this.requestsPerCourier = new TreeMap<>();
        for (Map.Entry<Long, ArrayList<Long>> entry : other.requestsPerCourier.entrySet()) {
            this.requestsPerCourier.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        this.warehouseAdressId = other.warehouseAdressId;
    }

    /**
     * Adds a request to a specific courier.
     *
     * @param courierId the ID of the courier
     * @param request the request to add
     * @return true if the request was successfully added
     */
    public boolean addRequestToCourier(long courierId, Request request) {
        requests.put(request.getId(), request);

        ArrayList<Long> requestsOfCourier = requestsPerCourier.get(courierId);

        if (requestsOfCourier == null) {
            requestsOfCourier = new ArrayList<>();
        }

        requestsOfCourier.add(request.getId());

        requestsPerCourier.put(courierId, requestsOfCourier);

        return true;
    }

    /**
     * Removes a request from a specific courier.
     *
     * @param courierId the ID of the courier
     * @param requestId the ID of the request to remove
     * @return true if the request was successfully removed, false otherwise
     */
    public boolean removeRequestFromCourier(long courierId, long requestId) {
        requests.remove(requestId);

        ArrayList<Long> requestsOfCourier = requestsPerCourier.get(courierId);
        if (requestsOfCourier == null) {
            return false;
        }

        requestsOfCourier.remove(requestId);

        return true;
    }

    /**
     * Retrieves all requests associated with a specific courier.
     *
     * @param courierId the ID of the courier
     * @return an array of requests for the courier
     */
    public Request[] getRequestsForCourier(long courierId) {
        ArrayList<Long> requestIds = requestsPerCourier.get(courierId);
        if (requestIds == null) {
            return new Request[0];
        }

        Request[] result = new Request[requestIds.size()];
        for (int i = 0; i < requestIds.size(); i++) {
            result[i] = requests.get(requestIds.get(i));
        }

        return result;
    }

    /**
     * Retrieves the mapping of couriers to their associated request IDs.
     *
     * @return a map of courier IDs to request IDs
     */
    public Map<Long, ArrayList<Long>> getRequestsPerCourier() {
        return requestsPerCourier;
    }

    /**
     * Retrieves all requests in the system.
     *
     * @return a TreeMap of requests, keyed by their IDs
     */
    public TreeMap<Long, Request> getRequests() {
        return requests;
    }

    /**
     * Retrieves the ID of the warehouse address.
     *
     * @return the warehouse address ID
     */
    public long getWarehouseAdressId() {
        return warehouseAdressId;
    }

    /**
     * Sets the ID of the warehouse address.
     *
     * @param warehouseAdressId the new warehouse address ID
     */
    public void setWarehouseAdressId(long warehouseAdressId) {
        this.warehouseAdressId = warehouseAdressId;
    }

    /**
     * Finds a request by its unique ID.
     *
     * @param requestId the ID of the request
     * @return the request if found, or null otherwise
     */
    public Request findRequestById(long requestId) {
        return requests.get(requestId);
    }

    /**
     * Finds a request by the intersection ID associated with it.
     *
     * @param intersectionId the intersection ID to search for
     * @return a map entry containing the request and its stop type (PICKUP or DELIVERY), or null if not found
     */
    public Map.Entry<Request, StopType> findRequestByIntersectionId(long intersectionId) {
        for(Request req : requests.values()) {
            if (req.getDeliveryIntersectionId() == intersectionId) {
                return  Map.entry(req, DELIVERY);
            }
            else if (req.getPickupIntersectionId() == intersectionId){
                return  Map.entry(req, PICKUP);
            }
        }
        return null;
    }

    /**
     * Returns a string representation of the PickupDelivery system.
     *
     * @return a string describing the warehouse address, requests per courier, and their details
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PickupDelivery:\n");
        sb.append("Warehouse Address ID: ").append(warehouseAdressId).append("\n");
        sb.append("Requests per Courier:\n");
        for (Map.Entry<Long, ArrayList<Long>> entry : requestsPerCourier.entrySet()) {
            sb.append("Courier ID ").append(entry.getKey()).append(": ");
            for (Long requestId : entry.getValue()) {
                sb.append(requests.get(requestId)).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}