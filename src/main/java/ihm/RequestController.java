package ihm;

import domain.model.Request;
import domain.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/request")
public class RequestController {

    private final RequestService requestService;

    @Autowired
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping("/add")
    public void addRequest(@RequestParam Long warehouseId,
                           @RequestParam Long pickupIntersectionId,
                           @RequestParam long pickupDuration,
                           @RequestParam Long deliveryIntersectionId,
                           @RequestParam long deliveryDuration,
                           @RequestParam Long courierId) {
        requestService.addRequest(courierId, warehouseId, pickupIntersectionId, Duration.ofSeconds(pickupDuration),
                                  deliveryIntersectionId, Duration.ofSeconds(deliveryDuration));
    }

    @PostMapping("/load")
    public void loadRequests(@RequestParam String filepath) {
        requestService.loadRequests(filepath);
    }

    @PostMapping("/save")
    public void saveRequests(@RequestParam String filepath) {
        requestService.saveRequests(filepath);
    }
}
