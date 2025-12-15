package persistence;

import domain.model.*;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link XMLWriters} class.
 */
class XMLWritersTest {

    /**
     * Tests that the {@code writeRequests} method writes a valid XML file
     * that can be parsed back with {@link XMLParsers#parseRequests}.
     */
    @Test
    void checkWriteRequestsCreatesValidXML() throws Exception {
        // Prepare a PickupDelivery with warehouse and two requests
        PickupDelivery original = new PickupDelivery();
        original.setWarehouseAddressId(342873658L);

        Request r1 = new Request(
                208769039L,
                Duration.ofSeconds(180),
                25173820L,
                Duration.ofSeconds(240)
        );
        Request r2 = new Request(
                123456789L,
                Duration.ofSeconds(60),
                987654321L,
                Duration.ofSeconds(120)
        );
        original.addRequest(r1);
        original.addRequest(r2);

        Path dir = Path.of("src", "test", "resources", "tmp");
        Files.createDirectories(dir);
        Path file = dir.resolve("writtenRequests.xml");

        try {
            // Write then parse again
            XMLWriters.writeRequests(original, file.toString());

            PickupDelivery parsed = new PickupDelivery();
            boolean ok = XMLParsers.parseRequests(file.toString(), parsed);

            assertTrue(ok, "Parsing of written requests XML should succeed");
            assertEquals(
                    original.getWarehouseAddressId(),
                    parsed.getWarehouseAddressId(),
                    "Warehouse address should be preserved"
            );
            assertEquals(
                    original.getRequests().size(),
                    parsed.getRequests().size(),
                    "Number of requests should be preserved"
            );

            assertEquals(
                    r1.getPickupIntersectionId(),
                    parsed.getRequests().get(0).getPickupIntersectionId(),
                    "First request pickup address should match"
            );
            assertEquals(
                    r1.getDeliveryIntersectionId(),
                    parsed.getRequests().get(0).getDeliveryIntersectionId(),
                    "First request delivery address should match"
            );
            assertEquals(
                    r1.getPickupDuration().toSeconds(),
                    parsed.getRequests().get(0).getPickupDuration().toSeconds(),
                    "First request pickup duration should match"
            );
            assertEquals(
                    r1.getDeliveryDuration().toSeconds(),
                    parsed.getRequests().get(0).getDeliveryDuration().toSeconds(),
                    "First request delivery duration should match"
            );
        } finally {
            Files.deleteIfExists(file);
        }
    }

    /**
     * Tests that the {@code writeTour} method writes a Tour to XML
     * with the expected root element and steps.
     */
    @Test
    void checkWriteTourCreatesValidXML() throws Exception {
        // Build a simple Tour
        long courierId = 1L;
        LocalDateTime startTime = LocalDateTime.of(2025, 1, 1, 8, 0);
        Tour tour = new Tour(courierId, startTime);

        // Stops: warehouse -> pickup -> delivery
        LocalDateTime t0 = startTime;
        LocalDateTime t1 = t0.plusMinutes(5);
        LocalDateTime t2 = t1.plusMinutes(10);

        TourStop warehouse = new TourStop(StopType.WAREHOUSE, -1L, 1L, t0, t0);
        TourStop pickup = new TourStop(StopType.PICKUP, 10L, 2L, t1, t1.plusMinutes(5));
        TourStop delivery = new TourStop(StopType.DELIVERY, 10L, 3L, t2, t2.plusMinutes(5));

        tour.addStop(warehouse);
        tour.addStop(pickup);
        tour.addStop(delivery);

        // Segments corresponding to those stops
        tour.addRoadSegment(new RoadSegment("1-2", 10.0, 1L, 2L));
        tour.addRoadSegment(new RoadSegment("2-3", 15.0, 2L, 3L));
        tour.addRoadSegment(new RoadSegment("3-1", 20.0, 3L, 1L));

        Path dir = Path.of("src", "test", "resources", "tmp");
        Files.createDirectories(dir);
        Path file = dir.resolve("writtenTour.xml");

        try {
            XMLWriters.writeTour(tour, file.toString());

            assertTrue(Files.exists(file), "Tour XML file should be created");

            // Parse back with DOM
            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(new File(file.toString()));
            doc.getDocumentElement().normalize();

            assertEquals("Tour", doc.getDocumentElement().getNodeName(), "Root element should be `Tour`");
            assertEquals(
                    String.valueOf(courierId),
                    doc.getDocumentElement().getAttribute("courierId"),
                    "Courier id should be written as attribute"
            );

            NodeList steps = doc.getElementsByTagName("step");
            assertEquals(3, steps.getLength(), "There should be one step per road segment");

            // Check first step types and ids
            var firstStep = (org.w3c.dom.Element) steps.item(0);
            assertEquals("1", firstStep.getAttribute("origine_adresse"));
            assertEquals("2", firstStep.getAttribute("destination_adresse"));
            assertEquals("WAREHOUSE", firstStep.getAttribute("type_Start"));
            assertEquals("PICKUP", firstStep.getAttribute("type_Finish"));
        } finally {
            try {
                Files.deleteIfExists(dir);
            } catch (Exception e) {
                // Ignore
            }
        }
    }
}