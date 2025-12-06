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
    arrivalTime: number;
    departureTime: number;
};

export type Tour = {
    courierId: number;
    stops: TourStop[];
    roadSegmentsTaken: L.LatLngExpression[][]; // Assuming road segments are also LatLngExpression[][] for rendering
    totalDistance: number;
    totalDuration: number;
};

const startIcon = new L.Icon({
    iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-green.png',
    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34],
    shadowSize: [41, 41]
});

const endIcon = new L.Icon({
    iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-red.png',
    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34],
    shadowSize: [41, 41]
});

const warehouseIcon = new L.Icon({
    iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-blue.png', // Using blue for warehouse for now
    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34],
    shadowSize: [41, 41]
});

export function Map(props: {
    intersections: Intersection[],
    roadSegments: L.LatLngExpression[][],
    bounds: L.LatLngExpression[],
    tours: Tour[],
    onMapClick?: (intersectionId: number) => void,
    selectionModeActive: boolean,
    pickupId: number | null,
    deliveryId: number | null,
    onDeleteRequest?: (requestId: number, courierId: number) => void,
    warehouseId: number | null,
}) {
    const mapBounds = useMemo(() => new L.LatLngBounds(props.bounds), [props.bounds]);

    const tourBounds = useMemo(() => {
        const tourPoints = props.tours.flatMap(tour => tour.roadSegmentsTaken.flat());
        return tourPoints.length > 0 ? new L.LatLngBounds(tourPoints) : mapBounds;
    }, [props.tours, mapBounds]);


    return (
        <MapContainer center={mapBounds.getCenter()}>
            <Pane name="roads-pane" style={{ zIndex: 450 }} />
            <Pane name="intersections-pane" style={{ zIndex: 500 }} />

            {props.intersections.map((intersection) => {
                if (intersection.id === props.warehouseId) {
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
                                    props.onMapClick(intersection.id);
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

            {props.tours.map(tour => (
                <div key={tour.courierId}>
                    {tour.roadSegmentsTaken.map((segment, index) => (
                        <Polyline key={index} positions={segment} color="red" pane="tour-pane" />
                    ))}
                    {tour.stops.map((stop, index) => {
                        const intersection = props.intersections.find(i => i.id === stop.intersectionId);
                        if (intersection) {
                            let icon = endIcon; // default

                            if (stop.type === StopType.PICKUP) {
                                icon = startIcon;
                            } else if (stop.type === StopType.WAREHOUSE) {
                                icon = warehouseIcon;
                            }

                            return (
                                <Marker key={`${tour.courierId}-${stop.intersectionId}-${index}`} position={intersection.position} icon={icon}>
                                    <Popup>
                                        <div>
                                            Courier ID: {tour.courierId} <br />
                                            Stop Type: {stop.type} <br />
                                            Request ID: {stop.requestID} <br />
                                            Intersection ID: {stop.intersectionId} <br />
                                            <button onClick={() => props.onDeleteRequest?.(stop.requestID, tour.courierId)}>
                                                Delete Request
                                            </button>
                                        </div>
                                    </Popup>
                                </Marker>
                            );
                        }
                        return null;
                    })}
                </div>
            ))}
            <FitBounds bounds={tourBounds} />
        </MapContainer>
    );
}

function FitBounds({ bounds }: { bounds: L.LatLngBounds }) {
    const map = useMap();
    useEffect(() => {
        if (bounds.isValid()) {
            map.fitBounds(bounds, { padding: [50, 50] });
        }
    }, [map, bounds]);
    return null;
}