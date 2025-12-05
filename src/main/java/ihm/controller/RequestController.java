package ihm.controller;


import domain.model.*;
import domain.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        requestService.loadRequests(filepath);

        // Recompute tours for the courier
        planningService.recomputeTourForCourier(courierId);
    }

    @PostMapping("/add")
    public void addRequest(@RequestParam Long warehouseId,
                           @RequestParam long pickupIntersectionId,
                           @RequestParam Duration pickupDuration,
                           @RequestParam long deliveryIntersectionId,
                           @RequestParam Duration deliveryDuration,
                           @RequestParam Long courierId) {
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
    @PostMapping("/delete")
    public void deleteRequest(@RequestParam long requestId,
                              @RequestParam long courierId) {
        requestService.deleteRequest(requestId, courierId);
        planningService.recomputeTourForCourier(courierId);
    }
}
