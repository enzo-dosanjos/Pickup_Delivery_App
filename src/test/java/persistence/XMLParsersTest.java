package persistence;

import domain.model.Intersection;
import domain.model.Map;
import domain.model.PickupDelivery;
import domain.model.Request;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.time.Duration;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

class XMLParsersTest {

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

    @Test
    void checkParseRequestsLoadsWarehouseRequests() {
        String filePath = "src/test/resources/testRequest.xml";
        PickupDelivery pickupDelivery = new PickupDelivery();

        XMLParsers.parseRequests(filePath, pickupDelivery);

        assertNotNull(pickupDelivery, "The pickupDelivery should not be null after parsing");
        assertEquals(342873658, pickupDelivery.getWarehouseAdressId(), "The warehouse address should match the expected value");
        assertEquals(208769039, pickupDelivery.getRequests().get(pickupDelivery.getRequestsPerCourier().get(1L)[0]).getPickupIntersectionId(), "The first request address for courier 1 should match the expected value");
        assertEquals(25173820, pickupDelivery.getRequests().get(pickupDelivery.getRequestsPerCourier().get(1L)[0]).getDeliveryIntersectionId(), "The first request delivery address for courier 1 should match the expected value");
        assertEquals(180, pickupDelivery.getRequests().get(pickupDelivery.getRequestsPerCourier().get(1L)[0]).getPickupDuration().toMinutes(), "The first request pickup duration for courier 1 should match the expected value");
        assertEquals(240, pickupDelivery.getRequests().get(pickupDelivery.getRequestsPerCourier().get(1L)[0]).getDeliveryDuration().toMinutes(), "The first request delivery duration for courier 1 should match the expected value");
    }

    @Test
    void checkFalseReturnWhenDifferentWarehouseIds() {
        String filePath = "src/test/resources/testRequest.xml";
        PickupDelivery pickupDelivery = new PickupDelivery();

        pickupDelivery.setWarehouseAdressId(123456789L); // Set a different warehouse ID

        boolean result = XMLParsers.parseRequests(filePath, pickupDelivery);

        assertFalse(result, "The parsing should return false due to different warehouse IDs");
    }
}