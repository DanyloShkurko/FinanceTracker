import {expenseApiClient} from "./ServicesApiClients.ts";
import ExpenseFormFields from "../expenseComponents/ExpenseFormFields.ts";

export function findAllExpenses(){
    return expenseApiClient.get("/expenses/listUser");
}

export function createExpense(expense: ExpenseFormFields){
    console.log(expense);
    return expenseApiClient.post("/expenses/add", expense);
}