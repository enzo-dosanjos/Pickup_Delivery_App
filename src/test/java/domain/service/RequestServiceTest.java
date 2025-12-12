package domain.service;

import domain.model.PickupDelivery;
import domain.model.Request;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link RequestService} class.
 * This class tests the functionality of adding requests to couriers and loading requests from an XML file.
 */
class RequestServiceTest {

    /**
     * Tests that the loadRequests method correctly loads requests from an XML file
     * and populates the PickupDelivery object.
     */
    @Test
    void checkLoadRequests() {
        String filePath = "src/test/resources/testRequest.xml";
        RequestService requestService = new RequestService();

        long courierId = 1L;

        requestService.loadRequests(filePath, courierId);

        assertNotNull(requestService.getPickupDeliveryPerCourier());
    }

    @Test
    void checkGettersAndSetters() {
        RequestService requestService = new RequestService();

        long courierId = 1L;

        String filePath = "src/test/resources/testRequest.xml";
        Request request = new Request(100L, Duration.ofMinutes(10), 200L, Duration.ofMinutes(20));
        requestService.addRequest(courierId, request);

        assertNotNull(requestService.getRequestById(request.getId(), courierId));

        requestService.setWarehouseAddress(50L, courierId);

        assertEquals(50L, requestService.getPickupDeliveryForCourier(courierId).getWarehouseAddressId());
    }

    /**
     * Verifies that loading requests from an invalid XML file returns false.
     */
    @Test
    public void checkLoadRequestsFromInvalidFile() {
        String filePath = "invalid/path/to/file.xml";
        RequestService requestService = new RequestService();

        long courierId = 1L;

        boolean result = requestService.loadRequests(filePath, courierId);

        assertFalse(result);
    }

    /**
     * Tests that the addRequest method correctly adds a request to the specified courier.
     */
    @Test
    void checkAddRequest() {
        RequestService requestService = new RequestService();
        long courierId = 1L;
        long pickupIntersectionId = 100L;
        Duration pickupDuration = Duration.ofMinutes(10);
        long deliveryIntersectionId = 200L;
        Duration deliveryDuration = Duration.ofMinutes(20);
        Request request = new Request(pickupIntersectionId, pickupDuration, deliveryIntersectionId, deliveryDuration);

        requestService.addRequest(courierId, request);

        assertEquals(request, requestService.getPickupDeliveryForCourier(courierId).getRequests().get(0));
    }


    /**
     * Verifies that adding a request to a non-existent courier does not throw an exception.
     * Instead, it should create a new courier entry in the PickupDelivery object.
     */
    @Test
    void addRequestToNonExistentCourier() {
        RequestService requestService = new RequestService();
        long courierId = 999L;
        long pickupIntersectionId = 300L;
        Duration pickupDuration = Duration.ofMinutes(15);
        long deliveryIntersectionId = 400L;
        Duration deliveryDuration = Duration.ofMinutes(25);
        Request request = new Request(pickupIntersectionId, pickupDuration, deliveryIntersectionId, deliveryDuration);

        requestService.addRequest(courierId, request);

        assertEquals(request, requestService.getPickupDeliveryForCourier(courierId).getRequests().get(0));
    }

    /**
     * Verifies that the deleteRequest method correctly removes a request from the specified courier.
     */
    @Test
    void checkDeleteRequest() {
        RequestService requestService = new RequestService();
        long courierId = 1L;
        long pickupIntersectionId = 100L;
        Duration pickupDuration = Duration.ofMinutes(10);
        long deliveryIntersectionId = 200L;
        Duration deliveryDuration = Duration.ofMinutes(20);

        Request request = new Request(pickupIntersectionId, pickupDuration, deliveryIntersectionId, deliveryDuration);

        // Add the request first
        requestService.addRequest(courierId, request);
        long requestId = request.getId();

        assertNotNull(requestService.getRequestById(requestId, courierId));

        // Delete the request
        requestService.deleteRequest(courierId, requestId);

        // Verify that the request was removed
        assertNull(requestService.getRequestById(requestId, courierId));
    }

    /**
     * Tests that the saveRequests method successfully writes the
     * courier's requests to an XML file (no exception and file created).
     */
    @Test
    void checkSaveRequests() {
        RequestService requestService = new RequestService();
        long courierId = 1L;
        long pickupIntersectionId = 100L;
        Duration pickupDuration = Duration.ofMinutes(10);
        long deliveryIntersectionId = 200L;
        Duration deliveryDuration = Duration.ofMinutes(20);

        Request request = new Request(pickupIntersectionId, pickupDuration, deliveryIntersectionId, deliveryDuration);

        // Add a request to ensure there is something to save
        requestService.addRequest(courierId, request);

        String filePath = "src/test/resources/outputRequests.xml";

        requestService.saveRequests(filePath, courierId);

        java.io.File outFile = new java.io.File(filePath);
        assertTrue(outFile.exists());

        // Clean up the created file after test
        outFile.delete();
    }
}