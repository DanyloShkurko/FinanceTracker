import {apiClient} from "./ServicesApiClients.ts";

export function loginViaUserService(email: string, password: string) {
    return apiClient.post(`/auth/login`, {email, password});
}

export function signupViaUserService(username: string, email: string, password: string) {
    return apiClient.post(`/auth/signup`, {username, email, password});
}