import React from "react";
import "./HamburgerButton.css";

type AnimatedHamburgerButtonProps = {
    onClick?: () => void;
    active?: boolean;
};

export const AnimatedHamburgerButton: React.FC<AnimatedHamburgerButtonProps> = ({
                                                                                    onClick,
                                                                                    active = false,
                                                                                }) => {
    return (
        <button
            type="button"
            onClick={onClick}
            className={`hamburger-btn ${active ? "active" : ""}`}
            aria-label="Toggle side panel"
        >
            <span className="bar bar1" />
            <span className="bar bar2" />
            <span className="bar bar3" />
        </button>
    );
};