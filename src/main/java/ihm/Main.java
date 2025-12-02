package ihm;

import domain.service.RequestService;
import domain.service.TourService;
import ihm.controller.TourController;
import domain.model.Request;
import domain.model.StopType;

import java.time.Duration;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // Initialize services and controller
        RequestService requestService = new RequestService();
        requestService.loadRequests("src/main/resources/requests.xml"); // Load requests to test
        TourService tourService = new TourService();
        TourController tourController = new TourController(tourService, requestService);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("=== Tour Console IHM ===");
            System.out.println("1. Add courier");
            System.out.println("2. Remove courier");
            System.out.println("3. Update request order");
            System.out.println("4. Show request details (by intersection id)");
            System.out.println("5. Exit");
            System.out.print("Choice: ");

            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> {
                        System.out.print("Courier id: ");
                        long id = Long.parseLong(scanner.nextLine().trim());
                        System.out.print("Courier name: ");
                        String name = scanner.nextLine().trim();
                        System.out.print("Shift duration in minutes: ");
                        long minutes = Long.parseLong(scanner.nextLine().trim());
                        Duration shiftDuration = Duration.ofMinutes(minutes);

                        boolean ok = tourController.addCourier(id, name, shiftDuration);
                        System.out.println(ok ? "Courier added." : "Failed to add courier.");
                    }
                    case "2" -> {
                        System.out.print("Courier id to remove: ");
                        long id = Long.parseLong(scanner.nextLine().trim());
                        boolean ok = tourController.removeCourier(id);
                        System.out.println(ok ? "Courier removed." : "Courier not found.");
                    }
                    case "3" -> {
                        System.out.print("Courier id: ");
                        long courierId = Long.parseLong(scanner.nextLine().trim());
                        System.out.print("Request id that must be BEFORE: ");
                        long beforeId = Long.parseLong(scanner.nextLine().trim());
                        System.out.print("Request id that must be AFTER: ");
                        long afterId = Long.parseLong(scanner.nextLine().trim());

                        tourController.updateRequestOrder(beforeId, afterId, courierId);
                        System.out.println("Request order updated (if courier exists).");
                    }
                    case "4" -> {
                        System.out.print("Intersection id: ");
                        long intersectionId = Long.parseLong(scanner.nextLine().trim());

                        Map.Entry<Request, StopType> entry =
                                tourController.showRequestDetails(intersectionId);

                        if (entry == null) {
                            System.out.println("No request found for this intersection.");
                        } else {
                            Request r = entry.getKey();
                            StopType type = entry.getValue();
                            System.out.println("Request id: " + r.getId());
                            System.out.println("Pickup intersection id: " + r.getPickupIntersectionId());
                            System.out.println("Delivery intersection id: " + r.getDeliveryIntersectionId());
                            System.out.println("Stop type at this intersection: " + type);
                        }
                    }
                    case "5" -> {
                        running = false;
                        System.out.println("Exiting.");
                    }
                    default -> System.out.println("Unknown choice.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

            System.out.println();
        }

        scanner.close();
    }
}