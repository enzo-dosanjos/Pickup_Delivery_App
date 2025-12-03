package persistence;

import domain.model.Map;
import domain.model.PickupDelivery;
import domain.model.Request;
import domain.model.Tour;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Collection; // For writeTours method if needed

/**
 * Utility class for writing application data (Map, Requests, Tours) to XML files.
 */
@Component
public class XMLWriters {

    /**
     * Writes a single Tour object to an XML file.
     * @param tour The Tour object to write.
     * @param filePath The path to the XML file.
     */
    public void writeTour(Tour tour, String filePath) {
        System.out.println("Writing tour to: " + filePath);
        // Implementation for writing a single tour to XML
    }

    /**
     * Writes a collection of Tour objects to an XML file.
     * This method is an extension that might be useful for saving all tours at once.
     * @param tours The collection of Tour objects to write.
     * @param filePath The path to the XML file.
     */
    public void writeTours(Collection<Tour> tours, String filePath) {
        System.out.println("Writing multiple tours to: " + filePath);
        // Implementation for writing multiple tours to XML
    }

    /**
     * Writes a PickupDelivery object (containing requests) to an XML file.
     * @param pickupDelivery The PickupDelivery object to write.
     * @param filePath The path to the XML file.
     */
    public void writeRequests(PickupDelivery pickupDelivery, String filePath) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            // root element
            Element rootElement = doc.createElement("planningRequest");
            doc.appendChild(rootElement);

            // depot element
            Element depot = doc.createElement("depot");
            depot.setAttribute("address", String.valueOf(pickupDelivery.getWarehouseAdressId()));
            depot.setAttribute("departureTime", "8:0:0"); // Hardcoded as per file format
            rootElement.appendChild(depot);

            // request elements
            for (Request req : pickupDelivery.getRequests().values()) {
                Element requestElement = doc.createElement("request");
                requestElement.setAttribute("pickupAddress", String.valueOf(req.getPickupIntersectionId()));
                requestElement.setAttribute("deliveryAddress", String.valueOf(req.getDeliveryIntersectionId()));
                requestElement.setAttribute("pickupDuration", String.valueOf(req.getPickupDuration()));
                requestElement.setAttribute("deliveryDuration", String.valueOf(req.getDeliveryDuration()));
                rootElement.appendChild(requestElement);
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.STANDALONE, "no");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filePath));
            transformer.transform(source, result);

            System.out.println("Requests saved to: " + filePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes a Map object to an XML file.
     * @param map The Map object to write.
     * @param filePath The path to the XML file.
     */
    public void writeMap(Map map, String filePath) {
        System.out.println("Writing map to: " + filePath);
        // Implementation for writing map to XML
    }
}