# Pickup & Delivery Optimization Application

An application for optimizing pickup and delivery tours in a city. This project provides functionalities for calculating optimal routes for a fleet of couriers based on a set of pickup and delivery requests.

## Features

-   Interactive React frontend with a playful pixel-inspired UI and loading screen.
-   Load/save pickup/delivery requests and couriers from XML.
-   Compute optimized tours (TSP with precedence and service times).
-   Visualize tours on a map with per-courier colors, stop details, delete actions, and request detail popups.
-   Export tours to XML from the UI.

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
    -   Requests can also be added manually from the UI and saved back to XML.

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

## REST API

The backend provides a REST API to interact with the application's features. Here are the main endpoints:

### Map
-   `GET /api/map`: Retrieves the map data, including intersections and road segments.

### Requests
-   `POST /api/request/load`: Loads pickup and delivery requests from a specified XML file.
-   `POST /api/request/add`: Adds a new pickup and delivery request.
-   `POST /api/request/save`: Saves the current set of requests to an XML file.
-   `GET /api/request/warehouse`: Retrieves the warehouse intersection ID.

### Tours
-   `POST /api/tour/save`: Saves the computed tour of a courier to an XML file.
-   `POST /api/tour/show-request-details`: Returns the request and stop type for a given intersection, plus arrival/departure times when a tour is computed.
-   `POST /api/tour/load-couriers`: Loads couriers from XML.
-   `POST /api/tour/add-courier` / `POST /api/tour/remove-courier`: Manage couriers.
-   `POST /api/tour/update-request-order`: Updates the order of requests for a courier.
-   `GET /api/tour/tours`: Lists current tours.
-   `GET /api/tour/available-couriers`: Lists available couriers.

## How to Run

To compile and run the application, you need to have **Java** and **Apache Maven** installed for the backend, and **Node.js** and **npm** for the frontend.

### Backend (Spring Boot Application)

1.  Navigate to the root directory of the project.
2.  **Compile and run the application:**
    ```bash
    mvn spring-boot:run
    ```
    The backend server will start on `http://localhost:8080`.

### Frontend (React Application)

The frontend of this application is a React project located in the `react-app/` directory. It provides a user interface to visualize maps, tours, and interact with the backend services.

### How to Run the React App

To see the React display, you need to have Node.js and npm installed.

1.  **Navigate to the React application directory:**
    ```bash
    cd react-app
    ```
2.  **Install project dependencies:**
    ```bash
    npm install
    ```

3.  **Start the development server:**
    ```bash
    npm run dev
    ```
    This command will start a local development server and display a link you can click on to open the React application in your web browser.

**Important: Node.js Version:**
This project's React dependencies require Node.js version 20 or higher.

### React App Dependencies

The React app relies on the following main dependencies:
-   `react` & `react-dom`
-   `react-router` for routing
-   `leaflet` & `react-leaflet` for interactive maps
-   `vite` as the build tool

All dependencies are listed in `react-app/package.json` and are installed automatically with `npm install`.

### Frontend usage tips

-   On load, you’ll see a pixel-style loading screen. Once ready, use the control panel to:
    -   Load couriers (`/api/tour/load-couriers`) and requests (`/api/request/load`).
    -   Add requests manually, delete requests, and recompute tours.
    -   Export a tour to XML via the “Export Tour” action.
-   Tours are shown with per-courier colors; clicking a stop shows details including arrival/departure times.
