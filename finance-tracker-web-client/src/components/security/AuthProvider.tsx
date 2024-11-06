import { ReactNode, useState } from "react";
import { AuthContext } from "./AuthContext";
import { loginViaUserService } from "../api/AuthApi.ts";

export default function AuthProvider({ children }: { children: ReactNode }) {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [email, setEmail] = useState("");
    const [basicToken, setBasicToken] = useState("");

    async function login(email: string, password: string): Promise<boolean> {
        try {
            const response = await loginViaUserService(email, password);

            if (response.status === 200) {
                const jwt = "Bearer " + response.data.token;
                setEmail(email);
                setIsAuthenticated(true);
                setBasicToken(jwt);
                return true;
            } else {
                logout();
                return false;
            }
        } catch (error) {
            logout();
            console.error("Login error:", error);
            return false;
        }
    }

    function logout() {
        setBasicToken("");
        setEmail("");
        setIsAuthenticated(false);
    }

    return (
        <AuthContext.Provider value={{ isAuthenticated, login, logout, email, basicToken }}>
            {children}
        </AuthContext.Provider>
    );
}
