package domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IntersectionTest {
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
}