package ihm;

import domain.model.StopType;
import domain.service.TourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tour")
public class TourController {

    private final TourService tourService;

    @Autowired
    public TourController(TourService tourService) {
        this.tourService = tourService;
    }

    @PostMapping("/save")
    public void saveTours(@RequestParam String filepath) {
        tourService.saveTours(filepath);
    }

    @GetMapping("/load")
    public java.util.Collection<domain.model.Tour> loadTours(@RequestParam String filepath) {
        tourService.loadTours(filepath);
        return tourService.getTours().values();
    }

    @PostMapping("/update-num-couriers")
    public void updateNumCouriers(@RequestParam int numCouriers) {
        tourService.updateNumCouriers(numCouriers);
    }

    @PostMapping("/update-request-order")
    public void updateRequestOrder(@RequestParam long requestBeforeId, @RequestParam long requestAfterId, @RequestParam long courierId) {
        tourService.updateRequestOrder(requestBeforeId, requestAfterId, courierId);
    }

    @GetMapping("/show-request-details")
    public void showRequestDetails(@RequestParam long requestId, @RequestParam StopType deliveryOrPickup) {
        // This method is not implemented in the service layer in the diagram,
        // so we will leave it empty for now.
    }
}
