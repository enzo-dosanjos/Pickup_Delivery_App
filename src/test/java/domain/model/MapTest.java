package domain.model;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

class MapTest {

    @Test
    void checkAddSameIntersectionMultipleTimes() {
        Map map = new Map();
        Intersection i1 = new Intersection(1L, 45.0, 4.0);

        boolean firstAdd = map.addIntersection(i1);
        boolean secondAdd = map.addIntersection(i1);

        assertTrue(firstAdd, "Adding a new id should return true");
        assertFalse(secondAdd, "Adding the same id should return false");

        TreeMap<Long, Intersection> intersections = map.getIntersections();
        assertEquals(1, intersections.size());
        assertSame(i1, intersections.get(1L));
    }

    @Test
    void checkAddRoadSegmentFailsIfIntersectionUnknown() {
        Map map = new Map();
        RoadSegment segment = new RoadSegment("test road", 10.0, 1L, 2L);

        boolean result = map.addRoadSegment(1L, segment);

        assertFalse(result);
        assertTrue(map.getAdjencyList().isEmpty());
    }

    @Test
    void checkAddRoadSegmentWorksWhenIntersectionExists() {
        Map map = new Map();
        Intersection i1 = new Intersection(1L, 45.0, 4.0);
        map.addIntersection(i1);

        RoadSegment s1 = new RoadSegment("test road", 10.0, 1L, 2L);
        boolean result = map.addRoadSegment(1L, s1);

        assertTrue(result);

        HashMap<Long, RoadSegment[]> adj = map.getAdjencyList();
        assertTrue(adj.containsKey(1L));
        assertEquals(1, adj.get(1L).length);
        assertSame(s1, adj.get(1L)[0]);
    }

    @Test
    void checkAddMultipleRoadSegments() {
        Map map = new Map();
        Intersection i1 = new Intersection(1L, 45.0, 4.0);
        map.addIntersection(i1);

        RoadSegment s1 = new RoadSegment("test road 1", 10.0, 1L, 2L);
        RoadSegment s2 = new RoadSegment("test road 2", 20.0, 1L, 3L);

        map.addRoadSegment(1L, s1);
        map.addRoadSegment(1L, s2);

        RoadSegment[] segments = map.getAdjencyList().get(1L);
        assertEquals(2, segments.length);
        assertSame(s1, segments[0]);
        assertSame(s2, segments[1]);
    }

    @Test
    void checkGetRoadSegmentReturnCorrectSegment() {
        Map map = new Map();
        Intersection i1 = new Intersection(1L, 45.0, 4.0);
        map.addIntersection(i1);

        RoadSegment s1 = new RoadSegment("test road 1", 10.0, 1L, 2L);
        RoadSegment s2 = new RoadSegment("test road 2", 20.0, 1L, 3L);

        map.addRoadSegment(1L, s1);
        map.addRoadSegment(1L, s2);

        RoadSegment found = map.getRoadSegment(1L, 3L);
        assertSame(s2, found);

        RoadSegment notFound = map.getRoadSegment(1L, 999L);
        assertNull(notFound);
    }
}