import Expense from "./model/Expense.ts";
import ExpenseFormFields from "./model/ExpenseFormFields.ts";
import { ErrorMessage, Field, Form, Formik } from "formik";
import { initialValues, validationSchema } from "./model/validation/ExpenseValidation.ts";
import {updateExpense} from "../api/ExpenseApi.ts";

interface UpdateExpensePopupProps {
    expense: Expense;
    show: boolean;
    onClose: () => void;
    onUpdate: (expense: Expense) => void;
}

export default function UpdateExpensePopup({ expense, show, onClose, onUpdate}: UpdateExpensePopupProps) {
    if (!show) return null;

    async function handleSubmit(values: ExpenseFormFields) {
        try {
            const updatedExpense = await updateExpense(expense.id, values);
            if (updatedExpense.status === 200 && updatedExpense.data) {
                onUpdate(updatedExpense.data as Expense);
                onClose();
            }
        } catch (e) {
            console.error("Expense creation failed:", e);
        }
    }

    return (
        <div>
            <Formik
                initialValues={initialValues}
                validationSchema={validationSchema}
                onSubmit={handleSubmit}
            >
                {({ isSubmitting }) => (
                    <Form id={expense.id + ""}>
                        <div className="mb-2">
                            <label htmlFor="title" className="form-label small">Expense title</label>
                            <Field
                                type="text"
                                id="title"
                                name="title"
                                className="form-control form-control-sm"
                                placeholder="Enter title"
                                text={expense.title}
                            />
                            <ErrorMessage name="title" component="div" className="text-danger small" />
                        </div>

                        <div className="mb-2">
                            <label htmlFor="description" className="form-label small">Expense description</label>
                            <Field
                                type="text"
                                id="description"
                                name="description"
                                className="form-control form-control-sm"
                                placeholder="Enter description"
                                text={expense.description}
                            />
                            <ErrorMessage name="description" component="div" className="text-danger small" />
                        </div>

                        <div className="mb-2">
                            <label htmlFor="amount" className="form-label small">Expense amount</label>
                            <Field
                                type="number"
                                id="amount"
                                name="amount"
                                className="form-control form-control-sm"
                                placeholder="Enter amount"
                                text={expense.amount}
                            />
                            <ErrorMessage name="amount" component="div" className="text-danger small" />
                        </div>

                        <div className="mb-2">
                            <label htmlFor="category" className="form-label small">Expense category</label>
                            <Field
                                as="select"
                                id="category"
                                name="category"
                                className="form-control form-control-sm"
                                text={expense.category}
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
                            <ErrorMessage name="category" component="div" className="text-danger small" />
                        </div>

                        <button
                            type="submit"
                            className="btn btn-outline-primary btn-sm w-10"
                            disabled={isSubmitting}
                        >
                            {isSubmitting ? 'Updating...' : 'Update'}
                        </button>
                        <button
                            className="btn btn-danger btn-sm w-20 m-2"
                            onClick={onClose}
                        >
                            Close
                        </button>
                    </Form>
                )}
            </Formik>
        </div>
    );
}
