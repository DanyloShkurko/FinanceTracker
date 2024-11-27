import UserInfo from "./UserInfo.ts";

export default function UserInfoComponent(user: UserInfo ) {
    return (
        <div className="form-group">
            User

            <p>Name: {user.username}</p>
            <p>Email: {user.email}</p>
            <p>Role: {user.role}</p>
        </div>
    )
}