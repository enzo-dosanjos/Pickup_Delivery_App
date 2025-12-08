package ihm.controller;


import domain.model.*;
import domain.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

/**
 * Controller class for managing requests and tours.
 * This class acts as an intermediary between the user interface and the service layer,
 * handling operations related to requests and tours.
 */
@RestController
@RequestMapping("/api/request")
public class RequestController {

    private final RequestService requestService;
    private final PlanningService planningService;

    /**
     * Constructs a RequestController with the specified request and tour services.
     *
     * @param requestService the service responsible for managing requests
     * @param planningService the service responsible for managing tours calculations
     */
    @Autowired
    public RequestController(RequestService requestService, PlanningService planningService) {
        this.requestService = requestService;
        this.planningService = planningService;
    }

    /**
     * Loads requests from a file specified by the given file path.
     *
     * @param filepath the path to the file containing the requests
     */
    @PostMapping("/load")
    public void loadRequests(@RequestParam String filepath,
                             @RequestParam long courierId) {
        // check if courier exists
        if (!planningService.courierExists(courierId)) {
            throw new IllegalArgumentException("Courier ID " + courierId + " does not exist.");
        }

        // Load requests from the specified file for the given courier
        requestService.loadRequests(filepath, courierId);

        // Recompute tours for the courier
        planningService.recomputeTourForCourier(courierId);
    }

    /**
     * Adds a new request to the system with the specified details.
     *
     * @param warehouseId the ID of the warehouse associated with the request
     * @param pickupIntersectionId the intersection ID for the pickup location
     * @param pickupDurationInSeconds the duration of the pickup in seconds
     * @param deliveryIntersectionId the intersection ID for the delivery location
     * @param deliveryDurationInSeconds the duration of the delivery in seconds
     * @param courierId the ID of the courier assigned to the request
     */
    @PostMapping("/add")
    public void addRequest(@RequestParam Long warehouseId,
                           @RequestParam long pickupIntersectionId,
                           @RequestParam long pickupDurationInSeconds,
                           @RequestParam long deliveryIntersectionId,
                           @RequestParam long deliveryDurationInSeconds,
                           @RequestParam Long courierId) {
        // check if courier exists
        if (!planningService.courierExists(courierId)) {
            throw new IllegalArgumentException("Courier ID " + courierId + " does not exist.");
        }

        // Convert durations from seconds to Duration
        Duration pickupDuration = Duration.ofSeconds(pickupDurationInSeconds);
        Duration deliveryDuration = Duration.ofSeconds(deliveryDurationInSeconds);

        // Build and register the new request
        Request newRequest = new Request(
                pickupIntersectionId,
                pickupDuration,
                deliveryIntersectionId,
                deliveryDuration
        );
        requestService.addRequest(courierId, newRequest);

        // Recompute the tour for the courier
        planningService.recomputeTourForCourier(courierId);
    }

    /**
     * Retrieves the warehouse address ID associated with the pickup and delivery service.
     *
     * @return the warehouse address ID
     */
    @GetMapping("/warehouse")
    public long getWarehouseAddress() {
        return requestService.getPickupDelivery().getWarehouseAdressId();
    }

    /**
     * Deletes a request with the specified ID from the system.
     *
     * @param requestId the ID of the request to be deleted
     * @param courierId the ID of the courier associated with the request
     */
    @PostMapping("/delete")
    public void deleteRequest(@RequestParam long requestId,
                              @RequestParam long courierId) {
        requestService.deleteRequest(courierId, requestId);
        planningService.recomputeTourForCourier(courierId);
    }
}
