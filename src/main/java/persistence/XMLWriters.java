package persistence;

import domain.model.RoadSegment;
import domain.model.Tour;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.FileOutputStream;

public class XMLWriters {


    public static void exportTourToXml(Tour tour, String filePath) throws Exception {
        XMLStreamWriter writer = XMLOutputFactory.newInstance()
                .createXMLStreamWriter(new FileOutputStream(filePath), "UTF-8");

        String indent = "    "; 

        writer.writeStartDocument("UTF-8", "1.0");

        // <Tour courierId="..." startTime="...">
        writer.writeStartElement("Tour");
        writer.writeAttribute("courierId", String.valueOf(tour.getCourierId()));
        writer.writeAttribute("startTime", tour.getStartTime().toString());
        writer.writeCharacters("\n");


        // Write each road segment as a <step>
        for (RoadSegment seg : tour.getRoadSegmentsTaken()) {
            writer.writeCharacters(indent); // indentation
            writer.writeStartElement("step");
            writer.writeAttribute("origine_adresse", String.valueOf(seg.getStartId()));
            writer.writeAttribute("destination_adresse", String.valueOf(seg.getEndId()));
            writer.writeEndElement(); // </step>
            writer.writeCharacters("\n");

        }

        writer.writeEndElement(); // </Tour>
        writer.writeCharacters("\n");
        writer.writeEndDocument();

        writer.flush();
        writer.close();
    }
}
