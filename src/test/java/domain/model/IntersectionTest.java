package domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Intersection} class.
 */
class IntersectionTest {

    /**
     * Verifies that the constructor initializes the fields correctly
     * and the getter methods return the expected values.
     */
    @Test
    void checkConstructorAndGetters() {
        long id = 1L;
        double lat = 45.0;
        double lng = 4.0;

        Intersection intersection = new Intersection(id, lat, lng);

        assertEquals(id, intersection.getId());
        assertEquals(lat, intersection.getLat());
        assertEquals(lng, intersection.getLng());
    }

    /**
     * Verifies that the toString method returns the correct string representation.
     */
    @Test
    void toStringReturnsCorrectRepresentation() {
        Intersection intersection = new Intersection(1L, 45.0, 4.0);
        String expected = "Intersection{id=1, lat=45.0, lng=4.0}";

        assertEquals(expected, intersection.toString());
    }

    /**
     * Verifies that the constructor handles negative latitude and longitude values correctly.
     */
    @Test
    void constructorHandlesNegativeCoordinates() {
        long id = 2L;
        double lat = -45.0;
        double lng = -4.0;

        Intersection intersection = new Intersection(id, lat, lng);

        assertEquals(id, intersection.getId());
        assertEquals(lat, intersection.getLat());
        assertEquals(lng, intersection.getLng());
    }

    /**
     * Verifies that the constructor handles zero latitude and longitude values correctly.
     */
    @Test
    void constructorHandlesZeroCoordinates() {
        long id = 3L;
        double lat = 0.0;
        double lng = 0.0;

        Intersection intersection = new Intersection(id, lat, lng);

        assertEquals(id, intersection.getId());
        assertEquals(lat, intersection.getLat());
        assertEquals(lng, intersection.getLng());
    }
}