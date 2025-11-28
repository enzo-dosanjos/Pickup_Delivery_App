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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

@Component
public class XMLParsers {

    public PickupDelivery parseRequests(String filepath) {
        PickupDelivery pickupDelivery = new PickupDelivery();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            File xmlFile = new File(filepath);
            if (!xmlFile.exists()) {
                System.err.println("Request XML file not found at: " + filepath);
                return pickupDelivery;
            }

            Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            Element root = doc.getDocumentElement();

            // Parse depot
            NodeList depotNodes = root.getElementsByTagName("depot");
            if (depotNodes.getLength() > 0) {
                Element depotElement = (Element) depotNodes.item(0);
                String addressStr = depotElement.getAttribute("address");
                if (addressStr == null || addressStr.isEmpty()) {
                    System.err.println("Warning: Depot address is missing.");
                } else {
                    long warehouseAddress = Long.parseLong(addressStr);
                    pickupDelivery.setWarehouseAddress(warehouseAddress);
                }
            }

            // Parse requests
            NodeList requestNodes = root.getElementsByTagName("request");
            for (int i = 0; i < requestNodes.getLength(); i++) {
                Element requestElement = (Element) requestNodes.item(i);
                String pickupAddressStr = requestElement.getAttribute("pickupAddress");
                String deliveryAddressStr = requestElement.getAttribute("deliveryAddress");
                String pickupDurationStr = requestElement.getAttribute("pickupDuration");
                String deliveryDurationStr = requestElement.getAttribute("deliveryDuration");

                if (pickupAddressStr == null || pickupAddressStr.isEmpty() ||
                    deliveryAddressStr == null || deliveryAddressStr.isEmpty() ||
                    pickupDurationStr == null || pickupDurationStr.isEmpty() ||
                    deliveryDurationStr == null || deliveryDurationStr.isEmpty()) {
                    System.err.println("Warning: Skipping a request due to missing attributes.");
                    continue;
                }

                long pickupAddress = Long.parseLong(pickupAddressStr);
                long deliveryAddress = Long.parseLong(deliveryAddressStr);
                long pickupDuration = Long.parseLong(pickupDurationStr);
                long deliveryDuration = Long.parseLong(deliveryDurationStr);

                // courierId is not in the file, so we'll assign a default one.
                long defaultCourierId = 1L;
                Request request = new Request(System.currentTimeMillis(), pickupAddress, pickupDuration, deliveryAddress, deliveryDuration);
                pickupDelivery.addRequest(defaultCourierId, request);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pickupDelivery;
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
                    long arrivalTime = LocalDateTime.parse(stopElement.getAttribute("arrivalTime")).atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
                    long departureTime = LocalDateTime.parse(stopElement.getAttribute("departureTime")).atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
                    stops.add(new TourStop(type, requestId, intersectionId, arrivalTime, departureTime));
                }

                List<RoadSegment> roadSegments = new ArrayList<>();
                NodeList roadSegmentNodes = tourElement.getElementsByTagName("segment");
                for (int j = 0; j < roadSegmentNodes.getLength(); j++) {
                    Element roadSegmentElement = (Element) roadSegmentNodes.item(j);
                    String name = roadSegmentElement.getAttribute("name");
                    double length = Double.parseDouble(roadSegmentElement.getAttribute("length"));
                    long startId = Long.parseLong(roadSegmentElement.getAttribute("startId"));
                    long endId = Long.parseLong(roadSegmentElement.getAttribute("endId"));
                    roadSegments.add(new RoadSegment(name, length, startId, endId));
                }

                long totalDistance = (long) Double.parseDouble(tourElement.getElementsByTagName("totalDistance").item(0).getTextContent());
                long totalDuration = Duration.parse(tourElement.getElementsByTagName("totalDuration").item(0).getTextContent()).toMillis();

                Tour tour = new Tour(courierId, stops, roadSegments, totalDistance, totalDuration);
                tours.put(courierId, tour);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tours;
    }

}