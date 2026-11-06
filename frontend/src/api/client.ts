import { 
  User, 
  Account, 
  Category, 
  Transaction, 
  Budget, 
  DashboardSummary, 
  MonthlyTrend, 
  CategorySpending 
} from '../types';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'https://monetra-api-production.up.railway.app/api/v1';

class ApiClient {
  private token: string | null = null;

  constructor() {
    this.token = localStorage.getItem('monetra_token');
  }

  public setToken(token: string | null) {
    this.token = token;
    if (token) {
      localStorage.setItem('monetra_token', token);
    } else {
      localStorage.removeItem('monetra_token');
    }
  }

  public getToken(): string | null {
    return this.token || localStorage.getItem('monetra_token');
  }

  private async request<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
    const token = this.getToken();
    const headers: Record<string, string> = {
      'Content-Type': 'application/json',
      ...(options.headers as Record<string, string> || {}),
    };

    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      ...options,
      headers,
    });

    if (!response.ok) {
      let errorMessage = 'An error occurred';
      try {
        const errorData = await response.json();
        errorMessage = errorData.message || errorData.error || response.statusText;
      } catch {
        errorMessage = response.statusText;
      }
      throw new Error(errorMessage);
    }

    if (response.status === 204) {
      return {} as T;
    }

    return response.json();
  }

  // Auth
  async login(credentials: { username?: string; email?: string; password: string }): Promise<{ token: string; user: User }> {
    const payload = {
      username: credentials.username || credentials.email,
      password: credentials.password,
    };
    const res = await this.request<{ token: string; user: User }>('/auth/login', {
      method: 'POST',
      body: JSON.stringify(payload),
    });
    this.setToken(res.token);
    return res;
  }

  async register(data: { username: string; email: string; password: string; fullName: string; currency?: string }): Promise<{ token: string; user: User }> {
    const res = await this.request<{ token: string; user: User }>('/auth/register', {
      method: 'POST',
      body: JSON.stringify({
        ...data,
        currency: data.currency || 'INR',
      }),
    });
    this.setToken(res.token);
    return res;
  }

  async getCurrentUser(): Promise<User> {
    return this.request<User>('/auth/me');
  }

  // Accounts
  async getAccounts(): Promise<Account[]> {
    return this.request<Account[]>('/accounts');
  }

  // Categories
  async getCategories(): Promise<Category[]> {
    return this.request<Category[]>('/categories');
  }

  async createCategory(category: Partial<Category>): Promise<Category> {
    return this.request<Category>('/categories', {
      method: 'POST',
      body: JSON.stringify(category),
    });
  }

  // Transactions
  async getTransactions(params?: { type?: string; categoryId?: number; startDate?: string; endDate?: string }): Promise<Transaction[]> {
    const query = new URLSearchParams();
    if (params?.type) query.append('type', params.type);
    if (params?.categoryId) query.append('categoryId', params.categoryId.toString());
    if (params?.startDate) query.append('startDate', params.startDate);
    if (params?.endDate) query.append('endDate', params.endDate);
    
    const qs = query.toString() ? `?${query.toString()}` : '';
    return this.request<Transaction[]>(`/transactions${qs}`);
  }

  async createTransaction(tx: {
    accountId: number;
    categoryId: number;
    amount: number;
    type: 'INCOME' | 'EXPENSE';
    description: string;
    transactionDate: string;
  }): Promise<Transaction> {
    return this.request<Transaction>('/transactions', {
      method: 'POST',
      body: JSON.stringify(tx),
    });
  }

  async updateTransaction(id: number, tx: Partial<Transaction>): Promise<Transaction> {
    return this.request<Transaction>(`/transactions/${id}`, {
      method: 'PUT',
      body: JSON.stringify(tx),
    });
  }

  async deleteTransaction(id: number): Promise<void> {
    await this.request<void>(`/transactions/${id}`, {
      method: 'DELETE',
    });
  }

  // Budgets
  async getBudgets(month?: number, year?: number): Promise<Budget[]> {
    const query = new URLSearchParams();
    if (month) query.append('month', month.toString());
    if (year) query.append('year', year.toString());
    const qs = query.toString() ? `?${query.toString()}` : '';
    return this.request<Budget[]>(`/budgets${qs}`);
  }

  async createBudget(budget: {
    categoryId: number;
    amount: number;
    budgetMonth: number;
    budgetYear: number;
  }): Promise<Budget> {
    return this.request<Budget>('/budgets', {
      method: 'POST',
      body: JSON.stringify(budget),
    });
  }

  async deleteBudget(id: number): Promise<void> {
    await this.request<void>(`/budgets/${id}`, {
      method: 'DELETE',
    });
  }

  // Reports
  async getSummary(month?: number, year?: number): Promise<any> {
    const query = new URLSearchParams();
    if (month) query.append('month', month.toString());
    if (year) query.append('year', year.toString());
    const qs = query.toString() ? `?${query.toString()}` : '';
    return this.request<any>(`/reports/summary${qs}`);
  }

  async getCategorySpending(month?: number, year?: number): Promise<CategorySpending[]> {
    const query = new URLSearchParams();
    if (month) query.append('month', month.toString());
    if (year) query.append('year', year.toString());
    const qs = query.toString() ? `?${query.toString()}` : '';
    return this.request<CategorySpending[]>(`/reports/category-spending${qs}`);
  }

  async getMonthlyTrends(months: number = 6): Promise<MonthlyTrend[]> {
    return this.request<MonthlyTrend[]>(`/reports/monthly-trends?months=${months}`);
  }
}

export const api = new ApiClient();

// High fidelity reference data matching the attached visual specification exactly
export const REFERENCE_METRICS: DashboardSummary = {
  totalBalance: 482300,
  totalIncome: 120000,
  totalExpenses: 42750,
  netSavings: 77250,
  balanceTrend: 12.4,
  incomeTrend: 8.2,
  expensesTrend: -6.1,
  savingsTrend: 18.7,
};

export const REFERENCE_MONTHLY_TRENDS: MonthlyTrend[] = [
  { monthName: 'Jun', income: 75000, expenses: 32000, savings: 43000 },
  { monthName: 'Jul', income: 90000, expenses: 39000, savings: 51000 },
  { monthName: 'Aug', income: 85000, expenses: 45000, savings: 40000 },
  { monthName: 'Sep', income: 110000, expenses: 48000, savings: 62000 },
  { monthName: 'Oct', income: 95000, expenses: 41000, savings: 54000 },
  { monthName: 'Nov', income: 120000, expenses: 42750, savings: 77250 },
];

export const REFERENCE_CATEGORY_SPENDING: CategorySpending[] = [
  { categoryId: 1, categoryName: 'Food & Dining', totalSpent: 12000, percentage: 28, categoryColor: '#00C853' },
  { categoryId: 2, categoryName: 'Transport', totalSpent: 7700, percentage: 18, categoryColor: '#F59E0B' },
  { categoryId: 3, categoryName: 'Bills & Utilities', totalSpent: 6850, percentage: 16, categoryColor: '#EAB308' },
  { categoryId: 4, categoryName: 'Shopping', totalSpent: 6000, percentage: 14, categoryColor: '#EC4899' },
  { categoryId: 5, categoryName: 'Others', totalSpent: 10200, percentage: 24, categoryColor: '#8B5CF6' },
];

export const REFERENCE_TRANSACTIONS: Transaction[] = [
  {
    id: 1,
    accountId: 1,
    categoryId: 9,
    categoryName: 'Income',
    categoryIcon: 'wallet',
    categoryColor: '#00C853',
    amount: 120000,
    type: 'INCOME',
    description: 'Salary',
    transactionDate: '2026-11-06',
  },
  {
    id: 2,
    accountId: 1,
    categoryId: 1,
    categoryName: 'Food & Dining',
    categoryIcon: 'utensils',
    categoryColor: '#00C853',
    amount: 4320,
    type: 'EXPENSE',
    description: 'Grocery Shopping',
    transactionDate: '2026-11-05',
  },
  {
    id: 3,
    accountId: 1,
    categoryId: 2,
    categoryName: 'Transport',
    categoryIcon: 'car',
    categoryColor: '#F59E0B',
    amount: 1500,
    type: 'EXPENSE',
    description: 'Fuel',
    transactionDate: '2026-11-04',
  },
  {
    id: 4,
    accountId: 1,
    categoryId: 3,
    categoryName: 'Bills & Utilities',
    categoryIcon: 'zap',
    categoryColor: '#EAB308',
    amount: 799,
    type: 'EXPENSE',
    description: 'Internet Bill',
    transactionDate: '2026-11-03',
  },
  {
    id: 5,
    accountId: 1,
    categoryId: 9,
    categoryName: 'Income',
    categoryIcon: 'wallet',
    categoryColor: '#00C853',
    amount: 15000,
    type: 'INCOME',
    description: 'Freelance Project',
    transactionDate: '2026-11-02',
  },
];

export const REFERENCE_BUDGETS: Budget[] = [
  {
    id: 1,
    userId: 1,
    categoryId: 1,
    categoryName: 'Food & Dining',
    categoryIcon: 'utensils',
    categoryColor: '#00C853',
    amount: 15000,
    budgetMonth: 11,
    budgetYear: 2026,
    spentAmount: 12000,
    remainingAmount: 3000,
    utilizationPercentage: 80,
    status: 'NORMAL',
  },
  {
    id: 2,
    userId: 1,
    categoryId: 2,
    categoryName: 'Transport',
    categoryIcon: 'car',
    categoryColor: '#F59E0B',
    amount: 6000,
    budgetMonth: 11,
    budgetYear: 2026,
    spentAmount: 4500,
    remainingAmount: 1500,
    utilizationPercentage: 75,
    status: 'NORMAL',
  },
  {
    id: 3,
    userId: 1,
    categoryId: 3,
    categoryName: 'Bills & Utilities',
    categoryIcon: 'zap',
    categoryColor: '#EAB308',
    amount: 7000,
    budgetMonth: 11,
    budgetYear: 2026,
    spentAmount: 5200,
    remainingAmount: 1800,
    utilizationPercentage: 74,
    status: 'NORMAL',
  },
  {
    id: 4,
    userId: 1,
    categoryId: 4,
    categoryName: 'Shopping',
    categoryIcon: 'shopping-bag',
    categoryColor: '#EC4899',
    amount: 10000,
    budgetMonth: 11,
    budgetYear: 2026,
    spentAmount: 8700,
    remainingAmount: 1300,
    utilizationPercentage: 87,
    status: 'WARNING',
  },
  {
    id: 5,
    userId: 1,
    categoryId: 5,
    categoryName: 'Entertainment',
    categoryIcon: 'film',
    categoryColor: '#8B5CF6',
    amount: 5000,
    budgetMonth: 11,
    budgetYear: 2026,
    spentAmount: 2100,
    remainingAmount: 2900,
    utilizationPercentage: 42,
    status: 'NORMAL',
  },
  {
    id: 6,
    userId: 1,
    categoryId: 6,
    categoryName: 'Others',
    categoryIcon: 'layers',
    categoryColor: '#06B6D4',
    amount: 5000,
    budgetMonth: 11,
    budgetYear: 2026,
    spentAmount: 3250,
    remainingAmount: 1750,
    utilizationPercentage: 65,
    status: 'NORMAL',
  },
];

export const REFERENCE_REPORTS_BREAKDOWN = [
  { category: 'Food & Dining', amount: 18420, percentage: 28, icon: 'utensils', color: '#00C853' },
  { category: 'Transport', amount: 11680, percentage: 18, icon: 'car', color: '#F59E0B' },
  { category: 'Bills & Utilities', amount: 10560, percentage: 16, icon: 'zap', color: '#EAB308' },
  { category: 'Shopping', amount: 9240, percentage: 14, icon: 'shopping-bag', color: '#EC4899' },
  { category: 'Others', amount: 15360, percentage: 24, icon: 'layers', color: '#8B5CF6' },
];
