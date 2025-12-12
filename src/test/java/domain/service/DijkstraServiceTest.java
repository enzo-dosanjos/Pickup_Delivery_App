package domain.service;


import domain.model.*;
import domain.model.dijkstra.DijkstraTable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link DijkstraService} class.
 * This class tests various scenarios for the DijkstraService, including edge cases such as empty maps,
 * disconnected graphs, and graphs with cycles.
 */
class DijkstraServiceTest {


    private DijkstraService dijkstraService; // The DijkstraService instance under test.


    private Map map; // The map containing intersections and road segments.


    private GrapheComplet grapheComplet; // The complete graph representation used for storing the shortest paths costs.

    /**
     * Verifies that computeShortestPath works correctly with an empty map.
     */
    @Test
    void computeShortestPathWithEmptyMap() {
        map = new Map();
        dijkstraService = new DijkstraService(map);

        grapheComplet = dijkstraService.computeShortestPath(new long[0]);

        assertEquals(0, grapheComplet.getNbSommets());
    }

    /**
     * Verifies that computeShortestPath works correctly with a single intersection.
     */
    @Test
    void computeShortestPathWithSingleIntersection() {
        map = new Map();
        Intersection intersection = new Intersection(1L, 0.0, 0.0);
        long[] intersectionsIds = {1L};
        map.addIntersection(intersection);
        dijkstraService = new DijkstraService(map);

        grapheComplet = dijkstraService.computeShortestPath(intersectionsIds);

        assertEquals(0, grapheComplet.getCout(0, 0));
    }

    /**
     * Verifies that computeShortestPath handles disconnected graphs correctly.
     */
    @Test
    void computeShortestPathWithDisconnectedGraph() {
        map = new Map();
        Intersection intersection = new Intersection(1L, 0.0, 0.0);
        Intersection intersection2 = new Intersection(2L, 10.0, 20.0);
        long[] intersectionsIds = {1L, 2L};
        map.addIntersection(intersection2);
        map.addIntersection(intersection);
        dijkstraService = new DijkstraService(map);

        grapheComplet = dijkstraService.computeShortestPath(intersectionsIds);

        assertEquals(0, grapheComplet.getCout(0, 0));
        assertEquals(0, grapheComplet.getCout(1, 1));
        assertEquals(Double.MAX_VALUE, grapheComplet.getCout(0, 1));
        assertEquals(Double.MAX_VALUE, grapheComplet.getCout(1, 0));
    }

    /**
     * Verifies that computeShortestPath works correctly with a connected graph.
     */
    @Test
    void computeShortestPathWithConnectedGraph() {
        map = new Map();
        Intersection intersection = new Intersection(1L, 0.0, 0.0);
        Intersection intersection2 = new Intersection(2L, 10.0, 20.0);
        long[] intersectionsIds = {1L, 2L};
        map.addIntersection(intersection2);
        map.addIntersection(intersection);
        RoadSegment roadSegment = new RoadSegment("Jean Paul", 5, 2L, 1L);
        map.addRoadSegment(2L, roadSegment);
        dijkstraService = new DijkstraService(map);

        grapheComplet = dijkstraService.computeShortestPath(intersectionsIds);

        assertEquals(5*60.0/15.0/1000.0, grapheComplet.getCout(1, 0));
        assertEquals(Double.MAX_VALUE, grapheComplet.getCout(0, 1));
    }

    /**
     * Verifies that computeShortestPath handles graphs with cycles correctly.
     */
    @Test
    void computeShortestPathWithCycleInGraph() {
        map = new Map();
        Intersection intersection = new Intersection(1L, 0.0, 0.0);
        Intersection intersection2 = new Intersection(2L, 10.0, 20.0);
        Intersection intersection3 = new Intersection(3L, 15.0, 25.0);
        long[] intersectionsIds = {1L, 2L, 3L};
        map.addIntersection(intersection);
        map.addIntersection(intersection2);
        map.addIntersection(intersection3);
        RoadSegment roadSegment = new RoadSegment("Jean Paul", 5, 1L, 2L);
        RoadSegment roadSegment2 = new RoadSegment("Jean Fred", 4, 2L, 3L);
        RoadSegment roadSegment3 = new RoadSegment("Jean Marc", 2, 3L, 1L);
        map.addRoadSegment(1L, roadSegment);
        map.addRoadSegment(2L, roadSegment2);
        map.addRoadSegment(3L, roadSegment3);
        dijkstraService = new DijkstraService(map);

        grapheComplet = dijkstraService.computeShortestPath(intersectionsIds);

        assertEquals(5*60.0/15.0/1000.0, grapheComplet.getCout(0, 1),0.0001);
        assertEquals(9*60.0/15.0/1000.0, grapheComplet.getCout(0, 2),0.0001);
        assertEquals(6*60.0/15.0/1000.0, grapheComplet.getCout(1, 0),0.0001);
        assertEquals(4*60.0/15.0/1000.0, grapheComplet.getCout(1, 2),0.0001);
        assertEquals(2*60.0/15.0/1000.0, grapheComplet.getCout(2, 0),0.0001);
        assertEquals(7*60.0/15.0/1000.0, grapheComplet.getCout(2, 1),0.0001);
    }

    /**
     * Verifies that computeShortestPath handles cases where the map contains more intersections than the graph.
     */
    @Test
    void computeShortestPathWithMapBiggerThanGraph() {
        map = new Map();
        Intersection intersection = new Intersection(1L, 0.0, 0.0);
        Intersection intersection2 = new Intersection(2L, 10.0, 20.0);
        Intersection intersection3 = new Intersection(3L, 15.0, 25.0);
        Intersection intersection4 = new Intersection(4L, 30.0, 40.0);
        long[] intersectionsIds = {1L, 2L, 3L};
        map.addIntersection(intersection);
        map.addIntersection(intersection2);
        map.addIntersection(intersection3);
        map.addIntersection(intersection4);
        RoadSegment roadSegment = new RoadSegment("Jean Paul", 5, 1L, 2L);
        RoadSegment roadSegment2 = new RoadSegment("Jean Fred", 4, 2L, 3L);
        RoadSegment roadSegment3 = new RoadSegment("Jean Marc", 2, 3L, 1L);
        RoadSegment roadSegment4 = new RoadSegment("Jean Luc", 10, 1L, 4L);
        RoadSegment roadSegment5 = new RoadSegment("Jean Pierre", 3, 4L, 2L);
        map.addRoadSegment(1L, roadSegment);
        map.addRoadSegment(2L, roadSegment2);
        map.addRoadSegment(3L, roadSegment3);
        map.addRoadSegment(1L, roadSegment4);
        map.addRoadSegment(4L, roadSegment5);
        dijkstraService = new DijkstraService(map);

        grapheComplet = dijkstraService.computeShortestPath(intersectionsIds);

        assertEquals(5*60.0/15.0/1000.0, grapheComplet.getCout(0, 1),0.0001);
        assertEquals(9*60.0/15.0/1000.0, grapheComplet.getCout(0, 2),0.0001);
        assertEquals(6*60.0/15.0/1000.0, grapheComplet.getCout(1, 0),0.0001);
        assertEquals(4*60.0/15.0/1000.0, grapheComplet.getCout(1, 2),0.0001);
        assertEquals(2*60.0/15.0/1000.0, grapheComplet.getCout(2, 0),0.0001);
        assertEquals(7*60.0/15.0/1000.0, grapheComplet.getCout(2, 1),0.0001);
    }

    /**
     * Verifies that DijkstraService initializes the Dijkstra table correctly.
     */
    @Test
    void dijkstraServiceInitializesDijkstraTableCorrectly() {
        map = new Map();
        Intersection intersection1 = new Intersection(1L, 0.0, 0.0);
        Intersection intersection2 = new Intersection(2L, 10.0, 20.0);
        map.addIntersection(intersection1);
        map.addIntersection(intersection2);
        dijkstraService = new DijkstraService(map);

        DijkstraTable dijkstraTable = dijkstraService.getDijkstraTable();

        assertEquals(0, dijkstraTable.get(1L, 1L).getDuration());
        assertEquals(Double.MAX_VALUE, dijkstraTable.get(1L, 2L).getDuration());
        assertEquals(Double.MAX_VALUE, dijkstraTable.get(2L, 1L).getDuration());
        assertEquals(0, dijkstraTable.get(2L, 2L).getDuration());
    }


    /**
     * Verifies that Dijkstra updates the shortest path when a shorter path is found.
     */
    @Test
    void dijkstraUpdatesShortestPathWhenShorterPathFound() {
        map = new Map();
        Intersection intersection1 = new Intersection(1L, 0.0, 0.0);
        Intersection intersection2 = new Intersection(2L, 10.0, 20.0);
        Intersection intersection3 = new Intersection(3L, 15.0, 25.0);
        map.addIntersection(intersection1);
        map.addIntersection(intersection2);
        map.addIntersection(intersection3);
        RoadSegment segment1 = new RoadSegment("Road1", 5, 1L, 2L);
        RoadSegment segment2 = new RoadSegment("Road2", 2, 2L, 3L);
        RoadSegment segment3 = new RoadSegment("Road3", 10, 1L, 3L);
        map.addRoadSegment(1L, segment1);
        map.addRoadSegment(2L, segment2);
        map.addRoadSegment(3L, segment3);
        long[] intersectionsIds = {1L, 2L, 3L};
        dijkstraService = new DijkstraService(map);

        grapheComplet = dijkstraService.computeShortestPath(intersectionsIds);

        assertEquals(7 * 60.0 / 15.0 / 1000.0, grapheComplet.getCout(0, 2), 0.0001);
    }
}