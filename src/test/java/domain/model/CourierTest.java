package domain.model;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Courier} class.
 */
class CourierTest {

    /**
     * Tests the constructor and getter methods of the {@link Courier} class.
     * Verifies that the fields are correctly initialized
     */
    @Test
    void checkConstructorAndGetters() {
        long id = 1L; // Unique identifier for the courier
        String name = "John Pickup"; // Name of the courier
        Duration shiftDuration = Duration.ofHours(8); // Duration of the courier's shift

        Courier courier = new Courier(id, name, shiftDuration);

        // Assert that the ID, name, and shift duration match the expected values
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