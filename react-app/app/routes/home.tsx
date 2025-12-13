import type { Route } from "./+types/home";
import { Map as MapComponent, type Intersection as MapIntersection, type Tour as MapTour, StopType as MapStopType } from "../map/map";
import { useState, useEffect, useRef } from "react";
import L from "leaflet";
import { ModificationPanel, type Courier as PanelCourier } from "../components/ModificationPanel";
import { CourierSelectionPanel } from "~/components/CourierSelectionPanel";
import "../components/ModificationPanel.css";
import Modal from "../components/Modal";
import "./home.css";
import {UpdateOrderPanel} from "~/components/UpdateOrderPanel";
import {useMap} from "react-leaflet";

// Define the types for the data we expect from the API
type ApiIntersection = {
    id: number;
    lat: number;
    lng: number;
};

type ApiRoadSegment = {
    startId: number;
    endId: number;
    name: string;
};

type ApiMapData = {
    intersections: Record<string, ApiIntersection>;
    adjacencyList: Record<string, ApiRoadSegment[]>;
};

type ApiTourStop = {
    type: MapStopType;
    requestID: number;
    intersectionId: number;
    arrivalTime: string;
    departureTime: string;
};

type ApiTour = {
    courierId: number;
    stops: ApiTourStop[];
    roadSegmentsTaken: ApiRoadSegment[];
    totalDistance: number;
    totalDuration: number;
};

type ApiRequest = {
    id: number;
    pickupIntersectionId: number;
    pickupDuration: number;
    deliveryIntersectionId: number;
    deliveryDuration: number;
}

type ApiCourier = {
    id: number;
    name: string;
    shiftDuration: string;
}

export function meta({}: Route.MetaArgs) {
    return [
        { title: "Pick-up & Delivery App" },
        { name: "description", content: "Welcome to our brand new pick-up & delivery app !" },
    ];
}

export default function Home() {
    const [intersections, setIntersections] = useState<MapIntersection[]>([]);
    const [roadSegments, setRoadSegments] = useState<L.LatLngExpression[][]>([]);
    const [intersectionIdToRoadName, setIntersectionIdToRoadName] = useState<Map<number, string>>(new Map());
    const [bounds, setBounds] = useState<L.LatLngExpression[]>([]);
    const [loading, setLoading] = useState(true);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [modalMessage, setModalMessage] = useState("");
    const [modalActions, setModalActions] = useState<{ label: string, onClick: () => void }[]>([]);
    const [isAddingRequest, setIsAddingRequest] = useState(false);
    const [isLoadingCouriers, setIsLoadingCouriers] = useState(false);
    const [isLoadingRequests, setIsLoadingRequests] = useState(false);
    const [isSavingRequests, setIsSavingRequests] = useState(false);
    const [isDeletingRequest, setIsDeletingRequest] = useState(false);
    const [tours, setTours] = useState<MapTour[]>([]);
    const [loadingTours, setLoadingTours] = useState(true);
    const [isExportingTour, setIsExportingTour] = useState(false);

    const [isSidePanelOpen, setIsSidePanelOpen] = useState<boolean>(true);
    const [isModificationPanelOpen, setIsModificationPanelOpen] = useState(false);
    const [selectionMode, setSelectionMode] = useState<'pickup' | 'delivery' | 'warehouse' | 'stops_only' | null>(null);
    const [pickupId, setPickupId] = useState<number | null>(null);
    const [deliveryId, setDeliveryId] = useState<number | null>(null);
    const [pickupName, setPickupName] = useState<string | null>(null);
    const [deliveryName, setDeliveryName] = useState<string | null>(null);
    const defaultDuration = 120;
    const [pickupDuration, setPickupDuration] = useState<number>(defaultDuration);
    const [deliveryDuration, setDeliveryDuration] = useState<number>(defaultDuration);
    const [couriersList, setCouriersList] = useState<PanelCourier[]>([]);
    const [selectedCourier, setSelectedCourier] = useState<string>("0");
    const [warehouseIds, setWarehouseIds] = useState<Map<string, number>>(() => new Map());
    const [selectionHint, setSelectionHint] = useState<string | null>(null);
    const [displayedCouriers, setDisplayedCouriers] = useState<string>("All");
    const [displayedTours, setDisplayedTours] = useState<MapTour[]>([]);
    const [displayedWarehouses, setDisplayedWarehouses] = useState<number[]>([]);
    const [prevStopIndex, setPrevStopIndex] = useState("");
    const [nextStopIndex, setNextStopIndex] = useState("");
    const [isUpdatingOrder, setIsUpdatingOrder] = useState(false);

    // file paths
    const [requestFilePath, setRequestFilePath] = useState<string>("src/main/resources/requests.xml");
    const [courierFilePath, setCourierFilePath] = useState<string>("src/main/resources/couriers.xml");
    const [exportTourFilePath, setExportTourFilePath] = useState<string>("src/main/resources/exported_tour.xml");
    const [exportRequestsFilePath, setExportRequestsFilePath] = useState<string>("src/main/resources/exported_requests.xml");

    const fetchInitiated = useRef(false);

    // store intersectionMap globally for this component
    const intersectionMapRef = useRef<Map<number, ApiIntersection> | null>(null);

    const closeModal = () => {
        setIsModalOpen(false);
        setModalMessage("");
        setModalActions([]);
    };

    useEffect(() => {
        if (fetchInitiated.current) {
            return;
        }
        fetchInitiated.current = true;

        const fetchData = async () => {
            try {
                // Fetch Map Data
                const mapResponse = await fetch("http://localhost:8080/api/map");
                if (!mapResponse.ok) {
                    throw new Error(`HTTP error! status: ${mapResponse.status}`);
                }
                const mapData: ApiMapData = await mapResponse.json();
                console.log("Raw map data from API:", mapData);

                const intersectionMap = new Map<number, ApiIntersection>();
                for (const key in mapData.intersections) {
                    const intersection = mapData.intersections[key];
                    if (intersection && typeof intersection.id !== 'undefined') {
                        intersectionMap.set(intersection.id, intersection);
                    }
                }
                console.log("Constructed intersectionMap:", intersectionMap);

                // save for later use in displayTour / handleAddRequest
                intersectionMapRef.current = intersectionMap;

                const nameMap = new Map<number, string>();
                const transformedRoadSegments: L.LatLngExpression[][] = [];
                for (const startIdStr in mapData.adjacencyList) {
                    const segments = mapData.adjacencyList[startIdStr];
                    const startId = parseInt(startIdStr, 10);
                    const startIntersection = intersectionMap.get(startId);

                    if (startIntersection) {
                        segments.forEach(segment => {
                            if (segment.name && segment.name.trim() !== "") { // Use first non-empty name
                                if (!nameMap.has(startId)) nameMap.set(startId, segment.name);
                                if (!nameMap.has(segment.endId)) nameMap.set(segment.endId, segment.name);
                            }
                            const endIntersection = intersectionMap.get(segment.endId);
                            if (endIntersection) {
                                transformedRoadSegments.push([
                                    [startIntersection.lat, startIntersection.lng],
                                    [endIntersection.lat, endIntersection.lng]
                                ]);
                            } else {
                                console.warn(`Could not find endIntersection for id: ${segment.endId}`);
                            }
                        });
                    } else {
                        console.warn(`Could not find startIntersection for id: ${startIdStr}`);
                    }
                }
                setIntersectionIdToRoadName(nameMap);

                const transformedIntersections: MapIntersection[] = Array.from(intersectionMap.values()).map(i => {
                    return {
                        id: i.id,
                        position: [i.lat, i.lng],
                    };
                });


                if (transformedIntersections.length > 0) {
                    const latitudes = transformedIntersections.map(i => (i.position as [number, number])[0]);
                    const longitudes = transformedIntersections.map(i => (i.position as [number, number])[1]);
                    const minLat = Math.min(...latitudes);
                    const maxLat = Math.max(...latitudes);
                    const minLng = Math.min(...longitudes);
                    const maxLng = Math.max(...longitudes);
                    setBounds([[minLat, minLng], [maxLat, maxLng]]);
                }
                setIntersections(transformedIntersections);
                setRoadSegments(transformedRoadSegments);

                // Finally, fetch and display tours
                await fetchTours();

            } catch (e: any) {
                console.error("Caught error object:", e);
                if (e.message && e.message.includes("TSP algorithm did not find a solution")) {
                    setModalMessage("Failed to fetch initial data: No tour could be found with the current requests. Please modify requests.");
                } else {
                    setModalMessage(`Failed to fetch data: ${e.message}`);
                }
                setIsModalOpen(true);
            } finally {
                setLoading(false);
                setLoadingTours(false);
            }
        };

        fetchData();
    }, []);

    useEffect(() => {
        if (couriersList && couriersList.length > 0) {
            setSelectedCourier(couriersList[0].id.toString());
        }
    }, [couriersList]);

    useEffect(() => {
        if (displayedCouriers === "All") {
            setDisplayedTours(tours);
            setDisplayedWarehouses(Array.from(warehouseIds.values()));
        } else {
            setDisplayedTours(tours.filter(tour => tour.courierId.toString() === displayedCouriers))
            setDisplayedWarehouses([warehouseIds.get(displayedCouriers) || -1]);
        }

    }, [tours, displayedCouriers]);

    const handleMapClick = (intersectionId: number) => {
        const roadName = intersectionIdToRoadName.get(intersectionId) || `Intersection ${intersectionId}`;
        if (selectionMode === 'pickup') {
            setPickupId(intersectionId);
            setPickupName(roadName);
            setSelectionMode(null); // Deactivate selection mode after a point is chosen
            setSelectionHint(null);
        } else if (selectionMode === 'delivery') {
            setDeliveryId(intersectionId);
            setDeliveryName(roadName);
            setSelectionMode(null); // Deactivate selection mode
            setSelectionHint(null);
        } else if (selectionMode === 'warehouse') {
            setSelectionMode(null);
            setSelectionHint(null);

            const params = new URLSearchParams();
            params.append('warehouseId', intersectionId.toString());
            params.append('courierId', selectedCourier);

            fetch('http://localhost:8080/api/request/addWarehouse', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: params,
            }).then(res => {
                if (!res.ok) {
                    throw new Error(`HTTP error! status: ${res.status}`);
                }
                // refresh warehouse ids
                fetchTours();

                // if a pickup and delivery are already chosen, finish the original add\-request flow
                if (pickupId !== null && deliveryId !== null) {
                    handleAddRequest();
                } else {
                    setModalMessage("Warehouse set successfully.");
                    setModalActions([]);
                    setIsModalOpen(true);
                }
            }).catch(e => {
                setModalMessage(`Failed to set warehouse: ${e.message}`);
                setModalActions([]);
                setIsModalOpen(true);
            });
        }
    };

    const fetchTours = async () => {
        if (!intersectionMapRef.current) {
            throw new Error("intersectionMap not initialized");
        }
        const intersectionMap = intersectionMapRef.current;

        const warehousesResponse = await fetch('http://localhost:8080/api/request/warehouse');

        if (!warehousesResponse.ok) {
            throw new Error(`HTTP error! status: ${warehousesResponse.status}`);
        }
        const data = await warehousesResponse.json();
        const fetchedWarehouseIds = new Map<string, number>(Object.entries(data));
        setWarehouseIds(fetchedWarehouseIds);

        // Fetch tours from backend
        const toursResponse = await fetch("http://localhost:8080/api/tour/tours");
        if (!toursResponse.ok) {
            throw new Error(`HTTP error! status: ${toursResponse.status}`);
        }

        // Backend returns Map<Long, Tour> -> JSON object: { "1": {..tour..}, "2": {..} }
        const toursJson: Record<string, ApiTour> = await toursResponse.json();

        const apiTours: ApiTour[] = Object.values(toursJson);
        console.log("Raw tour data from API:", apiTours);

        // Transform Tour Data
        const transformedTours: MapTour[] = apiTours.map(tour => {
            const transformedRoadSegments: L.LatLngExpression[][] = tour.roadSegmentsTaken.map(segment => {
                const startIntersection = intersectionMap.get(segment.startId);
                const endIntersection = intersectionMap.get(segment.endId);
                if (startIntersection && endIntersection) {
                    return [
                        [startIntersection.lat, startIntersection.lng],
                        [endIntersection.lat, endIntersection.lng]
                    ] as L.LatLngExpression[];
                }
                return null;

            }).filter((s): s is L.LatLngExpression[] => s !== null);

            return {
                courierId: tour.courierId,
                stops: tour.stops,
                roadSegmentsTaken: transformedRoadSegments,
                totalDistance: tour.totalDistance,
                totalDuration: tour.totalDuration,
            };
        });

        setTours(transformedTours);
    };

    const handleLoadCouriers = async () => {
        setIsLoadingCouriers(true);
        try {
            const params = new URLSearchParams();
            params.append("filepath", courierFilePath);

            const response = await fetch("http://localhost:8080/api/tour/load-couriers", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded",
                },
                body: params,
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            console.log("Couriers loaded successfully");

            fetchAvailableCouriers();

            setModalMessage("Couriers loaded successfully!");
            setIsModalOpen(true);
        } catch (e: any) {
            console.error("Failed to load couriers:", e);
            setModalMessage(`Failed to load couriers: ${e.message}`);
            setIsModalOpen(true);
        } finally {
            setIsLoadingCouriers(false);
        }
    };

    const handleLoadRequests = async () => {
        setIsLoadingRequests(true);
        try {
            // Load requests
            const requestParams = new URLSearchParams();
            requestParams.append("filepath", requestFilePath);
            requestParams.append("courierId", selectedCourier);

            const requestResponse = await fetch("http://localhost:8080/api/request/load", {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: requestParams,
            });

            if (!requestResponse.ok) {
                const errorText = await requestResponse.text();
                throw new Error(errorText || `HTTP error! status: ${requestResponse.status}`);
            }

            console.log('Requests loaded successfully');
            setModalMessage("Requests loaded successfully!");
            setIsModalOpen(true);
            await fetchTours();
            setDisplayedCouriers(selectedCourier);

        } catch (e: any) {
            console.error("Failed to load requests:", e);
            setModalMessage(`Failed to load requests: ${e.message}`);
            setIsModalOpen(true);
        } finally {
            setIsLoadingRequests(false);
        }
    };

    const fetchAvailableCouriers = async () =>
    {
        try {
            // Fetch couriers from backend
            const couriersResponse = await fetch("http://localhost:8080/api/tour/available-couriers");
            if (!couriersResponse.ok) {
                throw new Error(`HTTP error! status: ${couriersResponse.status}`);
            }

            // Backend returns ArrayList<Courier> -> JSON object: { "1": {..tour..}, "2": {..} }
            const apiCouriers: ApiCourier[] = await couriersResponse.json();
            console.log("Raw couriers data from API:", apiCouriers);

            setCouriersList(apiCouriers);

            console.log("Couriers list fetched successfully");
        } catch (e: any) {
            console.error("Failed to fetch couriers:", e);
            setModalMessage(`Failed to fetch couriers: ${e.message}`);
            setModalActions([]);
            setIsModalOpen(true);
        }
    }

    const handleAddRequest = async () => {
        if (pickupId === null || deliveryId === null) {
            setModalMessage("Please select both pickup and delivery locations on the map.");
            setModalActions([]);
            setIsModalOpen(true);
            return;
        }

        setIsAddingRequest(true);
        try {
            // First, get the warehouse ID from the backend
            const warehouseResponse = await fetch('http://localhost:8080/api/request/warehouse');
            if (!warehouseResponse.ok) {
                throw new Error(`HTTP error! status: ${warehouseResponse.status}`);
            }

            const data = await warehouseResponse.json();
            const fetchedWarehouseIds = new Map<string, number>(Object.entries(data));
            setWarehouseIds(fetchedWarehouseIds);

            const courierWarehouseId = fetchedWarehouseIds.get(selectedCourier);


            // If warehouse not set yet, prompt user to add it before proceeding
            if (courierWarehouseId === undefined || courierWarehouseId === -1) {
                setModalMessage("Warehouse is not set. Click 'Add warehouse' then select a point on the map.");
                setModalActions([{
                    label: "Add warehouse",
                    onClick: () => {
                        closeModal();
                        setSelectionMode('warehouse');
                        setSelectionHint("Click a point on the map to set the warehouse.");
                    }
                }]);
                setIsModalOpen(true);
                return;
            }

            // Now, add the request with the fetched warehouse ID
            const params = new URLSearchParams();
            params.append('warehouseId', warehouseIds?.get(selectedCourier)?.toString() || "");
            params.append('pickupIntersectionId', pickupId.toString());
            params.append('pickupDurationInSeconds', pickupDuration.toString());
            params.append('deliveryIntersectionId', deliveryId.toString());
            params.append('deliveryDurationInSeconds', deliveryDuration.toString());
            params.append('courierId', selectedCourier);

            const response = await fetch('http://localhost:8080/api/request/add', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: params,
            });

            if (!response.ok) {
                const errorText = await response.text();
                if (errorText.includes("TSP algorithm did not find a solution")) {
                    throw new Error("TSP algorithm did not find a solution");
                }
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            console.log('Request added successfully');
            setModalMessage("Request added successfully!");
            setIsModalOpen(true);

            // Finally, fetch and display tours
            await fetchTours();
            setDisplayedCouriers(selectedCourier);

            setPickupId(null)
            setDeliveryId(null)
        } catch (e: any) {
            console.error("Failed to add request:", e);
            if (e.message && e.message.includes("TSP algorithm did not find a solution")) {
                setModalMessage("Failed to add request: No tour could be found for this courier. Please try with different parameters or another courier.");
            } else {
                setModalMessage(`Failed to add request: ${e.message}`);
            }
            setIsModalOpen(true);
        } finally {
            setIsAddingRequest(false);
            setIsModificationPanelOpen(false);
            setSelectionMode(null);
            setPickupName(null);
            setDeliveryName(null);
            setPickupDuration(defaultDuration);
            setDeliveryDuration(defaultDuration);
        }
    };

    const handleUpdateOrderClick = async () => {
        if (displayedCouriers === "All") {
            setModalMessage("Please select a specific courier to update stop order.");
            setModalActions([]);
            setIsModalOpen(true);
            return;
        }

        const t = tours.find(t => t.courierId.toString() === displayedCouriers);
        if (!t) {
            setModalMessage("No tour available.");
            setModalActions([]);
            setIsModalOpen(true);
            return;
        }

        const prevIndex = parseInt(prevStopIndex, 10);
        const nextIndex = parseInt(nextStopIndex, 10);

        if (isNaN(prevIndex) || isNaN(nextIndex)) {
            setModalMessage("Please enter valid indices.");
            setModalActions([]);
            setIsModalOpen(true);
            return;
        }

        setIsUpdatingOrder(true);
        try {
            await handleUpdateStopOrder(t.courierId, prevIndex, nextIndex);
        } finally {
            setIsUpdatingOrder(false);
        }
    };

    const handleUpdateStopOrder = async (
        courierId: number,
        precStopIndex: number,
        followingStopIndex: number
    ) => {
        try {
            const params = new URLSearchParams();
            params.append("courierId", courierId.toString());
            params.append("precStopIndex", precStopIndex.toString());
            params.append("followingStopIndex", followingStopIndex.toString());

            const response = await fetch("http://localhost:8080/api/tour/update-stop-order", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded",
                },
                body: params,
            });

            if (!response.ok) {
                const errorText = await response.text();
                // Show error in the app without throwing
                setModalMessage(`Failed to update stop order: ${errorText}`);
                setModalActions([]);
                setIsModalOpen(true);
                return; // stop further processing
            }

            console.log("Stop order updated successfully");

            // Reload the tour to see changes
            await fetchTours();

        } catch (e: any) {
            console.error("Unexpected error while updating stop order:", e);
            // Still catch unexpected errors without breaking the app
            setModalMessage(`Unexpected error: ${e.message}`);
            setModalActions([]);
            setIsModalOpen(true);
        }
    };

    const handleClosePanel = () => {
        setIsModificationPanelOpen(false);
        setSelectionMode(null);
        setPickupId(null);
        setDeliveryId(null);
        setPickupName(null);
        setDeliveryName(null);
        setPickupDuration(defaultDuration);
        setDeliveryDuration(defaultDuration);
    };

    const openModificationPanel = () => {
        fetchAvailableCouriers();
        setIsModificationPanelOpen(true);
    };

    const handleSaveRequests = async () => {
        setIsSavingRequests(true);
        try {
            const params = new URLSearchParams();
            params.append('filepath', 'src/main/resources/requests_updated.xml');

            const response = await fetch('http://localhost:8080/api/request/save', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: params,
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            console.log('Requests saved successfully');
            setModalMessage("Requests saved successfully!");
            setIsModalOpen(true);
        } catch (e: any) {
            console.error("Failed to save requests:", e);
            setModalMessage(`Failed to save requests: ${e.message}`);
            setIsModalOpen(true);
        } finally {
            setIsSavingRequests(false);
        }
    };

    const handleDeleteRequest = async (requestId: number, courierId: number) => {
        setIsDeletingRequest(true);
        try {
            const params = new URLSearchParams();
            params.append('requestId', requestId.toString());
            params.append('courierId', courierId.toString());

            const response = await fetch('http://localhost:8080/api/request/delete', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: params,
            });

            if (!response.ok) {
                const errorText = await response.text();
                if (errorText.includes("TSP algorithm did not find a solution")) {
                    throw new Error("TSP algorithm did not find a solution");
                }
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            console.log('Request deleted successfully');
            await fetchTours();
            setModalMessage("Request deleted successfully!");
            setIsModalOpen(true);

        } catch (e: any) {
            console.error("Failed to delete request:", e);
            if (e.message && e.message.includes("TSP algorithm did not find a solution")) {
                setModalMessage("Failed to delete request: Deleting this request would lead to no valid tour. Please try another action.");
            } else {
                setModalMessage(`Failed to delete request: ${e.message}`);
            }
            setIsModalOpen(true);
        } finally {
            setIsDeletingRequest(false);
        }
    };

    const formatDateTime = (value?: string | null) => {
        if (!value) return "N/A";
        const parsed = new Date(value);
        if (!isNaN(parsed.getTime())) {
            return parsed.toLocaleString().split(" ")[1];
        }
        return value;
    };

    const handleExportTour = async () => {
        if (!selectedCourier) {
            setModalMessage("Please select a courier before exporting a tour.");
            setIsModalOpen(true);
            return;
        }

        setIsExportingTour(true);
        try {
            const params = new URLSearchParams();
            params.append('courierId', selectedCourier);
            params.append('filepath', exportTourFilePath);

            const response = await fetch('http://localhost:8080/api/tour/save', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: params,
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || `HTTP error! status: ${response.status}`);
            }

            setModalMessage(`Tour exported successfully to ${exportTourFilePath}`);
            setIsModalOpen(true);
        } catch (e: any) {
            setModalMessage(`Failed to export tour: ${e.message}`);
            setIsModalOpen(true);
        } finally {
            setIsExportingTour(false);
        }
    }

    if (loading || loadingTours) {
        return <div>Loading data...</div>;
    }

    return (
        <div className="home-container">
            {isModalOpen && (
                <Modal message={modalMessage} onClose={closeModal} actions={modalActions} />
            )}

            <div className={`side-panel-and-toggle ${isSidePanelOpen ? "open" : "closed"}`}>
                {/* Sliding side panel */}
                <div className={`side-panel`}>

                    <div className="side-panel-content">
                        <h1>Welcome to our brand new pick-up &amp; delivery app !</h1>

                        {/* Couriers XML + load */}
                        <div className="side-panel-section">
                            <label>
                                Couriers XML path:&nbsp;&nbsp;
                                <input
                                    type="text"
                                    value={courierFilePath}
                                    onChange={(e) => setCourierFilePath(e.target.value)}
                                />
                            </label>
                            <button
                                onClick={handleLoadCouriers}
                                className={"home-button"}
                                disabled={isLoadingCouriers}
                            >
                                {isLoadingCouriers ? "Loading..." : "Load Couriers"}
                            </button>
                        </div>

                        {/* Selected courier */}
                        <div className="side-panel-section">
                        <span className="info-label">
                            Selected courier:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        </span>
                            <select
                                id={"courier"}
                                value={selectedCourier}
                                onFocus={fetchAvailableCouriers}
                                onChange={(e) => setSelectedCourier(e.target.value)}
                            >
                                {couriersList?.map((courier) => (
                                    <option key={courier.id.toString()} value={courier.id.toString()}>
                                        {courier.name}
                                    </option>
                                ))}
                            </select>
                        </div>

                        {/* Requests / export paths */}
                        <div className="side-panel-section">
                            <div>
                                <label>
                                    Requests XML path:&nbsp;&nbsp;
                                    <input
                                        type="text"
                                        value={requestFilePath}
                                        onChange={(e) => setRequestFilePath(e.target.value)}
                                    />
                                </label>
                                <button
                                    onClick={handleLoadRequests}
                                    className={"home-button"}
                                    disabled={isLoadingRequests}
                                >
                                    {isLoadingRequests ? "Loading..." : "Load Requests"}
                                </button>
                            </div>

                            <div>
                                <label>
                                    Export tour path:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                    <input
                                        type="text"
                                        value={exportTourFilePath}
                                        onChange={(e) => setExportTourFilePath(e.target.value)}
                                    />
                                </label>
                                <button
                                    onClick={handleExportTour}
                                    className={"home-button"}
                                    disabled={isExportingTour}
                                >
                                    {isExportingTour ? "Exporting..." : "Export Tour"}
                                </button>
                            </div>

                            <div>
                                <label>
                                    Export requests path:
                                    <input
                                        type="text"
                                        value={exportRequestsFilePath}
                                        onChange={(e) => setExportRequestsFilePath(e.target.value)}
                                    />
                                </label>
                                <button
                                    onClick={handleSaveRequests}
                                    className={"home-button"}
                                    disabled={isSavingRequests}
                                >
                                    {isSavingRequests ? "Exporting..." : "Export Requests"}
                                </button>
                            </div>
                        </div>

                        {/* Add request / save requests buttons */}
                        <div className="side-panel-section">
                            <button onClick={openModificationPanel} className={"home-button"}>
                                Add a request
                            </button>
                            <button
                                onClick={handleSaveRequests}
                                className={"home-button"}
                                disabled={isSavingRequests}
                            >
                                {isSavingRequests ? "Saving..." : "Save Requests"}
                            </button>
                        </div>

                        {/* Stop order update controls */}
                        <div className="side-panel-section">
                            <label>
                                First stop index (must come BEFORE):
                                <input
                                    type="number"
                                    value={prevStopIndex}
                                    onChange={(e) => setPrevStopIndex(e.target.value)}
                                />
                            </label>

                            <label>
                                Second stop index (must come AFTER):
                                <input
                                    type="number"
                                    value={nextStopIndex}
                                    onChange={(e) => setNextStopIndex(e.target.value)}
                                />
                            </label>

                            <button className={"home-button"} onClick={handleUpdateOrderClick} disabled={isUpdatingOrder}>
                                {isUpdatingOrder ? "Updating..." : "Apply Update"}
                            </button>
                        </div>
                    </div>

                </div>
                {/* Arrow */}
                <div
                    className="side-panel-toggle"
                    onClick={() => setIsSidePanelOpen((prev) => !prev)}
                >
                    <img
                        src={"https://www.svgrepo.com/show/26445/menu-symbol-of-three-parallel-lines.svg"}
                        alt={"3 horizontal bars icon"}
                    />
                </div>
            </div>

            {/* Panels and map outside side panel, using new logic */}
            {isModificationPanelOpen && (
                <ModificationPanel
                    pickupId={pickupId}
                    deliveryId={deliveryId}
                    pickupName={pickupName}
                    deliveryName={deliveryName}
                    defaultDuration={defaultDuration}
                    pickupDuration={pickupDuration}
                    deliveryDuration={deliveryDuration}
                    setPickupDuration={setPickupDuration}
                    setDeliveryDuration={setDeliveryDuration}
                    couriersList={couriersList}
                    selectedCourier={selectedCourier}
                    setSelectedCourier={setSelectedCourier}
                    onAddRequest={handleAddRequest}
                    onCancel={handleClosePanel}
                    selectionMode={selectionMode}
                    setSelectionMode={setSelectionMode}
                    isAddingRequest={isAddingRequest}
                />
            )}

            {/*<UpdateOrderPanel
                firstId={pickupId}
                secondId={deliveryId}
                firstName={pickupName}
                secondName={deliveryName}
                couriersList={couriersList}
                selectedCourier={selectedCourier}
                setSelectedCourier={setSelectedCourier}
                onUpdateOrder={handleUpdateOrderClick}
                onCancel={handleClosePanel}
                selectionMode={selectionMode}
                setSelectionMode={(mode) => setSelectionMode(mode)}
                isUpdatingOrder={isUpdatingOrder}
            />*/}

            {bounds.length > 0 && (
                <MapComponent
                    intersections={intersections}
                    roadSegments={roadSegments}
                    bounds={bounds}
                    tours={displayedTours}
                    onMapClick={handleMapClick}
                    selectionModeActive={isModificationPanelOpen}
                    pickupId={pickupId}
                    deliveryId={deliveryId}
                    onDeleteRequest={handleDeleteRequest}
                    warehouseIds={displayedWarehouses}
                    formatDateTime={formatDateTime}
                />
            )}

            {couriersList.length > 0 && (
                <CourierSelectionPanel
                    couriersList={couriersList}
                    displayedCouriers={displayedCouriers}
                    setDisplayedCouriers={setDisplayedCouriers}
                />
            )}
        </div>
    );
}
