package ihm.controller;

import domain.model.*;
import domain.service.TourService;

import java.time.Duration;
import java.util.Map;

public class TourController {

    private final TourService tourService;
    private final PickupDelivery pickupDelivery;

    public TourController(TourService tourService, PickupDelivery pickupDelivery) {
        this.tourService = tourService;
        this.pickupDelivery = pickupDelivery;
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
        return pickupDelivery.findRequestByIntersectionId(instersectionId);  // todo: create a service method
    }
}
