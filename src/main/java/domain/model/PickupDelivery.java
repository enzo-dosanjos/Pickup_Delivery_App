package domain.model;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.*;
import java.util.Map;

import static domain.model.StopType.*;


/**
 * Represents a system for managing pickup and delivery requests.
 */
public class PickupDelivery {


    private ArrayList<Request> requests; // An array list of requests for pickup and delivery.
    private LocalDateTime departureTime; // The start time of the tour.
    private long warehouseAddressId; // The ID of the warehouse address, used for pickup and delivery operations.

    /**
     * Constructs an empty PickupDelivery system.
     */
    public PickupDelivery() {
        requests = new ArrayList<>();
        departureTime = LocalDate.now().atTime(8, 0).plusDays(1L);
        warehouseAddressId = -1;
    }

    /**
     * Constructs a PickupDelivery by copying another instance.
     *
     * @param other the PickupDelivery instance to copy
     */
    public PickupDelivery(PickupDelivery other) {
        this.requests = new ArrayList<>(other.requests);
        this.departureTime = other.departureTime;
        this.warehouseAddressId = other.warehouseAddressId;
    }

    /**
     * Adds a request to the pickup delivery.
     *
     * @param request the request to add
     */
    public void addRequest(Request request) {
        requests.add(request);
    }

    /**
     * Removes a request from the pickup delivery.
     *
     * @param requestId the ID of the request to remove
     */
    public void removeRequest(long requestId) {
        for (Request request : requests) {
            if (request.getId() == requestId) {
                requests.remove(request);
                return;
            }
        }
    }


    public ArrayList<Request> getRequests() {
        return requests;
    }


    public long getWarehouseAddressId() {
        return warehouseAddressId;
    }


    public void setWarehouseAddressId(long warehouseAddressId) {
        this.warehouseAddressId = warehouseAddressId;
    }

    public LocalDateTime getDepartureTime() { return departureTime; }

    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }

    /**
     * Finds a request by its unique ID.
     *
     * @param requestId the ID of the request
     * @return the request if found, or null otherwise
     */
    public Request findRequestById(long requestId) {
        for (Request request : requests) {
            if (request.getId() == requestId) {
                return request;
            }
        }

        return null;
    }

    /**
     * Finds a request by the intersection ID associated with it.
     *
     * @param intersectionId the intersection ID to search for
     * @return a map entry containing the request and its stop type (PICKUP or DELIVERY), or null if not found
     */
    public Map.Entry<Request, StopType> findRequestByIntersectionId(long intersectionId) {
        for (Request request : requests) {
            if (request.getPickupIntersectionId() == intersectionId) {
                return  Map.entry(request, PICKUP);
            } else if (request.getDeliveryIntersectionId() == intersectionId) {
                return  Map.entry(request, DELIVERY);
            }
        }

        return null;
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PickupDelivery:\n");
        sb.append("Warehouse Address ID: ").append(warehouseAddressId).append("\n");
        sb.append("Requests:\n");
        for (Request request : requests) {
            sb.append(request.toString()).append("\n");
        }
        return sb.toString();
    }
}