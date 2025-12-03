package domain.model;

import persistence.XMLParsers;

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
    private TreeMap<Long, Long[]> requestsPerCourier;

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
     * Adds a request to a specific courier.
     *
     * @param courierId the ID of the courier
     * @param request the request to add
     * @return true if the request was successfully added
     */
    public boolean addRequestToCourier(long courierId, Request request) {
        requests.put(request.getId(), request);

        Long[] requestsOfCourier = requestsPerCourier.get(courierId);
        if (requestsOfCourier == null) {
            requestsOfCourier = new Long[] { request.getId() };
        } else {
            Long[] newRequestsOfCourier = new Long[requestsOfCourier.length + 1];
            System.arraycopy(requestsOfCourier, 0, newRequestsOfCourier, 0, requestsOfCourier.length);
            newRequestsOfCourier[requestsOfCourier.length] = request.getId();
            requestsOfCourier = newRequestsOfCourier;
        }

        requestsPerCourier.put(courierId, requestsOfCourier);

        return true;
    }

    /**
     * Retrieves all requests associated with a specific courier.
     *
     * @param courierId the ID of the courier
     * @return an array of requests for the courier
     */
    public Request[] getRequestsForCourier(long courierId) {
        Long[] requestIds = requestsPerCourier.get(courierId);
        if (requestIds == null) {
            return new Request[0];
        }
        Request[] result = new Request[requestIds.length];
        for (int i = 0; i < requestIds.length; i++) {
            result[i] = requests.get(requestIds[i]);
        }

        return result;
    }

    /**
     * Loads requests from an XML file.
     *
     * @param filepath the path to the XML file
     * @return true if the requests were successfully loaded
     */
    public boolean loadRequests(String filepath) {
        return XMLParsers.parseRequests(filepath, this);
    }

    /**
     * Retrieves the mapping of couriers to their associated request IDs.
     *
     * @return a map of courier IDs to request IDs
     */
    public Map<Long, Long[]> getRequestsPerCourier() {
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
        for (Map.Entry<Long, Long[]> entry : requestsPerCourier.entrySet()) {
            sb.append("Courier ID ").append(entry.getKey()).append(": ");
            for (Long requestId : entry.getValue()) {
                sb.append(requests.get(requestId)).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}