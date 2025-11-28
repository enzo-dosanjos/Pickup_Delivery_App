package domain.service;

import domain.model.Courier;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.HashMap;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

class TourServiceTest {
    @Test
    void checkComputeDurationReturnsZeroWhenDistanceIsZeroOrNegative() {
        TourService service = new TourService();

        Duration d0 = service.computeDuration(0.0);
        Duration dNeg = service.computeDuration(-5.0);

        assertEquals(Duration.ZERO, d0);
        assertEquals(Duration.ZERO, dNeg);
    }

    @Test
    void checkComputeDurationUsesSpeedOf15KmPerHour() {
        TourService service = new TourService();

        // 15 km at 15 km/h -> 1 hour
        Duration d15 = service.computeDuration(15.0);
        assertEquals(Duration.ofHours(1), d15);

        // 7.5 km at 15 km/h -> 0.5 h
        Duration d75 = service.computeDuration(7.5);
        assertEquals(Duration.ofMinutes(30), d75);
    }

    @Test
    void checkUpdateNumCouriersWithValidValueResizesArrayAndReturnsTrue() {
        TourService service = new TourService();

        boolean result = service.updateNumCouriers(3);

        assertTrue(result);
        assertEquals(3, service.getNumCouriers());
        assertNotNull(service.getCouriers());
        assertEquals(3, service.getCouriers().length);
    }

    @Test
    void checkUpdateNumCouriersWithNegativeValueReturnsFalseAndDoesNotChangeState() {
        TourService service = new TourService();
        service.updateNumCouriers(3);
        Courier c1 = new Courier(1L, "Courier 1", Duration.ofHours(8));
        Courier c2 = new Courier(2L, "Courier 2", Duration.ofHours(8));
        Courier c3 = new Courier(3L, "Courier 3", Duration.ofHours(8));
        service.addCourier(c1);
        service.addCourier(c2);
        service.addCourier(c3);

        int originalNum = service.getNumCouriers();
        int originalLength = service.getCouriers().length;

        boolean result = service.updateNumCouriers(-1);

        assertFalse(result);
        assertEquals(originalNum, service.getNumCouriers());
        assertEquals(originalLength, service.getCouriers().length);
    }

    @Test
    void checkUpdateRequestOrderAddsConstraintForCourier() {
        TourService service = new TourService();

        long courierId = 2L;
        service.updateRequestOrder(1L, 2L, courierId);
        service.updateRequestOrder(3L, 4L, courierId);

        HashMap<Long, Long> constraints = service.getRequestOrder().get(courierId);
        assertEquals(2, constraints.size());
        assertEquals(2L, constraints.get(1L));
        assertEquals(4L, constraints.get(3L));
    }
}