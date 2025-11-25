package domain.service;

import domain.model.Map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MapServiceTest {

    @Test
    void checkConstructorLoadsMap() {
        String filePath = "src/test/resources/testMap.xml";

        MapService mapService = new MapService(filePath);

        assertNotNull(mapService.getMap(), "The map should be loaded in the constructor");
    }

    @Test
    void checkGetMapReturnsCurrentMap() {
        String filePath = "src/test/resources/testMap.xml";
        MapService mapService = new MapService(filePath);

        Map map = mapService.getMap();

        assertNotNull(map);
    }
}