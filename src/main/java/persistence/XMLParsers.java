package persistence;

import domain.model.Intersection;
import domain.model.Map;
import domain.model.RoadSegment;
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
}
