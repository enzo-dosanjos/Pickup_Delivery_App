package domain.model;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class RequestTest {

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
}