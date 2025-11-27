package domain.service;

import domain.model.Map;
import persistence.XMLParsers;

public class MapService {
    private Map map;

    public MapService() {
        this.map = new Map();
    }

    public MapService(String filePath) {
        loadMap(filePath);
    }

    public void loadMap(String filePath) {
        XMLParsers parser = new XMLParsers();

        map = parser.parseMap(filePath);
    }

    public Map getMap() {
        return map;
    }
}
