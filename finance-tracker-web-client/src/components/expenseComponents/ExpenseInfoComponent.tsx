import 'bootstrap/dist/css/bootstrap.min.css';
import ExpenseInfoComponentProps from "./ExpenseInfoComponentProps.ts";


export default function ExpenseListComponent({ expenses, user }: ExpenseInfoComponentProps) {
    return (
        <div className="container mt-4">
            {/* User Information */}
            <div className="card mb-4 shadow-sm">
                <div className="card-header bg-primary text-white">
                    <h4>User Information</h4>
                </div>
                <div className="card-body">
                    <p><strong>Name:</strong> {user.username}</p>
                    <p><strong>Email:</strong> {user.email}</p>
                    <p><strong>Role:</strong> {user.role}</p>
                </div>
            </div>

            {/* Expenses List */}
            <div className="card shadow-sm">
                <div className="card-header bg-secondary text-white">
                    <h4>Expenses</h4>
                </div>
                <ul className="list-group list-group-flush">
                    {expenses.map((expense, index) => (
                        <li key={index} className="list-group-item">
                            <h5 className="mb-1">{expense.title}</h5>
                            <p className="mb-1"><strong>Description:</strong> {expense.description}</p>
                            <p className="mb-1"><strong>Category:</strong> {expense.category}</p>
                            <p className="mb-1"><strong>Date:</strong> {expense.date.toString()}</p>
                            <p className="mb-1"><strong>Amount:</strong> ${expense.amount.toFixed(2)}</p>
                        </li>
                    ))}
                </ul>
            </div>
        </div>
    );
}