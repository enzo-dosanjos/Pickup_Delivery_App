package ihm.controller;

import domain.model.Graphe;
import domain.model.GrapheComplet;
import domain.model.Request;
import domain.service.RequestService;
import domain.service.TSP;
import domain.service.TSP1;
import domain.service.TourService;

import java.time.Duration;

/**
 * Controller class for managing requests and tours.
 * This class acts as an intermediary between the user interface and the service layer,
 * handling operations related to requests and tours.
 */
public class RequestController {

    private final RequestService requestService;
    private final TourService tourService;

    /**
     * Constructs a RequestController with the specified request and tour services.
     *
     * @param requestService the service responsible for managing requests
     * @param tourService the service responsible for managing tours
     */
    public RequestController(RequestService requestService, TourService tourService) {
        this.requestService = requestService;
        this.tourService = tourService;
    }

    /**
     * Loads requests from a file specified by the given file path.
     *
     * @param filepath the path to the file containing the requests
     */
    public void loadRequests(String filepath) {
        requestService.loadRequests(filepath);
    }

    /**
     * Adds a new request to the system with the specified details.
     *
     * @param warehouseId the ID of the warehouse associated with the request
     * @param pickupIntersectionId the intersection ID for the pickup location
     * @param pickupDuration the duration of the pickup
     * @param deliveryIntersectionId the intersection ID for the delivery location
     * @param deliveryDuration the duration of the delivery
     * @param courierId the ID of the courier assigned to the request
     */
    public void addRequest(Long warehouseId, Long pickupIntersectionId, Duration pickupDuration, Long deliveryIntersectionId, Duration deliveryDuration, Long courierId) {
        Request newRequest = new Request(pickupIntersectionId, pickupDuration, deliveryIntersectionId, deliveryDuration);
    }
}