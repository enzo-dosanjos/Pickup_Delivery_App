package domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequestTest {

    @Test
    void checkConstructorAndGetters() {
        long id = 1L;
        long pickupIntersectionId = 100L;
        int pickupDuration = 15;
        long deliveryIntersectionId = 200L;
        int deliveryDuration = 20;

        Request request = new Request(id, pickupIntersectionId, pickupDuration, deliveryIntersectionId, deliveryDuration);

        assertEquals(id, request.getId());
        assertEquals(pickupIntersectionId, request.getPickupIntersectionId());
        assertEquals(pickupDuration, request.getPickupDuration());
        assertEquals(deliveryIntersectionId, request.getDeliveryIntersectionId());
        assertEquals(deliveryDuration, request.getDeliveryDuration());
    }
}