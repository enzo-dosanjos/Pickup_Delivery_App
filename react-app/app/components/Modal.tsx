import React from 'react';
import './Modal.css';

interface ModalAction {
  label: string;
  onClick: () => void;
}

interface ModalProps {
  message: string;
  onClose: () => void;
  actions?: ModalAction[];
}

const Modal: React.FC<ModalProps> = ({ message, onClose, actions = [] }) => {
  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <p>{message}</p>
        <div className={"modal-actions"}>
          {actions.map((action, idx) => (
            <button key={idx} onClick={action.onClick}>
              {action.label}
            </button>
          ))}
          <button onClick={onClose}>Close</button>
        </div>
      </div>
    </div>
  );
};

export default Modal;
