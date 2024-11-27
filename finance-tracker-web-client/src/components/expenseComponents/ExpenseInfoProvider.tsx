import {useEffect, useState, useMemo} from "react";
import ExpenseListComponent from "./ExpenseListComponent.tsx";
import {findAllExpenses} from "../api/ExpenseApi.ts";
import {AxiosResponse} from "axios";
import {DateFilterComponent} from "./DateFilterComponent.tsx";
import 'bootstrap/dist/css/bootstrap.min.css';
import ExpenseDiagramComponent from "./ExpenseDiagramComponent.tsx";
import CreateExpensePopup from "./CreateExpensePopup.tsx";
import Expense from "./model/Expense.ts";
import UserInfoProvider from "../userComponents/UserInfoProvider.tsx";

export default function ExpenseInfoProvider() {
    const [expenses, setExpenses] = useState<Expense[]>([]);
    const [filteredExpenses, setFilteredExpenses] = useState<Expense[]>([]);

    const [isPopupOpen, setIsPopupOpen] = useState(false);

    const openPopup = () => setIsPopupOpen(true);
    const closePopup = () => setIsPopupOpen(false);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const expensesInfo: AxiosResponse<Expense[]> = await findAllExpenses();

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

    function handleUpdatingExpenses(expense: Expense) {
        setExpenses((prev) => [...prev, expense]);
        setFilteredExpenses((prev) => [...prev, expense]);
    }

    function handleDeletingExpense(id: number) {
        setExpenses((prev) => prev.filter((expense) => expense.id !== id));
        setFilteredExpenses((prev) => prev.filter((expense) => expense.id !== id));
    }

    const totalSpent = useMemo(
        () => filteredExpenses.reduce((sum, expense) => sum + expense.amount, 0),
        [filteredExpenses]
    );

    return (
        <div className="container my-4">
            <UserInfoProvider/>
            <section className="mb-4">
                <h3>Total Spent: <span className="text-primary">${totalSpent.toFixed(2)}</span></h3>
                <button className="btn btn-success btn-lg" hidden={isPopupOpen} onClick={openPopup}>
                    Create Expense
                </button>
                <CreateExpensePopup
                    show={isPopupOpen}
                    onClose={closePopup}
                    onAddExpense={handleUpdatingExpenses}
                />

                <DateFilterComponent onFilter={onFilter}/>
            </section>
            <section>
                <ExpenseDiagramComponent expenses={filteredExpenses}/>
            </section>
            <section>
                <ExpenseListComponent expenses={filteredExpenses} setExpenses={handleUpdatingExpenses} onRemoveExpense={handleDeletingExpense}/>
            </section>
        </div>
    );
}
