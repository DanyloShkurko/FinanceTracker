import {userApiClient} from "./ServicesApiClients.ts";
import UpdateApiRequest from "../userComponents/model/UpdateApiRequest.ts";

export function getUserInfo(){
    return userApiClient.get(`/user`);
}

export function updateUserInfo(request: UpdateApiRequest){
    return userApiClient.put(`/user/update`, request);
}