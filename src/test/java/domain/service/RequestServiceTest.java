package domain.service;

import domain.model.Request;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class RequestServiceTest {

    @Test
    void checkLoadRequests() {
        String filePath = "src/test/resources/testRequest.xml";
        RequestService requestService = new RequestService();

        long courierId = 1L;

        requestService.loadRequests(filePath, courierId);

        assertNotNull(requestService.getPickupDelivery());
    }

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
}