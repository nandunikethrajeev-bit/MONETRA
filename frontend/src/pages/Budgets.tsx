import React, { useState, useEffect } from 'react';
import { 
  Plus, 
  Utensils, 
  Car, 
  Zap, 
  ShoppingBag, 
  Film, 
  Layers, 
  Wallet 
} from 'lucide-react';
import { formatCurrency } from '../utils/formatters';
import { CreateBudgetModal } from '../components/CreateBudgetModal';
import { api, REFERENCE_BUDGETS } from '../api/client';
import { Budget } from '../types';

export const Budgets: React.FC = () => {
  const [budgets, setBudgets] = useState<Budget[]>(REFERENCE_BUDGETS);
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);

  const loadData = async () => {
    try {
      const data = await api.getBudgets(11, 2026);
      if (data && data.length > 0) {
        setBudgets(data);
      }
    } catch (err) {
      console.warn('Using reference budgets:', err);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const getCategoryDetails = (name?: string) => {
    const lower = (name || '').toLowerCase();
    if (lower.includes('food') || lower.includes('dining')) {
      return {
        icon: <Utensils size={18} className="text-[#00C853]" />,
        boxBg: 'bg-[#00C853]/15',
        barColor: '#00C853',
      };
    }
    if (lower.includes('transport') || lower.includes('fuel')) {
      return {
        icon: <Car size={18} className="text-[#F59E0B]" />,
        boxBg: 'bg-[#F59E0B]/15',
        barColor: '#00C853',
      };
    }
    if (lower.includes('bill') || lower.includes('utility')) {
      return {
        icon: <Zap size={18} className="text-[#EAB308]" />,
        boxBg: 'bg-[#EAB308]/15',
        barColor: '#00C853',
      };
    }
    if (lower.includes('shopping')) {
      return {
        icon: <ShoppingBag size={18} className="text-[#EC4899]" />,
        boxBg: 'bg-[#EC4899]/15',
        barColor: '#F59E0B', // warning high spending
      };
    }
    if (lower.includes('entertain')) {
      return {
        icon: <Film size={18} className="text-[#8B5CF6]" />,
        boxBg: 'bg-[#8B5CF6]/15',
        barColor: '#00C853',
      };
    }
    return {
      icon: <Layers size={18} className="text-[#06B6D4]" />,
      boxBg: 'bg-[#06B6D4]/15',
      barColor: '#00C853',
    };
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Budgets</h1>
          <p className="text-sm text-[#8B949E] mt-0.5">Set limits and keep your spending in control</p>
        </div>

        <button
          onClick={() => setIsModalOpen(true)}
          className="flex items-center gap-1.5 bg-[#00C853] hover:bg-[#00B048] text-white text-xs font-semibold px-4 py-2 rounded-lg transition-colors shadow-sm self-start sm:self-auto"
        >
          <Plus size={16} />
          <span>Create Budget</span>
        </button>
      </div>

      {/* 2x3 Grid of Budget Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
        {budgets.map((budget) => {
          const { icon, boxBg, barColor } = getCategoryDetails(budget.categoryName);
          const percent = Math.min(100, Math.round(budget.utilizationPercentage || (budget.spentAmount / budget.amount * 100)));
          const isWarning = percent >= 85;

          return (
            <div
              key={budget.id}
              className="bg-[#12161A] border border-[#1F242C] rounded-xl p-5 flex flex-col justify-between hover:border-[#2D3540] transition-colors"
            >
              {/* Category Header */}
              <div className="flex items-center gap-3 mb-4">
                <div className={`w-9 h-9 rounded-lg flex items-center justify-center ${boxBg}`}>
                  {icon}
                </div>
                <span className="text-white font-semibold text-sm">
                  {budget.categoryName}
                </span>
              </div>

              {/* Amounts & Percentage Badge */}
              <div className="flex items-center justify-between text-xs mb-2.5">
                <div className="text-[#D1D5DB] font-medium">
                  <span className="text-white font-bold">{formatCurrency(budget.spentAmount)}</span>
                  <span className="text-[#8B949E]"> / {formatCurrency(budget.amount)}</span>
                </div>
                <div className="text-xs font-semibold text-[#8B949E]">
                  {percent}%
                </div>
              </div>

              {/* Progress Bar */}
              <div className="w-full bg-[#1A2027] h-2 rounded-full overflow-hidden">
                <div
                  className="h-full rounded-full transition-all duration-500"
                  style={{
                    width: `${percent}%`,
                    backgroundColor: isWarning ? '#F59E0B' : barColor,
                  }}
                />
              </div>
            </div>
          );
        })}
      </div>

      <CreateBudgetModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSuccess={loadData}
      />
    </div>
  );
};
