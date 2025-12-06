package ihm.controller;

import domain.model.Map;
import domain.service.MapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MapController {

    private final MapService mapService;

    @Autowired
    public MapController(MapService mapService) {
        this.mapService = mapService;
        mapService.loadMap("src/main/resources/grandPlan.xml");
    }

    @RequestMapping("/api/map")
    public Map getMap() {
        return mapService.getMap();
    }
}
