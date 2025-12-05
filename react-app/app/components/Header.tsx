import React from 'react';
import './Header.css';

type HeaderProps = {
    onAddRequest: () => void;
    onSaveRequests: () => void;
};

export function Header({ onAddRequest, onSaveRequests }: HeaderProps) {
    return (
        <header className="header">
            <h1>Pick-up & Delivery</h1>
            <div className="actions">
                <button className="action-button" onClick={onAddRequest}>
                    Add a Request
                </button>
                <button className="action-button save-button" onClick={onSaveRequests}>
                    Save Requests
                </button>
            </div>
        </header>
    );
}
