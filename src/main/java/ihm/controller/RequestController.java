package ihm.controller;

import domain.model.Courier;
import domain.model.Request;
import domain.service.PlanningService;
import domain.service.RequestService;
import domain.service.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Controller class for managing requests and tours.
 * This class acts as an intermediary between the user interface and the service layer,
 * handling operations related to requests and tours.
 */
@RestController
@RequestMapping("/api/request")
public class RequestController {

    private final RequestService requestService; // The service responsible for managing requests.
    private final PlanningService planningService; // The service responsible for managing tours calculations.
    private final TourService tourService; // The service responsible for managing tours.

    /**
     * Constructs a RequestController with the specified request and tour services.
     *
     * @param requestService the service responsible for managing requests
     * @param planningService the service responsible for managing tours calculations
     * @param tourService the service responsible for managing tours
     */
    @Autowired
    public RequestController(RequestService requestService, PlanningService planningService, TourService tourService) {
        this.requestService = requestService;
        this.planningService = planningService;
        this.tourService = tourService;
    }

    /**
     * Saves the current requests to a file specified by the given file path.
     *
     * @param filepath the path to the file where the requests will be saved
     */
    @PostMapping("/save")
    public ResponseEntity<?> saveRequests(@RequestParam String filepath,
                                          @RequestParam long courierId) {
        requestService.saveRequests(filepath, courierId);
        return ResponseEntity.ok().build();
    }

    /**
     * Sets the warehouse address for the specified courier.
     *
     * @param warehouseId the ID of the warehouse intersection
     * @param courierId the ID of the courier
     */
    @PostMapping("/addWarehouse")
    public void addWarehouse(@RequestParam long warehouseId,
                             @RequestParam long courierId) {
        if (warehouseId <= 0) {
            throw new IllegalArgumentException("warehouseId must be a positive intersection id.");
        }

        requestService.setWarehouseAddress(warehouseId, courierId);
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Courier ID " + courierId + " does not exist.");        }

        // Load requests from the specified file for the given courier
        if (!requestService.loadRequests(filepath, courierId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("The request at " + filepath + " does not have the same warehouse as the courier");
        }

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

        long currentWarehouseId = requestService.getPickupDeliveryForCourier(courierId).getWarehouseAddressId();
        if (currentWarehouseId == -1) {
            if (warehouseId == null || warehouseId <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Warehouse is not set. Set it via /api/request/addWarehouse or provide a valid warehouseId.");
            }
            requestService.getPickupDeliveryForCourier(courierId).setWarehouseAddressId(warehouseId);
        } else if (warehouseId != null && warehouseId > 0 && warehouseId != currentWarehouseId) {
            requestService.getPickupDeliveryForCourier(courierId).setWarehouseAddressId(warehouseId);
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

        //update precedences
        planningService.updatePrecedences(courierId, newRequest);

        // Recompute the tour for the courier
        ResponseEntity<?> response = recomputeTourAndHandleExceptions(courierId);

        // Delete request if the tour computation failed
        if (!response.getStatusCode().is2xxSuccessful()) {
            requestService.deleteRequest(courierId, newRequest.getId());
        }

        return response;
    }

    /**
     * Retrieves all warehouse IDs from the system.
     *
     * @return a map of courier IDs to their corresponding warehouse IDs with -1 for couriers without a warehouse
     */
    @GetMapping("/warehouse")
    public Map<Long, Long> getWarehouseAddress() {
        TreeMap<Long, Long> warehouseIds = requestService.getAllWarehouseIds();

        for (Courier courier : tourService.getCouriers()) {
            warehouseIds.putIfAbsent(courier.getId(), -1L);
        }

        return warehouseIds;
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
        Request originalRequest = requestService.getRequestById(requestId, courierId);
        if (originalRequest == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Request with ID " + requestId + " not found.");
        }

        // 1. Speculatively delete the request
        planningService.deletePrecedences(courierId, requestId);
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

    /**
     * Helper method to recompute the tour for a courier and handle exceptions appropriately.
     *
     * @param courierId the ID of the courier whose tour is to be recomputed
     * @return a ResponseEntity indicating the result of the operation
     */
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
