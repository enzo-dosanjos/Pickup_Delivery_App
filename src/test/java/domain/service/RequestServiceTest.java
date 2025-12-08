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

        assertNotNull(requestService.getPickupDelivery());
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

        assertEquals(request, requestService.getPickupDelivery().getRequestsForCourier(courierId)[0]);
    }


    /**
     * Verifies that adding a request to a non-existent courier does not throw an exception.
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

        assertEquals(request, requestService.getPickupDelivery().getRequestsForCourier(courierId)[0]);
    }

    /**
     * Verifies that the PickupDelivery object is empty when no requests are added or loaded.
     */
    @Test
    void pickupDeliveryIsEmptyInitially() {
        RequestService requestService = new RequestService();

        assertEquals("PickupDelivery:\nWarehouse Address ID: -1\nRequests per Courier:\n", requestService.getPickupDelivery().toString());
    }
}