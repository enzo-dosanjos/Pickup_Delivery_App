package ihm.controller;

import domain.model.*;
import domain.service.RequestService;
import domain.service.TourService;

import java.time.Duration;
import java.util.Map;

public class TourController {

    private final TourService tourService;
    private final RequestService requestService;

    public TourController(TourService tourService, RequestService requestService) {
        this.tourService = tourService;
        this.requestService = requestService;
    }

    public boolean addCourier(long id, String name, Duration shiftDuration) {
        Courier courier = new Courier(id, name, shiftDuration);

        return tourService.addCourier(courier);
    }

    public boolean removeCourier(long id) {
        return tourService.removeCourier(id);
    }

    public void updateRequestOrder(long requestBeforeId, long requestAfterId, long courierId) {
        tourService.updateRequestOrder(requestBeforeId, requestAfterId, courierId);
    }

    public Map.Entry<Request, StopType> showRequestDetails(long instersectionId) {
        return requestService.getPickupDelivery().findRequestByIntersectionId(instersectionId);  // todo: create a service method
    }
}
