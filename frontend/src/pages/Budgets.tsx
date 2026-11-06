import React, { useState, useEffect } from 'react';
import { 
  Plus, 
  Utensils, 
  Car, 
  Zap, 
  ShoppingBag, 
  Film, 
  Layers, 
  Wallet, 
  Calendar,
  ChevronDown,
  Edit2, 
  Trash2, 
  AlertCircle,
  Loader2,
  PieChart
} from 'lucide-react';
import { formatCurrency } from '../utils/formatters';
import { CreateBudgetModal } from '../components/CreateBudgetModal';
import { api } from '../api/client';
import { Budget } from '../types';

export const Budgets: React.FC = () => {
  const [selectedMonth, setSelectedMonth] = useState<number>(() => new Date().getMonth() + 1);
  const [selectedYear, setSelectedYear] = useState<number>(() => new Date().getFullYear());
  const [isMonthPickerOpen, setIsMonthPickerOpen] = useState<boolean>(false);

  const [budgets, setBudgets] = useState<Budget[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);

  // Modals
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [editingBudget, setEditingBudget] = useState<Budget | null>(null);
  const [deletingId, setDeletingId] = useState<number | null>(null);
  const [isDeleting, setIsDeleting] = useState<boolean>(false);

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
      const data = await api.getBudgets(m, y);
      setBudgets(data || []);
    } catch (err) {
      console.error('Failed to load budgets:', err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadData(selectedMonth, selectedYear);
  }, [selectedMonth, selectedYear]);

  const handleDeleteBudget = async (id: number) => {
    setIsDeleting(true);
    try {
      await api.deleteBudget(id);
      setDeletingId(null);
      loadData(selectedMonth, selectedYear);
    } catch (err) {
      console.error('Failed to delete budget:', err);
    } finally {
      setIsDeleting(false);
    }
  };

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
        barColor: '#EC4899',
      };
    }
    if (lower.includes('entertain')) {
      return {
        icon: <Film size={18} className="text-[#8B5CF6]" />,
        boxBg: 'bg-[#8B5CF6]/15',
        barColor: '#8B5CF6',
      };
    }
    return {
      icon: <Layers size={18} className="text-[#06B6D4]" />,
      boxBg: 'bg-[#06B6D4]/15',
      barColor: '#06B6D4',
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

        <div className="flex items-center gap-3">
          {/* Functional Month/Year Selector */}
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

          <button
            onClick={() => {
              setEditingBudget(null);
              setIsModalOpen(true);
            }}
            className="flex items-center gap-1.5 bg-[#00C853] hover:bg-[#00B048] active:scale-[0.98] text-white text-xs font-semibold px-4 py-2 rounded-xl transition-all shadow-md shadow-[#00C853]/15"
          >
            <Plus size={16} />
            <span>Create Budget</span>
          </button>
        </div>
      </div>

      {/* Grid of Budget Cards */}
      {isLoading ? (
        <div className="py-16 text-center text-[#8B949E]">
          <Loader2 size={20} className="animate-spin inline-block text-[#00C853] mr-2" />
          <span>Loading budgets...</span>
        </div>
      ) : budgets.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
          {budgets.map((budget) => {
            const { icon, boxBg, barColor } = getCategoryDetails(budget.categoryName);
            const percent = Math.round(budget.utilizationPercentage ?? ((budget.spentAmount / budget.amount) * 100));
            const isExceeded = percent > 100;
            const isWarning = percent >= 85 && !isExceeded;

            return (
              <div
                key={budget.id}
                className="bg-[#12161A] border border-[#1F242C] rounded-2xl p-5 flex flex-col justify-between hover:border-[#2D3540] transition-colors group"
              >
                {/* Header row with Icon and Actions */}
                <div className="flex items-center justify-between mb-4">
                  <div className="flex items-center gap-3">
                    <div className={`w-10 h-10 rounded-xl flex items-center justify-center ${boxBg}`}>
                      {icon}
                    </div>
                    <div>
                      <span className="text-white font-semibold text-sm block">
                        {budget.categoryName || 'General'}
                      </span>
                      <span className="text-[10px] text-[#8B949E]">
                        Remaining: {formatCurrency(budget.remainingAmount ?? (budget.amount - budget.spentAmount))}
                      </span>
                    </div>
                  </div>

                  <div className="flex items-center gap-1 opacity-80 group-hover:opacity-100">
                    <button
                      title="Edit budget limit"
                      onClick={() => {
                        setEditingBudget(budget);
                        setIsModalOpen(true);
                      }}
                      className="p-1.5 rounded-lg text-[#8B949E] hover:text-white hover:bg-[#1E252E] transition-colors"
                    >
                      <Edit2 size={13} />
                    </button>
                    <button
                      title="Delete budget"
                      onClick={() => setDeletingId(budget.id)}
                      className="p-1.5 rounded-lg text-[#8B949E] hover:text-[#EF4444] hover:bg-[#EF4444]/10 transition-colors"
                    >
                      <Trash2 size={13} />
                    </button>
                  </div>
                </div>

                {/* Amounts & Percentage Badge */}
                <div className="flex items-center justify-between text-xs mb-2.5">
                  <div className="text-[#D1D5DB] font-medium">
                    <span className="text-white font-bold">{formatCurrency(budget.spentAmount)}</span>
                    <span className="text-[#8B949E]"> / {formatCurrency(budget.amount)}</span>
                  </div>
                  <div className={`text-xs font-bold ${
                    isExceeded ? 'text-[#EF4444]' : isWarning ? 'text-[#F59E0B]' : 'text-[#8B949E]'
                  }`}>
                    {percent}%
                  </div>
                </div>

                {/* Progress Bar */}
                <div className="w-full bg-[#1A2027] h-2 rounded-full overflow-hidden">
                  <div
                    className="h-full rounded-full transition-all duration-500"
                    style={{
                      width: `${Math.min(100, percent)}%`,
                      backgroundColor: isExceeded ? '#EF4444' : isWarning ? '#F59E0B' : barColor,
                    }}
                  />
                </div>
              </div>
            );
          })}
        </div>
      ) : (
        <div className="bg-[#12161A] border border-[#1F242C] rounded-2xl p-12 text-center max-w-lg mx-auto">
          <PieChart size={36} className="text-[#2D3748] mx-auto mb-3" />
          <h3 className="text-white font-semibold text-base mb-1">No Budgets for {currentMonthName} {selectedYear}</h3>
          <p className="text-xs text-[#8B949E] mb-5">
            Set spending limits to monitor your cash flow and keep category expenditures in check.
          </p>
          <button
            onClick={() => {
              setEditingBudget(null);
              setIsModalOpen(true);
            }}
            className="inline-flex items-center gap-1.5 bg-[#00C853] hover:bg-[#00B048] active:scale-[0.98] text-white text-xs font-semibold px-4 py-2.5 rounded-xl transition-all shadow"
          >
            <Plus size={16} />
            <span>Create First Budget</span>
          </button>
        </div>
      )}

      {/* Delete Confirmation Modal */}
      {deletingId && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/75 backdrop-blur-sm p-4 animate-in fade-in duration-150">
          <div className="w-full max-w-sm bg-[#12161A] border border-[#1F242C] rounded-2xl p-6 shadow-2xl space-y-4">
            <div className="flex items-center gap-3 text-[#EF4444]">
              <AlertCircle size={22} />
              <h3 className="text-white font-semibold text-base">Delete Budget</h3>
            </div>
            <p className="text-xs text-[#8B949E] leading-relaxed">
              Are you sure you want to remove this category budget for {currentMonthName} {selectedYear}?
            </p>
            <div className="flex items-center justify-end gap-3 pt-2">
              <button
                type="button"
                disabled={isDeleting}
                onClick={() => setDeletingId(null)}
                className="px-4 py-2 text-xs font-medium text-[#8B949E] hover:text-white"
              >
                Cancel
              </button>
              <button
                type="button"
                disabled={isDeleting}
                onClick={() => handleDeleteBudget(deletingId)}
                className="flex items-center gap-1.5 px-4 py-2 bg-[#EF4444] hover:bg-[#DC2626] text-white font-semibold text-xs rounded-xl shadow"
              >
                {isDeleting && <Loader2 size={13} className="animate-spin" />}
                <span>{isDeleting ? 'Deleting...' : 'Confirm Delete'}</span>
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Create / Edit Budget Modal */}
      <CreateBudgetModal
        isOpen={isModalOpen}
        editBudget={editingBudget}
        selectedMonth={selectedMonth}
        selectedYear={selectedYear}
        onClose={() => {
          setIsModalOpen(false);
          setEditingBudget(null);
        }}
        onSuccess={() => loadData(selectedMonth, selectedYear)}
      />
    </div>
  );
};
