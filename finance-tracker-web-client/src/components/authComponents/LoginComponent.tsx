import {useState} from "react";
import { Formik, Form, Field, ErrorMessage } from 'formik';
import * as Yup from 'yup';
import {useAuth} from "../security/AuthContext.tsx";
import {useNavigate} from "react-router-dom";

export default function LoginComponent() {
    type LoginFormValues = {
        email: string;
        password: string;
    };

    const initialValues: LoginFormValues = {
        email: '',
        password: '',
    };

    const validationSchema = Yup.object({
        email: Yup.string()
            .email('Invalid email format')
            .required('Email is required'),
        password: Yup.string()
            .min(6, 'Password must be at least 6 characters')
            .required('Password is required'),
    });

    const [showSuccessMsg, setShowSuccessMsg] = useState(false);
    const [showFailureMsg, setShowFailureMsg] = useState(false);

    const authContext = useAuth();
    const navigate = useNavigate();

    async function handleSubmit(values: LoginFormValues) {
        if (await authContext.login(
            values.email,
            values.password
        )) {
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
        <div className="container d-flex justify-content-center align-items-center vh-100">
            <div className="card p-4 shadow-sm" style={{ maxWidth: '400px', width: '100%' }}>
                <h2 className="text-center mb-4">Login</h2>

                {showSuccessMsg && (
                    <div className="alert alert-success">Signup successful!</div>
                )}

                {showFailureMsg && (
                    <div className="alert alert-danger">Authentication failed!</div>
                )}

                <Formik
                    initialValues={initialValues}
                    validationSchema={validationSchema}
                    onSubmit={handleSubmit}
                >
                    {({ isSubmitting }) => (
                        <Form>
                            <div className="mb-3">
                                <label htmlFor="email" className="form-label">Email</label>
                                <Field
                                    type="email"
                                    id="email"
                                    name="email"
                                    className="form-control"
                                    placeholder="Enter your email"
                                />
                                <ErrorMessage name="email" component="div" className="text-danger" />
                            </div>

                            <div className="mb-3">
                                <label htmlFor="password" className="form-label">Password</label>
                                <Field
                                    type="password"
                                    id="password"
                                    name="password"
                                    className="form-control"
                                    placeholder="Enter your password"
                                />
                                <ErrorMessage name="password" component="div" className="text-danger" />
                            </div>

                            <button
                                type="submit"
                                className="btn btn-primary w-100"
                                disabled={isSubmitting}
                            >
                                {isSubmitting ? 'Logging in...' : 'Login'}
                            </button>
                        </Form>
                    )}
                </Formik>
            </div>
        </div>
    );
};