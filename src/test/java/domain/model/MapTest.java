package domain.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
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
        Intersection i2 = new Intersection(2L, 45.2, 4.2);
        map.addIntersection(i1);
        map.addIntersection(i2);

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
        Intersection i2 = new Intersection(2L, 45.2, 4.2);
        Intersection i3 = new Intersection(3L, 45.4, 4.4);
        map.addIntersection(i1);
        map.addIntersection(i2);
        map.addIntersection(i3);

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
        Intersection i2 = new Intersection(2L, 45.2, 4.2);
        Intersection i3 = new Intersection(3L, 45.4, 4.4);
        map.addIntersection(i1);
        map.addIntersection(i2);
        map.addIntersection(i3);

        RoadSegment s1 = new RoadSegment("test road 1", 10.0, 1L, 2L);
        RoadSegment s2 = new RoadSegment("test road 2", 20.0, 1L, 3L);

        map.addRoadSegment(1L, s1);
        map.addRoadSegment(1L, s2);

        RoadSegment found = map.getRoadSegment(1L, 3L);
        assertSame(s2, found);

        RoadSegment notFound = map.getRoadSegment(1L, 999L);
        assertNull(notFound);
    }

    @Test
    void chechGetRoadSegmentByPartialName() {
        Map map = new Map();
        Intersection i1 = new Intersection(1L, 45.0, 4.0);
        Intersection i2 = new Intersection(2L, 45.2, 4.2);
        Intersection i3 = new Intersection(3L, 45.4, 4.4);
        Intersection i4 = new Intersection(4L, 45.6, 4.6);
        Intersection i5 = new Intersection(5L, 45.8, 4.8);
        map.addIntersection(i1);
        map.addIntersection(i2);
        map.addIntersection(i3);
        map.addIntersection(i4);
        map.addIntersection(i5);

        RoadSegment s1 = new RoadSegment("Main Street", 10.0, 1L, 2L);
        RoadSegment s2 = new RoadSegment("Main Avenue", 20.0, 2L, 5L);
        RoadSegment s3 = new RoadSegment("Main Boulevard", 15.0, 4L, 5L);
        RoadSegment s4 = new RoadSegment("Second Avenue", 20.0, 1L, 3L);

        map.addRoadSegment(1L, s1);
        map.addRoadSegment(2L, s2);
        map.addRoadSegment(4L, s3);
        map.addRoadSegment(1L, s4);

        ArrayList<RoadSegment> found = map.getRoadSegmentByName("Main");

        assertEquals(3, found.size());
        assertSame(s1, found.get(0));
        assertSame(s2, found.get(1));
        assertSame(s3, found.get(2));

        ArrayList<RoadSegment> notFound = map.getRoadSegmentByName("Road");

        assertEquals(0, notFound.size());
    }

    @Test
    void checkAddRoadSegmentFailsWhenStartIdDoesNotMatch() {
        Map map = new Map();
        Intersection i1 = new Intersection(1L, 45.0, 4.0);
        map.addIntersection(i1);

        RoadSegment segment = new RoadSegment("test road", 10.0, 2L, 3L);

        boolean result = map.addRoadSegment(1L, segment);

        assertFalse(result, "addRoadSegment should fail when startId does not match");
        assertTrue(map.getAdjencyList().isEmpty(), "No segment should be added");
    }

    @Test
    void checkAddRoadSegmentFailsWhenEndIntersectionUnknown() {
        Map map = new Map();
        Intersection i1 = new Intersection(1L, 45.0, 4.0);
        map.addIntersection(i1);

        RoadSegment segment = new RoadSegment("test road", 10.0, 1L, 2L);

        boolean result = map.addRoadSegment(1L, segment);

        assertFalse(result, "addRoadSegment should fail when end intersection is unknown");
        assertTrue(map.getAdjencyList().isEmpty(), "No segment should be added");
    }
}