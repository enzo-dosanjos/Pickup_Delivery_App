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

        PickupDelivery pickupDelivery = new PickupDelivery();
        pickupDelivery.addRequest(request1);
        pickupDelivery.addRequest(request2);

        assertEquals(2, pickupDelivery.getRequests().size());
        assertEquals(request1, pickupDelivery.getRequests().get(0));
        assertEquals(request2, pickupDelivery.getRequests().get(1));
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

        PickupDelivery pickupDelivery = new PickupDelivery();
        pickupDelivery.addRequest(request);

        assertEquals(1, pickupDelivery.getRequests().size());
        assertEquals(request, pickupDelivery.getRequests().getFirst());

        pickupDelivery.removeRequest(request.getId());

        assertNull(pickupDelivery.findRequestById(request.getId()));
    }

    /**
     * Verifies that finding a request by its ID returns the correct request.
     * Ensures that null is returned when the request ID does not exist.
     */
    @Test
    public void checkFindRequestById() {
        PickupDelivery pickupDelivery = new PickupDelivery();
        Request request = new Request(100L, Duration.ofMinutes(10), 200L, Duration.ofMinutes(15));
        pickupDelivery.addRequest(request);

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
        pickupDelivery.addRequest(request);

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
        pickupDelivery.setWarehouseAddressId(123L);

        assertEquals(123L, pickupDelivery.getWarehouseAddressId());
    }

    /**
     * Verifies that the toString method returns the correct format for an empty PickupDelivery system.
     */
    @Test
    public void checkToStringEmptyPickupDelivery() {
        PickupDelivery pickupDelivery = new PickupDelivery();

        String result = pickupDelivery.toString();

        assertEquals("PickupDelivery:\nWarehouse Address ID: -1\nRequests:\n", result);
    }

    /**
     * Verifies that the toString method handles multiple couriers and requests correctly.
     */
    @Test
    public void checkToString() {
        PickupDelivery pickupDelivery = new PickupDelivery();
        Request request1 = new Request(100L, Duration.ofMinutes(10), 200L, Duration.ofMinutes(15));
        Request request2 = new Request(101L, Duration.ofMinutes(12), 201L, Duration.ofMinutes(18));
        pickupDelivery.addRequest(request1);
        pickupDelivery.addRequest(request2);
        pickupDelivery.setWarehouseAddressId(123L);
        String result = pickupDelivery.toString();

        assertEquals("PickupDelivery:\nWarehouse Address ID: 123\nRequests:\n" +
                request1 + "\n" + request2 + "\n", result);
    }

    /**
     * Verifies that the copy constructor creates an accurate copy of the PickupDelivery instance.
     */
    @Test
    void checkCopyConstructor() {
        PickupDelivery original = new PickupDelivery();
        original.setWarehouseAddressId(500L);
        Request request = new Request(100L, Duration.ofMinutes(10), 200L, Duration.ofMinutes(15));
        original.addRequest(request);

        PickupDelivery copy = new PickupDelivery(original);

        assertEquals(original.getWarehouseAddressId(), copy.getWarehouseAddressId());
        assertEquals(original.getRequests().size(), copy.getRequests().size());
        assertEquals(original.getRequests().get(0), copy.getRequests().get(0));
    }
}