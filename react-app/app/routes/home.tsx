import type { Route } from "./+types/home";
import { Map as MapComponent, type Intersection as MapIntersection } from "../map/map";
import { useState, useEffect, useRef } from "react";
import L from "leaflet";
import { ModificationPanel } from "../components/ModificationPanel";
import "../components/ModificationPanel.css";

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
    adjencyList: Record<string, ApiRoadSegment[]>;
};

enum StopType {
    PICKUP = "PICKUP",
    DELIVERY = "DELIVERY",
    INTERMEDIATE_INTERSECTION = "INTERMEDIATE_INTERSECTION",
    WAREHOUSE = "WAREHOUSE",
}

type ApiTourStop = {
    type: StopType;
    requestID: number;
    intersectionId: number;
    arrivalTime: number;
    departureTime: number;
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
    shiftDuration: number;
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
    const [error, setError] = useState<string | null>(null);

    const [tours, setTours] = useState<ApiTour[]>([]);
    const [loadingTours, setLoadingTours] = useState(true);

    const [isPanelOpen, setIsPanelOpen] = useState(false);
    const [selectionMode, setSelectionMode] = useState<'pickup' | 'delivery' | null>(null);
    const [pickupId, setPickupId] = useState<number | null>(null);
    const [deliveryId, setDeliveryId] = useState<number | null>(null);
    const [pickupName, setPickupName] = useState<string | null>(null);
    const [deliveryName, setDeliveryName] = useState<string | null>(null);
    const fetchInitiated = useRef(false);


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

                const nameMap = new Map<number, string>();
                const transformedRoadSegments: L.LatLngExpression[][] = [];
                for (const startIdStr in mapData.adjencyList) {
                    const segments = mapData.adjencyList[startIdStr];
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

                // Add a courier
                const courierParams = new URLSearchParams();
                courierParams.append('id', '1');
                courierParams.append('name', 'Courier 1');
                courierParams.append('shiftDurationInSeconds', '28800'); // 8 hours in seconds
                const courierResponse = await fetch("http://localhost:8080/api/tour/add-courier", {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: courierParams,
                });
                if (!courierResponse.ok) {
                    throw new Error(`HTTP error! status: ${courierResponse.status}`);
                }

                // Load requests
                const requestParams = new URLSearchParams();
                requestParams.append('filepath', 'src/main/resources/requests.xml');
                requestParams.append("courierId", "1"); // todo: make dynamic
                const requestResponse = await fetch("http://localhost:8080/api/request/load", {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: requestParams,
                });
                if (!requestResponse.ok) {
                    throw new Error(`HTTP error! status: ${requestResponse.status}`);
                }

                /*// Fetch Tour Data
                const tourResponse = await fetch("http://localhost:8080/api/tour/load?filepath=src/test/resources/testTours.xml"); // Assuming a default filepath
                if (!tourResponse.ok) {
                    throw new Error(`HTTP error! status: ${tourResponse.status}`);
                }
                const tourData: ApiTour[] = await tourResponse.json();
                console.log("Raw tour data from API:", tourData);

                // Transform Tour Data
                const transformedTours = tourData.map(tour => {
                    const transformedRoadSegments = tour.roadSegmentsTaken.map(segment => {
                        const startIntersection = intersectionMap.get(segment.startId);
                        const endIntersection = intersectionMap.get(segment.endId);
                        if (startIntersection && endIntersection) {
                            return [
                                [startIntersection.lat, startIntersection.lng],
                                [endIntersection.lat, endIntersection.lng]
                            ];
                        }
                        return null;
                    }).filter((s): s is L.LatLngExpression[] => s !== null);

                    return {
                        ...tour,
                        roadSegmentsTaken: transformedRoadSegments
                    };
                });

                setTours(transformedTours);*/

            } catch (e: any) {
                console.error("Caught error object:", e);
                setError(`Failed to fetch data: ${e.message}`);
            } finally {
                setLoading(false);
                setLoadingTours(false);
            }
        };

        fetchData();
    }, []);

    const handleMapClick = (intersectionId: number) => {
        const roadName = intersectionIdToRoadName.get(intersectionId) || `Intersection ${intersectionId}`;
        if (selectionMode === 'pickup') {
            setPickupId(intersectionId);
            setPickupName(roadName);
            setSelectionMode(null); // Deactivate selection mode after a point is chosen
        } else if (selectionMode === 'delivery') {
            setDeliveryId(intersectionId);
            setDeliveryName(roadName);
            setSelectionMode(null); // Deactivate selection mode
        }
    };

    const handleAddRequest = async () => {
        if (pickupId === null || deliveryId === null) {
            return;
        }

        try {
            // First, get the warehouse ID from the backend
            const warehouseResponse = await fetch('http://localhost:8080/api/request/warehouse');
            if (!warehouseResponse.ok) {
                throw new Error(`HTTP error! status: ${warehouseResponse.status}`);
            }
            const warehouseId = await warehouseResponse.json();

            // Now, add the request with the fetched warehouse ID
            const params = new URLSearchParams();
            params.append('warehouseId', warehouseId.toString());
            params.append('pickupIntersectionId', pickupId.toString());
            params.append('pickupDuration', '300'); // Hardcoded duration
            params.append('deliveryIntersectionId', deliveryId.toString());
            params.append('deliveryDuration', '300'); // Hardcoded duration
            params.append('courierId', '1'); // Hardcoded courier ID

            const response = await fetch('http://localhost:8080/api/request/add', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: params,
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            console.log('Request added successfully');

        } catch (e: any) {
            console.error("Failed to add request:", e);
            setError(`Failed to add request: ${e.message}`);
        } finally {
            handleCancel();
        }
    };

    const handleCancel = () => {
        setIsPanelOpen(false);
        setSelectionMode(null);
        setPickupId(null);
        setDeliveryId(null);
        setPickupName(null);
        setDeliveryName(null);
    };

    const openModificationPanel = () => {
        setIsPanelOpen(true);
    };

    const handleSaveRequests = async () => {
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
        } catch (e: any) {
            console.error("Failed to save requests:", e);
            setError(`Failed to save requests: ${e.message}`);
        }
    };

    if (loading || loadingTours) {
        return <div>Loading data...</div>;
    }

    if (error) {
        return <div>Error: {error}</div>;
    }

    return (
        <div>
            <h1>Welcome to our brand new pick-up & delivery app !</h1>
            <div>
                <button onClick={openModificationPanel} style={{ marginBottom: '10px', padding: '10px', marginRight: '10px' }}>
                    Add a Request
                </button>
                <button onClick={handleSaveRequests} style={{ marginBottom: '10px', padding: '10px' }}>
                    Save Requests
                </button>
            </div>
            <br />
            {isPanelOpen && (
                <ModificationPanel
                    pickupId={pickupId}
                    deliveryId={deliveryId}
                    pickupName={pickupName}
                    deliveryName={deliveryName}
                    onAddRequest={handleAddRequest}
                    onCancel={handleCancel}
                    selectionMode={selectionMode}
                    setSelectionMode={setSelectionMode}
                />
            )}
            {bounds.length > 0 && (
                 <MapComponent
                    intersections={intersections}
                    roadSegments={roadSegments}
                    bounds={bounds}
                    tours={tours}
                    onMapClick={handleMapClick}
                    selectionModeActive={isPanelOpen}
                    pickupId={pickupId}
                    deliveryId={deliveryId}
                />
            )}
        </div>
    );
}
