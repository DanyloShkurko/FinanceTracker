# Expense Tracking Project

This project provides an API for tracking personal expenses. Users can register, log in, and manage their expenses effectively.

## Use Cases

### 1. User Registration and Authentication
- **Purpose**: Allow users to register and log into their accounts.
- **Endpoints**: 
  - `/register`
  - `/login`
  - `/logout`
- **Description**: Users can register with their email and password. Once logged in, users can access their expense data securely.

### 2. Create a New Expense
- **Purpose**: Allow users to add new expenses.
- **Endpoint**: `/expenses/add`
- **Description**: Users specify the amount, category (e.g., food, transport), date, and description. This information is saved in the database.

### 3. Edit and Delete Expenses
- **Purpose**: Provide flexibility in managing expenses.
- **Endpoints**:
  - `/expenses/edit/{id}`
  - `/expenses/delete/{id}`
- **Description**: Users can modify or delete expense information by specifying its ID.

### 4. View Expense List
- **Purpose**: Allow users to view all their expenses.
- **Endpoint**: `/expenses/list`
- **Description**: Users receive a list of all their expenses, with the option to filter by category and date.

### 5. Analyze Expenses by Category
- **Purpose**: Enable users to analyze expenses by category.
- **Endpoint**: `/expenses/analytics/category`
- **Description**: Displays the total expenses in each category for a specific period (e.g., month).

### 6. Analyze Expenses Over a Period
- **Purpose**: Allow users to view expenses for a selected time period.
- **Endpoint**: `/expenses/analytics/period`
- **Description**: Users select a period (week, month, year), and the application displays total spent amount and comparison with the previous period.

### 7. Set Category Limits
- **Purpose**: Notify users when they reach their spending limit in a specific category.
- **Endpoint**: `/expenses/limit/set`
- **Description**: Users set a maximum amount for a category, and they receive notifications when the limit is exceeded.

### 8. Export Expense Data
- **Purpose**: Allow users to export their expense data.
- **Endpoint**: `/expenses/export`
- **Description**: Users can export their data in CSV or Excel format for further analysis.
