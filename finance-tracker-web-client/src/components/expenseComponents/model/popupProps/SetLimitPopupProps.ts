import LimitRequest from "../request/LimitRequest.ts";

export interface SetLimitPopupProps {
    category: string;
    currentSpent: number;
    show: boolean;
    onClose: () => void;
    onSubmit: (requestEntity: LimitRequest) => void;
}