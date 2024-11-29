import * as Yup from "yup";

export const initialValues = {
    amount: 0,
    startDate: new Date(),
    endDate: new Date()
};

export const validationSchema = Yup.object({
    amount: Yup.number()
        .required('Amount is required')
        .positive('Amount should be positive'),
    startDate: Yup.date()
        .required("Start date is required")
        .typeError('Invalid date format')
        .min(new Date(), "Date mustn't be in the past"),
    endDate: Yup.date()
        .required('End date is required')
        .typeError('Invalid date format')
        .min(Yup.ref('startDate'), 'End date must be after or equal to start date')
})