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
    private final TourService tourService;

    /**
     * Constructs a RequestController with the specified request and tour services.
     *
     * @param requestService the service responsible for managing requests
     * @param planningService the service responsible for managing tours calculations
     */
    @Autowired
    public RequestController(RequestService requestService, PlanningService planningService, TourService tourService) {
        this.requestService = requestService;
        this.planningService = planningService;
        this.tourService = tourService;
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveRequests(@RequestParam String filepath) {
        requestService.saveRequests(filepath);
        return ResponseEntity.ok().build();
    }

    /**
     * Loads requests from a file specified by the given file path.
     *
     * @param filepath the path to the file containing the requests
     */
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
    public ResponseEntity<?> addRequest(@RequestParam Long warehouseId,
                                        @RequestParam long pickupIntersectionId,
                                        @RequestParam long pickupDurationInSeconds,
                                        @RequestParam long deliveryIntersectionId,
                                        @RequestParam long deliveryDurationInSeconds,
                                        @RequestParam Long courierId) {
        // check if courier exists
        if (!planningService.courierExists(courierId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Courier ID " + courierId + " does not exist.");
        }

        // Ensure warehouse is registered (when coming from manual add and no XML was loaded)
        if (requestService.getPickupDelivery().getWarehouseAddressId() == -1) {
            requestService.setWarehouseAddress(warehouseId);
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
        return requestService.getPickupDelivery().getWarehouseAddressId();
    }

    /**
     * Deletes a request with the specified ID from the system.
     *
     * @param requestId the ID of the request to be deleted
     * @param courierId the ID of the courier associated with the request
     */
    @PostMapping("/delete")
    public ResponseEntity<?> deleteRequest(@RequestParam long requestId,
                                           @RequestParam long courierId) {
        Request originalRequest = requestService.getRequestById(requestId);
        if (originalRequest == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Request with ID " + requestId + " not found.");
        }

        // 1. Speculatively delete the request
        requestService.deleteRequest(courierId, requestId);

        try {
            // 2. Attempt to recompute the tour with the request deleted
            planningService.recomputeTourForCourier(courierId);
            // If recomputation succeeds, the deletion is confirmed.
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            // 3. If recomputation fails, roll back the deletion by re-adding the request
            requestService.addRequest(courierId, originalRequest);
            // And revert tour to its previous state
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Deleting this request would lead to an unplannable tour for courier " + courierId + ". Deletion aborted. Error: " + e.getMessage());
        }
    }

    private ResponseEntity<?> recomputeTourAndHandleExceptions(long courierId) {
        try {
            planningService.recomputeTourForCourier(courierId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
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
