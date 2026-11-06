import React, { useState, useEffect } from 'react';
import { X, Loader2 } from 'lucide-react';
import { api } from '../api/client';
import { Category, Budget } from '../types';

interface CreateBudgetModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSuccess: () => void;
  editBudget?: Budget | null;
  selectedMonth?: number;
  selectedYear?: number;
}

export const CreateBudgetModal: React.FC<CreateBudgetModalProps> = ({
  isOpen,
  onClose,
  onSuccess,
  editBudget = null,
  selectedMonth = 11,
  selectedYear = 2026,
}) => {
  const [categories, setCategories] = useState<Category[]>([]);
  const [categoryId, setCategoryId] = useState<number>(1);
  const [amount, setAmount] = useState<string>('');
  const [month, setMonth] = useState<number>(selectedMonth);
  const [year, setYear] = useState<number>(selectedYear);
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  // Close on Escape key
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape' && isOpen && !isSubmitting) {
        onClose();
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [isOpen, isSubmitting, onClose]);

  useEffect(() => {
    if (isOpen) {
      setError(null);
      api.getCategories()
        .then(cats => {
          const expenseCats = cats.filter(c => c.type === 'EXPENSE');
          setCategories(expenseCats);

          if (editBudget) {
            setCategoryId(editBudget.categoryId);
            setAmount(String(editBudget.amount));
            setMonth(editBudget.budgetMonth);
            setYear(editBudget.budgetYear);
          } else {
            setAmount('');
            setMonth(selectedMonth);
            setYear(selectedYear);
            if (expenseCats.length > 0) {
              setCategoryId(expenseCats[0].id);
            }
          }
        })
        .catch((err) => {
          console.error('Failed to load categories for budget:', err);
        });
    }
  }, [isOpen, editBudget, selectedMonth, selectedYear]);

  if (!isOpen) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (isSubmitting) return;

    const parsedAmount = parseFloat(amount);
    if (!amount || isNaN(parsedAmount) || parsedAmount <= 0) {
      setError('Please enter a valid monthly limit amount');
      return;
    }

    setIsSubmitting(true);
    setError(null);

    try {
      const payload = {
        categoryId,
        amount: parsedAmount,
        budgetMonth: month,
        budgetYear: year,
      };

      if (editBudget) {
        await api.updateBudget(editBudget.id, payload);
      } else {
        await api.createBudget(payload);
      }

      onSuccess();
      onClose();
    } catch (err: any) {
      console.error('Budget submit error:', err);
      setError(err.message || 'Failed to save budget.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div 
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/75 backdrop-blur-sm p-4 animate-in fade-in duration-200"
      onClick={(e) => {
        if (e.target === e.currentTarget && !isSubmitting) onClose();
      }}
    >
      <div className="w-full max-w-md bg-[#12161A] border border-[#1F242C] rounded-2xl shadow-2xl overflow-hidden animate-in zoom-in-95 duration-200">
        <div className="flex items-center justify-between px-6 py-4 border-b border-[#1F242C]">
          <h3 className="text-white font-semibold text-base">
            {editBudget ? 'Edit Budget' : 'Create Budget'}
          </h3>
          <button
            onClick={onClose}
            disabled={isSubmitting}
            className="text-[#8B949E] hover:text-white transition-colors p-1.5 rounded-lg hover:bg-[#1A2027]"
          >
            <X size={18} />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          {error && (
            <div className="p-3 text-xs bg-[#EF4444]/15 border border-[#EF4444]/30 text-[#F87171] rounded-xl leading-relaxed">
              {error}
            </div>
          )}

          {/* Category */}
          <div>
            <label className="block text-xs font-medium text-[#8B949E] mb-1.5">Category</label>
            <select
              value={categoryId}
              onChange={(e) => setCategoryId(Number(e.target.value))}
              disabled={!!editBudget} // Category cannot be changed in update
              className="w-full bg-[#15191E] border border-[#1F242C] rounded-xl px-3.5 py-2.5 text-white text-xs focus:outline-none focus:border-[#00C853] disabled:opacity-70"
            >
              {categories.map((c) => (
                <option key={c.id} value={c.id} className="bg-[#12161A] text-white">
                  {c.name}
                </option>
              ))}
            </select>
          </div>

          {/* Monthly Limit */}
          <div>
            <label className="block text-xs font-medium text-[#8B949E] mb-1.5">Monthly Limit (₹)</label>
            <div className="relative">
              <span className="absolute left-3.5 top-1/2 -translate-y-1/2 text-[#8B949E] font-medium text-sm">₹</span>
              <input
                type="number"
                step="any"
                min="0.01"
                required
                placeholder="e.g. 15000"
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
                className="w-full bg-[#15191E] border border-[#1F242C] rounded-xl pl-8 pr-4 py-2.5 text-white placeholder-[#4B5563] text-sm focus:outline-none focus:border-[#00C853] transition-colors"
              />
            </div>
          </div>

          {/* Month & Year Selection */}
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-medium text-[#8B949E] mb-1.5">Month</label>
              <select
                value={month}
                onChange={(e) => setMonth(Number(e.target.value))}
                className="w-full bg-[#15191E] border border-[#1F242C] rounded-xl px-3 py-2 text-white text-xs focus:outline-none focus:border-[#00C853]"
              >
                {[
                  { m: 1, name: 'January' },
                  { m: 2, name: 'February' },
                  { m: 3, name: 'March' },
                  { m: 4, name: 'April' },
                  { m: 5, name: 'May' },
                  { m: 6, name: 'June' },
                  { m: 7, name: 'July' },
                  { m: 8, name: 'August' },
                  { m: 9, name: 'September' },
                  { m: 10, name: 'October' },
                  { m: 11, name: 'November' },
                  { m: 12, name: 'December' },
                ].map((item) => (
                  <option key={item.m} value={item.m} className="bg-[#12161A] text-white">
                    {item.name}
                  </option>
                ))}
              </select>
            </div>
            <div>
              <label className="block text-xs font-medium text-[#8B949E] mb-1.5">Year</label>
              <select
                value={year}
                onChange={(e) => setYear(Number(e.target.value))}
                className="w-full bg-[#15191E] border border-[#1F242C] rounded-xl px-3 py-2 text-white text-xs focus:outline-none focus:border-[#00C853]"
              >
                <option value={2025} className="bg-[#12161A] text-white">2025</option>
                <option value={2026} className="bg-[#12161A] text-white">2026</option>
                <option value={2027} className="bg-[#12161A] text-white">2027</option>
              </select>
            </div>
          </div>

          {/* Actions */}
          <div className="flex items-center justify-end gap-3 pt-3">
            <button
              type="button"
              onClick={onClose}
              disabled={isSubmitting}
              className="px-4 py-2 text-xs font-medium text-[#8B949E] hover:text-white transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={isSubmitting}
              className="flex items-center gap-1.5 px-5 py-2.5 bg-[#00C853] hover:bg-[#00B048] active:scale-[0.98] disabled:opacity-60 text-white font-semibold text-xs rounded-xl transition-all shadow"
            >
              {isSubmitting && <Loader2 size={14} className="animate-spin" />}
              <span>{isSubmitting ? 'Saving...' : (editBudget ? 'Update Budget' : 'Create Budget')}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
