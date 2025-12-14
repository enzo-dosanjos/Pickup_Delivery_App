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
    selectionMode: 'pickup' | 'delivery' | 'warehouse' | null;
    setSelectionMode: (mode: 'pickup' | 'delivery' | 'warehouse') => void;
    setStopsOnly: (mode: boolean) => void;
    setPrevStopIndex: (index: number) => void;
    setNextStopIndex: (index: number) => void;
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
                                     setStopsOnly,
                                     setPrevStopIndex,
                                     setNextStopIndex,
                                     isUpdatingOrder
                                 }: UpdateOrderPanelProps) {
    return (
        <div className="modification-panel update-order">
            <h3>Update tour stops order</h3>
            <div className="info">
                <span className="info-label">Courier:</span>
                <select id={"courier"} value={selectedCourier} onChange={(e) => {
                    setSelectedCourier(e.target.value); setPrevStopIndex(-1); setNextStopIndex(-1);}}>
                    {couriersList?.map(courier => (
                        <option key={courier.id.toString()} value={courier.id.toString()}>{courier.name}</option>
                    ))}
                </select>
            </div>
            <div className="info">
                <div>
                    <span className="info-label">First vertex (must come before):</span>
                    <span className="info-value">{firstName || 'Not selected'}</span>
                </div>
                <div>
                    <button
                        className={`select-button ${selectionMode === 'pickup' ? 'active' : ''}`}
                        onClick={() => {setSelectionMode('pickup'); setStopsOnly(true)}}
                        disabled={isUpdatingOrder}
                    >
                        Select first
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
                        className={`select-button ${selectionMode === 'delivery' ? 'active' : ''}`}
                        onClick={() => {setSelectionMode('delivery'); setStopsOnly(true)}}
                        disabled={isUpdatingOrder}
                    >
                        Select second
                    </button>
                </div>
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
                    {isUpdatingOrder ? "Updating..." : "Set order"}
                </button>
            </div>
        </div>
    );
}