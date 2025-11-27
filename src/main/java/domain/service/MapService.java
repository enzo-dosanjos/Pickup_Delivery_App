package domain.service;

import domain.model.Intersection;
import domain.model.Map;
import persistence.XMLParsers;
import persistence.XMLWriters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Vector;
import java.util.TreeMap;

@Service
public class MapService {
    private Map map;
    private final XMLParsers xmlParsers;
    private final XMLWriters xmlWriters;

    @Autowired
    public MapService(XMLParsers xmlParsers, XMLWriters xmlWriters) {
        this.xmlParsers = xmlParsers;
        this.xmlWriters = xmlWriters;
        this.map = new Map(); // Initialize with an empty map
    }

    /**
     * Loads a map from a specified file path.
     * @param filePath The path to the XML file containing the map data.
     * @return The loaded Map object.
     */
    public Map loadMap(String filePath) {
        map = xmlParsers.parseMap(filePath);
        return map;
    }

    /**
     * Saves the current map to a specified file path.
     * @param filePath The path to the XML file to save the map data.
     */
    public void saveMap(String filePath) {
        if (map != null) {
            xmlWriters.writeMap(map, filePath);
        } else {
            System.err.println("Cannot save map: No map currently loaded.");
        }
    }

    /**
     * Retrieves intersections whose names partially match the given name.
     * (Placeholder implementation)
     * @param name The partial name to search for.
     * @return A list of matching Intersection objects.
     */
    public Vector<Intersection> getIntersectionsPerName(String name) {
        Vector<Intersection> matchingIntersections = new Vector<>();
        if (map != null && map.getIntersections() != null) {
            for (Intersection intersection : map.getIntersections().values()) {
                // Assuming Intersection has a getName() method or similar for searching
                // For now, let's just return all intersections as a placeholder
                matchingIntersections.add(intersection);
            }
        }
        System.out.println("Searching for intersections matching: " + name);
        return matchingIntersections;
    }

    public Map getMap() {
        return map;
    }
}
