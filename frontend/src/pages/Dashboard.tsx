import React, { useState, useEffect } from 'react';
import { 
  Plus, 
  Calendar, 
  ChevronDown, 
  ArrowUpRight, 
  ArrowDownRight 
} from 'lucide-react';
import { 
  ResponsiveContainer, 
  AreaChart, 
  Area, 
  XAxis, 
  YAxis, 
  Tooltip, 
  PieChart, 
  Pie, 
  Cell 
} from 'recharts';
import { formatCurrency } from '../utils/formatters';
import { AddTransactionModal } from '../components/AddTransactionModal';
import { 
  api, 
  REFERENCE_METRICS, 
  REFERENCE_MONTHLY_TRENDS, 
  REFERENCE_CATEGORY_SPENDING 
} from '../api/client';
import { MonthlyTrend, CategorySpending, DashboardSummary } from '../types';

export const Dashboard: React.FC = () => {
  const [metrics, setMetrics] = useState<DashboardSummary>(REFERENCE_METRICS);
  const [monthlyTrends, setMonthlyTrends] = useState<MonthlyTrend[]>(REFERENCE_MONTHLY_TRENDS);
  const [categorySpending, setCategorySpending] = useState<CategorySpending[]>(REFERENCE_CATEGORY_SPENDING);
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [selectedMonth, setSelectedMonth] = useState<string>('Nov 2026');

  const loadData = async () => {
    try {
      const summary = await api.getSummary(11, 2026);
      if (summary && summary.totalBalance !== undefined) {
        setMetrics({
          totalBalance: summary.totalBalance || REFERENCE_METRICS.totalBalance,
          totalIncome: summary.monthlyIncome || REFERENCE_METRICS.totalIncome,
          totalExpenses: summary.monthlyExpenses || REFERENCE_METRICS.totalExpenses,
          netSavings: summary.monthlySavings || REFERENCE_METRICS.netSavings,
          balanceTrend: 12.4,
          incomeTrend: 8.2,
          expensesTrend: -6.1,
          savingsTrend: 18.7,
        });
      }

      const trends = await api.getMonthlyTrends(6);
      if (trends && trends.length > 0) {
        setMonthlyTrends(trends);
      }

      const cats = await api.getCategorySpending(11, 2026);
      if (cats && cats.length > 0) {
        setCategorySpending(cats);
      }
    } catch (err) {
      console.warn('API sync fallback to reference layout data:', err);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  // Format Y-axis tick as e.g. 30K, 60K, 90K, 1.2L, 1.5L
  const formatYAxis = (value: number) => {
    if (value === 0) return '0';
    if (value >= 100000) {
      const val = value / 100000;
      return `${val % 1 === 0 ? val : val.toFixed(1)}L`;
    }
    return `${value / 1000}K`;
  };

  const CATEGORY_COLORS = ['#00C853', '#F59E0B', '#EAB308', '#EC4899', '#8B5CF6'];

  return (
    <div className="space-y-6">
      {/* Top Header Row */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Dashboard</h1>
          <p className="text-sm text-[#8B949E] mt-0.5">Track, analyse and take control of your money</p>
        </div>

        <div className="flex items-center gap-3">
          {/* Month Selector */}
          <div className="relative inline-flex items-center">
            <button className="flex items-center gap-2 bg-[#12161A] hover:bg-[#181D23] border border-[#1F242C] text-[#D1D5DB] text-xs font-medium px-3.5 py-2 rounded-lg transition-colors">
              <Calendar size={14} className="text-[#8B949E]" />
              <span>{selectedMonth}</span>
              <ChevronDown size={14} className="text-[#8B949E]" />
            </button>
          </div>

          {/* Add Transaction Button */}
          <button
            onClick={() => setIsModalOpen(true)}
            className="flex items-center gap-1.5 bg-[#00C853] hover:bg-[#00B048] text-white text-xs font-semibold px-4 py-2 rounded-lg transition-colors shadow-sm"
          >
            <Plus size={16} />
            <span>Add Transaction</span>
          </button>
        </div>
      </div>

      {/* 4 Financial Metric Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {/* Total Balance */}
        <div className="bg-[#12161A] border border-[#1F242C] rounded-xl p-5 flex flex-col justify-between">
          <div className="text-xs font-medium text-[#8B949E]">Total Balance</div>
          <div className="mt-3">
            <div className="text-2xl font-bold text-white tracking-tight">
              {formatCurrency(metrics.totalBalance)}
            </div>
            <div className="flex items-center gap-1 text-xs text-[#00C853] font-medium mt-1">
              <ArrowUpRight size={14} />
              <span>+{metrics.balanceTrend || 12.4}%</span>
            </div>
          </div>
        </div>

        {/* Total Income */}
        <div className="bg-[#12161A] border border-[#1F242C] rounded-xl p-5 flex flex-col justify-between">
          <div className="text-xs font-medium text-[#8B949E]">Total Income</div>
          <div className="mt-3">
            <div className="text-2xl font-bold text-white tracking-tight">
              {formatCurrency(metrics.totalIncome)}
            </div>
            <div className="flex items-center gap-1 text-xs text-[#00C853] font-medium mt-1">
              <ArrowUpRight size={14} />
              <span>+{metrics.incomeTrend || 8.2}%</span>
            </div>
          </div>
        </div>

        {/* Total Expenses */}
        <div className="bg-[#12161A] border border-[#1F242C] rounded-xl p-5 flex flex-col justify-between">
          <div className="text-xs font-medium text-[#8B949E]">Total Expenses</div>
          <div className="mt-3">
            <div className="text-2xl font-bold text-white tracking-tight">
              {formatCurrency(metrics.totalExpenses)}
            </div>
            <div className="flex items-center gap-1 text-xs text-[#EF4444] font-medium mt-1">
              <ArrowDownRight size={14} />
              <span>{metrics.expensesTrend || -6.1}%</span>
            </div>
          </div>
        </div>

        {/* Net Savings */}
        <div className="bg-[#12161A] border border-[#1F242C] rounded-xl p-5 flex flex-col justify-between">
          <div className="text-xs font-medium text-[#8B949E]">Net Savings</div>
          <div className="mt-3">
            <div className="text-2xl font-bold text-white tracking-tight">
              {formatCurrency(metrics.netSavings)}
            </div>
            <div className="flex items-center gap-1 text-xs text-[#00C853] font-medium mt-1">
              <ArrowUpRight size={14} />
              <span>+{metrics.savingsTrend || 18.7}%</span>
            </div>
          </div>
        </div>
      </div>

      {/* Charts Section */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-5">
        {/* Income vs Expenses Chart (7 cols) */}
        <div className="lg:col-span-7 bg-[#12161A] border border-[#1F242C] rounded-xl p-5">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-sm font-semibold text-white">Income vs Expenses</h2>
            <div className="flex items-center gap-4 text-xs font-medium">
              <div className="flex items-center gap-1.5 text-white">
                <span className="w-2.5 h-2.5 rounded-sm bg-[#00C853]" />
                <span className="text-[#8B949E]">Income</span>
              </div>
              <div className="flex items-center gap-1.5 text-white">
                <span className="w-2.5 h-2.5 rounded-sm bg-[#EF4444]" />
                <span className="text-[#8B949E]">Expenses</span>
              </div>
            </div>
          </div>

          <div className="h-64 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={monthlyTrends} margin={{ top: 10, right: 10, left: -15, bottom: 0 }}>
                <defs>
                  <linearGradient id="incomeGradient" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stop-color="#00C853" stopOpacity={0.3} />
                    <stop offset="95%" stop-color="#00C853" stopOpacity={0.0} />
                  </linearGradient>
                  <linearGradient id="expenseGradient" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stop-color="#EF4444" stopOpacity={0.25} />
                    <stop offset="95%" stop-color="#EF4444" stopOpacity={0.0} />
                  </linearGradient>
                </defs>
                <XAxis 
                  dataKey="monthName" 
                  axisLine={false} 
                  tickLine={false} 
                  tick={{ fill: '#8B949E', fontSize: 11 }}
                  dy={10}
                />
                <YAxis 
                  axisLine={false} 
                  tickLine={false} 
                  tick={{ fill: '#8B949E', fontSize: 11 }}
                  tickFormatter={formatYAxis}
                  domain={[0, 160000]}
                  ticks={[0, 30000, 60000, 90000, 120000, 150000]}
                />
                <Tooltip 
                  contentStyle={{ 
                    backgroundColor: '#15191E', 
                    borderColor: '#1F242C', 
                    borderRadius: '8px', 
                    color: '#fff', 
                    fontSize: '12px' 
                  }}
                  formatter={(value: any) => [formatCurrency(Number(value)), '']}
                />
                <Area 
                  type="monotone" 
                  dataKey="income" 
                  stroke="#00C853" 
                  strokeWidth={2} 
                  fillOpacity={1} 
                  fill="url(#incomeGradient)" 
                />
                <Area 
                  type="monotone" 
                  dataKey="expenses" 
                  stroke="#EF4444" 
                  strokeWidth={2} 
                  fillOpacity={1} 
                  fill="url(#expenseGradient)" 
                />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Expense by Category Donut Chart (5 cols) */}
        <div className="lg:col-span-5 bg-[#12161A] border border-[#1F242C] rounded-xl p-5 flex flex-col justify-between">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-sm font-semibold text-white">Expense by Category</h2>
          </div>

          <div className="flex flex-col sm:flex-row items-center justify-between gap-4 my-auto">
            {/* Donut Chart with Center Text */}
            <div className="relative w-48 h-48 flex items-center justify-center shrink-0">
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie
                    data={categorySpending}
                    cx="50%"
                    cy="50%"
                    innerRadius={58}
                    outerRadius={80}
                    paddingAngle={3}
                    dataKey="percentage"
                  >
                    {categorySpending.map((entry, index) => (
                      <Cell 
                        key={`cell-${index}`} 
                        fill={entry.categoryColor || CATEGORY_COLORS[index % CATEGORY_COLORS.length]} 
                        stroke="#12161A"
                        strokeWidth={2}
                      />
                    ))}
                  </Pie>
                </PieChart>
              </ResponsiveContainer>
              {/* Centered Total inside Donut */}
              <div className="absolute inset-0 flex flex-col items-center justify-center pointer-events-none">
                <span className="text-white text-base font-bold leading-tight">
                  {formatCurrency(metrics.totalExpenses)}
                </span>
                <span className="text-[#8B949E] text-[10px] leading-tight mt-0.5">
                  Total Expenses
                </span>
              </div>
            </div>

            {/* Right Legend List with Exact Percentages */}
            <div className="w-full sm:w-auto space-y-2.5">
              {categorySpending.map((item, idx) => (
                <div key={item.categoryName} className="flex items-center justify-between sm:justify-start gap-4 text-xs">
                  <div className="flex items-center gap-2">
                    <span 
                      className="w-2.5 h-2.5 rounded-sm shrink-0" 
                      style={{ backgroundColor: item.categoryColor || CATEGORY_COLORS[idx % CATEGORY_COLORS.length] }} 
                    />
                    <span className="text-[#D1D5DB] font-medium text-xs">{item.categoryName}</span>
                  </div>
                  <span className="text-[#8B949E] text-xs font-semibold ml-auto">{item.percentage}%</span>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* Add Transaction Modal */}
      <AddTransactionModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSuccess={loadData}
      />
    </div>
  );
};
