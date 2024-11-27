import Expense from "./Expense.ts";

export default interface ExpenseInfoComponentProps {
    expenses: Expense[];
    setExpenses: (expense: Expense) => void;
    onRemoveExpense: (id: number) => void;
}