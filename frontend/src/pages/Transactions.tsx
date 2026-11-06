import React, { useState, useEffect, useMemo } from 'react';
import { 
  Plus, 
  Search, 
  Calendar, 
  ChevronDown, 
  ChevronLeft, 
  ChevronRight,
  ArrowUpDown,
  ArrowUp,
  ArrowDown,
  Edit2, 
  Trash2, 
  Utensils, 
  Car, 
  Zap, 
  Wallet, 
  ShoppingBag, 
  Film, 
  Layers, 
  AlertCircle,
  Loader2
} from 'lucide-react';
import { formatSignedCurrency, formatDate } from '../utils/formatters';
import { AddTransactionModal } from '../components/AddTransactionModal';
import { api } from '../api/client';
import { Transaction, Category } from '../types';

export const Transactions: React.FC = () => {
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);

  // Filters
  const [searchTerm, setSearchTerm] = useState<string>('');
  const [selectedType, setSelectedType] = useState<string>('ALL');
  const [selectedCategory, setSelectedCategory] = useState<string>('ALL');
  const [dateRangePreset, setDateRangePreset] = useState<string>('THIS_MONTH');
  const [customStartDate, setCustomStartDate] = useState<string>('');
  const [customEndDate, setCustomEndDate] = useState<string>('');
  const [isDateFilterOpen, setIsDateFilterOpen] = useState<boolean>(false);

  // Sorting
  const [sortField, setSortField] = useState<'date' | 'amount' | 'description' | 'category'>('date');
  const [sortDirection, setSortDirection] = useState<'asc' | 'desc'>('desc');

  // Pagination
  const [currentPage, setCurrentPage] = useState<number>(1);
  const pageSize = 10;

  // Modal states
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);
  const [editingTransaction, setEditingTransaction] = useState<Transaction | null>(null);
  const [deletingId, setDeletingId] = useState<number | null>(null);
  const [isDeleting, setIsDeleting] = useState<boolean>(false);

  // Compute actual date range dates
  const { startDate, endDate, rangeLabel } = useMemo(() => {
    const now = new Date();
    const y = now.getFullYear();
    const m = now.getMonth();

    if (dateRangePreset === 'ALL_TIME') {
      return { startDate: undefined, endDate: undefined, rangeLabel: 'All Time' };
    }
    if (dateRangePreset === 'THIS_MONTH') {
      const start = new Date(y, m, 1).toISOString().split('T')[0];
      const end = new Date(y, m + 1, 0).toISOString().split('T')[0];
      return { 
        startDate: start, 
        endDate: end, 
        rangeLabel: `${now.toLocaleString('default', { month: 'short' })} 1 - ${new Date(y, m + 1, 0).getDate()}, ${y}` 
      };
    }
    if (dateRangePreset === 'LAST_MONTH') {
      const start = new Date(y, m - 1, 1).toISOString().split('T')[0];
      const end = new Date(y, m, 0).toISOString().split('T')[0];
      const lastMonthName = new Date(y, m - 1, 1).toLocaleString('default', { month: 'short' });
      return { 
        startDate: start, 
        endDate: end, 
        rangeLabel: `Last Month (${lastMonthName})` 
      };
    }
    if (dateRangePreset === 'THIS_YEAR') {
      const start = `${y}-01-01`;
      const end = `${y}-12-31`;
      return { startDate: start, endDate: end, rangeLabel: `This Year (${y})` };
    }
    // Custom
    const label = (customStartDate && customEndDate) 
      ? `${customStartDate} to ${customEndDate}`
      : 'Custom Date Range';
    return { 
      startDate: customStartDate || undefined, 
      endDate: customEndDate || undefined, 
      rangeLabel: label 
    };
  }, [dateRangePreset, customStartDate, customEndDate]);

  const loadData = async () => {
    setIsLoading(true);
    try {
      const [txs, cats] = await Promise.all([
        api.getTransactions({
          type: selectedType !== 'ALL' ? selectedType : undefined,
          categoryId: selectedCategory !== 'ALL' ? Number(selectedCategory) : undefined,
          startDate,
          endDate,
          search: searchTerm ? searchTerm.trim() : undefined,
        }),
        api.getCategories(),
      ]);
      setTransactions(txs || []);
      setCategories(cats || []);
    } catch (err) {
      console.error('Failed to load transactions:', err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadData();
    setCurrentPage(1);
  }, [selectedType, selectedCategory, startDate, endDate, searchTerm]);

  // Handle column sort toggle
  const toggleSort = (field: 'date' | 'amount' | 'description' | 'category') => {
    if (sortField === field) {
      setSortDirection(sortDirection === 'asc' ? 'desc' : 'asc');
    } else {
      setSortField(field);
      setSortDirection('desc');
    }
  };

  // Filter and sort transactions in memory
  const sortedTransactions = useMemo(() => {
    return [...transactions].sort((a, b) => {
      let comparison = 0;
      if (sortField === 'date') {
        comparison = new Date(a.transactionDate).getTime() - new Date(b.transactionDate).getTime();
      } else if (sortField === 'amount') {
        comparison = Number(a.amount) - Number(b.amount);
      } else if (sortField === 'description') {
        comparison = a.description.localeCompare(b.description);
      } else if (sortField === 'category') {
        comparison = (a.categoryName || '').localeCompare(b.categoryName || '');
      }
      return sortDirection === 'asc' ? comparison : -comparison;
    });
  }, [transactions, sortField, sortDirection]);

  // Paginated transactions
  const totalPages = Math.ceil(sortedTransactions.length / pageSize) || 1;
  const paginatedTransactions = useMemo(() => {
    const startIndex = (currentPage - 1) * pageSize;
    return sortedTransactions.slice(startIndex, startIndex + pageSize);
  }, [sortedTransactions, currentPage, pageSize]);

  // Delete transaction action
  const handleDeleteTransaction = async (id: number) => {
    setIsDeleting(true);
    try {
      await api.deleteTransaction(id);
      setDeletingId(null);
      loadData();
    } catch (err) {
      console.error('Failed to delete transaction:', err);
    } finally {
      setIsDeleting(false);
    }
  };

  const getCategoryIcon = (name?: string) => {
    const lower = (name || '').toLowerCase();
    if (lower.includes('food') || lower.includes('dining')) return <Utensils size={14} className="text-[#00C853]" />;
    if (lower.includes('transport') || lower.includes('fuel')) return <Car size={14} className="text-[#F59E0B]" />;
    if (lower.includes('bill') || lower.includes('utility')) return <Zap size={14} className="text-[#EAB308]" />;
    if (lower.includes('shopping')) return <ShoppingBag size={14} className="text-[#EC4899]" />;
    if (lower.includes('entertain')) return <Film size={14} className="text-[#8B5CF6]" />;
    return <Wallet size={14} className="text-[#00C853]" />;
  };

  const getCategoryBg = (name?: string) => {
    const lower = (name || '').toLowerCase();
    if (lower.includes('food') || lower.includes('dining')) return 'bg-[#00C853]/15';
    if (lower.includes('transport') || lower.includes('fuel')) return 'bg-[#F59E0B]/15';
    if (lower.includes('bill') || lower.includes('utility')) return 'bg-[#EAB308]/15';
    if (lower.includes('shopping')) return 'bg-[#EC4899]/15';
    if (lower.includes('entertain')) return 'bg-[#8B5CF6]/15';
    return 'bg-[#00C853]/15';
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Transactions</h1>
          <p className="text-sm text-[#8B949E] mt-0.5">Manage your income and expenses</p>
        </div>

        <button
          onClick={() => {
            setEditingTransaction(null);
            setIsModalOpen(true);
          }}
          className="flex items-center gap-1.5 bg-[#00C853] hover:bg-[#00B048] active:scale-[0.98] text-white text-xs font-semibold px-4 py-2.5 rounded-xl transition-all shadow-md shadow-[#00C853]/15 self-start sm:self-auto"
        >
          <Plus size={16} />
          <span>Add Transaction</span>
        </button>
      </div>

      {/* Filter Bar */}
      <div className="flex flex-wrap items-center gap-3">
        {/* Search Input */}
        <div className="relative flex-1 min-w-[220px]">
          <Search size={15} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-[#6E7681]" />
          <input
            type="text"
            placeholder="Search transactions..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full bg-[#12161A] border border-[#1F242C] rounded-xl pl-9 pr-4 py-2 text-xs text-white placeholder-[#6E7681] focus:outline-none focus:border-[#00C853] transition-colors"
          />
        </div>

        {/* Type Filter */}
        <div className="relative">
          <select
            value={selectedType}
            onChange={(e) => setSelectedType(e.target.value)}
            className="appearance-none bg-[#12161A] border border-[#1F242C] rounded-xl pl-3.5 pr-8 py-2 text-xs text-[#D1D5DB] font-medium focus:outline-none focus:border-[#00C853] cursor-pointer"
          >
            <option value="ALL">All Types</option>
            <option value="INCOME">Income</option>
            <option value="EXPENSE">Expense</option>
          </select>
          <ChevronDown size={14} className="absolute right-2.5 top-1/2 -translate-y-1/2 text-[#8B949E] pointer-events-none" />
        </div>

        {/* Category Filter */}
        <div className="relative">
          <select
            value={selectedCategory}
            onChange={(e) => setSelectedCategory(e.target.value)}
            className="appearance-none bg-[#12161A] border border-[#1F242C] rounded-xl pl-3.5 pr-8 py-2 text-xs text-[#D1D5DB] font-medium focus:outline-none focus:border-[#00C853] cursor-pointer"
          >
            <option value="ALL">All Categories</option>
            {categories.map((c) => (
              <option key={c.id} value={c.id}>{c.name}</option>
            ))}
          </select>
          <ChevronDown size={14} className="absolute right-2.5 top-1/2 -translate-y-1/2 text-[#8B949E] pointer-events-none" />
        </div>

        {/* Date Range Selector Dropdown */}
        <div className="relative">
          <button 
            type="button"
            onClick={() => setIsDateFilterOpen(!isDateFilterOpen)}
            className="flex items-center gap-2 bg-[#12161A] hover:bg-[#181D23] border border-[#1F242C] text-[#D1D5DB] text-xs font-medium px-3.5 py-2 rounded-xl transition-colors"
          >
            <Calendar size={14} className="text-[#00C853]" />
            <span>{rangeLabel}</span>
            <ChevronDown size={14} className="text-[#8B949E]" />
          </button>

          {isDateFilterOpen && (
            <div className="absolute right-0 mt-2 w-64 bg-[#12161A] border border-[#1F242C] rounded-xl shadow-2xl p-3 z-40 animate-in fade-in zoom-in-95 duration-150">
              <div className="text-[10px] font-semibold text-[#8B949E] uppercase tracking-wider mb-2">
                Date Range Presets
              </div>
              <div className="space-y-1">
                {[
                  { id: 'THIS_MONTH', label: 'This Month' },
                  { id: 'LAST_MONTH', label: 'Last Month' },
                  { id: 'THIS_YEAR', label: 'This Year' },
                  { id: 'ALL_TIME', label: 'All Time' },
                  { id: 'CUSTOM', label: 'Custom Range' },
                ].map((preset) => (
                  <button
                    key={preset.id}
                    onClick={() => {
                      setDateRangePreset(preset.id);
                      if (preset.id !== 'CUSTOM') {
                        setIsDateFilterOpen(false);
                      }
                    }}
                    className={`w-full text-left px-3 py-1.5 text-xs rounded-lg font-medium transition-colors ${
                      dateRangePreset === preset.id
                        ? 'bg-[#00C853] text-white font-semibold'
                        : 'text-[#8B949E] hover:text-white hover:bg-[#181D23]'
                    }`}
                  >
                    {preset.label}
                  </button>
                ))}
              </div>

              {dateRangePreset === 'CUSTOM' && (
                <div className="mt-3 pt-3 border-t border-[#1F242C] space-y-2">
                  <div>
                    <label className="block text-[10px] text-[#8B949E] mb-1">Start Date</label>
                    <input
                      type="date"
                      value={customStartDate}
                      onChange={(e) => setCustomStartDate(e.target.value)}
                      className="w-full bg-[#15191E] border border-[#1F242C] rounded-lg px-2.5 py-1.5 text-xs text-white"
                    />
                  </div>
                  <div>
                    <label className="block text-[10px] text-[#8B949E] mb-1">End Date</label>
                    <input
                      type="date"
                      value={customEndDate}
                      onChange={(e) => setCustomEndDate(e.target.value)}
                      className="w-full bg-[#15191E] border border-[#1F242C] rounded-lg px-2.5 py-1.5 text-xs text-white"
                    />
                  </div>
                  <button
                    onClick={() => setIsDateFilterOpen(false)}
                    className="w-full py-1.5 bg-[#00C853] hover:bg-[#00B048] text-white font-semibold text-xs rounded-lg mt-2"
                  >
                    Apply Filter
                  </button>
                </div>
              )}
            </div>
          )}
        </div>
      </div>

      {/* Dense Transactions Table */}
      <div className="bg-[#12161A] border border-[#1F242C] rounded-2xl overflow-hidden shadow-sm">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="border-b border-[#1F242C] text-[11px] font-semibold text-[#8B949E] uppercase tracking-wider bg-[#0E1217]">
                {/* Date Header */}
                <th 
                  className="py-3 px-5 cursor-pointer hover:text-white select-none transition-colors"
                  onClick={() => toggleSort('date')}
                >
                  <div className="flex items-center gap-1.5">
                    <span>Date</span>
                    {sortField === 'date' ? (
                      sortDirection === 'asc' ? <ArrowUp size={12} className="text-[#00C853]" /> : <ArrowDown size={12} className="text-[#00C853]" />
                    ) : <ArrowUpDown size={12} className="opacity-40" />}
                  </div>
                </th>

                {/* Description Header */}
                <th 
                  className="py-3 px-5 cursor-pointer hover:text-white select-none transition-colors"
                  onClick={() => toggleSort('description')}
                >
                  <div className="flex items-center gap-1.5">
                    <span>Description</span>
                    {sortField === 'description' ? (
                      sortDirection === 'asc' ? <ArrowUp size={12} className="text-[#00C853]" /> : <ArrowDown size={12} className="text-[#00C853]" />
                    ) : <ArrowUpDown size={12} className="opacity-40" />}
                  </div>
                </th>

                {/* Category Header */}
                <th 
                  className="py-3 px-5 cursor-pointer hover:text-white select-none transition-colors"
                  onClick={() => toggleSort('category')}
                >
                  <div className="flex items-center gap-1.5">
                    <span>Category</span>
                    {sortField === 'category' ? (
                      sortDirection === 'asc' ? <ArrowUp size={12} className="text-[#00C853]" /> : <ArrowDown size={12} className="text-[#00C853]" />
                    ) : <ArrowUpDown size={12} className="opacity-40" />}
                  </div>
                </th>

                {/* Type Header */}
                <th className="py-3 px-5">Type</th>

                {/* Amount Header */}
                <th 
                  className="py-3 px-5 text-right cursor-pointer hover:text-white select-none transition-colors"
                  onClick={() => toggleSort('amount')}
                >
                  <div className="flex items-center justify-end gap-1.5">
                    <span>Amount</span>
                    {sortField === 'amount' ? (
                      sortDirection === 'asc' ? <ArrowUp size={12} className="text-[#00C853]" /> : <ArrowDown size={12} className="text-[#00C853]" />
                    ) : <ArrowUpDown size={12} className="opacity-40" />}
                  </div>
                </th>

                {/* Actions Header */}
                <th className="py-3 px-5 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-[#1B222B] text-xs font-medium">
              {isLoading ? (
                <tr>
                  <td colSpan={6} className="py-12 text-center text-[#8B949E]">
                    <div className="flex items-center justify-center gap-2">
                      <Loader2 size={16} className="animate-spin text-[#00C853]" />
                      <span>Loading records...</span>
                    </div>
                  </td>
                </tr>
              ) : paginatedTransactions.length > 0 ? (
                paginatedTransactions.map((tx) => {
                  const isIncome = tx.type === 'INCOME';
                  return (
                    <tr key={tx.id} className="hover:bg-[#151A20] transition-colors group">
                      {/* Date */}
                      <td className="py-3 px-5 text-[#8B949E] whitespace-nowrap">
                        {formatDate(tx.transactionDate)}
                      </td>

                      {/* Description */}
                      <td className="py-3 px-5 text-white font-medium">
                        {tx.description}
                      </td>

                      {/* Category with Icon Box */}
                      <td className="py-3 px-5">
                        <div className="flex items-center gap-2">
                          <div className={`w-6 h-6 rounded flex items-center justify-center ${getCategoryBg(tx.categoryName)}`}>
                            {getCategoryIcon(tx.categoryName)}
                          </div>
                          <span className="text-[#D1D5DB]">{tx.categoryName || 'General'}</span>
                        </div>
                      </td>

                      {/* Type Badge */}
                      <td className="py-3 px-5">
                        <span
                          className={`inline-flex items-center px-2 py-0.5 rounded text-[11px] font-semibold ${
                            isIncome
                              ? 'bg-[#00C853]/15 text-[#00C853]'
                              : 'bg-[#EF4444]/15 text-[#EF4444]'
                          }`}
                        >
                          {isIncome ? 'Income' : 'Expense'}
                        </span>
                      </td>

                      {/* Amount */}
                      <td className={`py-3 px-5 text-right font-semibold whitespace-nowrap ${
                        isIncome ? 'text-[#00C853]' : 'text-[#EF4444]'
                      }`}>
                        {formatSignedCurrency(tx.amount, tx.type)}
                      </td>

                      {/* Actions: Edit & Delete */}
                      <td className="py-3 px-5 text-right whitespace-nowrap">
                        <div className="flex items-center justify-end gap-1.5 opacity-80 group-hover:opacity-100">
                          <button
                            title="Edit transaction"
                            onClick={() => {
                              setEditingTransaction(tx);
                              setIsModalOpen(true);
                            }}
                            className="p-1.5 rounded-lg text-[#8B949E] hover:text-white hover:bg-[#1E252E] transition-colors"
                          >
                            <Edit2 size={13} />
                          </button>
                          <button
                            title="Delete transaction"
                            onClick={() => setDeletingId(tx.id)}
                            className="p-1.5 rounded-lg text-[#8B949E] hover:text-[#EF4444] hover:bg-[#EF4444]/10 transition-colors"
                          >
                            <Trash2 size={13} />
                          </button>
                        </div>
                      </td>
                    </tr>
                  );
                })
              ) : (
                <tr>
                  <td colSpan={6} className="py-12 text-center text-[#8B949E]">
                    No transactions match the selected filters.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination Bar */}
        {sortedTransactions.length > 0 && (
          <div className="flex flex-col sm:flex-row items-center justify-between gap-3 px-5 py-3 border-t border-[#1F242C] text-xs text-[#8B949E]">
            <div>
              Showing <span className="font-semibold text-white">{(currentPage - 1) * pageSize + 1}</span> to{' '}
              <span className="font-semibold text-white">
                {Math.min(currentPage * pageSize, sortedTransactions.length)}
              </span> of <span className="font-semibold text-white">{sortedTransactions.length}</span> records
            </div>

            <div className="flex items-center gap-1.5">
              <button
                disabled={currentPage === 1}
                onClick={() => setCurrentPage(p => Math.max(1, p - 1))}
                className="p-1.5 rounded-lg border border-[#1F242C] disabled:opacity-40 disabled:cursor-not-allowed hover:bg-[#181D23] text-white transition-colors"
              >
                <ChevronLeft size={14} />
              </button>

              <span className="px-3 py-1 font-semibold text-white">
                {currentPage} / {totalPages}
              </span>

              <button
                disabled={currentPage === totalPages}
                onClick={() => setCurrentPage(p => Math.min(totalPages, p + 1))}
                className="p-1.5 rounded-lg border border-[#1F242C] disabled:opacity-40 disabled:cursor-not-allowed hover:bg-[#181D23] text-white transition-colors"
              >
                <ChevronRight size={14} />
              </button>
            </div>
          </div>
        )}
      </div>

      {/* Delete Confirmation Modal */}
      {deletingId && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/75 backdrop-blur-sm p-4 animate-in fade-in duration-150">
          <div className="w-full max-w-sm bg-[#12161A] border border-[#1F242C] rounded-2xl p-6 shadow-2xl space-y-4">
            <div className="flex items-center gap-3 text-[#EF4444]">
              <AlertCircle size={22} />
              <h3 className="text-white font-semibold text-base">Delete Transaction</h3>
            </div>
            <p className="text-xs text-[#8B949E] leading-relaxed">
              Are you sure you want to permanently delete this transaction? The account balance will be automatically recalculated.
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
                onClick={() => handleDeleteTransaction(deletingId)}
                className="flex items-center gap-1.5 px-4 py-2 bg-[#EF4444] hover:bg-[#DC2626] active:scale-[0.98] text-white font-semibold text-xs rounded-xl shadow"
              >
                {isDeleting && <Loader2 size={13} className="animate-spin" />}
                <span>{isDeleting ? 'Deleting...' : 'Confirm Delete'}</span>
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Add / Edit Transaction Modal */}
      <AddTransactionModal
        isOpen={isModalOpen}
        editTransaction={editingTransaction}
        onClose={() => {
          setIsModalOpen(false);
          setEditingTransaction(null);
        }}
        onSuccess={loadData}
      />
    </div>
  );
};
