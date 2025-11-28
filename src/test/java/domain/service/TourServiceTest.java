package domain.service;

import domain.model.*;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TourServiceTest {

    @Test
    void convertGraphToTourWithValidInput() {
        PickupDelivery pickupDelivery = new PickupDelivery();
        pickupDelivery.setWarehouseAdressId(1L);
        Request request1 = new Request(2L, Duration.ofMinutes(5), 3L, Duration.ofMinutes(10));
        Request request2 = new Request(4L, Duration.ofMinutes(3), 5L, Duration.ofMinutes(8));
        pickupDelivery.addRequestToCourier(123L, request1);
        pickupDelivery.addRequestToCourier(123L, request2);

        LocalDateTime startTime = LocalDateTime.of(2023, 10, 1, 8, 0);
        Integer[] solution = {0, 1, 2, 3,};
        Long[] vertices = {2L, 3L, 4L, 5L};
        double[][] costs = {
                {0.0, 15.0, 25.0, 40.0},
                {15.0, 0.0, 20.0, 35.0},
                {25.0, 20.0, 0.0, 15.0},
                {40.0, 35.0, 15.0, 0.0}
        };

        TourService tourService = new TourService();
        Tour tour = tourService.convertGraphToTour(pickupDelivery, startTime, 123L, solution, vertices, costs);
        List<TourStop> tourStops = tour.getStops();

        assertEquals(4, tourStops.size());
        assertEquals(Duration.ofMinutes(5 + 10 + 3 + 8 + 15 + 20 + 15), tour.getTotalDuration());
        assertEquals(startTime, tour.getStartTime());
        assertEquals(123L, tour.getCourrierId());
        assertEquals(2L, tourStops.get(0).getIntersectionId());
        assertEquals(3L, tourStops.get(1).getIntersectionId());
        assertEquals(4L, tourStops.get(2).getIntersectionId());
        assertEquals(5L, tourStops.get(3).getIntersectionId());

    }

}
