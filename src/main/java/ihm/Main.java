package ihm;

import domain.model.*;
import domain.model.Map;
import domain.model.dijkstra.DijkstraTable;
import domain.service.TSP1;
import domain.service.TourService;
import persistence.XMLParsers;
import persistence.XMLWriters;
import domain.service.DijkstraService;

import java.time.LocalDateTime;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        // 1. Parse XML files
        Map map = XMLParsers.parseMap("src/main/resources/grandPlan.xml");
        PickupDelivery pickupDelivery = new PickupDelivery();
        pickupDelivery.loadRequests("src/main/resources/requests.xml");

        // 2. Build list of stops (warehouse + pickups + deliveries)
        int nbStops = 2 * pickupDelivery.getRequestsPerCourier().get(1L).length + 1;
        long[] stops = new long[nbStops];

        int idx = 0;
        stops[idx++] = pickupDelivery.getWarehouseAdressId();

        for (long reqId : pickupDelivery.getRequestsPerCourier().get(1L)) {
            Request r = pickupDelivery.getRequests().get(reqId);
            stops[idx++] = r.getPickupIntersectionId();
            stops[idx++] = r.getDeliveryIntersectionId();
        }

        // 3. biuldgraph
        GrapheComplet graph = new GrapheComplet(stops, nbStops);
        DijkstraTable dijkstraTable = new DijkstraTable();

        // 4. Distances with Dijkstra
        DijkstraService dijkstraService = new DijkstraService(map, graph);
        dijkstraService.computeShortestPath(dijkstraTable);

        // 5. Inicialize TSP
        TSP1 tsp = new TSP1();


        // 6.PRECEDENCES
    
        HashMap<Integer, Set<Integer>> precs = new HashMap<>();

        int requestIndex = 0;
        for (long reqId : pickupDelivery.getRequestsPerCourier().get(1L)) {
            int pickupIndex = 1 + requestIndex * 2;
            int deliveryIndex = pickupIndex + 1;

           
            precs.put(deliveryIndex, Set.of(pickupIndex));

            requestIndex++;
        }

        tsp.setPrecedences(precs);


        // 7. SERVICE TIMES
        double[] serviceTimes = new double[dijkstraService.getGraph().getNbSommets()];
        Arrays.fill(serviceTimes, 0); // warehouse = 0

        requestIndex = 0;
        for (long reqId : pickupDelivery.getRequestsPerCourier().get(1L)) {
            Request r = pickupDelivery.getRequests().get(reqId);

            int pickupIndex = 1 + requestIndex * 2;
            int deliveryIndex = pickupIndex + 1;

            serviceTimes[pickupIndex]   = r.getPickupDuration().getSeconds();   // dureeEnlevement
            serviceTimes[deliveryIndex] = r.getDeliveryDuration().getSeconds(); // dureeLivraison

            requestIndex++;
        }

        tsp.setServiceTimes(serviceTimes);

        // 8. execute TSP (SOP)
        tsp.chercheSolution(30000, dijkstraService.getGraph());

        // 9. result
      

        double[] serviceTimesUsed = tsp.getServiceTimes();

        System.out.println("\n============== OPTIMAL TOUR ==============\n");

        double currentTime = 0.0;  // time

        for (int i = 0; i < dijkstraService.getGraph().getNbSommets(); i++) {
            int node = tsp.getSolution(i);        
            long intersectionId = dijkstraService.getGraph().getSommets()[node];

            if (i > 0) {
                int prev = tsp.getSolution(i - 1);
                currentTime += dijkstraService.getGraph().getCout(prev, node);
            }

            // Arrival
            double arrival = currentTime;

            // Service time
            double service = serviceTimesUsed[node];
            double departure = arrival + service;

            //(warehouse / pickup / delivery)
            String label;
            if (node == 0) {
                label = "WAREHOUSE";
            } else {
                int logicalIdx = node - 1;
                if (logicalIdx % 2 == 0)
                    label = "PICKUP #" + (logicalIdx/2 + 1);
                else
                    label = "DELIVERY #" + (logicalIdx/2 + 1);
            }

            System.out.printf(
                    "%-12s | Node %2d | ID: %-12d | Arrive: %8.1f s | Service: %5.1f s | Depart: %8.1f s\n",
                    label, node, intersectionId, arrival, service, departure
            );

            currentTime = departure;
        }

        //return  warehouse
        int last = tsp.getSolution(dijkstraService.getGraph().getNbSommets() - 1);
        double finalReturn = dijkstraService.getGraph().getCout(last, 0);
        currentTime += finalReturn;

        System.out.println("\nReturn to warehouse: +" + finalReturn + " seconds");
        System.out.println("TOTAL TOUR DURATION: " + currentTime + " seconds");
        System.out.println("===========================================\n");

        // 10. convert graph to tour

        System.out.println("\n========== TEST convertGraphToTour() ==========\n");

        Integer[] sol = new Integer[graph.getNbSommets()];
        for (int i = 0; i < sol.length; i++)
            sol[i] = tsp.getSolution(i);

        Long[] vertices = Arrays.stream(graph.getSommets()).boxed().toArray(Long[]::new);

        TourService tourService = new TourService();
        LocalDateTime start = LocalDateTime.now();

        Tour tour = tourService.convertGraphToTour(
                pickupDelivery, start, 1L, sol, vertices, graph.getCout()
        );

        System.out.println("Generated tour stops:");
        for (TourStop stop : tour.getStops()) {
            System.out.println(stop);
        }

        System.out.println("\nTotal duration (min): " + tour.getTotalDuration().toMinutes());


        // 11. add roads to tour

        System.out.println("\n========== TEST addRoadsToTour() ==========\n");
        System.out.println("Road segments not found: ");
        tour = tourService.addRoadsToTour(tour, dijkstraTable, map);

        System.out.println("Road segments added: " + tour.getRoadSegmentsTaken().size());
        System.out.println("Total distance: " + tour.getTotalDistance() + " meters");

        System.out.println("\n===== ROAD SEGMENTS =====");
        tour.getRoadSegmentsTaken().forEach(seg ->
                System.out.println(seg.getStartId() + " → " +
                        seg.getEndId() + " | " +
                        "len=" + seg.getLength() )
        );

        //12. Export tour
        try {
            XMLWriters.exportTourToXml(tour, "tour.xml");
            System.out.println("Tour exported successfully!");
        } catch (Exception e) {
            //Auto-generated catch block
            System.out.println("------Fail exporting tour------");
            e.printStackTrace();
        }
       

        System.out.println("\n========= END TESTS =========\n");

    }
}
