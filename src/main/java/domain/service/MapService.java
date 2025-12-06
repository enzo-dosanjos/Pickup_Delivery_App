package domain.service;

import domain.model.Map;
import org.springframework.stereotype.Service;
import persistence.XMLParsers;

@Service
public class MapService {
    private Map map;

    public MapService() {
        map = new Map();
    }

    public void loadMap(String filePath) {
        map = XMLParsers.parseMap(filePath);
    }

    public Map getMap() {
        return map;
    }
}
