import React from 'react';
import './ModificationPanel.css';

type ModificationPanelProps = {
    pickupId: number | null;
    deliveryId: number | null;
    pickupName: string | null;
    deliveryName: string | null;
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
                <span className="info-label">Pickup:</span>
                <span className="info-value">{pickupName || 'Not selected'}</span>
                <button
                    className={`select-button ${selectionMode === 'pickup' ? 'active' : ''}`}
                    onClick={() => setSelectionMode('pickup')}
                    disabled={isAddingRequest}
                >
                    Select Pickup
                </button>
            </div>
            <div className="info">
                <span className="info-label">Delivery:</span>
                <span className="info-value">{deliveryName || 'Not selected'}</span>
                <button
                    className={`select-button ${selectionMode === 'delivery' ? 'active' : ''}`}
                    onClick={() => setSelectionMode('delivery')}
                    disabled={isAddingRequest}
                >
                    Select Delivery
                </button>
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
