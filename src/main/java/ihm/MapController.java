package ihm;

import domain.model.Map;
import domain.service.MapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MapController {

    private final MapService mapService;

    @Autowired
    public MapController(MapService mapService) {
        this.mapService = mapService;
    }

    @GetMapping("/api/map")
    public Map getMap() {
        // Load the map from the specified file path
        return mapService.loadMap("src/main/resources/grandPlan.xml");
    }
}
