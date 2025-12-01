package domain.service;

import domain.model.Courier;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.HashMap;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

class TourServiceTest {
    @Test
    void checkAddCourier() {
        TourService service = new TourService();

        boolean result = service.addCourier(new Courier(1L, "1", null));
        result = result &&  service.addCourier(new Courier(2L, "2", null));
        assertTrue(result);

        assertEquals(2, service.getNumCouriers());
        assertNotNull(service.getCouriers());
        assertEquals(2, service.getCouriers().size());
    }

    @Test
    void checkUpdateRequestOrderAddsConstraintForCourier() {
        TourService service = new TourService();
        service.addCourier(new Courier(2L, "Courier 2", Duration.ofHours(8)));

        long courierId = 2L;
        service.updateRequestOrder(1L, 2L, courierId);
        service.updateRequestOrder(3L, 4L, courierId);

        HashMap<Long, Long> constraints = service.getRequestOrder().get(courierId);
        assertEquals(2, constraints.size());
        assertEquals(2L, constraints.get(1L));
        assertEquals(4L, constraints.get(3L));
    }
}