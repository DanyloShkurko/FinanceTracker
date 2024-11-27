import * as Yup from "yup";

export const initialValues = {
    username: '',
    email: '',
    password: '',
    confirmPassword: ''
};

export const validationSchema = Yup.object({
    username: Yup.string()
        .required('Username is required')
        .min(3, 'Username should be at least 3 characters')
        .max(100, 'Username should be maximum 50 characters'),
    email: Yup.string()
        .email('Email should be valid')
        .required('Email is required'),
    password: Yup.string()
        .required('Password is required')
        .min(8, 'Password should be at least 8 characters')
        .matches(/[a-zA-Z]/, 'Password should contain both letters')
        .matches(/[0-9]/, 'Password should contain at least one number')
        .matches(/[!@#$%^&*(),.?":{}|<>]/, 'Password should contain at least one special character'),
    confirmPassword: Yup.string()
        .oneOf([Yup.ref('password'), undefined], 'Passwords must match')
        .required('Confirm password is required'),
})