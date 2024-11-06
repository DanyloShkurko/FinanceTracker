import axios from "axios";

export const userApiClient = axios.create({
    baseURL: 'http://localhost:8081/api/v1',
});

export const expenseApiClient = axios.create({
    baseURL: 'http://localhost:8080/api/v1',
});
