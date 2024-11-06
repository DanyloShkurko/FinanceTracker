import {userApiClient} from "./ServicesApiClients.ts";

export function loginViaUserService(email:string, password:string) {
    return userApiClient.post(`/auth/login`,{email, password})
}

export function signupViaUserService(username:string, email:string, password:string) {
    return userApiClient.post(`/auth/signup`,{username, email, password})
}