import React, {useState} from 'react';
import {ErrorMessage, Field, Form, Formik} from "formik";
import {createExpense} from "../api/ExpenseApi.ts";
import ExpenseFormFields from "./model/ExpenseFormFields.ts";
import {initialValues, validationSchema} from "./model/validation/ExpenseValidation.ts";
import {PopupProps} from "./model/popupProps/PopupProps.ts";
import Expense from "./model/Expense.ts";

const CreateExpensePopup: React.FC<PopupProps & { onAddExpense: (expense: Expense) => void }> = ({
                                                                                                     show,
                                                                                                     onClose,
                                                                                                     onAddExpense,
                                                                                                 }) => {
    const [showSuccessMsg, setShowSuccessMsg] = useState(false);
    const [showFailureMsg, setShowFailureMsg] = useState(false);

    if (!show) return null;

    async function handleSubmit(values: ExpenseFormFields) {
        try {
            const expenseResponse = await createExpense(values);
            if (expenseResponse.status === 200 && expenseResponse.data) {
                setShowFailureMsg(false);
                setShowSuccessMsg(true);
                onAddExpense(expenseResponse.data as Expense);
                onClose();
            } else {
                throw new Error("Invalid response");
            }
        } catch (error) {
            setShowSuccessMsg(false);
            setShowFailureMsg(true);
            console.error("Expense creation failed:", error);
        }
    }

    return (
        <div className="popup-overlay">
            <div className="popup-content">
                <button className="close-button btn btn-outline-danger btn-sm" onClick={onClose}>X</button>

                {showSuccessMsg && (
                    <div className="alert alert-success alert-dismissible fade show" role="alert">
                        Expense created successfully!
                        <button type="button" className="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                )}
                {showFailureMsg && (
                    <div className="alert alert-danger alert-dismissible fade show" role="alert">
                        Expense creation failed! Please check your input.
                        <button type="button" className="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                )}

                <Formik
                    initialValues={initialValues}
                    validationSchema={validationSchema}
                    onSubmit={handleSubmit}
                >
                    {({isSubmitting}) => (
                        <Form>
                            <div className="mb-2">
                                <label htmlFor="title" className="form-label small">Expense title</label>
                                <Field
                                    type="text"
                                    id="title"
                                    name="title"
                                    className="form-control form-control-sm"
                                    placeholder="Enter title"
                                />
                                <ErrorMessage name="title" component="div" className="text-danger small"/>
                            </div>

                            <div className="mb-2">
                                <label htmlFor="description" className="form-label small">Expense description</label>
                                <Field
                                    type="text"
                                    id="description"
                                    name="description"
                                    className="form-control form-control-sm"
                                    placeholder="Enter description"
                                />
                                <ErrorMessage name="description" component="div" className="text-danger small"/>
                            </div>

                            <div className="mb-2">
                                <label htmlFor="amount" className="form-label small">Expense amount</label>
                                <Field
                                    type="number"
                                    id="amount"
                                    name="amount"
                                    className="form-control form-control-sm"
                                    placeholder="Enter amount"
                                />
                                <ErrorMessage name="amount" component="div" className="text-danger small"/>
                            </div>

                            <div className="mb-2">
                                <label htmlFor="category" className="form-label small">Expense category</label>
                                <Field
                                    as="select"
                                    id="category"
                                    name="category"
                                    className="form-control form-control-sm"
                                >
                                    <option value="">Select category</option>
                                    <option value="FOOD_GROCERIES">Food</option>
                                    <option value="TRANSPORTATION">Transportation</option>
                                    <option value="HOUSING_UTILITIES">Housing utilities</option>
                                    <option value="ENTERTAINMENT">Entertainment</option>
                                    <option value="HEALTHCARE">Health</option>
                                    <option value="INSURANCE">Insurance</option>
                                    <option value="PERSONAL_CARE">Personal care</option>
                                    <option value="CLOTHING">Clothing</option>
                                    <option value="EDUCATION">Education</option>
                                    <option value="SUBSCRIPTIONS_MEMBERSHIPS">Subscriptions/Memberships</option>
                                    <option value="TRAVEL_VACATIONS">Travel vacations</option>
                                    <option value="GIFTS_DONATIONS">Gift donations</option>
                                    <option value="MISCELLANEOUS">Miscellaneous</option>
                                </Field>
                                <ErrorMessage name="category" component="div" className="text-danger small"/>
                            </div>

                            <button
                                type="submit"
                                className="btn btn-primary btn-sm w-100"
                                disabled={isSubmitting}
                            >
                                {isSubmitting ? 'Creating...' : 'Create'}
                            </button>
                        </Form>
                    )}
                </Formik>
            </div>
        </div>
    );
}

export default CreateExpensePopup;
