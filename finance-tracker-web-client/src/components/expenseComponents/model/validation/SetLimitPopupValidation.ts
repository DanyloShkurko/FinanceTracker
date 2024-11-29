import * as Yup from "yup";

export const initialValues = {
    amount: 0,
    endDate: new Date()
};

export const validationSchema = Yup.object({
    amount: Yup.number()
        .required('Amount is required')
        .positive('Amount should be positive'),
    endDate: Yup.date()
        .required('End date is required')
        .typeError('Invalid date format')
        .min(new Date(), 'End date must be after or equal to start date')
})