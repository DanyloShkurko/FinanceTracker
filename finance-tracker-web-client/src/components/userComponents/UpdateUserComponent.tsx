import {ErrorMessage, Field, Form, Formik} from "formik";
import {initialValues, validationSchema} from "./model/UpdateFormInfo.ts";
import UserFormFields from "./model/UserFormFields.ts";
import {useNavigate} from "react-router-dom";
import {updateUserInfo} from "../api/UserApi.ts";
import UpdateApiRequest from "./model/UpdateApiRequest.ts";
import {useState} from "react";
import {useAuth} from "../security/AuthContext.tsx";

export default function UpdateUserComponent() {
    const [showSuccessMsg, setShowSuccessMsg] = useState(false);
    const [showFailureMsg, setShowFailureMsg] = useState(false);
    const [errorMsg, setErrorMsg] = useState<string | null>(null);

    const navigate = useNavigate();
    const auth = useAuth();

    async function handleSubmit(values: UserFormFields) {
        try {
            const response = await updateUserInfo(values as UpdateApiRequest);
            if (response.status === 200) {
                setShowFailureMsg(false);
                setShowSuccessMsg(true);
                setErrorMsg(null);
                auth.logout();
                navigate("/login");
            } else {
                setShowFailureMsg(true);
                setShowSuccessMsg(false);
                setErrorMsg("An error occurred during signup.");
            }
        } catch (e) {
            setShowFailureMsg(true);
            setShowSuccessMsg(false);
            if (typeof e === 'object' && e !== null && 'response' in e) {
                const errorResponse = e as { response: { data: { message: string } } };
                setErrorMsg(errorResponse.response.data.message);
            } else {
                setErrorMsg("An unexpected error occurred.");
            }
            console.log("Error while updating user info:", e);
        }
    }

    return (
        <div className="container mt-5">
            {showSuccessMsg && (
                <div className="alert alert-success text-center">Update successful! Redirecting to login...</div>
            )}

            {showFailureMsg && errorMsg && (
                <div className="alert alert-danger text-center">{errorMsg}</div>
            )}

            <div className="card shadow">
                <div className="card-header text-center bg-primary text-white">
                    <h4>Update Your Information</h4>
                </div>
                <div className="card-body">
                    <Formik
                        initialValues={initialValues}
                        validationSchema={validationSchema}
                        onSubmit={handleSubmit}
                    >
                        {({isSubmitting}) => (
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
                                    <ErrorMessage name="username" component="div" className="text-danger"/>
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
                                    <ErrorMessage name="email" component="div" className="text-danger"/>
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
                                    <ErrorMessage name="password" component="div" className="text-danger"/>
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
                                    <ErrorMessage name="confirmPassword" component="div" className="text-danger"/>
                                </div>

                                <button
                                    type="submit"
                                    className="btn btn-outline-primary w-100"
                                    disabled={isSubmitting}
                                >
                                    {isSubmitting && <span className="spinner-border spinner-border-sm me-2"></span>}
                                    {isSubmitting ? 'Updating...' : 'Update'}
                                </button>
                            </Form>
                        )}
                    </Formik>
                </div>
            </div>
        </div>
    );
}
