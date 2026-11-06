import React, { useState, useEffect } from 'react';
import { 
  Plus, 
  Calendar, 
  ChevronDown, 
  ArrowUpRight, 
  ArrowDownRight,
  TrendingUp,
  CreditCard,
  CheckCircle2,
  AlertCircle,
  AlertTriangle,
  Sparkles,
  Info,
  Loader2
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
import { api } from '../api/client';
import { MonthlyTrend, CategorySpending, DashboardSummary, FinancialInsight } from '../types';

export const Dashboard: React.FC = () => {
  const [selectedMonth, setSelectedMonth] = useState<number>(() => new Date().getMonth() + 1);
  const [selectedYear, setSelectedYear] = useState<number>(() => new Date().getFullYear());
  const [isMonthPickerOpen, setIsMonthPickerOpen] = useState<boolean>(false);

  const [metrics, setMetrics] = useState<DashboardSummary>({
    totalBalance: 0,
    totalIncome: 0,
    totalExpenses: 0,
    netSavings: 0,
  });
  const [monthlyTrends, setMonthlyTrends] = useState<MonthlyTrend[]>([]);
  const [categorySpending, setCategorySpending] = useState<CategorySpending[]>([]);
  const [insights, setInsights] = useState<FinancialInsight[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);

  // Month selector options
  const monthOptions = [
    { m: 1, name: 'Jan' },
    { m: 2, name: 'Feb' },
    { m: 3, name: 'Mar' },
    { m: 4, name: 'Apr' },
    { m: 5, name: 'May' },
    { m: 6, name: 'Jun' },
    { m: 7, name: 'Jul' },
    { m: 8, name: 'Aug' },
    { m: 9, name: 'Sep' },
    { m: 10, name: 'Oct' },
    { m: 11, name: 'Nov' },
    { m: 12, name: 'Dec' },
  ];

  const currentMonthName = monthOptions.find(o => o.m === selectedMonth)?.name || 'Nov';

  const loadData = async (m = selectedMonth, y = selectedYear) => {
    setIsLoading(true);
    try {
      const [summary, trends, cats, insightList] = await Promise.all([
        api.getSummary(m, y).catch(() => null),
        api.getMonthlyTrends(6).catch(() => []),
        api.getCategorySpending(m, y).catch(() => []),
        api.getInsights(m, y).catch(() => []),
      ]);

      if (summary) {
        setMetrics({
          totalBalance: summary.totalBalance ?? 0,
          totalIncome: summary.monthlyIncome ?? summary.totalIncome ?? 0,
          totalExpenses: summary.monthlyExpenses ?? summary.totalExpenses ?? 0,
          netSavings: summary.monthlySavings ?? summary.totalSavings ?? 0,
          balanceTrend: summary.balanceTrend,
          incomeTrend: summary.incomeTrend,
          expensesTrend: summary.expensesTrend,
          savingsTrend: summary.savingsRatePercentage,
        });
      }

      setMonthlyTrends(trends || []);
      setCategorySpending(cats || []);
      setInsights(insightList || []);
    } catch (err) {
      console.error('Error fetching dashboard metrics:', err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadData(selectedMonth, selectedYear);
  }, [selectedMonth, selectedYear]);

  // Format Y-axis tick as e.g. 30K, 60K, 90K, 1.2L, 1.5L
  const formatYAxis = (value: number) => {
    if (value === 0) return '0';
    if (value >= 100000) {
      const val = value / 100000;
      return `${val % 1 === 0 ? val : val.toFixed(1)}L`;
    }
    return `${value / 1000}K`;
  };

  const CATEGORY_COLORS = ['#00C853', '#F59E0B', '#EAB308', '#EC4899', '#8B5CF6', '#06B6D4'];

  return (
    <div className="space-y-6">
      {/* Top Header Row */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Dashboard</h1>
          <p className="text-sm text-[#8B949E] mt-0.5">Track, analyse and take control of your money</p>
        </div>

        <div className="flex items-center gap-3">
          {/* Functional Month/Year Selector Dropdown */}
          <div className="relative">
            <button
              type="button"
              onClick={() => setIsMonthPickerOpen(!isMonthPickerOpen)}
              className="flex items-center gap-2 bg-[#12161A] hover:bg-[#181D23] border border-[#1F242C] text-[#D1D5DB] text-xs font-semibold px-3.5 py-2 rounded-xl transition-colors shadow-sm"
            >
              <Calendar size={14} className="text-[#00C853]" />
              <span>{currentMonthName} {selectedYear}</span>
              <ChevronDown size={14} className="text-[#8B949E]" />
            </button>

            {isMonthPickerOpen && (
              <div className="absolute right-0 mt-2 w-48 bg-[#12161A] border border-[#1F242C] rounded-xl shadow-2xl p-2 z-40 animate-in fade-in zoom-in-95 duration-150">
                <div className="text-[10px] font-semibold text-[#8B949E] px-2 py-1 uppercase tracking-wider">
                  Select Period
                </div>
                <div className="grid grid-cols-3 gap-1 my-1">
                  {monthOptions.map((opt) => (
                    <button
                      key={opt.m}
                      onClick={() => {
                        setSelectedMonth(opt.m);
                        setIsMonthPickerOpen(false);
                      }}
                      className={`py-1.5 text-xs rounded-lg font-medium transition-colors ${
                        opt.m === selectedMonth
                          ? 'bg-[#00C853] text-white font-semibold'
                          : 'text-[#8B949E] hover:text-white hover:bg-[#181D23]'
                      }`}
                    >
                      {opt.name}
                    </button>
                  ))}
                </div>
                <div className="border-t border-[#1F242C] pt-1.5 flex justify-between px-1">
                  <button
                    onClick={() => setSelectedYear(selectedYear - 1)}
                    className="text-[10px] text-[#8B949E] hover:text-white px-2 py-1 rounded"
                  >
                    &larr; {selectedYear - 1}
                  </button>
                  <span className="text-xs font-bold text-white py-1">{selectedYear}</span>
                  <button
                    onClick={() => setSelectedYear(selectedYear + 1)}
                    className="text-[10px] text-[#8B949E] hover:text-white px-2 py-1 rounded"
                  >
                    {selectedYear + 1} &rarr;
                  </button>
                </div>
              </div>
            )}
          </div>

          {/* Add Transaction Button */}
          <button
            onClick={() => setIsModalOpen(true)}
            className="flex items-center gap-1.5 bg-[#00C853] hover:bg-[#00B048] active:scale-[0.98] text-white text-xs font-semibold px-4 py-2 rounded-xl transition-all shadow-md shadow-[#00C853]/15"
          >
            <Plus size={16} />
            <span>Add Transaction</span>
          </button>
        </div>
      </div>

      {/* 4 Financial Metric Cards (Real User Data) */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {/* Total Balance */}
        <div className="bg-[#12161A]/85 backdrop-blur-md border border-[#1F242C] rounded-2xl p-5 flex flex-col justify-between hover:border-[#28323C] transition-colors">
          <div className="text-xs font-medium text-[#8B949E]">Total Balance</div>
          <div className="mt-3">
            <div className="text-2xl font-bold text-white tracking-tight">
              {formatCurrency(metrics.totalBalance)}
            </div>
            <div className="flex items-center gap-1 text-xs text-[#00C853] font-medium mt-1">
              <ArrowUpRight size={14} />
              <span>Net Worth</span>
            </div>
          </div>
        </div>

        {/* Total Income */}
        <div className="bg-[#12161A]/85 backdrop-blur-md border border-[#1F242C] rounded-2xl p-5 flex flex-col justify-between hover:border-[#28323C] transition-colors">
          <div className="text-xs font-medium text-[#8B949E]">Monthly Income</div>
          <div className="mt-3">
            <div className="text-2xl font-bold text-white tracking-tight">
              {formatCurrency(metrics.totalIncome)}
            </div>
            <div className="flex items-center gap-1 text-xs text-[#00C853] font-medium mt-1">
              <ArrowUpRight size={14} />
              <span>{currentMonthName} {selectedYear}</span>
            </div>
          </div>
        </div>

        {/* Total Expenses */}
        <div className="bg-[#12161A]/85 backdrop-blur-md border border-[#1F242C] rounded-2xl p-5 flex flex-col justify-between hover:border-[#28323C] transition-colors">
          <div className="text-xs font-medium text-[#8B949E]">Monthly Expenses</div>
          <div className="mt-3">
            <div className="text-2xl font-bold text-white tracking-tight">
              {formatCurrency(metrics.totalExpenses)}
            </div>
            <div className="flex items-center gap-1 text-xs text-[#EF4444] font-medium mt-1">
              <ArrowDownRight size={14} />
              <span>{currentMonthName} {selectedYear}</span>
            </div>
          </div>
        </div>

        {/* Net Savings */}
        <div className="bg-[#12161A]/85 backdrop-blur-md border border-[#1F242C] rounded-2xl p-5 flex flex-col justify-between hover:border-[#28323C] transition-colors">
          <div className="text-xs font-medium text-[#8B949E]">Net Savings</div>
          <div className="mt-3">
            <div className="text-2xl font-bold text-white tracking-tight">
              {formatCurrency(metrics.netSavings)}
            </div>
            <div className="flex items-center gap-1 text-xs text-[#00C853] font-medium mt-1">
              <ArrowUpRight size={14} />
              <span>{metrics.savingsTrend ? `${metrics.savingsTrend}% rate` : 'Positive'}</span>
            </div>
          </div>
        </div>
      </div>

      {/* Charts Section */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-5">
        {/* Income vs Expenses Chart (7 cols) */}
        <div className="lg:col-span-7 bg-[#12161A]/85 backdrop-blur-md border border-[#1F242C] rounded-2xl p-5">
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
            {monthlyTrends.length > 0 ? (
              <ResponsiveContainer width="100%" height="100%">
                <AreaChart data={monthlyTrends} margin={{ top: 10, right: 10, left: -15, bottom: 0 }}>
                  <defs>
                    <linearGradient id="incomeGradient" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="5%" stopColor="#00C853" stopOpacity={0.3} />
                      <stop offset="95%" stopColor="#00C853" stopOpacity={0.0} />
                    </linearGradient>
                    <linearGradient id="expenseGradient" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="5%" stopColor="#EF4444" stopOpacity={0.25} />
                      <stop offset="95%" stopColor="#EF4444" stopOpacity={0.0} />
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
            ) : (
              <div className="h-full flex flex-col items-center justify-center text-center p-4">
                <TrendingUp size={32} className="text-[#2D3748] mb-2" />
                <p className="text-xs text-[#8B949E]">No trend history available yet</p>
                <button
                  onClick={() => setIsModalOpen(true)}
                  className="mt-3 text-xs text-[#00C853] hover:underline font-semibold"
                >
                  Record your first transaction &rarr;
                </button>
              </div>
            )}
          </div>
        </div>

        {/* Expense by Category Donut Chart (5 cols) */}
        <div className="lg:col-span-5 bg-[#12161A]/85 backdrop-blur-md border border-[#1F242C] rounded-2xl p-5 flex flex-col justify-between">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-sm font-semibold text-white">Expense by Category</h2>
            <span className="text-xs text-[#8B949E]">{currentMonthName} {selectedYear}</span>
          </div>

          {categorySpending.length > 0 ? (
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

              {/* Right Legend List */}
              <div className="w-full sm:w-auto space-y-2.5 max-h-48 overflow-y-auto pr-1">
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
          ) : (
            <div className="h-56 flex flex-col items-center justify-center text-center p-4">
              <CreditCard size={32} className="text-[#2D3748] mb-2" />
              <p className="text-xs text-[#8B949E]">No expense transactions for {currentMonthName} {selectedYear}</p>
              <button
                onClick={() => setIsModalOpen(true)}
                className="mt-3 text-xs text-[#00C853] hover:underline font-semibold"
              >
                + Add an expense
              </button>
            </div>
          )}
        </div>
      </div>

      {/* Financial Intelligence & Real-Time Insights */}
      {insights.length > 0 && (
        <div className="bg-[#12161A]/85 backdrop-blur-md border border-[#1F242C] rounded-2xl p-5">
          <div className="flex items-center justify-between mb-4">
            <div className="flex items-center gap-2">
              <Sparkles size={16} className="text-[#00C853]" />
              <h2 className="text-sm font-semibold text-white">Financial Intelligence & Insights</h2>
            </div>
            <span className="text-xs text-[#8B949E]">{currentMonthName} {selectedYear}</span>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-3">
            {insights.map((insight, idx) => {
              const isPositive = insight.type === 'POSITIVE';
              const isWarning = insight.type === 'WARNING';
              const isDanger = insight.type === 'DANGER';

              const borderClass = isPositive 
                ? 'border-[#00C853]/25 bg-[#00C853]/5' 
                : isWarning 
                ? 'border-[#F59E0B]/25 bg-[#F59E0B]/5' 
                : isDanger 
                ? 'border-[#EF4444]/25 bg-[#EF4444]/5' 
                : 'border-[#3B82F6]/25 bg-[#3B82F6]/5';

              const badgeClass = isPositive
                ? 'bg-[#00C853]/15 text-[#00C853]'
                : isWarning
                ? 'bg-[#F59E0B]/15 text-[#F59E0B]'
                : isDanger
                ? 'bg-[#EF4444]/15 text-[#EF4444]'
                : 'bg-[#3B82F6]/15 text-[#60A5FA]';

              const Icon = isPositive 
                ? CheckCircle2 
                : isWarning 
                ? AlertTriangle 
                : isDanger 
                ? AlertCircle 
                : Info;

              return (
                <div 
                  key={`${insight.title}-${idx}`} 
                  className={`rounded-xl border p-4 flex flex-col justify-between transition-colors ${borderClass}`}
                >
                  <div>
                    <div className="flex items-center justify-between gap-2 mb-2">
                      <div className="flex items-center gap-2">
                        <Icon size={16} className={isPositive ? 'text-[#00C853]' : isWarning ? 'text-[#F59E0B]' : isDanger ? 'text-[#EF4444]' : 'text-[#60A5FA]'} />
                        <span className="text-xs font-bold text-white tracking-wide">{insight.title}</span>
                      </div>
                      {insight.metricHighlight && (
                        <span className={`text-[10px] font-bold px-2 py-0.5 rounded-full ${badgeClass}`}>
                          {insight.metricHighlight}
                        </span>
                      )}
                    </div>
                    <p className="text-xs text-[#9CA3AF] leading-relaxed">
                      {insight.description}
                    </p>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      )}

      {/* Add Transaction Modal */}
      <AddTransactionModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSuccess={() => loadData(selectedMonth, selectedYear)}
      />
    </div>
  );
};
