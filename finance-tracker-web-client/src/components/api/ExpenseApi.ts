import {expenseApiClient} from "./ServicesApiClients.ts";
import ExpenseFormFields from "../expenseComponents/model/ExpenseFormFields.ts";
import LimitRequest from "../expenseComponents/model/request/LimitRequest.ts";

export function findAllExpenses(){
    return expenseApiClient.get("/expenses/listUser");
}

export function createExpense(expense: ExpenseFormFields){
    console.log(expense);
    return expenseApiClient.post("/expenses/add", expense);
}

export function updateExpense(expenseId:number, expense: ExpenseFormFields){
    return expenseApiClient.put("/expenses/update?expenseId="+expenseId, expense);
}

export function removeExpense(expenseId:number){
    return expenseApiClient.delete("/expenses/delete?expenseId="+expenseId);
}

export function fetchLimits(){
    return expenseApiClient.get("/expenses/limits");
}

export function createLimit(limit: LimitRequest){
    return expenseApiClient.post("/expenses/limit", limit);
}