package domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PickupDeliveryTest {

    @Test
    public void testConstructorAddRequestAndGetters() {
        Request request1 = new Request(1L, 100L, 10, 200L, 15);
        Request request2 = new Request(2L, 101L, 12, 201L, 18);
        long defaultCourierId = 1L;

        PickupDelivery pickupDelivery = new PickupDelivery();
        pickupDelivery.addRequest(defaultCourierId, request1);
        pickupDelivery.addRequest(defaultCourierId, request2);

        assertEquals(2, pickupDelivery.getRequestsPerCourier().get(defaultCourierId).size());
        assertEquals(request1, pickupDelivery.getRequestsPerCourier().get(defaultCourierId).get(0));
        assertEquals(request2, pickupDelivery.getRequestsPerCourier().get(defaultCourierId).get(1));
    }

}