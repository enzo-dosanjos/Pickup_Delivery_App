import type { Route } from "./+types/home";
import { Map, Intersection as MapIntersection } from "../map/map";
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

    useEffect(() => {
        const fetchMapData = async () => {
            try {
                const response = await fetch("http://localhost:8080/api/map");
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                const data: ApiMapData = await response.json();

                // --- Data Transformation ---

                // 1. Transform Intersections
                const intersectionMap = new Map<number, ApiIntersection>(
                    Object.values(data.intersections).map(i => [i.id, i])
                );
                const transformedIntersections: MapIntersection[] = Array.from(intersectionMap.values()).map(i => ({
                    id: i.id,
                    position: [i.lat, i.lng],
                }));

                // 2. Transform Road Segments
                const transformedRoadSegments: L.LatLngExpression[][] = [];
                for (const startIdStr in data.adjencyList) {
                    const segments = data.adjencyList[startIdStr];
                    const startIntersection = intersectionMap.get(parseInt(startIdStr, 10));

                    if (startIntersection) {
                        segments.forEach(segment => {
                            const endIntersection = intersectionMap.get(segment.endId);
                            if (endIntersection) {
                                transformedRoadSegments.push([
                                    [startIntersection.lat, startIntersection.lng],
                                    [endIntersection.lat, endIntersection.lng]
                                ]);
                            }
                        });
                    }
                }

                // 3. Calculate Bounds
                if (transformedIntersections.length > 0) {
                    const latitudes = transformedIntersections.map(i => i.position[0]);
                    const longitudes = transformedIntersections.map(i => i.position[1]);
                    const minLat = Math.min(...latitudes);
                    const maxLat = Math.max(...latitudes);
                    const minLng = Math.min(...longitudes);
                    const maxLng = Math.max(...longitudes);
                    setBounds([[minLat, minLng], [maxLat, maxLng]]);
                }

                // --- Update State ---
                setIntersections(transformedIntersections);
                setRoadSegments(transformedRoadSegments);
                
            } catch (e: any) {
                setError(`Failed to fetch map data: ${e.message}`);
                console.error(e);
            } finally {
                setLoading(false);
            }
        };

        fetchMapData();
    }, []); // Empty dependency array means this effect runs once on mount

    if (loading) {
        return <div>Loading map...</div>;
    }

    if (error) {
        return <div>Error: {error}</div>;
    }

    return (
        <div>
            <h1>Welcome to our brand new pick-up & delivery app !</h1>
            <br />
            {bounds.length > 0 && (
                 <Map
                    intersections={intersections}
                    roadSegments={roadSegments}
                    bounds={bounds}
                />
            )}
        </div>
    );
}
