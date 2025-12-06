package ihm.controller;


import domain.model.*;
import domain.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/request")
public class RequestController {

    private final RequestService requestService;
    private final PlanningService planningService;

    @Autowired
    public RequestController(RequestService requestService, PlanningService planningService) {
        this.requestService = requestService;
        this.planningService = planningService;
    }

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

    @GetMapping("/warehouse")
    public long getWarehouseAddress() {
        return requestService.getPickupDelivery().getWarehouseAdressId();
    }

    @PostMapping("/delete")
    public void deleteRequest(@RequestParam long requestId,
                              @RequestParam long courierId) {
        requestService.deleteRequest(requestId, courierId);
        planningService.recomputeTourForCourier(courierId);
    }
}
