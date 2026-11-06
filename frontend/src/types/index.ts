export interface User {
  id: number;
  username: string;
  email: string;
  fullName: string;
  currency: string;
  createdAt?: string;
}

export interface Account {
  id: number;
  userId: number;
  accountName: string;
  accountType: string;
  balance: number;
  currency: string;
  isDefault: boolean;
}

export interface Category {
  id: number;
  userId?: number | null;
  name: string;
  type: 'INCOME' | 'EXPENSE';
  icon?: string;
  color?: string;
  isSystem?: boolean;
}

export interface Transaction {
  id: number;
  accountId: number;
  categoryId: number;
  categoryName?: string;
  categoryIcon?: string;
  categoryColor?: string;
  amount: number;
  type: 'INCOME' | 'EXPENSE';
  description: string;
  transactionDate: string;
  createdAt?: string;
}

export interface Budget {
  id: number;
  userId: number;
  categoryId: number;
  categoryName?: string;
  categoryIcon?: string;
  categoryColor?: string;
  amount: number;
  budgetMonth: number;
  budgetYear: number;
  spentAmount: number;
  remainingAmount: number;
  utilizationPercentage: number;
  status: 'NORMAL' | 'WARNING' | 'EXCEEDED';
}

export interface MonthlyTrend {
  monthName: string;
  income: number;
  expenses: number;
  savings: number;
}

export interface CategorySpending {
  categoryId: number;
  categoryName: string;
  categoryColor?: string;
  totalSpent: number;
  percentage: number;
}

export interface DashboardSummary {
  totalBalance: number;
  totalIncome: number;
  totalExpenses: number;
  netSavings: number;
  balanceTrend?: number;
  incomeTrend?: number;
  expensesTrend?: number;
  savingsTrend?: number;
}

