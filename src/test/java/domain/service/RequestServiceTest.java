package domain.service;

import domain.model.Map;
import domain.model.PickupDelivery;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequestServiceTest {

    @Test
    void checkLoadRequests() {
        String filePath = "src/test/resources/testRequest.xml";
        RequestService requestService = new RequestService();

        requestService.loadRequests(filePath);

        assertNotNull(requestService.getPickupDelivery());
    }
}