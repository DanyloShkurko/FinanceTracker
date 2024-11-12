import {useEffect, useState} from "react";
import ExpenseListComponent from "./ExpenseInfoComponent.tsx";
import findAllExpenses from "../api/ExpenseApi.ts";
import Expense from "./Expense.ts";
import {AxiosResponse} from "axios";
import {getUserInfo} from "../api/UserApi.ts";
import UserInfo from "../userComponents/UserInfo.ts";

export default function ExpenseInfoProvider() {
    const [userInfo, setUserInfo] = useState<UserInfo | undefined>(undefined);
    const [expenses, setExpenses] = useState<Expense[]>([]);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const user: AxiosResponse<UserInfo> = await getUserInfo();
                const expensesInfo: AxiosResponse<Expense[]> = await findAllExpenses();

                setUserInfo(user.data);
                setExpenses(expensesInfo.data);
            } catch (error) {
                console.error("Error fetching user info:", error);
            }
        };

        fetchData();
    }, []);

    if (!userInfo) return <div>Loading...</div>;

    return (
        <div className="container">
            <ExpenseListComponent expenses={expenses} user={userInfo} />
        </div>
    );
}
