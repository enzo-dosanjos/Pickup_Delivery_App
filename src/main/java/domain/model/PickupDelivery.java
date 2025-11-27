package domain.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PickupDelivery {

    private final Map<Long, Request> requests;
    private final Map<Long, List<Request>> requestsPerCourier;
    private long warehouseAddress;

    public PickupDelivery() {
        this.requests = new HashMap<>();
        this.requestsPerCourier = new HashMap<>();
    }

    public void addRequest(long courierId, Request request) {
        requests.put(request.getId(), request);
        requestsPerCourier.computeIfAbsent(courierId, k -> new ArrayList<>()).add(request);
    }

    public List<Request> getRequestsForCourier(long courierId) {
        return requestsPerCourier.getOrDefault(courierId, Collections.emptyList());
    }

    public Map<Long, Request> getRequests() {
        return requests;
    }

    public Map<Long, List<Request>> getRequestsPerCourier() {
        return requestsPerCourier;
    }

    public long getWarehouseAddress() {
        return warehouseAddress;
    }

    public void setWarehouseAddress(long warehouseAddress) {
        this.warehouseAddress = warehouseAddress;
    }
}
