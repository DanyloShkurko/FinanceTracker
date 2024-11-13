import './App.css';
import LoginComponent from "./components/authComponents/LoginComponent.tsx";
import AuthProvider from "./components/security/AuthProvider.tsx";
import {BrowserRouter, Navigate, Route, Routes} from "react-router-dom";
import {useAuth} from "./components/security/AuthContext.tsx";
import {ReactNode, useEffect} from "react";
import UserInfoProvider from "./components/userComponents/UserInfoProvider.tsx";
import 'bootstrap/dist/css/bootstrap.min.css';
import ExpenseInfoProvider from "./components/expenseComponents/ExpenseInfoProvider.tsx";
import SignupComponent from "./components/authComponents/SignupComponent.tsx";

function AuthenticatedRoute({children}: { children: ReactNode }) {
    const authContext = useAuth();

    useEffect(() => {
        console.log("Auth context updated:", authContext);
    }, [authContext]);

    if (!authContext.isAuthenticated) {
        return <Navigate to="/login"/>;
    }

    return <>{children}</>;
}

function App() {
    return (
        <AuthProvider>
            <BrowserRouter>
                <Routes>
                    <Route path="/login" element={<LoginComponent/>}/>
                    <Route path="/signup" element={<SignupComponent/>}/>
                    <Route path="/me" element={
                        <AuthenticatedRoute>
                            <UserInfoProvider/>
                        </AuthenticatedRoute>
                    }/>
                    <Route path="/list" element={
                        <AuthenticatedRoute>
                            <ExpenseInfoProvider/>
                        </AuthenticatedRoute>
                    }/>
                </Routes>
            </BrowserRouter>
        </AuthProvider>
    );
}

export default App;
