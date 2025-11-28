package domain.model;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.Assert.assertEquals;

class PickupDeliveryTest {

    @Test
    public void checkConstructorAddRequestToCourierAndGetters() {
        Duration pickupDuration1 = Duration.ofMinutes(10);
        Duration pickupDuration2 = Duration.ofMinutes(12);
        Duration deliveryDuration1 = Duration.ofMinutes(15);
        Duration deliveryDuration2 = Duration.ofMinutes(18);
        Request request1 = new Request(100L, pickupDuration1, 200L, deliveryDuration1);
        Request request2 = new Request(101L, pickupDuration2, 201L, deliveryDuration2);
        long defaultCourierId = 1L;

        PickupDelivery pickupDelivery = new PickupDelivery();
        pickupDelivery.addRequestToCourier(defaultCourierId, request1);
        pickupDelivery.addRequestToCourier(defaultCourierId, request2);

        assertEquals(2, pickupDelivery.getRequestsPerCourier().get(defaultCourierId).length);
        assertEquals(request1, pickupDelivery.getRequests().get(pickupDelivery.getRequestsPerCourier().get(defaultCourierId)[0]));
        assertEquals(request2, pickupDelivery.getRequests().get(pickupDelivery.getRequestsPerCourier().get(defaultCourierId)[1]));
    }

    @Test
    public void checkGetRequestsForCourier() {
        Duration pickupDuration1 = Duration.ofMinutes(10);
        Duration pickupDuration2 = Duration.ofMinutes(12);
        Duration deliveryDuration1 = Duration.ofMinutes(15);
        Duration deliveryDuration2 = Duration.ofMinutes(18);
        Request request1 = new Request(100L, pickupDuration1, 200L, deliveryDuration1);
        Request request2 = new Request(101L, pickupDuration2, 201L, deliveryDuration2);
        Request request3 = new Request(101L, pickupDuration2, 201L, deliveryDuration2);
        long defaultCourierId = 1L;

        PickupDelivery pickupDelivery = new PickupDelivery();
        pickupDelivery.addRequestToCourier(defaultCourierId, request1);
        pickupDelivery.addRequestToCourier(defaultCourierId, request2);
        pickupDelivery.addRequestToCourier(2L, request3); // Different courier

        Request[] requestsForCourier = pickupDelivery.getRequestsForCourier(defaultCourierId);
        assertEquals(2, requestsForCourier.length);
        assertEquals(request1, requestsForCourier[0]);
        assertEquals(request2, requestsForCourier[1]);
    }

    @Test
    public void checkAddRequestToCourier() {
        Duration pickupDuration = Duration.ofMinutes(10);
        Duration deliveryDuration = Duration.ofMinutes(15);
        Request request = new Request(100L, pickupDuration, 200L, deliveryDuration);
        long courierId = 1L;

        PickupDelivery pickupDelivery = new PickupDelivery();
        boolean added = pickupDelivery.addRequestToCourier(courierId, request);

        assertEquals(true, added);
        assertEquals(1, pickupDelivery.getRequestsPerCourier().get(courierId).length);
        assertEquals(request, pickupDelivery.getRequests().get(pickupDelivery.getRequestsPerCourier().get(courierId)[0]));
    }
}