package persistence;

import domain.model.RoadSegment;
import domain.model.Tour;
import domain.model.TourStop;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.FileOutputStream;

public class XMLWriters {


    public static void exportTourToXml(Tour tour, String filePath) throws Exception {
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
            writer.writeCharacters(indent); // indentation
            writer.writeStartElement("step");
            writer.writeAttribute("origine_adresse", String.valueOf(seg.getStartId()));

            
            TourStop start = tour.getStopByIntersectionId(seg.getStartId());
            String type_1;
            if (start != null) {
                type_1 =  String.valueOf(start.getType());
            } else {
                type_1 = "intermediaire";
            }

            writer.writeAttribute("type_Start", type_1);
            writer.writeAttribute("destination_adresse", String.valueOf(seg.getEndId()));

            TourStop stop = tour.getStopByIntersectionId(seg.getEndId());
            String type_2;
            if (stop != null) {
                type_2 =  String.valueOf(stop.getType());
            } else {
                type_2 = "intermediaire";
            }
            writer.writeAttribute("type_Finish", type_2);
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
