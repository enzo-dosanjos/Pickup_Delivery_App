package domain.model;

import java.lang.reflect.Array;
import java.util.*;
import java.util.Map;

import static domain.model.StopType.*;


public class PickupDelivery {

    private TreeMap<Long, Request> requests;
    private TreeMap<Long, ArrayList<Long>> requestsPerCourier;
    private long warehouseAdressId;

    public PickupDelivery() {
        requests = new TreeMap<>();
        requestsPerCourier = new TreeMap<>();
        warehouseAdressId = -1;
    }

    public PickupDelivery(PickupDelivery other) {
        this.requests = new TreeMap<>(other.requests);
        this.requestsPerCourier = new TreeMap<>();
        for (Map.Entry<Long, ArrayList<Long>> entry : other.requestsPerCourier.entrySet()) {
            this.requestsPerCourier.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        this.warehouseAdressId = other.warehouseAdressId;
    }

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

    public boolean removeRequestFromCourier(long courierId, long requestId) {
        requests.remove(requestId);

        ArrayList<Long> requestsOfCourier = requestsPerCourier.get(courierId);
        if (requestsOfCourier == null) {
            return false;
        }

        requestsOfCourier.remove(requestId);

        return true;
    }

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

    public Map<Long, ArrayList<Long>> getRequestsPerCourier() {
        return requestsPerCourier;
    }

    public TreeMap<Long, Request> getRequests() {
        return requests;
    }

    public long getWarehouseAdressId() {
        return warehouseAdressId;
    }

    public void setWarehouseAdressId(long warehouseAdressId) {
        this.warehouseAdressId = warehouseAdressId;
    }

    public Request findRequestById(long requestId) {return requests.get(requestId);}

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
