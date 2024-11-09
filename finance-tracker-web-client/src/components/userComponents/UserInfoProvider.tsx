import { useState, useEffect } from "react";
import { getUserInfo } from "../api/UserApi.ts";
import { AxiosResponse } from "axios";
import UserInfoComponent from "./UserInfoComponent.tsx";

interface UserInfo {
    id: number;
    username: string;
    email: string;
    role: string;
}

export default function UserInfoProvider() {
    const [userInfo, setUserInfo] = useState<UserInfo | null>(null);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response: AxiosResponse<UserInfo> = await getUserInfo();
                setUserInfo(response.data);
            } catch (error) {
                console.error("Error fetching user info:", error);
            }
        };

        fetchData(); // Вызов асинхронной функции
    }, []);

    return (
        <div>
            {userInfo ? (
                <UserInfoComponent username={userInfo.username} email={userInfo.email} role={userInfo.role}/>
            ) : (
                <p>Loading...</p>
            )}
        </div>
    );
}
