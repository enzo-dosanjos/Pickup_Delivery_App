package persistence;

import domain.model.Intersection;
import domain.model.Map;
import domain.model.RoadSegment;
import org.junit.jupiter.api.Test;

import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

class XMLParsersTest {

    @Test
    void checkParseMapLoadsIntersectionsAndRoadSegments() {
        String filePath = "src/test/resources/testMap.xml";
        XMLParsers parser = new XMLParsers();

        Map map = parser.parseMap(filePath);

        assertNotNull(map, "The map should not be null after parsing");
        TreeMap<Long, Intersection> intersections = map.getIntersections();
        assertFalse(intersections.isEmpty(), "Intersections should be loaded");
        assertEquals(5, intersections.size(), "There should be 5 intersections loaded");
        assertFalse(map.getAdjencyList().isEmpty(), "Road segments should be loaded");
        assertEquals(2, map.getAdjencyList().size(), "There should be 2 road segments list for 5 intersections");
    }
}