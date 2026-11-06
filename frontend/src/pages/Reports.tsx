import React, { useState, useEffect } from 'react';
import { 
  ChevronDown, 
  Utensils, 
  Car, 
  Zap, 
  ShoppingBag, 
  Film, 
  Layers, 
  Loader2,
  BarChart3,
  PieChart
} from 'lucide-react';
import { 
  ResponsiveContainer, 
  BarChart, 
  Bar, 
  XAxis, 
  YAxis, 
  Tooltip 
} from 'recharts';
import { formatCurrency } from '../utils/formatters';
import { api } from '../api/client';
import { MonthlyTrend, CategorySpending } from '../types';

export const Reports: React.FC = () => {
  const [monthsCount, setMonthsCount] = useState<number>(6);
  const [isRangeDropdownOpen, setIsRangeDropdownOpen] = useState<boolean>(false);

  const [trends, setTrends] = useState<MonthlyTrend[]>([]);
  const [breakdown, setBreakdown] = useState<CategorySpending[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);

  const rangeOptions = [
    { count: 3, label: 'Last 3 Months' },
    { count: 6, label: 'Last 6 Months' },
    { count: 12, label: 'Last 12 Months' },
  ];

  const currentRangeLabel = rangeOptions.find(r => r.count === monthsCount)?.label || 'Last 6 Months';

  const loadData = async (mCount = monthsCount) => {
    setIsLoading(true);
    try {
      const now = new Date();
      const currentMonth = now.getMonth() + 1;
      const currentYear = now.getFullYear();

      const [trendData, catData] = await Promise.all([
        api.getMonthlyTrends(mCount).catch(() => []),
        api.getCategorySpending(currentMonth, currentYear).catch(() => []),
      ]);

      setTrends(trendData || []);
      setBreakdown(catData || []);
    } catch (err) {
      console.error('Failed to load reports telemetry:', err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadData(monthsCount);
  }, [monthsCount]);

  const formatYAxis = (value: number) => {
    if (value === 0) return '0';
    if (value >= 100000) {
      const val = value / 100000;
      return `${val % 1 === 0 ? val : val.toFixed(1)}L`;
    }
    return `${value / 1000}K`;
  };

  const getIcon = (category: string) => {
    const lower = category.toLowerCase();
    if (lower.includes('food') || lower.includes('dining')) return <Utensils size={16} className="text-[#00C853]" />;
    if (lower.includes('transport') || lower.includes('fuel')) return <Car size={16} className="text-[#F59E0B]" />;
    if (lower.includes('bill') || lower.includes('utility')) return <Zap size={16} className="text-[#EAB308]" />;
    if (lower.includes('shopping')) return <ShoppingBag size={16} className="text-[#EC4899]" />;
    if (lower.includes('entertain')) return <Film size={16} className="text-[#8B5CF6]" />;
    return <Layers size={16} className="text-[#06B6D4]" />;
  };

  const getIconBg = (category: string) => {
    const lower = category.toLowerCase();
    if (lower.includes('food') || lower.includes('dining')) return 'bg-[#00C853]/15';
    if (lower.includes('transport') || lower.includes('fuel')) return 'bg-[#F59E0B]/15';
    if (lower.includes('bill') || lower.includes('utility')) return 'bg-[#EAB308]/15';
    if (lower.includes('shopping')) return 'bg-[#EC4899]/15';
    if (lower.includes('entertain')) return 'bg-[#8B5CF6]/15';
    return 'bg-[#06B6D4]/15';
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Reports</h1>
          <p className="text-sm text-[#8B949E] mt-0.5">Analyse your financial behaviour</p>
        </div>

        {/* Functional Range Dropdown */}
        <div className="relative inline-flex items-center self-start sm:self-auto">
          <button 
            type="button"
            onClick={() => setIsRangeDropdownOpen(!isRangeDropdownOpen)}
            className="flex items-center gap-2 bg-[#12161A] hover:bg-[#181D23] border border-[#1F242C] text-[#D1D5DB] text-xs font-semibold px-3.5 py-2 rounded-xl transition-colors shadow-sm"
          >
            <span>{currentRangeLabel}</span>
            <ChevronDown size={14} className="text-[#8B949E]" />
          </button>

          {isRangeDropdownOpen && (
            <div className="absolute right-0 top-full mt-2 w-44 bg-[#12161A] border border-[#1F242C] rounded-xl shadow-2xl p-1.5 z-40 animate-in fade-in zoom-in-95 duration-150">
              {rangeOptions.map((opt) => (
                <button
                  key={opt.count}
                  onClick={() => {
                    setMonthsCount(opt.count);
                    setIsRangeDropdownOpen(false);
                  }}
                  className={`w-full text-left px-3 py-2 text-xs rounded-lg font-medium transition-colors ${
                    monthsCount === opt.count
                      ? 'bg-[#00C853] text-white font-semibold'
                      : 'text-[#8B949E] hover:text-white hover:bg-[#181D23]'
                  }`}
                >
                  {opt.label}
                </button>
              ))}
            </div>
          )}
        </div>
      </div>

      {/* 2-Column Section */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-5">
        {/* Monthly Cash Flow Grouped Bar Chart (7 cols) */}
        <div className="lg:col-span-7 bg-[#12161A] border border-[#1F242C] rounded-2xl p-5">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-sm font-semibold text-white">Monthly Cash Flow</h2>
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
            {isLoading ? (
              <div className="h-full flex items-center justify-center text-[#8B949E]">
                <Loader2 size={20} className="animate-spin text-[#00C853]" />
              </div>
            ) : trends.length > 0 ? (
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={trends} margin={{ top: 10, right: 10, left: -15, bottom: 0 }} barGap={6}>
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
                  <Bar 
                    dataKey="income" 
                    fill="#00C853" 
                    radius={[3, 3, 0, 0]} 
                    maxBarSize={22}
                  />
                  <Bar 
                    dataKey="expenses" 
                    fill="#EF4444" 
                    radius={[3, 3, 0, 0]} 
                    maxBarSize={22}
                  />
                </BarChart>
              </ResponsiveContainer>
            ) : (
              <div className="h-full flex flex-col items-center justify-center text-center p-4">
                <BarChart3 size={32} className="text-[#2D3748] mb-2" />
                <p className="text-xs text-[#8B949E]">No transaction history recorded for {currentRangeLabel.toLowerCase()}</p>
              </div>
            )}
          </div>
        </div>

        {/* Category Breakdown (5 cols) */}
        <div className="lg:col-span-5 bg-[#12161A] border border-[#1F242C] rounded-2xl p-5 flex flex-col justify-between">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-sm font-semibold text-white">Category Breakdown</h2>
            <span className="text-[11px] text-[#8B949E]">This Month</span>
          </div>

          {isLoading ? (
            <div className="py-12 flex items-center justify-center text-[#8B949E]">
              <Loader2 size={18} className="animate-spin text-[#00C853]" />
            </div>
          ) : breakdown.length > 0 ? (
            <div className="divide-y divide-[#1B222B] max-h-64 overflow-y-auto pr-1">
              {breakdown.map((item) => (
                <div key={item.categoryName} className="py-3 flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <div className={`w-8 h-8 rounded-xl flex items-center justify-center ${getIconBg(item.categoryName)}`}>
                      {getIcon(item.categoryName)}
                    </div>
                    <span className="text-[#D1D5DB] text-xs font-medium">
                      {item.categoryName}
                    </span>
                  </div>
                  <div className="text-xs font-semibold">
                    <span className="text-white font-bold">{formatCurrency(item.totalSpent)}</span>
                    <span className="text-[#8B949E] ml-1.5 font-normal">({item.percentage}%)</span>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="h-56 flex flex-col items-center justify-center text-center p-4">
              <PieChart size={32} className="text-[#2D3748] mb-2" />
              <p className="text-xs text-[#8B949E]">No category expenses recorded for the current month</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
