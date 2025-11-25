import type {Route} from "./+types/home";
import {Map} from "../map/map";

export function meta({}: Route.MetaArgs) {
    return [
        {title: "Pick-up & Delivery App"},
        {name: "description", content: "Welcome to our brand new pick-up & delivery app !"},
    ];
}

export default function Home() {
    return <div>
        <h1>Welcome to our brand new pick-up & delivery app !</h1>
        <br />
        <Map
            intersections={[
                { id: 1, position: [23.3,1.11] },
                { id: 2, position: [23.23,1.12] },
                { id: 3, position: [23.25,1.11] }
            ]}
            roadSegments={[
                [[23.3,1.11],[23.23,1.12]],
                [[23.3,1.11],[23.25,1.11]]
            ]}
            bounds={[
                [23.23, 1.11],
                [23.3, 1.12]
            ]}
        />
    </div>;
}