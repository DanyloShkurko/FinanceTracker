export default function UserInfoComponent(user: {
    username: string,
    email: string,
    password: string,
    role: string
}) {
    return (
        <div className="form-group">
            User

            Name: {user.username}
            Email: {user.email}
            Password: {user.password}
            Role: {user.role}
        </div>
    )
}