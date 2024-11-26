import 'bootstrap/dist/css/bootstrap.min.css';
import ExpenseInfoComponentProps from "./model/ExpenseInfoComponentProps.ts";
import Expense from "./model/Expense.ts";
import UpdateExpensePopup from "./UpdateExpensePopup.tsx";
import { useState } from "react";

export default function ExpenseListComponent({ expenses, setExpenses }: ExpenseInfoComponentProps) {
    const [openPopupId, setOpenPopupId] = useState<number | null>(null);


    const openPopup = (id: number) => {
        setOpenPopupId(id);
    };

    const closePopup = () => {
        setOpenPopupId(null);
    };

    if (expenses.length === 0) {
        return <p className="text-muted text-center">No expenses found for the selected date range.</p>;
    }

    const groupedExpenses = expenses.reduce((acc, expense) => {
        if (!acc[expense.category]) {
            acc[expense.category] = [];
        }
        acc[expense.category].push(expense);
        return acc;
    }, {} as Record<string, Expense[]>);

    return (
        <div className="container mt-4">
            {Object.entries(groupedExpenses).length === 0 ? (
                <p className="text-muted text-center">No categories found.</p>
            ) : (
                Object.entries(groupedExpenses).map(([category, categoryExpenses]) => (
                    <div key={category} className="mb-4">
                        <h4 className="text-primary">{category}</h4>
                        <div className="row">
                            {categoryExpenses.map((expenseEntity) => (
                                <div className="col" key={expenseEntity.id}>
                                    <div className="card shadow">
                                        <div className="card-body">
                                            <h5 className="card-title">{expenseEntity.title}</h5>
                                            <p className="card-text">{expenseEntity.description}</p>
                                            <p className="text-muted">Amount: ${expenseEntity.amount}</p>
                                        </div>
                                        <button
                                            className="btn btn-success btn-lg"
                                            onClick={() => openPopup(expenseEntity.id)}
                                            hidden={openPopupId === expenseEntity.id}
                                        >
                                            Update
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
                ))
            )}
        </div>
    );
}
