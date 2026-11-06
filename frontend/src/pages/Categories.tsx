import React, { useState, useEffect } from 'react';
import { Plus, Utensils, Car, Zap, ShoppingBag, Film, Layers, Wallet, Tag } from 'lucide-react';
import { api } from '../api/client';
import { Category } from '../types';

export const Categories: React.FC = () => {
  const [categories, setCategories] = useState<Category[]>([]);
  const [isAdding, setIsAdding] = useState<boolean>(false);
  const [newCatName, setNewCatName] = useState<string>('');
  const [newCatType, setNewCatType] = useState<'INCOME' | 'EXPENSE'>('EXPENSE');

  const loadCategories = async () => {
    try {
      const data = await api.getCategories();
      if (data && data.length > 0) {
        setCategories(data);
      }
    } catch {
      setCategories([
        { id: 1, name: 'Food & Dining', type: 'EXPENSE', color: '#00C853' },
        { id: 2, name: 'Transport', type: 'EXPENSE', color: '#F59E0B' },
        { id: 3, name: 'Bills & Utilities', type: 'EXPENSE', color: '#EAB308' },
        { id: 4, name: 'Shopping', type: 'EXPENSE', color: '#EC4899' },
        { id: 5, name: 'Entertainment', type: 'EXPENSE', color: '#8B5CF6' },
        { id: 6, name: 'Others', type: 'EXPENSE', color: '#06B6D4' },
        { id: 9, name: 'Salary', type: 'INCOME', color: '#00C853' },
        { id: 10, name: 'Freelance', type: 'INCOME', color: '#00C853' },
        { id: 11, name: 'Investments', type: 'INCOME', color: '#00C853' },
      ]);
    }
  };

  useEffect(() => {
    loadCategories();
  }, []);

  const handleAddCategory = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newCatName.trim()) return;

    try {
      await api.createCategory({
        name: newCatName.trim(),
        type: newCatType,
      });
      loadCategories();
      setNewCatName('');
      setIsAdding(false);
    } catch {
      // Local fallback
      setCategories(prev => [
        ...prev,
        { id: Date.now(), name: newCatName.trim(), type: newCatType }
      ]);
      setNewCatName('');
      setIsAdding(false);
    }
  };

  const getCategoryIcon = (name: string) => {
    const lower = name.toLowerCase();
    if (lower.includes('food') || lower.includes('dining')) return <Utensils size={18} className="text-[#00C853]" />;
    if (lower.includes('transport') || lower.includes('fuel')) return <Car size={18} className="text-[#F59E0B]" />;
    if (lower.includes('bill') || lower.includes('utility')) return <Zap size={18} className="text-[#EAB308]" />;
    if (lower.includes('shopping')) return <ShoppingBag size={18} className="text-[#EC4899]" />;
    if (lower.includes('entertain')) return <Film size={18} className="text-[#8B5CF6]" />;
    if (lower.includes('salary') || lower.includes('income')) return <Wallet size={18} className="text-[#00C853]" />;
    return <Layers size={18} className="text-[#06B6D4]" />;
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Categories</h1>
          <p className="text-sm text-[#8B949E] mt-0.5">Organise and categorise your cash flows</p>
        </div>

        <button
          onClick={() => setIsAdding(!isAdding)}
          className="flex items-center gap-1.5 bg-[#00C853] hover:bg-[#00B048] text-white text-xs font-semibold px-4 py-2 rounded-lg transition-colors shadow-sm self-start sm:self-auto"
        >
          <Plus size={16} />
          <span>Add Category</span>
        </button>
      </div>

      {isAdding && (
        <form onSubmit={handleAddCategory} className="bg-[#12161A] border border-[#1F242C] rounded-xl p-5 space-y-4">
          <h3 className="text-sm font-semibold text-white">Create New Category</h3>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-medium text-[#8B949E] mb-1.5">Category Name</label>
              <input
                type="text"
                required
                placeholder="e.g. Healthcare, Books, Subscriptions"
                value={newCatName}
                onChange={(e) => setNewCatName(e.target.value)}
                className="w-full bg-[#15191E] border border-[#1F242C] rounded-lg px-3.5 py-2 text-white text-xs focus:outline-none focus:border-[#00C853]"
              />
            </div>
            <div>
              <label className="block text-xs font-medium text-[#8B949E] mb-1.5">Type</label>
              <select
                value={newCatType}
                onChange={(e) => setNewCatType(e.target.value as any)}
                className="w-full bg-[#15191E] border border-[#1F242C] rounded-lg px-3.5 py-2 text-white text-xs focus:outline-none focus:border-[#00C853]"
              >
                <option value="EXPENSE">Expense</option>
                <option value="INCOME">Income</option>
              </select>
            </div>
          </div>
          <div className="flex justify-end gap-3 pt-2">
            <button
              type="button"
              onClick={() => setIsAdding(false)}
              className="text-xs text-[#8B949E] hover:text-white"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="bg-[#00C853] hover:bg-[#00B048] text-white text-xs font-semibold px-4 py-2 rounded-lg"
            >
              Save Category
            </button>
          </div>
        </form>
      )}

      {/* Grid of Categories */}
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
        {categories.map((c) => (
          <div
            key={c.id}
            className="bg-[#12161A] border border-[#1F242C] rounded-xl p-4 flex items-center justify-between hover:border-[#2D3540] transition-colors"
          >
            <div className="flex items-center gap-3">
              <div className="w-9 h-9 rounded-lg bg-[#181D23] border border-[#1F242C] flex items-center justify-center">
                {getCategoryIcon(c.name)}
              </div>
              <div>
                <div className="text-white text-xs font-semibold">{c.name}</div>
                <div className="text-[10px] text-[#8B949E] capitalize">{c.type.toLowerCase()}</div>
              </div>
            </div>
            <span
              className={`text-[10px] font-semibold px-2 py-0.5 rounded ${
                c.type === 'INCOME' ? 'bg-[#00C853]/15 text-[#00C853]' : 'bg-[#EF4444]/15 text-[#EF4444]'
              }`}
            >
              {c.type}
            </span>
          </div>
        ))}
      </div>
    </div>
  );
};
