import type { Route } from "./+types/home";
import { Map as MapComponent, type Intersection as MapIntersection } from "../map/map";
import { useState, useEffect } from "react";
import L from "leaflet";

// Define the types for the data we expect from the API
type ApiIntersection = {
    id: number;
    lat: number;
    lng: number;
};

type ApiRoadSegment = {
    startId: number;
    endId: number;
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

export function meta({}: Route.MetaArgs) {
    return [
        { title: "Pick-up & Delivery App" },
        { name: "description", content: "Welcome to our brand new pick-up & delivery app !" },
    ];
}

export default function Home() {
    const [intersections, setIntersections] = useState<MapIntersection[]>([]);
    const [roadSegments, setRoadSegments] = useState<L.LatLngExpression[][]>([]);
    const [bounds, setBounds] = useState<L.LatLngExpression[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const [tours, setTours] = useState<ApiTour[]>([]);
    const [loadingTours, setLoadingTours] = useState(true);

    useEffect(() => {
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

                const transformedIntersections: MapIntersection[] = Array.from(intersectionMap.values()).map(i => {
                    return {
                        id: i.id,
                        position: [i.lat, i.lng],
                    };
                });

                const transformedRoadSegments: L.LatLngExpression[][] = [];
                for (const startIdStr in mapData.adjencyList) {
                    const segments = mapData.adjencyList[startIdStr];
                    const startIntersection = intersectionMap.get(parseInt(startIdStr, 10));

                    if (startIntersection) {
                        segments.forEach(segment => {
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

                // Fetch Tour Data
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

                setTours(transformedTours);

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

    if (loading || loadingTours) {
        return <div>Loading data...</div>;
    }

    if (error) {
        return <div>Error: {error}</div>;
    }

    return (
        <div>
            <h1>Welcome to our brand new pick-up & delivery app !</h1>
            <br />
            {bounds.length > 0 && (
                 <MapComponent
                    intersections={intersections}
                    roadSegments={roadSegments}
                    bounds={bounds}
                    tours={tours}
                />
            )}
        </div>
    );
}
