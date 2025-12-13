package ihm.controller;

import domain.model.Map;
import domain.model.RoadSegment;
import domain.service.MapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

/**
 * Controller class for managing map-related operations.
 * This class acts as an intermediary between the user interface and the service layer,
 * handling operations related to the map.
 */
@RestController
public class MapController {

    private final MapService mapService; // Service responsible for managing the map.

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


    @RequestMapping("/api/map")
    public Map getMap() {
        return mapService.getMap();
    }

    @GetMapping("/search")
    public ArrayList<RoadSegment> searchRoadSegments(@RequestParam String name) {
        ArrayList<RoadSegment> segments = mapService.searchRoadSegmentsByName(name);
        return segments;
    }
}
