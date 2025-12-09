package domain.service;

import domain.model.*;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

//import static org.junit.Assert.assertEquals;


import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class TourServiceTest {
    @Test
    void checkAddAndRemoveCourier() {
        TourService service = new TourService();

        boolean result = service.addCourier(new Courier(1L, "1", null));
        result = result && service.addCourier(new Courier(2L, "2", null));
        assertTrue(result);

        assertEquals(2, service.getNumCouriers());
        assertNotNull(service.getCouriers());
        assertEquals(2, service.getCouriers().size());

        result = service.removeCourier(1L);
        assertTrue(result);

        assertEquals(1, service.getNumCouriers());
        assertNotNull(service.getCouriers());
        assertEquals(1, service.getCouriers().size());
    }

    @Test
    void convertGraphToTourWithValidInput() {
        PickupDelivery pickupDelivery = new PickupDelivery();
        pickupDelivery.setWarehouseAdressId(1L);
        Request request1 = new Request(2L, Duration.ofMinutes(5), 3L, Duration.ofMinutes(10));
        Request request2 = new Request(4L, Duration.ofMinutes(3), 5L, Duration.ofMinutes(8));
        pickupDelivery.addRequestToCourier(123L, request1);
        pickupDelivery.addRequestToCourier(123L, request2);

        LocalDateTime startTime = LocalDateTime.of(2023, 10, 1, 8, 0);
        Integer[] solution = {0, 1, 2, 3, 4};
        Long[] vertices = {0L, 2L, 3L, 4L, 5L};
        double[][] costs = {
                {0.0, 12.0, 20.0, 25.0, 30.0},
                {12.0, 0.0, 15.0, 22.0, 27.0},
                {20.0, 15.0, 0.0, 10.0, 18.0},
                {25.0, 22.0, 10.0, 0.0, 12.0},
                {30.0, 27.0, 18.0, 12.0, 0.0}
        };

        TourService tourService = new TourService();
        Tour tour = tourService.convertGraphToTour(pickupDelivery, startTime, 123L, solution, vertices, costs);
        List<TourStop> tourStops = tour.getStops();

        assertEquals(5, tourStops.size());
        assertEquals(Duration.ofMinutes(5 + 10 + 3 + 8 + 12+15+10+12+30), tour.getTotalDuration());
        assertEquals(startTime, tour.getStartTime());
        assertEquals(123L, tour.getCourierId());
        assertEquals(2L, tourStops.get(1).getIntersectionId());
        assertEquals(3L, tourStops.get(2).getIntersectionId());
        assertEquals(4L, tourStops.get(3).getIntersectionId());
        assertEquals(5L, tourStops.get(4).getIntersectionId());

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

    @Test
    void checkGetAvailableCouriers() {
        TourService service = new TourService();
        service.addCourier(new Courier(1L, "Courier 1", Duration.ofHours(8)));
        service.addCourier(new Courier(2L, "Courier 2", Duration.ofHours(8)));
        assertEquals(2, service.getAvailableCouriers().size());

        service.getCouriers().get(1).setAvailabilityStatus(AvailabilityStatus.BUSY);
        assertEquals(1, service.getAvailableCouriers().size());
        assertEquals(1L, service.getAvailableCouriers().getFirst().getId());
    }
}