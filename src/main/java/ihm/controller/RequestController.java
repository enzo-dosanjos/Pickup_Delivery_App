package ihm.controller;

import domain.model.Request;
import domain.service.PlanningService;
import domain.service.RequestService;
import domain.service.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/request")
public class RequestController {

    private final RequestService requestService;
    private final PlanningService planningService;
    
    private final TourService tourService;

    @Autowired
    public RequestController(RequestService requestService, PlanningService planningService, TourService tourService) {
        this.requestService = requestService;
        this.planningService = planningService;
        this.tourService = tourService;
    }

    @PostMapping("/load")
    public ResponseEntity<?> loadRequests(@RequestParam String filepath,
                                          @RequestParam long courierId) {
        // check if courier exists
        if (!planningService.courierExists(courierId)) {
            throw new IllegalArgumentException("Courier ID " + courierId + " does not exist.");
        }

        // Load requests from the specified file for the given courier
        requestService.loadRequests(filepath, courierId);

        return recomputeTourAndHandleExceptions(courierId);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addRequest(@RequestParam Long warehouseId,
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

        return recomputeTourAndHandleExceptions(courierId);
    }

    @GetMapping("/warehouse")
    public long getWarehouseAddress() {
        return requestService.getPickupDelivery().getWarehouseAdressId();
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteRequest(@RequestParam long requestId,
                                           @RequestParam long courierId) {
        requestService.deleteRequest(courierId, requestId);
        return recomputeTourAndHandleExceptions(courierId);
    }

    private ResponseEntity<?> recomputeTourAndHandleExceptions(long courierId) {
        try {
            planningService.recomputeTourForCourier(courierId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            var otherCouriers = tourService.getCouriers().stream()
                    .filter(c -> c.getId() != courierId)
                    .collect(Collectors.toList());

            if (!otherCouriers.isEmpty()) {
                // There are other couriers available, suggest trying another one.
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("No tour found for the current courier. Please select another courier.");
            } else {
                // No other couriers available, the request is rejected.
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body("No tour found and no other couriers are available. The delivery request is rejected.");
            }
        }
    }
}
