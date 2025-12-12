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
                    <input type={"radio"}
                           value={"All"}
                           name={"courierSelection"}
                           checked={displayedCouriers === "All"}
                           onChange={(e) => setDisplayedCouriers(e.target.value)}/>
                    All tours
                </label>
                {couriersList?.map(courier => (
                    <label key={courier.id}>
                        <input type={"radio"}
                               value={courier.id.toString()}
                               name={"courierSelection"}
                               checked={displayedCouriers === courier.id.toString()}
                               onChange={(e) => setDisplayedCouriers(e.target.value)}/>
                        {courier.name}
                    </label>
                ))}
            </div>
        </div>
    );
}
