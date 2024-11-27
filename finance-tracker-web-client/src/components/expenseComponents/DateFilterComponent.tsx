import 'bootstrap/dist/css/bootstrap.min.css';
import React, { useState } from 'react';

interface FilterProps {
    onFilter: (startDate: Date, endDate: Date) => void;
}

export const DateFilterComponent: React.FC<FilterProps> = ({ onFilter }) => {
    const [startDate, setStartDate] = useState<string>('');
    const [endDate, setEndDate] = useState<string>('');

    const handleFilterChange = () => {
        const start = new Date(startDate);
        const end = new Date(endDate);

        if (!startDate || !endDate) {
            alert("Please select both start and end dates.");
            return;
        }

        if (start > end) {
            alert("Start date cannot be later than end date.");
            return;
        }

        onFilter(start, end);
    };

    return (
        <div className="row">
            <div className="col-md-5">
                <label htmlFor="startDate" className="form-label">Start Date</label>
                <input
                    type="date"
                    id="startDate"
                    className="form-control"
                    value={startDate}
                    onChange={(e) => setStartDate(e.target.value)}
                />
            </div>
            <div className="col-md-5">
                <label htmlFor="endDate" className="form-label">End Date</label>
                <input
                    type="date"
                    id="endDate"
                    className="form-control"
                    value={endDate}
                    onChange={(e) => setEndDate(e.target.value)}
                />
            </div>
            <div className="col-md-2 d-flex align-items-end">
                <button className="btn btn-primary" onClick={handleFilterChange}>
                    Apply
                </button>
            </div>
        </div>
    );
};
