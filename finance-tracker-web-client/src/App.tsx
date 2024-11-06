import './App.css'
import LoginComponent from "./components/authComponents/LoginComponent.tsx";
import AuthProvider from "./components/security/AuthProvider.tsx";
import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { useAuth } from "./components/security/AuthContext.tsx";
import { ReactNode } from "react";
import UserInfoProvider from "./components/userComponents/UserInfoProvider.tsx";

function AuthenticatedRoute({ children }: { children: ReactNode }) {
    const auth = useAuth();
    if (auth.isAuthenticated) {
        return <>{children}</>;
    }

    return <Navigate to="/login" />;
}

function App() {
    return (
        <AuthProvider>
            <BrowserRouter>
                <Routes>
                    <Route path="/login" element={<LoginComponent />} />
                    <Route
                        path="/me"
                        element={
                            <AuthenticatedRoute>
                                <UserInfoProvider />
                            </AuthenticatedRoute>
                        }
                    />
                </Routes>
            </BrowserRouter>
        </AuthProvider>
    );
}

export default App;
