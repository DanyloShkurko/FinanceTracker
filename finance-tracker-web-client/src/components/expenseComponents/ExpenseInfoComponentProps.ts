import Expense from "./Expense.ts";
import UserInfo from "../userComponents/UserInfo.ts";

export default interface ExpenseInfoComponentProps {
    expenses: Expense[];
    user: UserInfo;
}