import React from 'react';
import './ModificationPanel.css';

export type Courier = {
    id: number;
    name: string | null;
    shiftDuration: string;
}

type ModificationPanelProps = {
    pickupId: number | null;
    deliveryId: number | null;
    pickupName: string | null;
    deliveryName: string | null;
    defaultDuration: number | null;
    pickupDuration: number;
    deliveryDuration: number;
    setPickupDuration: (duration: number) => void;
    setDeliveryDuration: (duration: number) => void;
    couriersList: Courier[] | null;
    selectedCourier: string;
    setSelectedCourier: (courier: string) => void;
    onAddRequest: () => void;
    onCancel: () => void;
    selectionMode: 'pickup' | 'delivery' | null;
    setSelectionMode: (mode: 'pickup' | 'delivery') => void;
    isAddingRequest: boolean;
};

export function ModificationPanel({
    pickupId,
    deliveryId,
    pickupName,
    deliveryName,
    defaultDuration,
    pickupDuration,
    deliveryDuration,
    setPickupDuration,
    setDeliveryDuration,
    couriersList,
    selectedCourier,
    setSelectedCourier,
    onAddRequest,
    onCancel,
    selectionMode,
    setSelectionMode,
    isAddingRequest
}: ModificationPanelProps) {
    return (
        <div className="modification-panel">
            <h3>Add a New Request</h3>
            <div className="info">
                <div>
                    <span className="info-label">Pickup:</span>
                    <span className="info-value">{pickupName || 'Not selected'}</span>
                </div>
                <div>
                    <span className="info-value">Duration (s):</span>
                    <input name={"pickupDuration"} defaultValue={defaultDuration?.toString()}
                           value={pickupDuration} onChange={(duration) => setPickupDuration(Number(duration.target.value))}
                    />
                </div>
                <div>
                    <button
                        className={`select-button ${selectionMode === 'pickup' ? 'active' : ''}`}
                        onClick={() => setSelectionMode('pickup')}
                        disabled={isAddingRequest}
                    >
                        Select Pickup
                    </button>
                </div>
            </div>
            <div className="info">
                <div>
                    <span className="info-label">Delivery:</span>
                    <span className="info-value">{deliveryName || 'Not selected'}</span>
                </div>
                <div>
                    <span className="info-value">Duration (s):</span>
                    <input name={"deliveryDuration"} defaultValue={defaultDuration?.toString()}
                           value={deliveryDuration} onChange={(duration) => setDeliveryDuration(Number(duration.target.value))}
                    />
                </div>
                <div>
                    <button
                        className={`select-button ${selectionMode === 'delivery' ? 'active' : ''}`}
                        onClick={() => setSelectionMode('delivery')}
                        disabled={isAddingRequest}
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
                <button className="cancel-button" onClick={onCancel} disabled={isAddingRequest}>
                    Cancel
                </button>
                <button
                    className="add-button"
                    onClick={onAddRequest}
                    disabled={pickupId === null || deliveryId === null || isAddingRequest}
                >
                    {isAddingRequest ? "Adding..." : "Add Request"}
                </button>
            </div>
        </div>
    );
}
