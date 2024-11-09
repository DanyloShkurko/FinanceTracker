import {useState} from "react";
import * as React from "react";
import {useAuth} from "../security/AuthContext.tsx";
import {useNavigate} from "react-router-dom";

export default function LoginComponent() {
    const [email, setEmail] = useState("")
    const [password, setPassword] = useState("")

    const [showSuccessMsg, setShowSuccessMsg] = useState(false);
    const [showFailureMsg, setShowFailureMsg] = useState(false);

    function handleEmailChange(e: React.ChangeEvent<HTMLInputElement>) {
        setEmail(e.target.value);
    }

    function handlePasswordChange(e: React.ChangeEvent<HTMLInputElement>) {
        setPassword(e.target.value);
    }

    const authContext = useAuth();
    const navigate = useNavigate();

    async function handleSubmit() {
        if (await authContext.login(email, password)) {
            setShowFailureMsg(false);
            setShowSuccessMsg(true);
            console.log(authContext.jwtToken);
            console.log(authContext.email);
            console.log(authContext.isAuthenticated)
            navigate("/me");
        } else {
            setShowSuccessMsg(false);
            setShowFailureMsg(true);
        }
    }

    return (
        <div className="login-form">

            {showSuccessMsg && <div className="login-success">Authentication successfully!</div>}
            {showFailureMsg && <div className="login-fail">Authentication failed!</div>}

            <div className="username">
                <label htmlFor="username">Username: </label>
                <input type="text" name="username" id="username" value={email} onChange={handleEmailChange}/>
            </div>
            <div className="password">
                <label htmlFor="password">Password: </label>
                <input type="password" name="password" id="password" value={password} onChange={handlePasswordChange}/>
            </div>
            <button type='button' name="login" onClick={handleSubmit}>Login</button>
        </div>
    );
}