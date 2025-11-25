import { MapContainer, Marker, Popup, Polyline, useMap } from 'react-leaflet'
import L from "leaflet";

export type Intersection = {
    id: number;
    position: L.LatLngExpression;
};

const emptyIcon = L.icon({
    iconUrl: "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8Xw8AAoMBgQEqXKkAAAAASUVORK5CYII=",
    iconSize: [0, 0],
    iconAnchor: [0, 0],
});

export function Map(props: { intersections: Intersection[], roadSegments: L.LatLngExpression[][], bounds: L.LatLngExpression[] }) {
    const mapBounds = new L.LatLngBounds(props.bounds);

    return (
        <MapContainer center={mapBounds.getCenter()}>
            {props.intersections.map((intersection) => (
                <Marker key={intersection.id} position={intersection.position} icon={emptyIcon} />
            ))}

            {props.roadSegments.map((segment, id) => (
                <Polyline key={id} positions={segment} />
            ))}
            <FitBounds bounds={L.latLngBounds(props.bounds[0],props.bounds[1])} />
        </MapContainer>
    );
}

function FitBounds({ bounds }: { bounds: L.LatLngBounds }) {
    const map = useMap();
    map.fitBounds(bounds, { padding: [20, 20] });
    return null;
}