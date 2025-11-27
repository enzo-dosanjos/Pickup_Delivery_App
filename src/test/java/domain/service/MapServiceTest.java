package domain.service;

import domain.model.Map;
import org.junit.jupiter.api.Test;
import persistence.XMLParsers;
import persistence.XMLWriters;

import static org.junit.jupiter.api.Assertions.*;

class MapServiceTest {

    @Test
    void checkConstructorLoadsMap() {
        String filePath = "src/test/resources/testMap.xml";

        MapService mapService = new MapService(new XMLParsers(), new XMLWriters());
        mapService.loadMap(filePath);

        assertNotNull(mapService.getMap(), "The map should be loaded in the constructor");
    }

    @Test
    void checkGetMapReturnsCurrentMap() {
        String filePath = "src/test/resources/testMap.xml";
        MapService mapService = new MapService(new XMLParsers(), new XMLWriters());
        mapService.loadMap(filePath);

        Map map = mapService.getMap();

        assertNotNull(map);
    }
}