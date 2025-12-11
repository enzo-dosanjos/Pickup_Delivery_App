package domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link RoadSegment} class.
 */
class RoadSegmentTest {

    /**
     * Verifies the constructor and getter methods of the {@link RoadSegment} class.
     * Ensures that all fields are correctly initialized and retrievable.
     */
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

    /**
     * Verifies that the toString method returns a string containing all relevant details of the road segment.
     */
    @Test
    void toStringIncludesAllDetails() {
        String name = "Main Street";
        double length = 250.75;
        long startId = 10L;
        long endId = 20L;

        RoadSegment segment = new RoadSegment(name, length, startId, endId);

        String result = segment.toString();

        assertTrue(result.contains("RoadSegment{name='" + name + "'"));
        assertTrue(result.contains("length=" + length));
        assertTrue(result.contains("startId=" + startId));
        assertTrue(result.contains("endId=" + endId));
    }

}