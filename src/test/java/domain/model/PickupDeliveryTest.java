package domain.model;

import org.junit.jupiter.api.Test;

import java.util.Map.Entry;

import java.time.Duration;

import static domain.model.StopType.*;
import static org.junit.Assert.*;

/**
 * Unit tests for the {@link PickupDelivery} class.
 */
class PickupDeliveryTest {

    /**
     * Verifies the constructor, adding requests to a courier, and the corresponding getters.
     * Ensures that requests are correctly added and retrievable.
     */
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

        assertEquals(2, pickupDelivery.getRequestsPerCourier().get(defaultCourierId).size());
        assertEquals(request1, pickupDelivery.getRequests().get(pickupDelivery.getRequestsPerCourier().get(defaultCourierId).get(0)));
        assertEquals(request2, pickupDelivery.getRequests().get(pickupDelivery.getRequestsPerCourier().get(defaultCourierId).get(1)));
    }

    /**
     * Verifies that requests for a specific courier can be retrieved correctly.
     * Ensures that only requests associated with the given courier ID are returned.
     */
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

    /**
     * Verifies that a request can be successfully added and removed to a courier.
     * Ensures that the request is stored and retrievable.
     */
    @Test
    public void checkAddAndRemoveRequestToCourier() {
        Duration pickupDuration = Duration.ofMinutes(10);
        Duration deliveryDuration = Duration.ofMinutes(15);
        Request request = new Request(100L, pickupDuration, 200L, deliveryDuration);
        long courierId = 1L;

        PickupDelivery pickupDelivery = new PickupDelivery();
        boolean added = pickupDelivery.addRequestToCourier(courierId, request);

        assertTrue(added);
        assertEquals(1, pickupDelivery.getRequestsPerCourier().get(courierId).size());
        assertEquals(request, pickupDelivery.getRequests().get(pickupDelivery.getRequestsPerCourier().get(courierId).getFirst()));

        boolean removed = pickupDelivery.removeRequestFromCourier(courierId, request.getId());

        assertTrue(removed);
        assertNull(pickupDelivery.getRequests().get(request.getId()));
    }

    /**
     * Verifies that finding a request by its ID returns the correct request.
     * Ensures that null is returned when the request ID does not exist.
     */
    @Test
    public void checkFindRequestById() {
        PickupDelivery pickupDelivery = new PickupDelivery();
        Request request = new Request(100L, Duration.ofMinutes(10), 200L, Duration.ofMinutes(15));
        pickupDelivery.addRequestToCourier(1L, request);

        assertEquals(request, pickupDelivery.findRequestById(request.getId()));
        assertNull(pickupDelivery.findRequestById(999L));
    }

    /**
     * Verifies that finding a request by intersection ID returns the correct request and stop type.
     * Ensures that null is returned when the intersection ID does not exist.
     */
    @Test
    public void checkFindRequestByIntersectionId() {
        PickupDelivery pickupDelivery = new PickupDelivery();
        Request request = new Request(100L, Duration.ofMinutes(10), 200L, Duration.ofMinutes(15));
        pickupDelivery.addRequestToCourier(1L, request);

        Entry<Request, StopType> pickupResult = pickupDelivery.findRequestByIntersectionId(100L);
        Entry<Request, StopType> deliveryResult = pickupDelivery.findRequestByIntersectionId(200L);
        Entry<Request, StopType> notFoundResult = pickupDelivery.findRequestByIntersectionId(999L);

        assertEquals(request, pickupResult.getKey());
        assertEquals(PICKUP, pickupResult.getValue());
        assertEquals(request, deliveryResult.getKey());
        assertEquals(DELIVERY, deliveryResult.getValue());
        assertNull(notFoundResult);
    }

    /**
     * Verifies that setting and retrieving the warehouse address ID works correctly.
     */
    @Test
    public void checkSetAndGetWarehouseAddressId() {
        PickupDelivery pickupDelivery = new PickupDelivery();
        pickupDelivery.setWarehouseAdressId(123L);

        assertEquals(123L, pickupDelivery.getWarehouseAdressId());
    }

    /**
     * Verifies that the toString method includes all relevant details for a populated PickupDelivery system.
     */
    @Test
    public void checkToStringIncludesDetails() {
        PickupDelivery pickupDelivery = new PickupDelivery();
        Request request = new Request(100L, Duration.ofMinutes(10), 200L, Duration.ofMinutes(15));
        pickupDelivery.addRequestToCourier(1L, request);
        pickupDelivery.setWarehouseAdressId(123L);

        String result = pickupDelivery.toString();

        assertTrue(result.contains("Warehouse Address ID: 123"));
        assertTrue(result.contains("Courier ID 1:"));
        assertTrue(result.contains(request.toString()));
    }

    /**
     * Verifies that the toString method returns the correct format for an empty PickupDelivery system.
     */
    @Test
    public void checkToStringEmptyPickupDelivery() {
        PickupDelivery pickupDelivery = new PickupDelivery();

        String result = pickupDelivery.toString();

        assertEquals("PickupDelivery:\nWarehouse Address ID: -1\nRequests per Courier:\n", result);
    }

    /**
     * Verifies that the toString method handles multiple couriers and requests correctly.
     */
    @Test
    public void checkToStringMultipleCouriersAndRequests() {
        PickupDelivery pickupDelivery = new PickupDelivery();
        Request request1 = new Request(100L, Duration.ofMinutes(10), 200L, Duration.ofMinutes(15));
        Request request2 = new Request(101L, Duration.ofMinutes(12), 201L, Duration.ofMinutes(18));
        pickupDelivery.addRequestToCourier(1L, request1);
        pickupDelivery.addRequestToCourier(2L, request2);
        pickupDelivery.setWarehouseAdressId(123L);
        String result = pickupDelivery.toString();
        assertEquals("PickupDelivery:\nWarehouse Address ID: 123\nRequests per Courier:\nCourier ID 1: " +
                request1 + " \nCourier ID 2: " +
                request2 + " \n", result);
    }

}