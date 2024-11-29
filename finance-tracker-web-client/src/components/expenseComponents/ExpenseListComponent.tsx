import 'bootstrap/dist/css/bootstrap.min.css';
import ExpenseInfoComponentProps from "./model/popupProps/ExpenseInfoComponentProps.ts";
import Expense from "./model/Expense.ts";
import UpdateExpensePopup from "./UpdateExpensePopup.tsx";
import {useState} from "react";
import {removeExpense} from "../api/ExpenseApi.ts";
import SetLimitPopup from "./SetLimitPopup.tsx";
import LimitRequest from "./model/request/LimitRequest.ts";

export default function ExpenseListComponent({expenses, setExpenses, onRemoveExpense}: ExpenseInfoComponentProps) {
    const [openPopupId, setOpenPopupId] = useState<number | null>(null);
    const [openSetLimitPopupName, setOpenSetLimitPopupName] = useState<string | null>(null);
    const [limits, setLimits] = useState<Record<string, LimitRequest | null>>({});

    const openPopup = (id: number) => setOpenPopupId(id);
    const closePopup = () => setOpenPopupId(null);

    const openSetLimitPopup = (name: string) => setOpenSetLimitPopupName(name);
    const closeSetLimitPopup = () => setOpenSetLimitPopupName(null);

    if (expenses.length === 0) {
        return <p className="text-muted text-center">No expenses found for the selected date range.</p>;
    }

    const groupedExpenses = expenses.reduce((acc, expense) => {
        if (!acc[expense.category]) acc[expense.category] = [];
        acc[expense.category].push(expense);
        return acc;
    }, {} as Record<string, Expense[]>);

    const handleDeleteExpense = async (id: number) => {
        try {
            const response = await removeExpense(id);
            if (response.status === 200) {
                onRemoveExpense(id);
            }
        } catch (e) {
            console.error("Expense removing failed:", e);
        }
    };

    const handleSetLimit = (requestEntity: LimitRequest) => {
        setLimits((prev) => ({...prev, [requestEntity.category]: requestEntity}));
        closeSetLimitPopup();
        console.log("Limit Set:", requestEntity);
    };

    return (
        <div className="container mt-4">
            {Object.entries(groupedExpenses).map(([category, categoryExpenses]) => {
                const currentSpent = categoryExpenses.reduce((sum, expense) => sum + expense.amount, 0);
                const limit = limits[category];

                return (
                    <div key={category} className="mb-4">
                        <h4 className="text-primary">{category}</h4>

                        {!limit && (
                            <button
                                className="btn btn-primary mb-2"
                                onClick={() => openSetLimitPopup(category)}
                            >
                                Set Limit
                            </button>
                        )}

                        {limit && (
                            <div className="mb-2 text-muted">
                                <p>Limit: ${limit.limitAmount}</p>
                                <p>Current Spent: ${currentSpent}</p>
                            </div>
                        )}

                        <SetLimitPopup
                            category={category}
                            show={openSetLimitPopupName === category}
                            onClose={closeSetLimitPopup}
                            currentSpent={currentSpent}
                            onSubmit={handleSetLimit}
                        />

                        <div className="row">
                            {categoryExpenses.map((expenseEntity) => (
                                <div className="col" key={expenseEntity.id}>
                                    <div className="card shadow mb-3">
                                        <div className="card-body">
                                            <h5 className="card-title">{expenseEntity.title}</h5>
                                            <p className="card-text">{expenseEntity.description}</p>
                                            <p className="text-muted">Amount: ${expenseEntity.amount}</p>
                                            <p className="text-muted">
                                                Date: {new Date(expenseEntity.date).toLocaleDateString('en-US')}
                                            </p>
                                        </div>
                                        <button
                                            className="btn btn-primary m-2"
                                            onClick={() => openPopup(expenseEntity.id)}
                                        >
                                            Update
                                        </button>
                                        <button
                                            className="btn btn-danger m-2"
                                            onClick={() => handleDeleteExpense(expenseEntity.id)}
                                        >
                                            Delete
                                        </button>

                                        <UpdateExpensePopup
                                            expense={expenseEntity}
                                            show={openPopupId === expenseEntity.id}
                                            onClose={closePopup}
                                            onUpdate={setExpenses}
                                        />
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                );
            })}
        </div>
    );
}
