package ihm.controller;

import domain.service.RequestService;
import domain.service.TourService;

public class RequestController {

    private RequestService requestService;
    private TourService tourService;

    public RequestController(RequestService requestService, TourService tourService) {
        this.requestService = requestService;
        this.tourService = tourService;
    }

    public void loadRequests(String filepath) {
        requestService.loadRequests(filepath);
        tourService.assignRequestsToCouriers();
    }
}
