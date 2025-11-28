package persistence;

import domain.model.Intersection;
import domain.model.Map;
import domain.model.PickupDelivery;
import domain.model.RoadSegment;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
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
    void checkParseRequestsLoadsWarehouseRequests() throws IOException, ParserConfigurationException, SAXException {
        String filePath = "src/test/resources/testRequest.xml";
        XMLParsers parser = new XMLParsers();

        PickupDelivery pickupDelivery = parser.parseRequests(filePath);

        assertNotNull(pickupDelivery, "The pickupDelivery should not be null after parsing");
        assertEquals(342873658, pickupDelivery.getWarehouseAddress(), "The warehouse address should match the expected value");
        assertEquals(208769039, pickupDelivery.getRequestsPerCourier().get(1L).get(0).getPickupIntersectionId(), "The first request address for courier 1 should match the expected value");
        assertEquals(25173820, pickupDelivery.getRequestsPerCourier().get(1L).get(0).getDeliveryIntersectionId(), "The first request delivery address for courier 1 should match the expected value");
        assertEquals(180, pickupDelivery.getRequestsPerCourier().get(1L).get(0).getPickupDuration(), "The first request pickup duration for courier 1 should match the expected value");
        assertEquals(240, pickupDelivery.getRequestsPerCourier().get(1L).get(0).getDeliveryDuration(), "The first request delivery duration for courier 1 should match the expected value");
    }
}