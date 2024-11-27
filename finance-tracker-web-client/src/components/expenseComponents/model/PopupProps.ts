import React from "react";

export interface PopupProps {
    show: boolean;
    onClose: () => void;
    children?: React.ReactNode;
}