package persistence;

import domain.model.*;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link XMLParsers} class.
 * Verifies the correctness of XML parsing for map and request data.
 */
class XMLParsersTest {

    /**
     * Tests that the {@code parseMap} method correctly loads intersections and road segments
     * from a valid XML file.
     */
    @Test
    void checkParseMapLoadsIntersectionsAndRoadSegments() {
        String filePath = "src/test/resources/testMap.xml";
        XMLParsers parser = new XMLParsers();

        Map map = parser.parseMap(filePath);

        assertNotNull(map, "The map should not be null after parsing");
        TreeMap<Long, Intersection> intersections = map.getIntersections();
        assertFalse(intersections.isEmpty(), "Intersections should be loaded");
        assertEquals(5, intersections.size(), "There should be 5 intersections loaded");
        assertFalse(map.getAdjencyList().isEmpty(), "Road segments should be loaded");
        assertEquals(2, map.getAdjencyList().size(), "There should be 2 road segments list for 5 intersections");
    }

    /**
     * Tests that the {@code parseRequests} method correctly loads warehouse and request data
     * from a valid XML file.
     */
    @Test
    void checkParseRequestsLoadsWarehouseRequests() {
        String filePath = "src/test/resources/testRequest.xml";
        PickupDelivery pickupDelivery = new PickupDelivery();

        long courierId = 1L;

        XMLParsers.parseRequests(filePath, courierId, pickupDelivery);

        assertNotNull(pickupDelivery, "The pickupDelivery should not be null after parsing");
        assertEquals(342873658, pickupDelivery.getWarehouseAdressId(), "The warehouse address should match the expected value");
        assertEquals(208769039, pickupDelivery.getRequests().get(pickupDelivery.getRequestsPerCourier().get(1L).getFirst()).getPickupIntersectionId(), "The first request address for courier 1 should match the expected value");
        assertEquals(25173820, pickupDelivery.getRequests().get(pickupDelivery.getRequestsPerCourier().get(1L).getFirst()).getDeliveryIntersectionId(), "The first request delivery address for courier 1 should match the expected value");
        assertEquals(180, pickupDelivery.getRequests().get(pickupDelivery.getRequestsPerCourier().get(1L).getFirst()).getPickupDuration().toSeconds(), "The first request pickup duration for courier 1 should match the expected value");
        assertEquals(240, pickupDelivery.getRequests().get(pickupDelivery.getRequestsPerCourier().get(1L).getFirst()).getDeliveryDuration().toSeconds(), "The first request delivery duration for courier 1 should match the expected value");
    }

    /**
     * Tests that the {@code parseRequests} method returns false when the warehouse ID in the XML file
     * does not match the existing warehouse ID in the {@link PickupDelivery} object.
     */
    @Test
    void checkFalseReturnWhenDifferentWarehouseIds() {
        String filePath = "src/test/resources/testRequest.xml";
        PickupDelivery pickupDelivery = new PickupDelivery();

        pickupDelivery.setWarehouseAdressId(123456789L); // Set a different warehouse ID

        long courierId = 1L;

        boolean result = XMLParsers.parseRequests(filePath, courierId, pickupDelivery);

        assertFalse(result, "The parsing should return false due to different warehouse IDs");
    }

    @Test
    void checkParseCouriersLoadsCouriers() {
        String filePath = "src/test/resources/testCouriers.xml";
        ArrayList<Courier> couriers = XMLParsers.parseCouriers(filePath);

        assertNotNull(couriers, "The couriers list should not be null after parsing");
        assertEquals(3, couriers.size(), "There should be 3 couriers loaded");

        assertEquals(1L, couriers.get(0).getId(), "First courier ID should match expected value");
        assertEquals("Courier 1", couriers.get(0).getName(), "First courier name should match expected value");
        assertEquals(Duration.ofHours(8), couriers.get(0).getShiftDuration(), "First courier shift duration should match expected value");

        assertEquals(2L, couriers.get(1).getId(), "Second courier ID should match expected value");
        assertEquals("Courier 2", couriers.get(1).getName(), "Second courier name should match expected value");
        assertEquals(Duration.ofHours(6), couriers.get(1).getShiftDuration(), "Second courier shift duration should match expected value");

        assertEquals(3L, couriers.get(2).getId(), "Third courier ID should match expected value");
        assertEquals("Courier 3", couriers.get(2).getName(), "Third courier name should match expected value");
        assertEquals(Duration.ofHours(7), couriers.get(2).getShiftDuration(), "Third courier shift duration should match expected value");
    }
}