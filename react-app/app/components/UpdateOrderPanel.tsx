import React from 'react';
import './ModificationPanel.css';

export type Courier = {
    id: number;
    name: string | null;
    shiftDuration: string;
}

type UpdateOrderPanelProps = {
    firstId: number | null;
    secondId: number | null;
    firstName: string | null;
    secondName: string | null;
    couriersList: Courier[] | null;
    selectedCourier: string;
    setSelectedCourier: (courier: string) => void;
    onUpdateOrder: () => void;
    onCancel: () => void;
    selectionMode: 'pickup' | 'delivery' | 'warehouse' | 'stops_only' | null;
    setSelectionMode: (mode: 'stops_only') => void;
    isUpdatingOrder: boolean;
};

export function UpdateOrderPanel({
                                     firstId,
                                     secondId,
                                     firstName,
                                     secondName,
                                     couriersList,
                                     selectedCourier,
                                     setSelectedCourier,
                                     onUpdateOrder,
                                     onCancel,
                                     selectionMode,
                                     setSelectionMode,
                                     isUpdatingOrder
                                 }: UpdateOrderPanelProps) {
    return (
        <div className="modification-panel">
            <h3>Add a New Request</h3>
            <div className="info">
                <div>
                    <span className="info-label">First vertex (must come before):</span>
                    <span className="info-value">{firstName || 'Not selected'}</span>
                </div>
                <div>
                    <button
                        className={`select-button ${selectionMode === 'stops_only' ? 'active' : ''}`}
                        onClick={() => setSelectionMode('stops_only')}
                        disabled={isUpdatingOrder}
                    >
                        Select Pickup
                    </button>
                </div>
            </div>
            <div className="info">
                <div>
                    <span className="info-label">Second vertex (must come after):</span>
                    <span className="info-value">{secondName || 'Not selected'}</span>
                </div>
                <div>
                    <button
                        className={`select-button ${selectionMode === 'stops_only' ? 'active' : ''}`}
                        onClick={() => setSelectionMode('stops_only')}
                        disabled={isUpdatingOrder}
                    >
                        Select Delivery
                    </button>
                </div>
            </div>
            <div className="info">
                <span className="info-label">Courier:</span>
                <select id={"courier"} value={selectedCourier} onChange={(e) => setSelectedCourier(e.target.value)}>
                    {couriersList?.map(courier => (
                        <option key={courier.id.toString()} value={courier.id.toString()}>{courier.name}</option>
                    ))}
                </select>
            </div>
            <div className="actions">
                <button className="cancel-button" onClick={onCancel} disabled={isUpdatingOrder}>
                    Cancel
                </button>
                <button
                    className="add-button"
                    onClick={onUpdateOrder}
                    disabled={firstId === null || secondId === null || isUpdatingOrder}
                >
                    {isUpdatingOrder ? "Updating..." : "Updating Order"}
                </button>
            </div>
        </div>
    );
}