import { Cell, Pie, PieChart, ResponsiveContainer, Tooltip } from "recharts";
import Expense from "./model/Expense.ts";

type DataItem = {
    name: string;
    value: number;
};

const categories: DataItem[] = [
    { name: "FOOD_GROCERIES", value: 0 },
    { name: "TRANSPORTATION", value: 0 },
    { name: "HOUSING_UTILITIES", value: 0 },
    { name: "ENTERTAINMENT", value: 0 },
    { name: "HEALTHCARE", value: 0 },
    { name: "INSURANCE", value: 0 },
    { name: "PERSONAL_CARE", value: 0 },
    { name: "CLOTHING", value: 0 },
    { name: "EDUCATION", value: 0 },
    { name: "SUBSCRIPTIONS_MEMBERSHIPS", value: 0 },
    { name: "TRAVEL_VACATIONS", value: 0 },
    { name: "GIFTS_DONATIONS", value: 0 },
    { name: "MISCELLANEOUS", value: 0 },
];

const COLORS = [
    "#0088FE", "#00C49F", "#FFBB28", "#FF8042", "#FF6384", "#36A2EB",
    "#FF9F40", "#4BC0C0", "#9966FF", "#C9CBCF", "#66BB6A", "#FFA726", "#EF5350"
];

interface ExpenseDiagramComponentProps {
    expenses: Expense[];
}

export default function ExpenseDiagramComponent({ expenses }: ExpenseDiagramComponentProps) {
    const filteredData = categories.map((category) => {
        const totalValue = expenses
            .filter((expense) => expense.category === category.name)
            .reduce((sum, expense) => sum + expense.amount, 0);

        return { ...category, value: totalValue };
    });

    const chartData = filteredData.filter((item) => item.value > 0);

    return (
        <ResponsiveContainer width="110%">
            <PieChart>
                <Pie
                    data={chartData}
                    dataKey="value"
                    nameKey="name"
                    cx="50%"
                    cy="50%"
                    outerRadius={150}
                    fill="#8884d8"
                    label
                >
                    {chartData.map((_entry, index) => (
                        <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                    ))}
                </Pie>
                <Tooltip />
            </PieChart>
        </ResponsiveContainer>
    );
}
