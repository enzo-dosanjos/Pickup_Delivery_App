package domain.model;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class CourierTest {
    @Test
    void checkConstructorAndGetters() {
        long id = 1L;
        String name = "John Pickup";
        Duration shiftDuration = Duration.ofHours(8);

        Courier courier = new Courier(id, name, shiftDuration);

        assertEquals(id, courier.getId());
        assertEquals(name, courier.getName());
        assertEquals(shiftDuration, courier.getShiftDuration());
        assertEquals(AvailabilityStatus.AVAILABLE, courier.getAvailabilityStatus());
    }

    @Test
    void checkAvailabilityStatus() {
        long id = 1L;
        String name = "John Pickup";
        Duration shiftDuration = Duration.ofHours(8);
        Courier courier = new Courier(id, name, shiftDuration);
        assertTrue(courier.isAvailable());
        assertFalse(courier.isBusy());

        courier.setAvailabilityStatus(AvailabilityStatus.BUSY);
        assertFalse(courier.isAvailable());
        assertTrue(courier.isBusy());
    }
}