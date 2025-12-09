package persistence;

import domain.model.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.time.Duration;
import java.util.ArrayList;

/**
 * Data access class for parsing XML files to extract map and request data.
 */
public class XMLParsers {

    /**
     * Parses an XML file to create a Map object containing intersections and road segments.
     *
     * @param filePath the path to the XML file containing the map data
     * @return a Map object populated with intersections and road segments
     */
    public static Map parseMap(String filePath) {
        Map map = new Map();

        try {
            // Initialise XML parser
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(filePath));
            doc.getDocumentElement().normalize();

            Element root = doc.getDocumentElement();

            // Load intersections
            NodeList nodes = root.getElementsByTagName("noeud");
            for (int i = 0; i < nodes.getLength(); i++) {
                Element node = (Element) nodes.item(i);

                long id = Long.parseLong(node.getAttribute("id"));
                double lat = Double.parseDouble(node.getAttribute("latitude"));
                double lng = Double.parseDouble(node.getAttribute("longitude"));

                Intersection intersection = new Intersection(id, lat, lng);
                map.addIntersection(intersection);
            }

            // Load road segments
            NodeList roadSegments = root.getElementsByTagName("troncon");
            for (int i = 0; i < roadSegments.getLength(); i++) {
                Element roadSegment = (Element) roadSegments.item(i);

                long origin = Long.parseLong(roadSegment.getAttribute("origine"));
                long dest = Long.parseLong(roadSegment.getAttribute("destination"));
                double length = Double.parseDouble(roadSegment.getAttribute("longueur"));
                String name = roadSegment.getAttribute("nomRue");

                RoadSegment segment = new RoadSegment(
                        name,
                        length,
                        origin,
                        dest
                );

                map.addRoadSegment(origin, segment);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    /**
     * Parses an XML file to populate a PickupDelivery object with requests and depot information.
     *
     * @param filePath the path to the XML file containing the requests data
     * @param courierId the ID of the courier for whom the requests are being parsed
     * @param pickupDeliveryToFill the PickupDelivery object to populate with parsed data
     * @return true if the parsing was successful, false otherwise
     */
    public static boolean parseRequests(String filePath, long courierId, PickupDelivery pickupDeliveryToFill) {
        try {
            // Initialise XML parser
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(filePath));
            doc.getDocumentElement().normalize();

            Element root = doc.getDocumentElement(); // planningRequest

            // Parse depot information
            NodeList depotElements = root.getElementsByTagName("depot");
            for (int i = 0; i < depotElements.getLength(); i++) {
                Element depotElement = (Element) depotElements.item(i);

                long warehouseAddress = Long.parseLong(depotElement.getAttribute("address"));
                if (pickupDeliveryToFill.getWarehouseAddressId() == -1) {
                    pickupDeliveryToFill.setWarehouseAddressId(warehouseAddress);
                }

                if (pickupDeliveryToFill.getWarehouseAddressId() != warehouseAddress) {
                    return false;
                }
                // Ignore departureTime for now as it's not in the Request model
            }

            // Parse requests
            NodeList requestNodes = root.getElementsByTagName("request");
            for (int i = 0; i < requestNodes.getLength(); i++) {
                Element requestElement = (Element) requestNodes.item(i);

                long pickupIntersectionId = Long.parseLong(requestElement.getAttribute("pickupAddress"));
                long deliveryIntersectionId = Long.parseLong(requestElement.getAttribute("deliveryAddress"));
                Duration pickupDuration = Duration.ofSeconds(
                        Long.parseLong(requestElement.getAttribute("pickupDuration"))
                );
                Duration deliveryDuration = Duration.ofSeconds(
                        Long.parseLong(requestElement.getAttribute("deliveryDuration"))
                );

                Request request = new Request(pickupIntersectionId, pickupDuration, deliveryIntersectionId, deliveryDuration);

                pickupDeliveryToFill.addRequestToCourier(courierId, request);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Parses an XML file to create a list of Courier objects.
     *
     * @param filePath the path to the XML file containing courier data
     * @return an ArrayList of Courier objects
     */
    public static ArrayList<Courier> parseCouriers(String filePath) {
        ArrayList<Courier> couriers = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(filePath));
            doc.getDocumentElement().normalize();

            Element root = doc.getDocumentElement();

            NodeList courierNodes = root.getElementsByTagName("courier");
            for (int i = 0; i < courierNodes.getLength(); i++) {
                Element courierElement = (Element) courierNodes.item(i);

                long id = Long.parseLong(courierElement.getAttribute("id"));
                String name = courierElement.getAttribute("name");

                String shiftDurationMinutesStr = courierElement.getAttribute("shiftDurationMinutes");
                long shiftDurationMinutes = Long.parseLong(shiftDurationMinutesStr);
                Duration shiftDuration = Duration.ofMinutes(shiftDurationMinutes);

                Courier courier = new Courier(id, name, shiftDuration);

                couriers.add(courier);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return couriers;
    }
}
