package domain.service;

import domain.model.PickupDelivery;
import domain.model.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import persistence.XMLParsers;
import persistence.XMLWriters;
import java.time.Duration;
import java.util.List;
import java.util.Vector;

@Service
public class RequestService {

    private PickupDelivery pickupDelivery;
    private final XMLParsers xmlParsers;
    private final XMLWriters xmlWriters;

    @Autowired
    public RequestService(XMLParsers xmlParsers, XMLWriters xmlWriters) {
        this.xmlParsers = xmlParsers;
        this.xmlWriters = xmlWriters;
        this.pickupDelivery = new PickupDelivery(); // Initialize with an empty PickupDelivery
    }

    /**
     * Loads requests from a specified file path.
     * @param filepath The path to the XML file containing the requests data.
     * @return The loaded PickupDelivery object.
     */
    public PickupDelivery loadRequests(String filepath) {
        this.pickupDelivery = xmlParsers.parseRequests(filepath);
        return this.pickupDelivery;
    }

    /**
     * Saves the current PickupDelivery (containing requests) to an XML file.
     * @param filepath The path to the XML file to save the requests data.
     */
    public void saveRequests(String filepath) {
        if (pickupDelivery != null) {
            xmlWriters.writeRequests(pickupDelivery, filepath);
        } else {
            System.err.println("Cannot save requests: No requests currently loaded.");
        }
    }

    /**
     * Adds a new request to the system for a given courier.
     * @param courierId The ID of the courier to assign the request to.
     * @param warehouseId The ID of the warehouse for the request.
     * @param pickupIntersectionId The intersection ID for pickup.
     * @param pickupDuration The duration of the pickup.
     * @param deliveryIntersectionId The intersection ID for delivery.
     * @param deliveryDuration The duration of the delivery.
     */
    public void addRequest(long courierId, long warehouseId, long pickupIntersectionId, Duration pickupDuration,
                           long deliveryIntersectionId, Duration deliveryDuration) {
        // Create a new Request object. Note: Request constructor doesn't take warehouseId and Duration directly.
        // Assuming `pickupDuration` and `deliveryDuration` are just values, not Duration objects in Request constructor.
        // Adjusting to match existing Request constructor: Request(long id, long pickupIntersectionId, long pickupDuration, long deliveryIntersectionId, long deliveryDuration)
        // Need to generate a unique ID for the new request. For now, using a simple timestamp or counter.
        // Let's use a dummy ID for now.
        long newRequestId = System.currentTimeMillis(); // Simple way to get a unique ID

        // The Request model's constructor takes long for durations, not Duration objects.
        // Assuming Duration needs to be converted to long (e.g., in seconds or milliseconds).
        Request newRequest = new Request(newRequestId, pickupIntersectionId, pickupDuration.getSeconds(),
                                         deliveryIntersectionId, deliveryDuration.getSeconds());

        pickupDelivery.addRequest(courierId, newRequest);
        System.out.println("Added request " + newRequestId + " for courier " + courierId);
    }

    /**
     * Gets all requests currently loaded in the system for a specific courier.
     * @param courierId The ID of the courier.
     * @return A list of Request objects for the given courier.
     */
    public List<Request> getRequestsForCourier(long courierId) {
        List<Request> courierRequests = pickupDelivery.getRequestsForCourier(courierId);
        if (courierRequests == null) {
            return new Vector<>(); // Return an empty list if no requests for courier
        }
        return courierRequests;
    }

    public PickupDelivery getPickupDelivery() {
        return pickupDelivery;
    }
}
