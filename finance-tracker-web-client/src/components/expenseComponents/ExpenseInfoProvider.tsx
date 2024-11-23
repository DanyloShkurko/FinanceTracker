import { useEffect, useState, useMemo } from "react";
import ExpenseListComponent from "./ExpenseListComponent.tsx";
import findAllExpenses from "../api/ExpenseApi.ts";
import Expense from "./Expense.ts";
import { AxiosResponse } from "axios";
import { getUserInfo } from "../api/UserApi.ts";
import UserInfo from "../userComponents/UserInfo.ts";
import { DateFilterComponent } from "./DateFilterComponent.tsx";
import 'bootstrap/dist/css/bootstrap.min.css';
import ExpenseDiagramComponent from "./ExpenseDiagramComponent.tsx";
import CreateExpensePopup from "./CreateExpensePopup.tsx";

export default function ExpenseInfoProvider() {
    const [userInfo, setUserInfo] = useState<UserInfo | undefined>(undefined);
    const [expenses, setExpenses] = useState<Expense[]>([]);
    const [filteredExpenses, setFilteredExpenses] = useState<Expense[]>([]);

    const [isPopupOpen, setIsPopupOpen] = useState(false);

    const openPopup = () => setIsPopupOpen(true);
    const closePopup = () => setIsPopupOpen(false);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const user: AxiosResponse<UserInfo> = await getUserInfo();
                const expensesInfo: AxiosResponse<Expense[]> = await findAllExpenses();

                setUserInfo(user.data);
                setExpenses(expensesInfo.data);
                setFilteredExpenses(expensesInfo.data);
            } catch (error) {
                console.error("Error fetching user info:", error);
            }
        };

        fetchData();
    }, []);

    function onFilter(startDate: Date, endDate: Date) {
        const filtered = expenses.filter((expense) => {
            const expenseDate = new Date(expense.date);
            return expenseDate >= startDate && expenseDate <= endDate;
        });
        setFilteredExpenses(filtered);
    }

    const totalSpent = useMemo(
        () => filteredExpenses.reduce((sum, expense) => sum + expense.amount, 0),
        [filteredExpenses]
    );

    if (!userInfo) return <div>Loading...</div>;

    return (
        <div className="container my-4">
            <header className="mb-4">
                <h1 className="text-center">Expense Tracker</h1>
                <h4 className="text-center text-muted">Welcome, {userInfo.username}!</h4>
            </header>
            <section className="mb-4">
                <h3>Total Spent: <span className="text-primary">${totalSpent.toFixed(2)}</span></h3>
                <button className="btn btn-success btn-md" onClick={openPopup}>Create expense</button>
                <CreateExpensePopup show={isPopupOpen} onClose={closePopup}/>
                <DateFilterComponent onFilter={onFilter} />
            </section>
            <section>
                <ExpenseDiagramComponent expenses={filteredExpenses} />
            </section>
            <section>
                <ExpenseListComponent expenses={filteredExpenses} />
            </section>
        </div>
    );
}
