import React, { useState, useEffect } from 'react';
import { X, Loader2 } from 'lucide-react';
import { api } from '../api/client';
import { Category, Transaction, Account } from '../types';

interface AddTransactionModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSuccess: () => void;
  editTransaction?: Transaction | null;
}

export const AddTransactionModal: React.FC<AddTransactionModalProps> = ({
  isOpen,
  onClose,
  onSuccess,
  editTransaction = null,
}) => {
  const [type, setType] = useState<'INCOME' | 'EXPENSE'>('EXPENSE');
  const [amount, setAmount] = useState<string>('');
  const [description, setDescription] = useState<string>('');
  const [categoryId, setCategoryId] = useState<number>(1);
  const [accountId, setAccountId] = useState<number>(1);
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [transactionDate, setTransactionDate] = useState<string>(
    new Date().toISOString().split('T')[0]
  );
  const [categories, setCategories] = useState<Category[]>([]);
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

  // Load accounts and categories
  useEffect(() => {
    if (isOpen) {
      setError(null);
      Promise.all([
        api.getCategories(),
        api.getAccounts().catch(() => [] as Account[]),
      ]).then(([cats, accs]) => {
        setCategories(cats);
        setAccounts(accs);

        if (editTransaction) {
          setType(editTransaction.type);
          setAmount(String(Math.abs(editTransaction.amount)));
          setDescription(editTransaction.description);
          setCategoryId(editTransaction.categoryId);
          if (editTransaction.accountId) {
            setAccountId(editTransaction.accountId);
          } else if (accs.length > 0) {
            setAccountId(accs[0].id);
          }
          setTransactionDate(
            editTransaction.transactionDate.includes('T')
              ? editTransaction.transactionDate.split('T')[0]
              : editTransaction.transactionDate
          );
        } else {
          // Fresh form
          const initialType = 'EXPENSE';
          setType(initialType);
          setAmount('');
          setDescription('');
          setTransactionDate(new Date().toISOString().split('T')[0]);
          const filtered = cats.filter(c => c.type === initialType);
          if (filtered.length > 0) {
            setCategoryId(filtered[0].id);
          }
          if (accs.length > 0) {
            setAccountId(accs[0].id);
          }
        }
      }).catch((err) => {
        console.error('Failed to load transaction form dependencies:', err);
      });
    }
  }, [isOpen, editTransaction]);

  // Auto-switch category selection when switching type in create mode
  const handleTypeChange = (newType: 'INCOME' | 'EXPENSE') => {
    setType(newType);
    const filtered = categories.filter(c => c.type === newType);
    if (filtered.length > 0) {
      setCategoryId(filtered[0].id);
    }
  };

  if (!isOpen) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (isSubmitting) return;

    const parsedAmount = parseFloat(amount);
    if (!amount || isNaN(parsedAmount) || parsedAmount <= 0) {
      setError('Please enter a valid positive amount');
      return;
    }
    if (!description.trim()) {
      setError('Please enter a transaction description');
      return;
    }
    if (!transactionDate) {
      setError('Please select a valid transaction date');
      return;
    }

    setIsSubmitting(true);
    setError(null);

    try {
      let resolvedAccountId = accountId;
      if (!resolvedAccountId && accounts.length > 0) {
        resolvedAccountId = accounts[0].id;
      }
      if (!resolvedAccountId) {
        const freshAccounts = await api.getAccounts();
        if (freshAccounts.length > 0) {
          resolvedAccountId = freshAccounts[0].id;
        }
      }

      const payload = {
        accountId: resolvedAccountId || 1,
        categoryId,
        amount: parsedAmount,
        type,
        description: description.trim(),
        transactionDate,
      };

      if (editTransaction) {
        await api.updateTransaction(editTransaction.id, payload);
      } else {
        await api.createTransaction(payload);
      }

      onSuccess();
      onClose();
    } catch (err: any) {
      console.error('Transaction submit error:', err);
      setError(err.message || 'Failed to record transaction. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const filteredCategories = categories.filter(c => c.type === type);

  return (
    <div 
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/75 backdrop-blur-sm p-4 animate-in fade-in duration-200"
      onClick={(e) => {
        if (e.target === e.currentTarget && !isSubmitting) onClose();
      }}
    >
      <div className="w-full max-w-md bg-[#12161A] border border-[#1F242C] rounded-2xl shadow-2xl overflow-hidden animate-in zoom-in-95 duration-200">
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-[#1F242C]">
          <h3 className="text-white font-semibold text-base">
            {editTransaction ? 'Edit Transaction' : 'Add Transaction'}
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

          {/* Type Toggle Tabs */}
          <div className="grid grid-cols-2 p-1 bg-[#0E1217] rounded-xl border border-[#1F242C]">
            <button
              type="button"
              onClick={() => handleTypeChange('EXPENSE')}
              className={`py-2 text-xs font-semibold rounded-lg transition-all ${
                type === 'EXPENSE'
                  ? 'bg-[#EF4444] text-white shadow'
                  : 'text-[#8B949E] hover:text-white'
              }`}
            >
              Expense
            </button>
            <button
              type="button"
              onClick={() => handleTypeChange('INCOME')}
              className={`py-2 text-xs font-semibold rounded-lg transition-all ${
                type === 'INCOME'
                  ? 'bg-[#00C853] text-white shadow'
                  : 'text-[#8B949E] hover:text-white'
              }`}
            >
              Income
            </button>
          </div>

          {/* Amount */}
          <div>
            <label className="block text-xs font-medium text-[#8B949E] mb-1.5">Amount (₹)</label>
            <div className="relative">
              <span className="absolute left-3.5 top-1/2 -translate-y-1/2 text-[#8B949E] font-medium text-sm">₹</span>
              <input
                type="number"
                step="any"
                required
                min="0.01"
                placeholder="0.00"
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
                className="w-full bg-[#15191E] border border-[#1F242C] rounded-xl pl-8 pr-4 py-2.5 text-white placeholder-[#4B5563] text-sm focus:outline-none focus:border-[#00C853] transition-colors"
              />
            </div>
          </div>

          {/* Category */}
          <div>
            <label className="block text-xs font-medium text-[#8B949E] mb-1.5">Category</label>
            <select
              value={categoryId}
              onChange={(e) => setCategoryId(Number(e.target.value))}
              className="w-full bg-[#15191E] border border-[#1F242C] rounded-xl px-3.5 py-2.5 text-white text-xs focus:outline-none focus:border-[#00C853]"
            >
              {filteredCategories.map((c) => (
                <option key={c.id} value={c.id} className="bg-[#12161A] text-white">
                  {c.name}
                </option>
              ))}
            </select>
          </div>

          {/* Description */}
          <div>
            <label className="block text-xs font-medium text-[#8B949E] mb-1.5">Description</label>
            <input
              type="text"
              required
              placeholder="e.g. Grocery Shopping, Salary, Fuel"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              className="w-full bg-[#15191E] border border-[#1F242C] rounded-xl px-3.5 py-2.5 text-white placeholder-[#4B5563] text-xs focus:outline-none focus:border-[#00C853] transition-colors"
            />
          </div>

          {/* Date */}
          <div>
            <label className="block text-xs font-medium text-[#8B949E] mb-1.5">Date</label>
            <input
              type="date"
              required
              value={transactionDate}
              onChange={(e) => setTransactionDate(e.target.value)}
              className="w-full bg-[#15191E] border border-[#1F242C] rounded-xl px-3.5 py-2.5 text-white text-xs focus:outline-none focus:border-[#00C853]"
            />
          </div>

          {/* Action buttons */}
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
              <span>{isSubmitting ? 'Saving...' : (editTransaction ? 'Update Transaction' : 'Save Transaction')}</span>
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
