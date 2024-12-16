import './App.css';
import LoginComponent from "./components/authComponents/LoginComponent.tsx";
import AuthProvider from "./components/security/AuthProvider.tsx";
import {BrowserRouter, Route, Routes, Navigate} from "react-router-dom";
import {useAuth} from "./components/security/AuthContext.tsx";
import {ReactNode, useEffect} from "react";
import 'bootstrap/dist/css/bootstrap.min.css';
import ExpenseInfoProvider from "./components/expenseComponents/ExpenseInfoProvider.tsx";
import SignupComponent from "./components/authComponents/SignupComponent.tsx";
import UpdateUserComponent from "./components/userComponents/UpdateUserComponent.tsx";

function AuthenticatedRoute({ children }: { children: ReactNode }) {
    const authContext = useAuth();

    useEffect(() => {
        console.log("Auth context in AuthenticatedRoute:", authContext);
    }, [authContext]);

    if (!authContext.isAuthenticated) {
        console.log("User not authenticated, redirecting to /login");
        return <Navigate to="/login" />;
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
                    <Route path="/" element={
                        <AuthenticatedRoute>
                            <ExpenseInfoProvider/>
                        </AuthenticatedRoute>
                    }/>
                    <Route path="/update" element={
                       <AuthenticatedRoute>
                           <UpdateUserComponent/>
                       </AuthenticatedRoute>
                    }/>
                </Routes>
            </BrowserRouter>
        </AuthProvider>
    );
}

export default App;
