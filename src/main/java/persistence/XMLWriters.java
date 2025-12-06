package persistence;

import domain.model.*;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
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
    public static void writeTour(Tour tour, String filePath) throws Exception {
        XMLStreamWriter writer = XMLOutputFactory.newInstance()
                .createXMLStreamWriter(new FileOutputStream(filePath), "UTF-8");

        String indent = "    "; 

        writer.writeStartDocument("UTF-8", "1.0");
        writer.writeCharacters("\n");
        
        // <Tour courierId="..." startTime="...">
        writer.writeStartElement("Tour");
        writer.writeAttribute("courierId", String.valueOf(tour.getCourierId()));
        writer.writeAttribute("startTime", tour.getStartTime().toString());
        writer.writeAttribute("TotalDistance", String.valueOf(tour.getTotalDistance()));
        writer.writeAttribute("TotalDuration", String.valueOf(tour.getTotalDuration().toMinutes()));
        writer.writeCharacters("\n");


        // Write each road segment as a <step>
        for (RoadSegment seg : tour.getRoadSegmentsTaken()) {
            writer.writeCharacters(indent);
            writer.writeStartElement("step");

            writer.writeAttribute("origine_adresse", String.valueOf(seg.getStartId()));
            writer.writeAttribute("destination_adresse", String.valueOf(seg.getEndId()));

            TourStop start = tour.getStopByIntersectionId(seg.getStartId());
            String type_1 = (start != null) ? start.getType().toString() : "intermediaire";
            writer.writeAttribute("type_Start", type_1);

            TourStop stop = tour.getStopByIntersectionId(seg.getEndId());
            String type_2 = (stop != null) ? stop.getType().toString() : "intermediaire";
            writer.writeAttribute("type_Finish", type_2);

            // Only add departureTime if start is not intermediaire
            if (start != null && !"intermediaire".equalsIgnoreCase(type_1) && start.getDepartureTime() != null) {
                writer.writeAttribute("departureTime", start.getDepartureTime().toString());
            }

            // Only add arrivalTime if destination is not intermediaire
            if (stop != null && !"intermediaire".equalsIgnoreCase(type_2) && stop.getArrivalTime() != null) {
                writer.writeAttribute("arrivalTime", stop.getArrivalTime().toString());
            }

            writer.writeEndElement(); // </step>
            writer.writeCharacters("\n");
        }


        writer.writeEndElement(); // </Tour>
        writer.writeCharacters("\n");
        writer.writeEndDocument();

        writer.flush();
        writer.close();
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
