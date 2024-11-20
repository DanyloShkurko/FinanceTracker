import 'bootstrap/dist/css/bootstrap.min.css';
import ExpenseInfoComponentProps from "./ExpenseInfoComponentProps.ts";
import Expense from "./Expense.ts";

export default function ExpenseListComponent({ expenses }: ExpenseInfoComponentProps) {
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
                        <div className="row g-3">
                            {categoryExpenses.map((expense) => (
                                <div className="col" key={expense.id}>
                                    <div className="card shadow-sm">
                                        <div className="card-body">
                                            <h5 className="card-title text-truncate">{expense.title}</h5>
                                            <p className="card-text text-muted">{expense.description}</p>
                                            <ul className="list-unstyled mb-0">
                                                <li><strong>Date:</strong> {expense.date ? new Date(expense.date).toLocaleDateString() : "No date provided"}</li>
                                                <li><strong>Amount:</strong> ${expense.amount.toFixed(2)}</li>
                                            </ul>
                                        </div>
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
