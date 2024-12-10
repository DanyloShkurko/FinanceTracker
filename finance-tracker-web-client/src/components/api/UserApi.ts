import {apiClient} from "./ServicesApiClients.ts";
import UpdateApiRequest from "../userComponents/model/UpdateApiRequest.ts";

export function getUserInfo(){
    return apiClient.get(`/user`);
}

export function updateUserInfo(request: UpdateApiRequest){
    return apiClient.put(`/user/update`, request);
}