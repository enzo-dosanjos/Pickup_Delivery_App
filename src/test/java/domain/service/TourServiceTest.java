package domain.service;

import domain.model.*;
import domain.model.dijkstra.CellInfo;
import domain.model.dijkstra.DijkstraTable;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test class for the {@link TourService} class.
 * This class contains tests to verify the functionality of the TourService methods.
 */
class TourServiceTest {

    /**
     * Verifies that couriers can be added to the TourService and their details are correctly stored.
     */
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

    /**
     * Verifies that a graph solution is correctly converted into a tour with valid input.
     */
    @Test
    void convertGraphToTourWithValidInput() {
        PickupDelivery pickupDelivery = new PickupDelivery();
        pickupDelivery.setWarehouseAddressId(1L);
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

    /**
     * Verifies that adding a tour for a courier updates the tours map correctly.
     */
    @Test
    void setTourForCourierUpdatesToursMap() {
        TourService service = new TourService();
        Tour tour = new Tour(1L, LocalDateTime.now());

        service.setTourForCourier(1L, tour);

        assertEquals(tour, service.getTours().get(1L));
    }

    /**
     * Verifies that adding road segments to a tour updates the tour correctly.
     */
    @Test
    void addRoadsToTourUpdatesTourWithRoadSegments() {
        TourService service = new TourService();
        Tour tour = new Tour(1L, LocalDateTime.now());
        DijkstraTable table = new DijkstraTable();
        Map map = new Map();

        // Mock data setup
        table.put(1L, 1L, new CellInfo(0, -1, true));
        table.put(1L, 2L, new CellInfo(10.0, 1L, true));
        table.put(1L, 3L, new CellInfo(25.0, 2L, true));
        table.put(1L, 4L, new CellInfo(45.0, 3L, true));
        table.put(1L, 5L, new CellInfo(70.0, 4L, true));
        table.put(2L, 2L, new CellInfo(0, -1, true));
        table.put(2L, 3L, new CellInfo(15.0, 2L, true));
        table.put(2L, 4L, new CellInfo(35.0, 3L, true));
        table.put(2L, 5L, new CellInfo(60.0, 4L, true));
        table.put(2L, 1L, new CellInfo(61.0, 5L, true));
        table.put(3L, 3L, new CellInfo(0, -1, true));
        table.put(3L, 4L, new CellInfo(20.0, 3L, true));
        table.put(3L, 5L, new CellInfo(45.0, 4L, true));
        table.put(3L, 1L, new CellInfo(46.0, 5L, true));
        table.put(3L, 2L, new CellInfo(56.0, 1L, true));
        table.put(4L, 4L, new CellInfo(0, -1, true));
        table.put(4L, 5L, new CellInfo(25.0, 4L, true));
        table.put(5L, 5L, new CellInfo(0, -1, true));
        table.put(5L, 1L, new CellInfo(1.0, 5L, true));
        map.addIntersection(new Intersection(1L, 0.0, 0.0));
        map.addIntersection(new Intersection(2L, 1.0, 1.0));
        map.addIntersection(new Intersection(3L, 2.0, 2.0));
        map.addIntersection(new Intersection(4L, 3.0, 3.0));
        map.addIntersection(new Intersection(5L, 4.0, 4.0));
        map.addRoadSegment(1L, new RoadSegment("Road 1-2",10.0, 1L, 2L));
        map.addRoadSegment(2L, new RoadSegment("Road 2-3", 15.0, 2L, 3L));
        map.addRoadSegment(3L, new RoadSegment("Road 3-4", 20.0, 3L, 4L));
        map.addRoadSegment(4L, new RoadSegment("Road 4-5", 25.0, 4L, 5L));
        map.addRoadSegment(5L, new RoadSegment("Road 5-1", 1.0, 5L, 1L));
        tour.addStop(new TourStop(StopType.WAREHOUSE, 0L, 1L, LocalDateTime.now(), LocalDateTime.now()));
        tour.addStop(new TourStop(StopType.PICKUP, 1L, 3L, LocalDateTime.now(), LocalDateTime.now()));
        tour.addStop(new TourStop(StopType.DELIVERY, 2L, 5L, LocalDateTime.now(), LocalDateTime.now()));

        service.setTourForCourier(1L, tour);
        assertEquals(0, tour.getRoadSegmentsTaken().size());

        Tour updatedTour = service.addRoadsToTour(tour, table, map);

        assertEquals(5, updatedTour.getRoadSegmentsTaken().size());
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

    @Test
    void addCourierIncreasesNumCouriers() {
        TourService service = new TourService();
        assertEquals(0, service.getNumCouriers());

        service.addCourier(new Courier(1L, "Courier 1", Duration.ofHours(8)));
        assertEquals(1, service.getNumCouriers());
    }

    @Test
    void removeCourierDecreasesNumCouriers() {
        TourService service = new TourService();
        service.addCourier(new Courier(1L, "Courier 1", Duration.ofHours(8)));
        service.addCourier(new Courier(2L, "Courier 2", Duration.ofHours(8)));
        assertEquals(2, service.getNumCouriers());

        boolean removed = service.removeCourier(1L);
        assertTrue(removed);
        assertEquals(1, service.getNumCouriers());
    }

    @Test
    void removeCourierReturnsFalseIfCourierNotFound() {
        TourService service = new TourService();
        service.addCourier(new Courier(1L, "Courier 1", Duration.ofHours(8)));

        boolean removed = service.removeCourier(2L);
        assertFalse(removed);
        assertEquals(1, service.getNumCouriers());
    }

    @Test
    void getAvailableCouriersReturnsOnlyAvailable() {
        TourService service = new TourService();
        service.addCourier(new Courier(1L, "Courier 1", Duration.ofHours(8)));
        service.addCourier(new Courier(2L, "Courier 2", Duration.ofHours(8)));

        service.getCouriers().get(1).setAvailabilityStatus(AvailabilityStatus.BUSY);
        List<Courier> availableCouriers = service.getAvailableCouriers();

        assertEquals(1, availableCouriers.size());
        assertEquals(1L, availableCouriers.get(0).getId());
    }

    @Test
    void updateStopOrderThrowsExceptionForWarehouse() {
        TourService service = new TourService();
        Tour tour = new Tour(1L, LocalDateTime.now());
        tour.addStop(new TourStop(StopType.WAREHOUSE, -1, 1L, LocalDateTime.now(), LocalDateTime.now()));
        tour.addStop(new TourStop(StopType.PICKUP, 1, 1L, LocalDateTime.now(), LocalDateTime.now()));
        service.setTourForCourier(1L, tour);

        assertThrows(IllegalArgumentException.class, () -> service.updateStopOrder(1L, 0, 1));
    }

    @Test
    void updateStopOrderThrowsExceptionForSameRequest() {
        TourService service = new TourService();
        Tour tour = new Tour(1L, LocalDateTime.now());
        tour.addStop(new TourStop(StopType.PICKUP, 1L, 2L, LocalDateTime.now(), LocalDateTime.now()));
        tour.addStop(new TourStop(StopType.DELIVERY, 1L, 3L, LocalDateTime.now(), LocalDateTime.now()));
        service.setTourForCourier(1L, tour);

        assertThrows(IllegalArgumentException.class, () -> service.updateStopOrder(1L, 0, 1));
    }

    @Test
    void updateStopOrderAddsPrecedence() {
        TourService service = new TourService();
        Tour tour = new Tour(1L, LocalDateTime.now());
        tour.addStop(new TourStop(StopType.PICKUP, 1L, 2L, LocalDateTime.now(), LocalDateTime.now()));
        tour.addStop(new TourStop(StopType.PICKUP, 2L, 3L, LocalDateTime.now(), LocalDateTime.now()));
        service.setTourForCourier(1L, tour);
        service.initPrecedences(1L, new ArrayList<>(), new PickupDelivery());

        service.updateStopOrder(1L, 0, 1);
        assertTrue(service.getPrecedencesByCourier().get(1L).containsKey("2/3/p"));
    }
}