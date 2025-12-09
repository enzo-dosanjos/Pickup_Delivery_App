package domain.model;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Request} class.
 */
class RequestTest {

    /**
     * Verifies the constructor and getter methods of the {@link Request} class.
     * Ensures that all fields are correctly initialized and retrievable.
     */
    @Test
    void checkConstructorAndGetters() {
        long pickupIntersectionId = 100L;
        Duration pickupDuration = Duration.ofMinutes(10);
        long deliveryIntersectionId = 200L;
        Duration deliveryDuration = Duration.ofMinutes(20);

        Request request = new Request(pickupIntersectionId, pickupDuration, deliveryIntersectionId, deliveryDuration);

        assertEquals(pickupIntersectionId, request.getPickupIntersectionId());
        assertEquals(pickupDuration, request.getPickupDuration());
        assertEquals(deliveryIntersectionId, request.getDeliveryIntersectionId());
        assertEquals(deliveryDuration, request.getDeliveryDuration());
    }



    /**
     * Verifies that the toString method returns a string containing all relevant details of the request.
     */
    @Test
    void toStringIncludesAllDetails() {
        long pickupIntersectionId = 100L;
        Duration pickupDuration = Duration.ofMinutes(10);
        long deliveryIntersectionId = 200L;
        Duration deliveryDuration = Duration.ofMinutes(20);

        Request request = new Request(pickupIntersectionId, pickupDuration, deliveryIntersectionId, deliveryDuration);

        String result = request.toString();

        assertTrue(result.contains("Request ID: " + request.getId()));
        assertTrue(result.contains("Pickup at: " + pickupIntersectionId));
        assertTrue(result.contains("Duration: " + pickupDuration.toMinutes() + " mins"));
        assertTrue(result.contains("Delivery at: " + deliveryIntersectionId));
        assertTrue(result.contains("Duration: " + deliveryDuration.toMinutes() + " mins"));
    }


}