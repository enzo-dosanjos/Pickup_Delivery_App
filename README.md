# Pickup & Delivery Optimization Application

An application for optimizing pickup and delivery tours in a city. This project provides functionalities for calculating optimal routes for a fleet of couriers based on a set of pickup and delivery requests.

## Features

.

### 1. Import Pickup/Delivery Requests

-   **Functionality:** Imports a list of pickup and delivery requests from an XML file (`planningRequest`).
-   **XML Structure:**
    -   A root `<planningRequest>` element.
    -   A `<depot>` element specifying the warehouse address (`address`).
    -   Multiple `<request>` elements, each with `pickupAddress`, `deliveryAddress`, `pickupDuration`, and `deliveryDuration` attributes.
-   **Implementation Details:**
    -   The `XMLParsers.parseRequests()` method handles the parsing logic.
    -   A `RequestService` orchestrates the import process.
    -   Data is loaded into a `PickupDelivery` model, which organizes requests by courier and stores the depot location.

### 2. Import Tours

-   **Functionality:** Imports a pre-defined tour for a specific courier from an XML file (`tour`).
-   **XML Structure:**
    -   A root `<tour>` element with a `courierId`.
    -   Multiple `<tourStop>` elements, each with `stopType` (pickup/delivery), `requestId`, `intersectionId`, `arrivalTime`, and `departureTime`.
-   **Implementation Details:**
    -   The `XMLParsers.parseTours()` method parses the tour file.
    -   The data is loaded into a `Tour` model, containing a list of `TourStop` objects.
    -   The parser handles case-insensitive `stopType` values and converts time strings to `LocalDateTime` objects.

### 3. Tour Optimization (TSP)

-   **Functionality:** The application includes a core feature for solving the Traveling Salesperson Problem (TSP) to find the shortest possible route that visits a set of stops and returns to the origin. This is essential for calculating optimized delivery tours.
-   **Implementation Details:**
    -   The `domain.service` package contains the TSP implementation.
    -   `TemplateTSP.java` provides a generic template for branch-and-bound TSP algorithms.
    -   `TSP1.java` is a specific implementation of the TSP algorithm, extending the template.
    -   These algorithms are designed to be used by the `TourService` to compute optimized tours based on the imported requests.

The project is organized into the following main packages:

-   `domain`: Contains the core business logic and data models.
    -   `domain.model`: Defines the main data classes (e.g., `Tour`, `Request`, `PickupDelivery`).
    -   `domain.service`: Contains services for business logic (e.g., `RequestService`, `TourService`).
-   `ihm` (Interface Homme-Machine): The presentation layer.
    -   `ihm.controller`: Controllers that connect the UI to the domain services (e.g., `RequestController`).
    -   `Main.java`: The main entry point of the application.
-   `persistence`: Handles data storage and retrieval.
    -   `XMLParsers.java`: Contains the logic for parsing XML data files.

The project's architecture is based on the design specified in the PlantUML diagrams located in the `/diagrams` directory.

## How to Run

To compile and run the application, you need to have **Java** and **Apache Maven** installed on your system.

1.  **Compile the project:**
    ```bash
    mvn compile
    ```
2.  **Run the application:**
    ```bash
    mvn exec:java -Dexec.mainClass="ihm.Main"
    ```
    The `Main` class is currently configured to run test-level code that executes both the `parseRequests` and `parseTours` methods and prints the parsed data to the console. You can inspect the output to verify that the XML files (`requests.xml` and `tour.xml`) are being read correctly.

## Current Limitations