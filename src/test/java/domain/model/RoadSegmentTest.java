package domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoadSegmentTest {
    @Test
    void checkConstructorAndGetters() {
        String name = "test road";
        double length = 123.45;
        long startId = 1L;
        long endId = 2L;

        RoadSegment segment = new RoadSegment(name, length, startId, endId);

        assertEquals(name, segment.getName());
        assertEquals(length, segment.getLength());
        assertEquals(startId, segment.getStartId());
        assertEquals(endId, segment.getEndId());
    }
}