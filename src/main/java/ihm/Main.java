package ihm;

import domain.model.*;
import domain.model.Map;
import domain.service.PlanningService;
import domain.service.RequestService;
import domain.service.TourService;
import ihm.controller.RequestController;
import persistence.XMLParsers;

import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        // Domain + services
        Map map = XMLParsers.parseMap("src/main/resources/grandPlan.xml");
        RequestService requestService = new RequestService();
        PickupDelivery pickupDelivery = requestService.getPickupDelivery();
        TourService tourService = new TourService();
        PlanningService planningService = new PlanningService(map, requestService, tourService);

        Courier courier1 = new Courier(1L, "Courier 1", Duration.ofHours(8));
        tourService.addCourier(courier1);

        // Controller
        RequestController requestController = new RequestController(requestService, planningService);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

            while (running) {
            System.out.println("\n===== REQUEST / TOUR CONSOLE IHM =====");
            System.out.println("1) Load requests from XML");
            System.out.println("2) Add a new request (courier 1)");
            System.out.println("3) List current requests");
            System.out.println("4) Quit");
            System.out.print("Choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> loadRequestsMenu(scanner, requestController);
                case "2" -> addRequestMenu(scanner, requestController);
                case "3" -> listRequests(pickupDelivery);
                case "4" -> running = false;
                default -> System.out.println("Unknown choice.");
            }
        }

            scanner.close();
    }

    private static void loadRequestsMenu(Scanner scanner, RequestController requestController) {
        System.out.print("Courier id: ");
        long id = Long.parseLong(scanner.nextLine().trim());

        System.out.print("Enter XML requests file path (by default : `src/main/resources/requests.xml`): ");
        String path = scanner.nextLine().trim();
        if (path.isEmpty()) {
            path = "src/main/resources/requests.xml";
        }

        try {
            requestController.loadRequests(path, id);
            System.out.println("Requests loaded and internal model updated.");
        } catch (Exception e) {
            System.out.println("Error while loading requests: " + e.getMessage());
        }
    }

    private static void addRequestMenu(Scanner scanner, RequestController requestController) {
        try {
            System.out.print("Courier id: ");
            long courierId = Long.parseLong(scanner.nextLine().trim());

            System.out.print("Pickup intersection id: ");
            long pickupId = Long.parseLong(scanner.nextLine().trim());

            System.out.print("Pickup duration in seconds: ");
            long pickupSec = Long.parseLong(scanner.nextLine().trim());

            System.out.print("Delivery intersection id: ");
            long deliveryId = Long.parseLong(scanner.nextLine().trim());

            System.out.print("Delivery duration in seconds: ");
            long deliverySec = Long.parseLong(scanner.nextLine().trim());

            // In this simple IHM we fix:
            // \- warehouse id is not used by Request constructor here
            Long warehouseId = 0L;

            requestController.addRequest(
                    warehouseId,
                    pickupId,
                    Duration.ofSeconds(pickupSec),
                    deliveryId,
                    Duration.ofSeconds(deliverySec),
                    courierId
            );

            System.out.println("Request added and tour recomputed for courier " + courierId + ".");
        } catch (NumberFormatException e) {
            System.out.println("Invalid numeric value: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error while adding request: " + e.getMessage());
        }
    }

    private static void listRequests(PickupDelivery pickupDelivery) {
        TreeMap<Long, Request> requests = pickupDelivery.getRequests();
        if (requests.isEmpty()) {
            System.out.println("No requests loaded.");
            return;
        }

        System.out.println("\nCurrent requests:");
        for (Request r : requests.values()) {
            System.out.println(
                    "Request #" + r.getId()
                            + " | pickup=" + r.getPickupIntersectionId()
                            + " (" + formatDuration(r.getPickupDuration()) + ")"
                            + " | delivery=" + r.getDeliveryIntersectionId()
                            + " (" + formatDuration(r.getDeliveryDuration()) + ")"
            );
        }
    }

    private static String formatDuration(Duration d) {
        long sec = d.getSeconds();
        long h = sec / 3600;
        long m = (sec % 3600) / 60;
        long s = sec % 60;
        return LocalTime.of((int) h, (int) m, (int) s).toString();
    }
}