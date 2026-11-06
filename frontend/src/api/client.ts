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
  private onUnauthorizedCallback: (() => void) | null = null;

  constructor() {
    this.token = localStorage.getItem('monetra_token') || sessionStorage.getItem('monetra_token');
  }

  public setOnUnauthorized(cb: () => void) {
    this.onUnauthorizedCallback = cb;
  }

  public setToken(token: string | null, rememberMe: boolean = true) {
    this.token = token;
    if (token) {
      if (rememberMe) {
        localStorage.setItem('monetra_token', token);
        sessionStorage.removeItem('monetra_token');
      } else {
        sessionStorage.setItem('monetra_token', token);
        localStorage.removeItem('monetra_token');
      }
    } else {
      localStorage.removeItem('monetra_token');
      sessionStorage.removeItem('monetra_token');
    }
  }

  public getToken(): string | null {
    return this.token || localStorage.getItem('monetra_token') || sessionStorage.getItem('monetra_token');
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

    let response: Response;
    try {
      response = await fetch(`${API_BASE_URL}${endpoint}`, {
        ...options,
        headers,
      });
    } catch (networkErr: any) {
      throw new Error(`Unable to reach the MONETRA server. Please check your connection: ${networkErr.message}`);
    }

    if (response.status === 401) {
      // Token expired or invalid
      if (this.onUnauthorizedCallback && token) {
        this.onUnauthorizedCallback();
      }
      let errorMessage = 'Invalid username or password';
      try {
        const errorData = await response.json();
        errorMessage = errorData.message || errorData.error || 'Authentication failed';
      } catch {}
      throw new Error(errorMessage);
    }

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
  async login(credentials: { username?: string; email?: string; password: string }, rememberMe: boolean = true): Promise<{ token: string; user: User }> {
    const payload = {
      username: credentials.username || credentials.email,
      password: credentials.password,
    };
    const res = await this.request<{ token: string; user: User }>('/auth/login', {
      method: 'POST',
      body: JSON.stringify(payload),
    });
    this.setToken(res.token, rememberMe);
    return res;
  }

  async register(data: { username?: string; email: string; password: string; fullName: string; currency?: string }, rememberMe: boolean = true): Promise<{ token: string; user: User }> {
    const res = await this.request<{ token: string; user: User }>('/auth/register', {
      method: 'POST',
      body: JSON.stringify({
        ...data,
        currency: data.currency || 'INR',
      }),
    });
    this.setToken(res.token, rememberMe);
    return res;
  }

  async getCurrentUser(): Promise<User> {
    return this.request<User>('/auth/me');
  }

  async updateProfile(data: { fullName?: string; email?: string; currency?: string }): Promise<User> {
    return this.request<User>('/users/profile', {
      method: 'PUT',
      body: JSON.stringify(data),
    });
  }

  async changePassword(data: { currentPassword: string; newPassword: string }): Promise<void> {
    await this.request<void>('/users/password', {
      method: 'PUT',
      body: JSON.stringify(data),
    });
  }

  // Accounts
  async getAccounts(): Promise<Account[]> {
    return this.request<Account[]>('/accounts');
  }

  async createAccount(acc: { name: string; type: string; balance?: number; currency?: string; description?: string }): Promise<Account> {
    return this.request<Account>('/accounts', {
      method: 'POST',
      body: JSON.stringify(acc),
    });
  }

  // Categories
  async getCategories(): Promise<Category[]> {
    return this.request<Category[]>('/categories');
  }

  async createCategory(category: { name: string; type: 'INCOME' | 'EXPENSE'; icon?: string; color?: string }): Promise<Category> {
    return this.request<Category>('/categories', {
      method: 'POST',
      body: JSON.stringify(category),
    });
  }

  async updateCategory(id: number, category: { name: string; type?: 'INCOME' | 'EXPENSE'; icon?: string; color?: string }): Promise<Category> {
    return this.request<Category>(`/categories/${id}`, {
      method: 'PUT',
      body: JSON.stringify(category),
    });
  }

  async deleteCategory(id: number): Promise<void> {
    await this.request<void>(`/categories/${id}`, {
      method: 'DELETE',
    });
  }

  // Transactions
  async getTransactions(params?: { type?: string; categoryId?: number; startDate?: string; endDate?: string; search?: string }): Promise<Transaction[]> {
    const query = new URLSearchParams();
    if (params?.type && params.type !== 'ALL') query.append('type', params.type);
    if (params?.categoryId) query.append('categoryId', params.categoryId.toString());
    if (params?.startDate) query.append('startDate', params.startDate);
    if (params?.endDate) query.append('endDate', params.endDate);
    if (params?.search) query.append('search', params.search);
    
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

  async updateTransaction(id: number, tx: {
    accountId: number;
    categoryId: number;
    amount: number;
    type: 'INCOME' | 'EXPENSE';
    description: string;
    transactionDate: string;
  }): Promise<Transaction> {
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

  async updateBudget(id: number, budget: {
    categoryId: number;
    amount: number;
    budgetMonth: number;
    budgetYear: number;
  }): Promise<Budget> {
    return this.request<Budget>(`/budgets/${id}`, {
      method: 'PUT',
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
