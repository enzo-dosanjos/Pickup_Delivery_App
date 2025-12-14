package domain.service;

import domain.model.Intersection;
import domain.model.RoadSegment;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
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
        assertEquals(1, mapService.getMap().getAdjacencyList().size(), "Precondition: Map should have 1 adjacency entry");

        mapService.loadMap("src/test/resources/emptyTestMap.xml");

        assertTrue(mapService.getMap().getIntersections().isEmpty(), "Intersections should be updated");
        assertTrue(mapService.getMap().getAdjacencyList().isEmpty(), "Adjacency list should be updated");
    }

    /**
     * Verifies that searchRoadSegmentsByName returns the correct result.
     */
    @Test
    void checkSearchRoadSegmentsByNameReturnsCorrectResult() {
        MapService mapService = new MapService();

        mapService.loadMap("src/test/resources/testMap.xml");

        ArrayList<RoadSegment> result = mapService.searchRoadSegmentsByName("Rue");

        assertEquals(2, result.size());
        assertEquals("Rue Danton", result.get(0).getName());
        assertEquals("Rue de l'Abondance", result.get(1).getName());
    }
}