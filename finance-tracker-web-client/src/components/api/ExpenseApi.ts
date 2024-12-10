import {apiClient} from "./ServicesApiClients.ts";
import ExpenseFormFields from "../expenseComponents/model/ExpenseFormFields.ts";
import LimitRequest from "../expenseComponents/model/request/LimitRequest.ts";

export function findAllExpenses(){
    return apiClient.get("/expenses/listUser");
}

export function createExpense(expense: ExpenseFormFields){
    console.log(expense);
    return apiClient.post("/expenses/add", expense);
}

export function updateExpense(expenseId:number, expense: ExpenseFormFields){
    return apiClient.put("/expenses/update?expenseId="+expenseId, expense);
}

export function removeExpense(expenseId:number){
    return apiClient.delete("/expenses/delete?expenseId="+expenseId);
}

export function fetchLimits(){
    return apiClient.get("/expenses/limits");
}

export function createLimit(limit: LimitRequest){
    return apiClient.post("/expenses/limit", limit);
}