package domain.model;

import persistence.XMLParsers;

import java.util.*;
import java.util.Map;

import static domain.model.StopType.*;


public class PickupDelivery {

    private TreeMap<Long, Request> requests;
    private TreeMap<Long, Long[]> requestsPerCourier;
    private long warehouseAdressId;

    public PickupDelivery() {
        requests = new TreeMap<>();
        requestsPerCourier = new TreeMap<>();
        warehouseAdressId = -1;
    }

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

    public boolean loadRequests(String filepath) {
        return XMLParsers.parseRequests(filepath, this);
    }

    public Map<Long, Long[]> getRequestsPerCourier() {
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
