package domain.service;

import domain.model.PickupDelivery;
import org.jdom2.JDOMException;
import persistence.XMLParsers;

import java.io.IOException;

public class RequestService {

    private final XMLParsers xmlParsers;
    private PickupDelivery pickupDelivery;

    public RequestService(XMLParsers xmlParsers) {
        this.xmlParsers = xmlParsers;
    }

    public void loadRequests(String filepath) {
        try {
            this.pickupDelivery = xmlParsers.parseRequests(filepath);
        } catch (JDOMException | IOException e) {

            e.printStackTrace();
        }
    }

    public PickupDelivery getPickupDelivery() {
        return pickupDelivery;
    }
}
