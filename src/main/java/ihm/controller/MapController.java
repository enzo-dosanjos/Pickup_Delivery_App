package ihm.controller;

import domain.model.Map;
import domain.service.MapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for managing map-related operations.
 * This class acts as an intermediary between the user interface and the service layer,
 * handling operations related to the map.
 */
@RestController
public class MapController {

    private final MapService mapService;

    /**
     * Constructs a MapController with the specified map service.
     *
     * @param mapService the service responsible for managing the map
     */
    @Autowired
    public MapController(MapService mapService) {
        this.mapService = mapService;
        mapService.loadMap("src/main/resources/grandPlan.xml");
    }

    /**
     * Retrieves the current map.
     *
     * @return the current map
     */
    @RequestMapping("/api/map")
    public Map getMap() {
        return mapService.getMap();
    }
}
