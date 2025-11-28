package ihm.controller;

import domain.model.Graphe;
import domain.model.GrapheComplet;
import domain.model.Request;
import domain.service.RequestService;
import domain.service.TSP;
import domain.service.TSP1;
import domain.service.TourService;

import java.time.Duration;

public class RequestController {

    private RequestService requestService;
    private TourService tourService;

    public RequestController(RequestService requestService, TourService tourService) {
        this.requestService = requestService;
        this.tourService = tourService;
    }

    public void loadRequests(String filepath) {
        requestService.loadRequests(filepath);
    }

    public void addRequest(Long warehouseId, Long pickupIntersectionId, Duration pickupDuration, Long deliveryIntersectionId, Duration deliveryDuration, Long courierId) {

        Request newRequest = new Request(pickupIntersectionId, pickupDuration, deliveryIntersectionId, deliveryDuration);

        TSP tsp = new TSP1();
        for (int nbSommets = 8; nbSommets <= 16; nbSommets += 2){
            System.out.println("Graphes de "+nbSommets+" sommets :");
            Graphe g = new GrapheComplet(nbSommets);
            long tempsDebut = System.currentTimeMillis();
            tsp.chercheSolution(60000, g);
            System.out.print("Solution de longueur "+tsp.getCoutSolution()+" trouvee en "
                    +(System.currentTimeMillis() - tempsDebut)+"ms : ");
            for (int i=0; i<nbSommets; i++)
                System.out.print(tsp.getSolution(i)+" ");
            System.out.println();
        }
    }
}
