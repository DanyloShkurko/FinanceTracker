import React from 'react';
import {Form, Formik} from "formik";
import * as Yup from 'yup';

interface PopupProps {
    show: boolean;
    onClose: () => void;
    children?: React.ReactNode;
}

const CreateExpensePopup: React.FC<PopupProps> = ({ show, onClose }) => {
    if (!show) return null;

    type ExpenseFormValues = {
        title: string;
        amount: number;
        date: Date;
        category: string;
        description: string;
    }

    const initialValues = {
        title: '',
        amount: 0,
        date: new Date(),
        category: '',
        description: ''
    };

    const validationSchema = Yup.object({
        title: Yup.string()
            .required('Title is required')
            .min(6, 'Title should be at least 6 characters')
            .max(50, 'Title should be maximum 50 characters'),
        amount: Yup.number()
            .required('Amount is required')
            .positive('Amount should be positive'),
        category: Yup.string()
            .required('Category is required'),
        description: Yup.string()
            .required('Description is required')
    })

    async function handleSubmit(values: ExpenseFormValues) {
        /*
        TODO: finish creating todo function
         */
    }

    return (
        <div className="popup-overlay">
            <div className="popup-content">
                <button className="close-button" onClick={onClose}>
                    ×
                </button>
                <Formik
                initialValues={initialValues}
                validationSchema={validationSchema}
                onSubmit={handleSubmit}
                >
                    <Form>

                    </Form>
                </Formik>
            </div>
        </div>
    );
};

export default CreateExpensePopup;
