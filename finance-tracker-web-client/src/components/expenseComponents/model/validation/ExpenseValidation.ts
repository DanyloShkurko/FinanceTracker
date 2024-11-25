import * as Yup from 'yup';

export const initialValues = {
    title: '',
    amount: 0,
    category: '',
    description: ''
};

export const validationSchema = Yup.object({
    title: Yup.string()
        .required('Title is required')
        .min(6, 'Title should be at least 6 characters')
        .max(100, 'Title should be maximum 50 characters'),
    amount: Yup.number()
        .required('Amount is required')
        .positive('Amount should be positive'),
    category: Yup.string()
        .required('Category is required'),
    description: Yup.string()
        .required('Description is required')
        .max(50, "Description should be maximum 50 characters")
})