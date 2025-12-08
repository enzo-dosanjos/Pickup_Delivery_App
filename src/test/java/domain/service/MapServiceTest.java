package domain.service;

import domain.model.Intersection;
import domain.model.Map;
import domain.model.RoadSegment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link MapService} class.
 */
class MapServiceTest {
    /**
     * Verifies that loading a map from an XML file correctly updates intersections and adjacency list.
     */
    @Test
    void checkLoadMapUpdatesIntersectionsAndAdjacencyList() {
        MapService mapService = new MapService();

        Intersection i1 = new Intersection(1L, 45.0, 4.0);
        Intersection i2 = new Intersection(2L, 45.2, 4.2);
        mapService.getMap().addIntersection(i1);
        mapService.getMap().addIntersection(i2);

        RoadSegment s1 = new RoadSegment("test road", 10.0, 1L, 2L);
        mapService.getMap().addRoadSegment(1L, s1);

        assertEquals(2, mapService.getMap().getIntersections().size(), "Precondition: Map should have 2 intersections");
        assertEquals(1, mapService.getMap().getAdjencyList().size(), "Precondition: Map should have 1 adjacency entry");

        mapService.loadMap("src/test/resources/empty_test_map.xml");

        assertTrue(mapService.getMap().getIntersections().isEmpty(), "Intersections should be updated");
        assertTrue(mapService.getMap().getAdjencyList().isEmpty(), "Adjacency list should be updated");
    }
}