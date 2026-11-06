import React, { useState, useEffect } from 'react';
import { 
  Plus, 
  Search, 
  Calendar, 
  ChevronDown, 
  Utensils, 
  Car, 
  Zap, 
  Wallet, 
  ShoppingBag, 
  Film, 
  Layers 
} from 'lucide-react';
import { formatSignedCurrency, formatDate } from '../utils/formatters';
import { AddTransactionModal } from '../components/AddTransactionModal';
import { api, REFERENCE_TRANSACTIONS } from '../api/client';
import { Transaction, Category } from '../types';

export const Transactions: React.FC = () => {
  const [transactions, setTransactions] = useState<Transaction[]>(REFERENCE_TRANSACTIONS);
  const [categories, setCategories] = useState<Category[]>([]);
  const [searchTerm, setSearchTerm] = useState<string>('');
  const [selectedType, setSelectedType] = useState<string>('ALL');
  const [selectedCategory, setSelectedCategory] = useState<string>('ALL');
  const [isModalOpen, setIsModalOpen] = useState<boolean>(false);

  const loadData = async () => {
    try {
      const [txs, cats] = await Promise.all([
        api.getTransactions(),
        api.getCategories(),
      ]);
      if (txs && txs.length > 0) {
        setTransactions(txs);
      }
      if (cats && cats.length > 0) {
        setCategories(cats);
      }
    } catch (err) {
      console.warn('Using reference transactions:', err);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

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

  // Filter transactions
  const filteredTransactions = transactions.filter((tx) => {
    const matchesSearch = tx.description.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesType = selectedType === 'ALL' || tx.type === selectedType;
    const matchesCategory = selectedCategory === 'ALL' || String(tx.categoryId) === selectedCategory;
    return matchesSearch && matchesType && matchesCategory;
  });

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Transactions</h1>
          <p className="text-sm text-[#8B949E] mt-0.5">Manage your income and expenses</p>
        </div>

        <button
          onClick={() => setIsModalOpen(true)}
          className="flex items-center gap-1.5 bg-[#00C853] hover:bg-[#00B048] text-white text-xs font-semibold px-4 py-2 rounded-lg transition-colors shadow-sm self-start sm:self-auto"
        >
          <Plus size={16} />
          <span>Add Transaction</span>
        </button>
      </div>

      {/* Filter Bar */}
      <div className="flex flex-wrap items-center gap-3">
        {/* Search Input */}
        <div className="relative flex-1 min-w-[200px]">
          <Search size={15} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-[#6E7681]" />
          <input
            type="text"
            placeholder="Search transactions..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full bg-[#12161A] border border-[#1F242C] rounded-lg pl-9 pr-4 py-2 text-xs text-white placeholder-[#6E7681] focus:outline-none focus:border-[#00C853] transition-colors"
          />
        </div>

        {/* Type Filter */}
        <div className="relative">
          <select
            value={selectedType}
            onChange={(e) => setSelectedType(e.target.value)}
            className="appearance-none bg-[#12161A] border border-[#1F242C] rounded-lg pl-3.5 pr-8 py-2 text-xs text-[#D1D5DB] font-medium focus:outline-none focus:border-[#00C853] cursor-pointer"
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
            className="appearance-none bg-[#12161A] border border-[#1F242C] rounded-lg pl-3.5 pr-8 py-2 text-xs text-[#D1D5DB] font-medium focus:outline-none focus:border-[#00C853] cursor-pointer"
          >
            <option value="ALL">All Categories</option>
            {categories.map((c) => (
              <option key={c.id} value={c.id}>{c.name}</option>
            ))}
          </select>
          <ChevronDown size={14} className="absolute right-2.5 top-1/2 -translate-y-1/2 text-[#8B949E] pointer-events-none" />
        </div>

        {/* Date Range Selector */}
        <div className="relative">
          <button className="flex items-center gap-2 bg-[#12161A] hover:bg-[#181D23] border border-[#1F242C] text-[#D1D5DB] text-xs font-medium px-3.5 py-2 rounded-lg transition-colors">
            <Calendar size={14} className="text-[#8B949E]" />
            <span>Nov 1, 2026 - Nov 30, 2026</span>
            <ChevronDown size={14} className="text-[#8B949E]" />
          </button>
        </div>
      </div>

      {/* Dense Transactions Table */}
      <div className="bg-[#12161A] border border-[#1F242C] rounded-xl overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="border-b border-[#1F242C] text-[11px] font-semibold text-[#8B949E] uppercase tracking-wider">
                <th className="py-3 px-5">
                  <div className="flex items-center gap-1 cursor-pointer hover:text-white">
                    <span>Date</span>
                    <ChevronDown size={12} />
                  </div>
                </th>
                <th className="py-3 px-5">Description</th>
                <th className="py-3 px-5">
                  <div className="flex items-center gap-1 cursor-pointer hover:text-white">
                    <span>Category</span>
                    <ChevronDown size={12} />
                  </div>
                </th>
                <th className="py-3 px-5">
                  <div className="flex items-center gap-1 cursor-pointer hover:text-white">
                    <span>Type</span>
                    <ChevronDown size={12} />
                  </div>
                </th>
                <th className="py-3 px-5 text-right">Amount</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-[#1B222B] text-xs font-medium">
              {filteredTransactions.map((tx) => {
                const isIncome = tx.type === 'INCOME';
                return (
                  <tr key={tx.id} className="hover:bg-[#151A20] transition-colors">
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
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </div>

      <AddTransactionModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSuccess={loadData}
      />
    </div>
  );
};

