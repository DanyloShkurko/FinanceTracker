import { ReactNode, useState } from "react";
import { AuthContext } from "./AuthContext";
import { loginViaUserService } from "../api/AuthApi.ts";
import {userApiClient} from "../api/ServicesApiClients.ts";

export default function AuthProvider({ children }: { children: ReactNode }) {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [email, setEmail] = useState("");
    const [jwtToken, setJwtToken] = useState("");

    async function login(email: string, password: string): Promise<boolean> {
        try {
            const response = await loginViaUserService(email, password);

            if (response.status === 200) {
                const jwt = "Bearer " + response.data.token;
                setEmail(email);
                setIsAuthenticated(true);
                setJwtToken(jwt);

                userApiClient.interceptors.request.use(
                    (config) => {
                        config.headers.Authorization = jwt;
                        return config;
                    }
                )

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
        setJwtToken("");
        setEmail("");
        setIsAuthenticated(false);
    }

    return (
        <AuthContext.Provider value={{
            isAuthenticated,
            setIsAuthenticated,
            login,
            logout,
            email,
            setEmail,
            jwtToken,
            setJwtToken
        }}>
            {children}
        </AuthContext.Provider>
    );
}
