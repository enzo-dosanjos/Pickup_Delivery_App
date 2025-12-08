package domain.service;

import domain.model.Map;
import org.springframework.stereotype.Service;
import persistence.XMLParsers;

/**
 * Service class for managing map data.
 * Provides functionality to load and retrieve the map.
 */
@Service
public class MapService {
    /** The map object managed by the service. */
    private Map map;

    /**
     * Constructs a new MapService and initializes the map.
     */
    public MapService() {
        map = new Map();
    }

    /**
     * Loads the map from an XML file.
     *
     * @param filePath the path to the XML file containing the map data
     */
    public void loadMap(String filePath) {
        map = XMLParsers.parseMap(filePath);
    }

    /**
     * Retrieves the current map.
     *
     * @return the map object
     */
    public Map getMap() {
        return map;
    }
}
