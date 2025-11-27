import { MapContainer, Marker, Popup, Polyline, useMap } from 'react-leaflet'
import L from "leaflet";

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

const emptyIcon = L.icon({
    iconUrl: "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8Xw8AAoMBgQEqXKkAAAAASUVORK5CYII=",
    iconSize: [0, 0],
    iconAnchor: [0, 0],
});

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

export function Map(props: { intersections: Intersection[], roadSegments: L.LatLngExpression[][], bounds: L.LatLngExpression[], tours: Tour[] }) {
    const mapBounds = new L.LatLngBounds(props.bounds);

    return (
        <MapContainer center={mapBounds.getCenter()}>
            {props.intersections.map((intersection) => (
                <Marker key={intersection.id} position={intersection.position} icon={emptyIcon} />
            ))}

            {props.roadSegments.map((segment, id) => (
                <Polyline key={id} positions={segment} />
            ))}
            {props.tours.map(tour => (
                tour.stops.map((stop, index) => {
                    const intersection = props.intersections.find(i => i.id === stop.intersectionId);
                    if (intersection) {
                        const icon = stop.type === StopType.PICKUP ? startIcon : endIcon;
                        return (
                            <Marker key={`${tour.courierId}-${stop.intersectionId}-${index}`} position={intersection.position} icon={icon}>
                                <Popup>
                                    <div>
                                        Courier ID: {tour.courierId} <br />
                                        Stop Type: {stop.type} <br />
                                        Request ID: {stop.requestID} <br />
                                        Intersection ID: {stop.intersectionId} <br />
                                    </div>
                                </Popup>
                            </Marker>
                        );
                    }
                    return null;
                })
            ))}
            <FitBounds bounds={mapBounds} />
        </MapContainer>
    );
}

function FitBounds({ bounds }: { bounds: L.LatLngBounds }) {
    const map = useMap();
    map.fitBounds(bounds, { padding: [20, 20] });
    return null;
}