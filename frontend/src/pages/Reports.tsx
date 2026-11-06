import React, { useState, useEffect } from 'react';
import { 
  ChevronDown, 
  Utensils, 
  Car, 
  Zap, 
  ShoppingBag, 
  Layers 
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
import { api, REFERENCE_MONTHLY_TRENDS, REFERENCE_REPORTS_BREAKDOWN } from '../api/client';
import { MonthlyTrend } from '../types';

export const Reports: React.FC = () => {
  const [trends, setTrends] = useState<MonthlyTrend[]>(REFERENCE_MONTHLY_TRENDS);
  const [breakdown, setBreakdown] = useState(REFERENCE_REPORTS_BREAKDOWN);

  const loadData = async () => {
    try {
      const data = await api.getMonthlyTrends(6);
      if (data && data.length > 0) {
        setTrends(data);
      }
    } catch (err) {
      console.warn('Using reference trends:', err);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const formatYAxis = (value: number) => {
    if (value === 0) return '0';
    if (value >= 100000) {
      const val = value / 100000;
      return `${val % 1 === 0 ? val : val.toFixed(1)}L`;
    }
    return `${value / 1000}K`;
  };

  const getIcon = (category: string) => {
    switch (category) {
      case 'Food & Dining':
        return <Utensils size={16} className="text-[#00C853]" />;
      case 'Transport':
        return <Car size={16} className="text-[#F59E0B]" />;
      case 'Bills & Utilities':
        return <Zap size={16} className="text-[#EAB308]" />;
      case 'Shopping':
        return <ShoppingBag size={16} className="text-[#EC4899]" />;
      default:
        return <Layers size={16} className="text-[#8B5CF6]" />;
    }
  };

  const getIconBg = (category: string) => {
    switch (category) {
      case 'Food & Dining':
        return 'bg-[#00C853]/15';
      case 'Transport':
        return 'bg-[#F59E0B]/15';
      case 'Bills & Utilities':
        return 'bg-[#EAB308]/15';
      case 'Shopping':
        return 'bg-[#EC4899]/15';
      default:
        return 'bg-[#8B5CF6]/15';
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Reports</h1>
          <p className="text-sm text-[#8B949E] mt-0.5">Analyse your financial behaviour</p>
        </div>

        <div className="relative inline-flex items-center self-start sm:self-auto">
          <button className="flex items-center gap-2 bg-[#12161A] hover:bg-[#181D23] border border-[#1F242C] text-[#D1D5DB] text-xs font-medium px-3.5 py-2 rounded-lg transition-colors">
            <span>Last 6 Months</span>
            <ChevronDown size={14} className="text-[#8B949E]" />
          </button>
        </div>
      </div>

      {/* 2-Column Section */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-5">
        {/* Monthly Cash Flow Grouped Bar Chart (7 cols) */}
        <div className="lg:col-span-7 bg-[#12161A] border border-[#1F242C] rounded-xl p-5">
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
          </div>
        </div>

        {/* Category Breakdown (5 cols) */}
        <div className="lg:col-span-5 bg-[#12161A] border border-[#1F242C] rounded-xl p-5 flex flex-col justify-between">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-sm font-semibold text-white">Category Breakdown</h2>
          </div>

          <div className="divide-y divide-[#1B222B]">
            {breakdown.map((item) => (
              <div key={item.category} className="py-3 flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <div className={`w-8 h-8 rounded-lg flex items-center justify-center ${getIconBg(item.category)}`}>
                    {getIcon(item.category)}
                  </div>
                  <span className="text-[#D1D5DB] text-xs font-medium">
                    {item.category}
                  </span>
                </div>
                <div className="text-xs font-semibold">
                  <span className="text-white font-bold">{formatCurrency(item.amount)}</span>
                  <span className="text-[#8B949E] ml-1.5 font-normal">({item.percentage}%)</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

