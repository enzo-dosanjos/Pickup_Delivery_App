package ihm.controller;

import domain.model.*;
import domain.service.PlanningService;
import domain.service.RequestService;
import domain.service.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Controller class for managing tours and couriers.
 * This class acts as an intermediary between the user interface and the service layer,
 * handling operations related to tours and couriers.
 */
@RestController
@RequestMapping("/api/tour")
public class TourController {

    private final PlanningService planningService; // The service responsible for managing tours calculations.

    private final TourService tourService; // The service responsible for managing tours and couriers.

    private final RequestService requestService; // The service responsible for managing requests.

    /**
     * Constructs a TourController with the specified tour and request services.
     *
     * @param tourService the service responsible for managing tours and couriers
     * @param requestService the service responsible for managing requests
     */
    @Autowired
    public TourController(TourService tourService, RequestService requestService, PlanningService planningService) {
        this.tourService = tourService;
        this.requestService = requestService;
        this.planningService = planningService;
    }

    /**
     * Adds a new courier to the system with the specified details.
     *
     * @param id the ID of the courier
     * @param name the name of the courier
     * @param shiftDurationInSeconds the duration of the courier's shift in seconds
     * @return true if the courier was added successfully, false otherwise
     */
    @PostMapping("/add-courier")
    public boolean addCourier(@RequestParam long id,
                              @RequestParam String name,
                              @RequestParam long shiftDurationInSeconds) {
        Duration shiftDuration = Duration.ofSeconds(shiftDurationInSeconds);
        Courier courier = new Courier(id, name, shiftDuration);

        return tourService.addCourier(courier);
    }

    /**
     * Removes a courier from the system based on the specified courier ID.
     *
     * @param courierId the ID of the courier to be removed
     * @return true if the courier was removed successfully, false otherwise
     */
    @PostMapping("/remove-courier")
    public boolean removeCourier(@RequestParam long courierId) {
        return tourService.removeCourier(courierId);
    }

    /**
     * Loads couriers from a file specified by the given file path.
     *
     * @param filepath the path to the file containing the couriers
     */
    @PostMapping("/load-couriers")
    public void loadCouriers(@RequestParam String filepath) {
        tourService.loadCouriers(filepath);
    }




    /**
     * Updates the order of stops for a courier's tour and recomputes the tour.
     * If the update is invalid (e.g., involves the warehouse or stops from the same request),
     * an appropriate error response is returned.
     *
     * @param courierId The ID of the courier whose stop order is being updated.
     * @param precStopIndex The index of the preceding stop in the tour.
     * @param followingStopIndex The index of the following stop in the tour.
     * @return A ResponseEntity indicating the result of the operation:
     *         - 200 OK if the update and recomputation succeed.
     *         - 400 BAD REQUEST if the update is invalid.
     *         - 409 CONFLICT if an error occurs during tour recomputation.
     */
    @PostMapping("/update-stop-order")
    public ResponseEntity<?> updateStopOrder(@RequestParam long courierId,
                                             @RequestParam Integer precStopIndex,
                                             @RequestParam Integer followingStopIndex) {
        try {
            // Updating Stops order
            tourService.updateStopOrder(courierId, precStopIndex, followingStopIndex);
            // Recomputing tour
            planningService.recomputeTourForCourier(courierId);

            // If no exceptions arose
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            // Validation exceptions (ex: warehouse or same request)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Impossible to update stops order : " + e.getMessage());
        } catch (RuntimeException e) {
            // PlanningService exceptions (recomputeTourForCourier)
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Error when recomputing tour for courier " + courierId + ". " +
                            "Tour update cancelled. Details : " + e.getMessage());
        }
    }

    /**
     * Shows the details of a request based on the specified intersection ID.
     *
     * @param instersectionId the intersection ID of the request
     * @return a map entry containing the request and its stop type
     */
    @PostMapping("/show-request-details")
    public Map.Entry<Request, StopType> showRequestDetails(@RequestParam long instersectionId) {
        return requestService.getPickupDelivery().findRequestByIntersectionId(instersectionId);  // todo: create a service method
    }

    @GetMapping("/tours")
    public Map<Long, Tour> getTours() {
        return tourService.getTours();
    }

    @GetMapping("/available-couriers")
    public List<Courier> getAvailableCouriers() {
        return tourService.getAvailableCouriers();
    }
}
