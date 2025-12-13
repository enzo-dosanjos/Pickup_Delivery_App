import React from 'react';
import './ModificationPanel.css';

export type Courier = {
    id: number;
    name: string | null;
    shiftDuration: string;
}

type CourierSelectionPanelProps = {
    couriersList: Courier[] | null;
    displayedCouriers: string;
    setDisplayedCouriers: (courier: string) => void;
};

export function CourierSelectionPanel({
    couriersList,
    displayedCouriers,
    setDisplayedCouriers
}: CourierSelectionPanelProps) {
    return (
        <div className="modification-panel courier-selection">
            <h3>Select a courier to display their tour</h3>
            <div>
                <label>
                    <p> All tours </p>
                    <input type={"radio"}
                           value={"All"}
                           name={"courierSelection"}
                           checked={displayedCouriers === "All"}
                           onChange={(e) => setDisplayedCouriers(e.target.value)}/>
                </label>
                {couriersList?.map(courier => (
                    <label key={courier.id}>
                        <p> {courier.name} </p>
                        <input type={"radio"}
                               value={courier.id.toString()}
                               name={"courierSelection"}
                               checked={displayedCouriers === courier.id.toString()}
                               onChange={(e) => setDisplayedCouriers(e.target.value)}/>
                    </label>
                ))}
            </div>
        </div>
    );
}
