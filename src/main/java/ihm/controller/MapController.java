package ihm.controller;

import domain.model.Map;
import domain.service.PlanningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MapController {

    private final PlanningService planningService;

    @Autowired
    public MapController(PlanningService planningService) {
        this.planningService = planningService;
    }

    @RequestMapping("/api/map")
    public Map getMap() {
        return planningService.getMap();
    }
}
