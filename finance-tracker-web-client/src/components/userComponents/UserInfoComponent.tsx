import UserInfo from "./model/UserInfo.ts";
import {useNavigate} from "react-router-dom";
import {useAuth} from "../security/AuthContext.tsx";

export default function UserInfoComponent(userInfo: UserInfo ) {
    const navigate = useNavigate();
    const auth = useAuth();
    return (
        <header className="bg-primary text-white p-4 rounded shadow mb-4 bg-white border-2 border-dark">
            <h1 className="text-center display-5 text-black">Expense Tracker</h1>
            <h4 className="text-center text-black">Welcome, {userInfo.username}!</h4>
            <button className="btn btn-outline-primary m-2" onClick={() => navigate("/update")}>Update profile</button>
            <button className="btn btn-outline-danger m-2" onClick={() => auth.logout()}>Log out</button>
        </header>
    )
}