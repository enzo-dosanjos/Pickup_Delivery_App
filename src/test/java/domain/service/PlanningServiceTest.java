package domain.service;

import domain.model.*;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link PlanningService} class.
 */
class PlanningServiceTest {

    /**
     * Verifies that recomputeTourForCourier throws an exception when the specified courier is not found.
     */
    @Test
    void recomputeTourForCourierThrowsExceptionIfCourierNotFound() {
        PlanningService planningService = new PlanningService(new RequestService(), new TourService(), new MapService());
        assertThrows(IllegalArgumentException.class, () -> planningService.recomputeTourForCourier(999L));
    }

    /**
     * Verifies that recomputeTourForCourier successfully updates the tour for a given courier.
     */
    @Test
    void recomputeTourForCourierUpdatesTourSuccessfully() {
        RequestService requestService = new RequestService();
        TourService tourService = new TourService();
        MapService mapService = new MapService();
        mapService.loadMap("src/main/resources/grandPlan.xml");
        PlanningService planningService = new PlanningService(requestService, tourService, mapService);

        Courier courier = new Courier(1L, "Courier 1", Duration.ofHours(8));
        tourService.addCourier(courier);

        Request request = new Request(8358135L, Duration.ofMinutes(10), 25173820L, Duration.ofMinutes(15));
        requestService.getPickupDeliveryForCourier(1L).addRequest(request);

        requestService.getPickupDeliveryForCourier(1L).setWarehouseAddressId(342873658L);
        planningService.recomputeTourForCourier(1L);

        Tour tour = tourService.getTours().get(1L);
        assertNotNull(tour);
        assertEquals(1L, tour.getCourierId());
        assertTrue(tour.getTotalDuration().toMinutes() > 0);
    }

    /**
     * Verifies that courierExists returns true for an existing courier.
     */
    @Test
    void courierExistsReturnsTrueForExistingCourier() {
        TourService tourService = new TourService();
        PlanningService planningService = new PlanningService(new RequestService(), tourService, new MapService());

        tourService.addCourier(new Courier(1L, "Courier 1", Duration.ofHours(8)));
        assertTrue(planningService.courierExists(1L));
    }

    /**
     * Verifies that courierExists returns false for a non-existing courier.
     */
    @Test
    void courierExistsReturnsFalseForNonExistingCourier() {
        PlanningService planningService = new PlanningService(new RequestService(), new TourService(), new MapService());
        assertFalse(planningService.courierExists(999L));
    }

    /**
     * Verifies that updatePrecedences adds a new precedence for a request.
     */
    @Test
    void updatePrecedencesAddsNewPrecedence() {
        TourService tourService = new TourService();
        PlanningService planningService = new PlanningService(new RequestService(), tourService, new MapService());

        tourService.initPrecedences(1L, new ArrayList<>());
        PickupDelivery pickupDelivery = new PickupDelivery();
        Request request = new Request(1L, Duration.ofMinutes(10), 2L, Duration.ofMinutes(15));
        pickupDelivery.addRequest(request);

        planningService.updatePrecedences(1L, request);

        assertTrue(tourService.getPrecedencesByCourier().get(1L).containsKey("20/2/d"));
    }

    /**
     * Verifies that deletePrecedences removes the precedence for a specific request.
     */
    @Test
    void deletePrecedencesRemovesPrecedenceForRequest() {
        TourService tourService = new TourService();
        PlanningService planningService = new PlanningService(new RequestService(), tourService, new MapService());

        tourService.initPrecedences(1L, new ArrayList<>());
        HashMap<String, Set<String>> precs = tourService.getPrecedencesByCourier().get(1L);
        precs.put("1/2/d", new HashSet<>(List.of("1/2/p")));

        planningService.deletePrecedences(1L, 1L);

        assertFalse(precs.containsKey("1/2/d"));
        assertTrue(precs.values().stream().noneMatch(set -> set.contains("1/2/p")));
    }

}