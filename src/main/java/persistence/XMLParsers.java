package persistence;

import domain.model.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class XMLParsers {
    public Map parseMap(String filePath) {
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

    public PickupDelivery parseRequests(String filePath) throws JDOMException, IOException {
        SAXBuilder saxBuilder = new SAXBuilder();
        File inputFile = new File(filePath);
        Document document = saxBuilder.build(inputFile);
        Element rootElement = document.getRootElement(); // planningRequest

        PickupDelivery pickupDelivery = new PickupDelivery();

        try {
            // Initialise XML parser
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(filePath));
            doc.getDocumentElement().normalize();

            Element root = doc.getDocumentElement(); // planningRequest

            long requestIdCounter = 1L; // To generate sequential IDs for requests

            // Parse depot information
            NodeList depotElements = root.getElementsByTagName("depot");
            for (int i = 0; i < depotElements.getLength(); i++) {
                Element depotElement = (Element) depotElements.item(i);

                long warehouseAddress = Long.parseLong(depotElement.getAttribute("address"));
                pickupDelivery.setWarehouseAddress(warehouseAddress);
                // Ignore departureTime for now as it's not in the Request model
            }

            // Parse requests
            NodeList requestNodes = root.getElementsByTagName("request");
            for (int i = 0; i < requestNodes.getLength(); i++) {
                Element requestElement = (Element) requestNodes.item(i);

                long pickupIntersectionId = Long.parseLong(requestElement.getAttribute("pickupAddress"));
                long deliveryIntersectionId = Long.parseLong(requestElement.getAttribute("deliveryAddress"));
                int pickupDuration = Integer.parseInt(requestElement.getAttribute("pickupDuration"));
                int deliveryDuration = Integer.parseInt(requestElement.getAttribute("deliveryDuration"));

                // Use a default courierId as it's not in the XML
                long defaultCourierId = 1L;

                Request request = new Request(requestIdCounter++, pickupIntersectionId, pickupDuration, deliveryIntersectionId, deliveryDuration);
                pickupDelivery.addRequest(defaultCourierId, request);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return pickupDelivery;
    }
}
