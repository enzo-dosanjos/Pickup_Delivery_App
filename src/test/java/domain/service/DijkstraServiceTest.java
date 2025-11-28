package domain.service;


import domain.model.GrapheComplet;
import domain.model.Intersection;
import domain.model.Map;
import domain.model.RoadSegment;
import domain.utils.DurationUtil;
import org.junit.jupiter.api.Test;

import domain.service.DijkstraService;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class DijkstraServiceTest {

    private DijkstraService dijkstraService;
    private Map map;
    private GrapheComplet grapheComplet;


    @Test
    void computeShortestPathWithEmptyMap() {
        map = new Map();
        grapheComplet = new GrapheComplet(0);
        dijkstraService = new DijkstraService(map, grapheComplet);

        dijkstraService.computeShortestPath();

        assertEquals(0, grapheComplet.getNbSommets());
    }

    @Test
    void computeShortestPathWithSingleIntersection() {
        map = new Map();
        Intersection intersection = new Intersection(1L, 0.0, 0.0);
        long[] intersectionsIds = {1L};
        map.addIntersection(intersection);
        grapheComplet = new GrapheComplet(intersectionsIds, 1);
        dijkstraService = new DijkstraService(map, grapheComplet);

        dijkstraService.computeShortestPath();

        assertEquals(0, grapheComplet.getCout(0, 0));
    }

    @Test
    void computeShortestPathWithDisconnectedGraph() {
        map = new Map();
        Intersection intersection = new Intersection(1L, 0.0, 0.0);
        Intersection intersection2 = new Intersection(2L, 10.0, 20.0);
        long[] intersectionsIds = {1L, 2L};
        map.addIntersection(intersection2);
        map.addIntersection(intersection);
        grapheComplet = new GrapheComplet(intersectionsIds, 2);
        dijkstraService = new DijkstraService(map, grapheComplet);


        dijkstraService.computeShortestPath();

        assertEquals(Double.MAX_VALUE, grapheComplet.getCout(0, 1));
        assertEquals(Double.MAX_VALUE, grapheComplet.getCout(1, 0));
    }

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
        grapheComplet = new GrapheComplet(intersectionsIds, 2);
        dijkstraService = new DijkstraService(map, grapheComplet);

        dijkstraService.computeShortestPath();

        assertEquals(5*60.0/15.0/1000.0, grapheComplet.getCout(1, 0));
        assertEquals(Double.MAX_VALUE, grapheComplet.getCout(0, 1));
    }

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
        grapheComplet = new GrapheComplet(intersectionsIds, 3);
        dijkstraService = new DijkstraService(map, grapheComplet);


        dijkstraService.computeShortestPath();

        assertEquals(5*60.0/15.0/1000.0, grapheComplet.getCout(0, 1),0.0001);
        assertEquals(9*60.0/15.0/1000.0, grapheComplet.getCout(0, 2),0.0001);
        assertEquals(6*60.0/15.0/1000.0, grapheComplet.getCout(1, 0),0.0001);
        assertEquals(4*60.0/15.0/1000.0, grapheComplet.getCout(1, 2),0.0001);
        assertEquals(2*60.0/15.0/1000.0, grapheComplet.getCout(2, 0),0.0001);
        assertEquals(7*60.0/15.0/1000.0, grapheComplet.getCout(2, 1),0.0001);
    }

    @Test
    void computeShortestPathWithMapBiggerThanGraph() {
        // Setup a map with 4 intersections but only 3 in the graph (take the same as previous test but add one more intersection and two road segments to connect it)
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
        grapheComplet = new GrapheComplet(intersectionsIds, 3);
        dijkstraService = new DijkstraService(map, grapheComplet);


        dijkstraService.computeShortestPath();

        assertEquals(5*60.0/15.0/1000.0, grapheComplet.getCout(0, 1),0.0001);
        assertEquals(9*60.0/15.0/1000.0, grapheComplet.getCout(0, 2),0.0001);
        assertEquals(6*60.0/15.0/1000.0, grapheComplet.getCout(1, 0),0.0001);
        assertEquals(4*60.0/15.0/1000.0, grapheComplet.getCout(1, 2),0.0001);
        assertEquals(2*60.0/15.0/1000.0, grapheComplet.getCout(2, 0),0.0001);
        assertEquals(7*60.0/15.0/1000.0, grapheComplet.getCout(2, 1),0.0001);
    }
}

