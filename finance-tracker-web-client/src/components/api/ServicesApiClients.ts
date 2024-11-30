import axios from "axios";

export const apiClient = axios.create({
    baseURL: 'http://localhost:9090/api/v1',
    withCredentials: true,
    headers: {
        'Content-Type': 'application/json',
    }
});