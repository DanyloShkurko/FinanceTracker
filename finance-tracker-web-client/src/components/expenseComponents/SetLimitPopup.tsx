import {SetLimitPopupProps} from "./model/popupProps/SetLimitPopupProps.ts";
import {ErrorMessage, Field, Form, Formik} from "formik";
import {initialValues, validationSchema} from "./model/validation/SetLimitPopupValidation.ts";
import LimitRequest from "./model/request/LimitRequest.ts";

export default function SetLimitPopup({category, show, onSubmit, onClose, currentSpent}: SetLimitPopupProps) {
    if (!show) return null;

    return (
        <div>
            <Formik
                initialValues={initialValues}
                validationSchema={validationSchema}
                onSubmit={(values) => {
                    onSubmit(
                        new LimitRequest(
                            values.amount,
                            currentSpent,
                            category,
                            new Date(),
                            values.endDate)
                    )
                }}
            >
                {({isSubmitting}) => (
                    <Form>

                        <div className="row mb-2">
                            <div className="col mb-2">
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
                            <div className="col-md-6">
                                <label htmlFor="endDate" className="form-label small">End Date</label>
                                <Field
                                    type="date"
                                    id="endDate"
                                    name="endDate"
                                    className="form-control form-control-sm"
                                />
                                <ErrorMessage name="endDate" component="div" className="text-danger small"/>
                            </div>
                        </div>

                        <button
                            type="submit"
                            className="btn btn-success btn-sm w-10"
                            disabled={isSubmitting}
                        >
                            {isSubmitting ? 'Create...' : 'Create'}
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
    )
}