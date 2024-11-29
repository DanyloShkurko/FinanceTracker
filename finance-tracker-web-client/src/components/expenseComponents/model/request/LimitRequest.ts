export default class LimitRequest{
    limitAmount: number;
    currentSpent: number;
    category: string;
    startDate: Date;
    endDate: Date;

    constructor(limitAmount: number, currentSpent: number, category: string, startDate: Date, endDate: Date) {
        this.limitAmount = limitAmount;
        this.currentSpent = currentSpent;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}