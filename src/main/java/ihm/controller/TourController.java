package ihm.controller;

import domain.model.*;
import domain.service.RequestService;
import domain.service.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/tour")
public class TourController {

    private final TourService tourService;
    private final RequestService requestService;

    @Autowired
    public TourController(TourService tourService, RequestService requestService) {
        this.tourService = tourService;
        this.requestService = requestService;
    }

    @PostMapping("/add-courier")
    public boolean addCourier(@RequestParam long id,
                              @RequestParam String name,
                              @RequestParam long shiftDurationInSeconds) {
        Duration shiftDuration = Duration.ofSeconds(shiftDurationInSeconds);
        Courier courier = new Courier(id, name, shiftDuration);

        return tourService.addCourier(courier);
    }

    @PostMapping("/remove-courier")
    public boolean removeCourier(@RequestParam long id) {
        return tourService.removeCourier(id);
    }

    @PostMapping("/update-request-order")
    public void updateRequestOrder(@RequestParam long requestBeforeId,
                                   @RequestParam long requestAfterId,
                                   @RequestParam long courierId) {
        tourService.updateRequestOrder(requestBeforeId, requestAfterId, courierId);
    }

    @PostMapping("/show-request-details")
    public Map.Entry<Request, StopType> showRequestDetails(@RequestParam long instersectionId) {
        return requestService.getPickupDelivery().findRequestByIntersectionId(instersectionId);  // todo: create a service method
    }
}
