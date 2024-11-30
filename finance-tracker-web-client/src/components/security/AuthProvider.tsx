import { ReactNode, useEffect, useState } from "react";
import { JwtPayload } from "jwt-decode";
import { AuthContext } from "./AuthContext";
import { loginViaUserService } from "../api/AuthApi.ts";
import { apiClient } from "../api/ServicesApiClients.ts";
import { jwtDecode } from "jwt-decode";

export default function AuthProvider({ children }: { children: ReactNode }) {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [email, setEmail] = useState("");
    const [jwtToken, setJwtToken] = useState("");
    const [isInitializing, setIsInitializing] = useState(true);

    useEffect(() => {
        const storedToken = localStorage.getItem("jwtToken");
        const storedEmail = localStorage.getItem("email");

        if (storedToken && storedEmail) {
            try {
                const decodedToken: JwtPayload = jwtDecode(storedToken);
                const currentTime = Date.now() / 1000;

                if (decodedToken.exp && decodedToken.exp > currentTime) {
                    setJwtToken(storedToken);
                    setEmail(storedEmail);
                    setIsAuthenticated(true);

                    apiClient.interceptors.request.use((config) => {
                        config.headers.Authorization = storedToken;
                        return config;
                    });

                    console.log("Auth state restored successfully");
                } else {
                    console.log("Token expired, logging out");
                    logout();
                }
            } catch (error) {
                console.error("Failed to decode token:", error);
                logout();
            }
        }

        setIsInitializing(false);
    }, []);

    async function login(email: string, password: string): Promise<boolean> {
        try {
            const response = await loginViaUserService(email, password);

            if (response.status === 200) {
                const jwt = "Bearer " + response.data.token;
                setEmail(email);
                setIsAuthenticated(true);
                setJwtToken(jwt);

                localStorage.setItem("jwtToken", jwt);
                localStorage.setItem("email", email);

                console.log(jwt);

                apiClient.interceptors.request.use((config) => {
                    config.headers.Authorization = jwt;
                    return config;
                });

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

        localStorage.removeItem("jwtToken");
        localStorage.removeItem("email");
    }

    if (isInitializing) {
        return <div>Loading...</div>;
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
            setJwtToken,
        }}>
            {children}
        </AuthContext.Provider>
    );
}
