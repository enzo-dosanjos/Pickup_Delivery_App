package persistence;

import domain.model.*;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

@Component
public class XMLParsers {

    public PickupDelivery parseRequests(String filepath) {
        System.out.println("Parsing requests from: " + filepath);
        // Placeholder implementation: create a dummy PickupDelivery
        PickupDelivery dummyPickupDelivery = new PickupDelivery();
        // Add some dummy requests for testing purposes
        dummyPickupDelivery.addRequest(1L, new Request(101L, 1L, 10L, 2L, 5L)); // courierId, request
        dummyPickupDelivery.addRequest(1L, new Request(102L, 3L, 12L, 4L, 6L));
        dummyPickupDelivery.addRequest(2L, new Request(201L, 5L, 8L, 6L, 4L));

        // In a real scenario, this would parse an XML file and populate the PickupDelivery object.
        // Example parsing logic (simplified):
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true);

            DocumentBuilder builder = factory.newDocumentBuilder();
            File xmlFile = new File(filepath);
            if (!xmlFile.exists()) {
                System.err.println("Request XML file not found at: " + filepath + ". Returning dummy data.");
                return dummyPickupDelivery; // Return dummy data if file doesn't exist
            }
            Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            Element root = doc.getDocumentElement();
            NodeList requestsNodes = root.getElementsByTagName("request"); // Assuming "request" tag
            for (int i = 0; i < requestsNodes.getLength(); i++) {
                Element requestElement = (Element) requestsNodes.item(i);
                // Parse attributes and create Request objects
                long id = Long.parseLong(requestElement.getAttribute("id"));
                long pickupIntersectionId = Long.parseLong(requestElement.getAttribute("pickupIntersectionId"));
                long pickupDuration = Long.parseLong(requestElement.getAttribute("pickupDuration"));
                long deliveryIntersectionId = Long.parseLong(requestElement.getAttribute("deliveryIntersectionId"));
                long deliveryDuration = Long.parseLong(requestElement.getAttribute("deliveryDuration"));
                long courierId = Long.parseLong(requestElement.getAttribute("courierId")); // Assuming courierId is in request XML

                Request request = new Request(id, pickupIntersectionId, pickupDuration, deliveryIntersectionId, deliveryDuration);
                dummyPickupDelivery.addRequest(courierId, request);
            }
        } catch (Exception e) {
            System.err.println("Error parsing requests from " + filepath + ": " + e.getMessage());
            // Fallback to dummy data if parsing fails
        }

        return dummyPickupDelivery;
    }

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

    public TreeMap<Long, Tour> parseTours(String filePath) {
        TreeMap<Long, Tour> tours = new TreeMap<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(filePath));
            document.getDocumentElement().normalize();
            NodeList tourNodes = document.getElementsByTagName("tour");
            for (int i = 0; i < tourNodes.getLength(); i++) {
                Element tourElement = (Element) tourNodes.item(i);
                long courierId = Long.parseLong(tourElement.getAttribute("courierId"));
                
                List<TourStop> stops = new ArrayList<>();
                NodeList stopNodes = tourElement.getElementsByTagName("stop");
                for (int j = 0; j < stopNodes.getLength(); j++) {
                    Element stopElement = (Element) stopNodes.item(j);
                    StopType type = StopType.valueOf(stopElement.getAttribute("type"));
                    long requestId = Long.parseLong(stopElement.getAttribute("requestId"));
                    long intersectionId = Long.parseLong(stopElement.getAttribute("intersectionId"));
                    long arrivalTime = 0; // Simplified
                    long departureTime = 0; // Simplified
                    stops.add(new TourStop(type, requestId, intersectionId, arrivalTime, departureTime));
                }
                
                long totalDistance = Long.parseLong(tourElement.getElementsByTagName("totalDistance").item(0).getTextContent());
                long totalDuration = Duration.parse(tourElement.getElementsByTagName("totalDuration").item(0).getTextContent()).toMillis();
                
                Tour tour = new Tour(courierId, stops, totalDistance, totalDuration);
                tours.put(courierId, tour);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tours;
    }

}