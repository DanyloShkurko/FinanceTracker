export default interface Expense {
    id: number;
    title: string;
    description: string;
    category: string;
    date: Date;
    amount: number;
}