import { MapContainer, Marker, Popup, Polyline, useMap, Circle, Pane } from 'react-leaflet'
import L from "leaflet";
import { useEffect, useMemo } from 'react';

export type Intersection = {
    id: number;
    position: L.LatLngExpression;
};

export enum StopType {
    PICKUP = "PICKUP",
    DELIVERY = "DELIVERY",
    INTERMEDIATE_INTERSECTION = "INTERMEDIATE_INTERSECTION",
    WAREHOUSE = "WAREHOUSE",
}

export type TourStop = {
    type: StopType;
    requestID: number;
    intersectionId: number;
    arrivalTime: string;
    departureTime: string;
};

export type Tour = {
    courierId: number;
    stops: TourStop[];
    roadSegmentsTaken: L.LatLngExpression[][]; // Assuming road segments are also LatLngExpression[][] for rendering
    totalDistance: number;
    totalDuration: number;
};

const tourColors = [
    "#d0021b", // red
    "#417505", // green
    "#4a3700", // blue
    "#f5a623", // orange
    "#9013fe", // purple
    "#50e3c2", // teal
    "#b8e986", // light green
    "#f8e71c", // yellow
    "#8b572a", // brown
    "#7ed321", // lime
];

const getTourColor = (courierId: number) => {
    const idx = Math.abs(Math.floor(courierId)) % tourColors.length;
    return tourColors[idx];
};

const pickupIcon = new L.Icon({
    iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-green.png',
    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34],
    shadowSize: [41, 41]
});

const deliveryIcon = new L.Icon({
    iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-red.png',
    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34],
    shadowSize: [41, 41]
});

const warehouseIcon = new L.Icon({
    // House-shaped marker for the warehouse
    iconUrl: "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 64 64' fill='none'%3E%3Cpath fill='%231e88e5' d='M27.6 8.2a4 4 0 0 1 4.8 0l21 16.2a4 4 0 0 1 1.6 3.2V52a4 4 0 0 1-4 4h-10a4 4 0 0 1-4-4V40h-10v12a4 4 0 0 1-4 4h-10a4 4 0 0 1-4-4V27.6a4 4 0 0 1 1.6-3.2l21-16.2Z'/%3E%3Cpath fill='%23fff' d='M28 36h8v-8a4 4 0 0 0-8 0v8Z'/%3E%3C/svg%3E",
    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
    iconSize: [41, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34],
    shadowSize: [41, 41]
});

export function Map(props: {
    intersections: Intersection[],
    roadSegments: L.LatLngExpression[][],
    bounds: L.LatLngExpression[],
    tours: Tour[],
    onMapClick?: (intersectionId: number, index: number | null) => void,
    selectionModeActive: boolean,
    stopsOnly: boolean,
    pickupId: number | null,
    deliveryId: number | null,
    onDeleteRequest?: (requestId: number, courierId: number) => void,
    warehouseIds: Array<number>,
    formatDateTime: (value?: string | null) => string,
    mapRef?: React.RefObject<L.Map | null>;
}) {
    const mapBounds = useMemo(() => new L.LatLngBounds(props.bounds), [props.bounds]);

    const tourBounds = useMemo(() => {
        const tourPoints = props.tours.flatMap(tour => tour.roadSegmentsTaken.flat());
        return tourPoints.length > 0 ? new L.LatLngBounds(tourPoints) : mapBounds;
    }, [props.tours, mapBounds]);

    const stopIntersectionIds = useMemo(() => {
        return new Set(
            props.tours.flatMap(tour => tour.stops.filter(stop => stop.type != StopType.WAREHOUSE).map(stop => stop.intersectionId))
        );
    }, [props.tours]);

    return (
        <MapContainer
            center={mapBounds.getCenter()}
            ref={props.mapRef as any}
            zoomControl={false} // disable default top-left control
        >
            <Pane name="roads-pane" style={{ zIndex: 450 }} />
            <Pane name="intersections-pane" style={{ zIndex: 500 }} />

            {props.intersections.map((intersection) => {
                const isStop = stopIntersectionIds.has(intersection.id);

                if (props.stopsOnly && !isStop) {
                    return null; // ne pas afficher l'intersection
                }

                if (props.warehouseIds && props.warehouseIds.includes(intersection.id)) {
                    return (
                        <Marker
                            key={intersection.id}
                            position={intersection.position}
                            icon={warehouseIcon}
                        >
                            <Popup>Warehouse</Popup>
                        </Marker>
                    );
                }
                let color = 'blue';
                let radius = 8;
                if (intersection.id === props.pickupId) {
                    color = 'green';
                    radius = 12;
                } else if (intersection.id === props.deliveryId) {
                    color = 'red';
                    radius = 12;
                }
                return (
                    <Circle
                        key={intersection.id}
                        center={intersection.position}
                        radius={radius} // Radius in meters, will scale with zoom
                        pathOptions={{ color: color }}
                        pane="intersections-pane"
                        eventHandlers={{
                            click: () => {
                                if (props.onMapClick) {
                                    props.onMapClick(intersection.id, null);
                                }
                            },
                        }}
                    />
                );
            })}

            <Pane name="road-segments-pane" style={{ zIndex: 400 }} />
            <Pane name="tour-pane" style={{ zIndex: 470 }} />
            {props.roadSegments.map((segment, id) => (
                <Polyline key={id} positions={segment} pane="road-segments-pane" />
            ))}

            {props.tours.map(tour => {
                const tourColor = getTourColor(tour.courierId);
                return (
                <div key={tour.courierId}>
                    {tour.roadSegmentsTaken.map((segment, index) => (
                        <Polyline
                            key={index}
                            positions={segment}
                            color={tourColor}
                            weight={5}
                            opacity={0.85}
                            pane="tour-pane"
                        />
                    ))}
                    {tour.stops.map((stop, index) => {
                        const intersection = props.intersections.find(i => i.id === stop.intersectionId);
                        if (intersection) {
                            let icon = deliveryIcon; // default

                            if (stop.type === StopType.PICKUP) {
                                icon = pickupIcon;
                            } else if (stop.type === StopType.WAREHOUSE) {
                                icon = warehouseIcon;
                            }
                            const stopOrder = index;

                            return (
                                <Marker key={`${tour.courierId}-${stop.intersectionId}-${index}`}
                                        position={intersection.position}
                                        icon={icon}
                                        eventHandlers={{
                                            click: () => {
                                                if (props.onMapClick && props.stopsOnly) {
                                                    props.onMapClick(intersection.id, stopOrder);
                                                }
                                            },
                                        }}
                                >
                                    <Popup>
                                        <div className="map-popup-content">
                                            Courier ID: {tour.courierId} <br />
                                            Stop Type: {stop.type} <br />
                                            Request ID: {stop.requestID} <br />
                                            Intersection ID: {stop.intersectionId} <br />
                                            Order in tour: {stopOrder} <br />
                                            Arrival time: {props.formatDateTime(stop.arrivalTime)} <br />
                                            Departure time: {props.formatDateTime(stop.departureTime)} <br />
                                            {stop.type !== StopType.WAREHOUSE && stop.requestID >= 0 && (
                                                <button className="delete-button" onClick={() => props.onDeleteRequest?.(stop.requestID, tour.courierId)}>
                                                    Delete Request
                                                </button>
                                            )}
                                        </div>
                                    </Popup>
                                </Marker>
                            );
                        }
                        return null;
                    })}
                </div>)
            })}
            <FitBounds bounds={tourBounds} />
        </MapContainer>
    );
}

function FitBounds({ bounds }: { bounds: L.LatLngBounds }) {
    const map = useMap();
    useEffect(() => {
        if (bounds.isValid()) {
            map.fitBounds(bounds, { padding: [320, 50] });
        }
    }, [map, bounds]);
    return null;
}
