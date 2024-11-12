import {expenseApiClient} from "./ServicesApiClients.ts";

export default function findAllExpenses(){
    return expenseApiClient.get("/expenses/listUser")
}