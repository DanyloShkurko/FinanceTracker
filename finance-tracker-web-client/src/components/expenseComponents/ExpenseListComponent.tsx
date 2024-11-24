import 'bootstrap/dist/css/bootstrap.min.css';
import ExpenseInfoComponentProps from "./model/ExpenseInfoComponentProps.ts";
import Expense from "./model/Expense.ts";

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
                        <div className="row">
                            {categoryExpenses.map((expense) => (
                                <div className="col-md-4 mb-4" key={expense.id}>
                                    <div className="card shadow">
                                        <div className="card-body">
                                            <h5 className="card-title">{expense.title}</h5>
                                            <p className="card-text">{expense.description}</p>
                                            <p className="text-muted">Amount: ${expense.amount}</p>
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
