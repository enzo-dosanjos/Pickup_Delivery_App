package ihm.controller;

import domain.model.*;
import domain.service.RequestService;
import domain.service.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Controller class for managing tours and couriers.
 * This class acts as an intermediary between the user interface and the service layer,
 * handling operations related to tours and couriers.
 */
@RestController
@RequestMapping("/api/tour")
public class TourController {

    private final TourService tourService; // The service responsible for managing tours and couriers.

    private final RequestService requestService; // The service responsible for managing requests.

    /**
     * Constructs a TourController with the specified tour and request services.
     *
     * @param tourService the service responsible for managing tours and couriers
     * @param requestService the service responsible for managing requests
     */
    @Autowired
    public TourController(TourService tourService, RequestService requestService) {
        this.tourService = tourService;
        this.requestService = requestService;
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
     * Updates the order of requests for a specific courier.
     *
     * @param requestBeforeId the ID of the request that should come before
     * @param requestAfterId the ID of the request that should come after
     * @param courierId the ID of the courier whose request order is to be updated
     */
    @PostMapping("/update-request-order")
    public void updateRequestOrder(@RequestParam long requestBeforeId,
                                   @RequestParam long requestAfterId,
                                   @RequestParam long courierId) {
        tourService.updateRequestOrder(requestBeforeId, requestAfterId, courierId);
    }

    /**
     * Shows the details of a request based on the specified intersection ID.
     *
     * @param instersectionId the intersection ID of the request
     * @return request details enriched with arrival/departure times when available
     */
    @PostMapping("/show-request-details")
    public ResponseEntity<?> showRequestDetails(@RequestParam long instersectionId) {
        Entry<Request, StopType> requestEntry =
                requestService.getPickupDelivery().findRequestByIntersectionId(instersectionId);

        if (requestEntry == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No request found for intersection " + instersectionId);
        }

        for (Entry<Long, Tour> tourEntry : tourService.getTours().entrySet()) {
            Tour tour = tourEntry.getValue();
            TourStop stop = tour.getStopByIntersectionId(instersectionId);
            if (stop != null) {
                RequestDetailsResponse payload = new RequestDetailsResponse(
                        requestEntry.getKey(),
                        requestEntry.getValue(),
                        stop.getArrivalTime(),
                        stop.getDepartureTime(),
                        tourEntry.getKey()
                );
                return ResponseEntity.ok(payload);
            }
        }

        RequestDetailsResponse payload = new RequestDetailsResponse(
                requestEntry.getKey(),
                requestEntry.getValue(),
                null,
                null,
                -1L
        );
        return ResponseEntity.ok(payload);
    }


    /**
     * Lists all computed tours keyed by courier id.
     * @return current tours managed in memory.
     */
    @GetMapping("/tours")
    public Map<Long, Tour> getTours() {
        return tourService.getTours();
    }

    /**
     * Returns couriers flagged as available (not busy).
     * @return list of available couriers.
     */
    @GetMapping("/available-couriers")
    public List<Courier> getAvailableCouriers() {
        return tourService.getAvailableCouriers();
    }

    /**
     * Persists a courier's tour to an XML file.
     * @param courierId courier whose tour is exported.
     * @param filepath output path for the XML file.
     * @return HTTP 200 on success, 404 if tour missing, 500 if write fails.
     */
    @PostMapping("/save")
    public ResponseEntity<?> saveTour(@RequestParam long courierId,
                                      @RequestParam String filepath) {
        try {
            tourService.exportTour(courierId, filepath);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to export tour: " + e.getMessage());
        }
    }
    /**
     * DTO returned by show-request-details for front-end consumption.
     */
    public static class RequestDetailsResponse {
        private final Request request;
        private final StopType stopType;
        private final LocalDateTime arrivalTime;
        private final LocalDateTime departureTime;
        private final long courierId;

        public RequestDetailsResponse(Request request, StopType stopType,
                                      LocalDateTime arrivalTime, LocalDateTime departureTime,
                                      long courierId) {
            this.request = request;
            this.stopType = stopType;
            this.arrivalTime = arrivalTime;
            this.departureTime = departureTime;
            this.courierId = courierId;
        }

        public Request getRequest() {
            return request;
        }

        public StopType getStopType() {
            return stopType;
        }

        public LocalDateTime getArrivalTime() {
            return arrivalTime;
        }

        public LocalDateTime getDepartureTime() {
            return departureTime;
        }

        public long getCourierId() {
            return courierId;
        }
    }
}
