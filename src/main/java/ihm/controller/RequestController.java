package ihm.controller;


import domain.model.*;
import domain.model.dijkstra.DijkstraTable;
import domain.service.*;
import persistence.XMLParsers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

public class RequestController {

    private final RequestService requestService;
    private final PlanningService planningService;

    public RequestController(RequestService requestService, PlanningService planningService) {
        this.requestService = requestService;
        this.planningService = planningService;
    }

    public void loadRequests(String filepath, long courierId) {
        requestService.loadRequests(filepath);

        // Recompute tours for the courier
        planningService.recomputeTourForCourier(courierId);
    }

    public void addRequest(Long warehouseId, long pickupIntersectionId, Duration pickupDuration,
                           long deliveryIntersectionId, Duration deliveryDuration, Long courierId) {
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
}
