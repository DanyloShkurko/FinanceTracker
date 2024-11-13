import { useState } from "react";
import { Formik, Form, Field, ErrorMessage } from 'formik';
import { useNavigate } from "react-router-dom";
import { signupViaUserService } from "../api/AuthApi.ts";
import * as Yup from 'yup';

export default function SignupComponent() {
    const [showSuccessMsg, setShowSuccessMsg] = useState(false);
    const [showFailureMsg, setShowFailureMsg] = useState(false);
    const [errorMsg, setErrorMsg] = useState<string | null>(null);

    type SignupFormValues = {
        username: string;
        email: string;
        password: string;
    };

    const initialValues: SignupFormValues = {
        username: "",
        email: "",
        password: ""
    };

    const navigate = useNavigate();

    async function handleSubmit(values: SignupFormValues) {
        try {
            const response = await signupViaUserService(
                values.username,
                values.email,
                values.password
            );

            if (response && response.status === 200) {
                setShowFailureMsg(false);
                setShowSuccessMsg(true);
                setErrorMsg(null);
                navigate("/login");
            } else {
                setShowFailureMsg(true);
                setShowSuccessMsg(false);
                setErrorMsg("An error occurred during signup.");
            }
        } catch (error: unknown) {
            setShowFailureMsg(true);
            setShowSuccessMsg(false);
            if (typeof error === 'object' && error !== null && 'response' in error) {
                const errorResponse = error as { response: { data: { message: string } } };
                setErrorMsg(errorResponse.response.data.message);
            } else {
                setErrorMsg("An unexpected error occurred.");
            }

            console.error(error);
        }
    }

    const validationSchema = Yup.object({
        username: Yup.string()
            .required('Username is required')
            .min(3, 'Username should be at least 3 characters')
            .max(50, 'Username should be maximum 50 characters'),
        email: Yup.string()
            .email('Email should be valid')
            .required('Email is required'),
        password: Yup.string()
            .required('Password is required')
            .min(8, 'Password should be at least 8 characters'),
        confirmPassword: Yup.string()
            .oneOf([Yup.ref('password'), undefined], 'Passwords must match')
            .required('Confirm password is required'),
    });

    return (
        <div className="container d-flex justify-content-center align-items-center vh-100">
            <div className="card p-4 shadow-sm" style={{ maxWidth: '400px', width: '100%' }}>
                <h2 className="text-center mb-4">Sign up</h2>

                {showSuccessMsg && (
                    <div className="alert alert-success">Signup successful!</div>
                )}

                {showFailureMsg && errorMsg && (
                    <div className="alert alert-danger">{errorMsg}</div>
                )}

                <Formik
                    initialValues={initialValues}
                    validationSchema={validationSchema}
                    onSubmit={handleSubmit}
                >
                    {({ isSubmitting }) => (
                        <Form>
                            <div className="mb-3">
                                <label htmlFor="username" className="form-label">Username</label>
                                <Field
                                    type="text"
                                    id="username"
                                    name="username"
                                    className="form-control"
                                    placeholder="Enter your username"
                                />
                                <ErrorMessage name="username" component="div" className="text-danger" />
                            </div>

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

                            <div className="mb-3">
                                <label htmlFor="confirmPassword" className="form-label">Confirm password</label>
                                <Field
                                    type="password"
                                    id="confirmPassword"
                                    name="confirmPassword"
                                    className="form-control"
                                    placeholder="Confirm your password"
                                />
                                <ErrorMessage name="confirmPassword" component="div" className="text-danger" />
                            </div>

                            <button
                                type="submit"
                                className="btn btn-primary w-100"
                                disabled={isSubmitting}
                            >
                                {isSubmitting ? 'Signing up...' : 'Sign up'}
                            </button>
                        </Form>
                    )}
                </Formik>
            </div>
        </div>
    );
};
