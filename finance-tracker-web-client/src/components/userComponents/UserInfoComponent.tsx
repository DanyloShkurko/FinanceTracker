export default function UserInfoComponent(user: {
    username: string,
    email: string,
    role: string
}) {
    return (
        <div className="form-group">
            User

            <p>Name: {user.username}</p>
            <p>Email: {user.email}</p>
            <p>Role: {user.role}</p>
        </div>
    )
}