import {userApiClient} from "./ServicesApiClients.ts";

export function getUserInfo(){
    return userApiClient.get(`/user`)
}