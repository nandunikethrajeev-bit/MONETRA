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
  Tag, 
  Edit2, 
  Trash2, 
  Lock, 
  AlertCircle, 
  CheckCircle2, 
  Loader2,
  X
} from 'lucide-react';
import { api } from '../api/client';
import { Category } from '../types';

export const Categories: React.FC = () => {
  const [categories, setCategories] = useState<Category[]>([]);
  const [typeFilter, setTypeFilter] = useState<'ALL' | 'EXPENSE' | 'INCOME'>('ALL');
  const [isLoading, setIsLoading] = useState<boolean>(true);

  // Form states
  const [isFormOpen, setIsFormOpen] = useState<boolean>(false);
  const [editingCategory, setEditingCategory] = useState<Category | null>(null);
  const [catName, setCatName] = useState<string>('');
  const [catType, setCatType] = useState<'INCOME' | 'EXPENSE'>('EXPENSE');
  const [catColor, setCatColor] = useState<string>('#00C853');
  const [catIcon, setCatIcon] = useState<string>('tag');
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);

  // Alerts
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  // Deletion modal
  const [deletingId, setDeletingId] = useState<number | null>(null);
  const [isDeleting, setIsDeleting] = useState<boolean>(false);

  const loadCategories = async () => {
    setIsLoading(true);
    try {
      const data = await api.getCategories();
      setCategories(data || []);
    } catch (err: any) {
      console.error('Failed to load categories:', err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadCategories();
  }, []);

  const openAddForm = () => {
    setEditingCategory(null);
    setCatName('');
    setCatType('EXPENSE');
    setCatColor('#00C853');
    setCatIcon('tag');
    setErrorMessage(null);
    setIsFormOpen(true);
  };

  const openEditForm = (category: Category) => {
    setEditingCategory(category);
    setCatName(category.name);
    setCatType(category.type);
    setCatColor(category.color || '#00C853');
    setCatIcon(category.icon || 'tag');
    setErrorMessage(null);
    setIsFormOpen(true);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!catName.trim()) {
      setErrorMessage('Please enter a valid category name.');
      return;
    }

    setIsSubmitting(true);
    setErrorMessage(null);

    try {
      if (editingCategory) {
        await api.updateCategory(editingCategory.id, {
          name: catName.trim(),
          type: catType,
          color: catColor,
          icon: catIcon,
        });
        setSuccessMessage(`Category "${catName}" updated successfully.`);
      } else {
        await api.createCategory({
          name: catName.trim(),
          type: catType,
          color: catColor,
          icon: catIcon,
        });
        setSuccessMessage(`Custom category "${catName}" created.`);
      }

      setIsFormOpen(false);
      loadCategories();
      setTimeout(() => setSuccessMessage(null), 3500);
    } catch (err: any) {
      setErrorMessage(err.message || 'Failed to save category.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDelete = async (id: number) => {
    setIsDeleting(true);
    setErrorMessage(null);
    try {
      await api.deleteCategory(id);
      setDeletingId(null);
      setSuccessMessage('Category deleted successfully.');
      loadCategories();
      setTimeout(() => setSuccessMessage(null), 3500);
    } catch (err: any) {
      setDeletingId(null);
      setErrorMessage(err.message || 'Failed to delete category.');
      setTimeout(() => setErrorMessage(null), 6000);
    } finally {
      setIsDeleting(false);
    }
  };

  const filteredCategories = categories.filter(c => {
    if (typeFilter === 'ALL') return true;
    return c.type === typeFilter;
  });

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
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Categories</h1>
          <p className="text-sm text-[#8B949E] mt-0.5">Organise and categorise your cash flows</p>
        </div>

        <button
          onClick={openAddForm}
          className="flex items-center gap-1.5 bg-[#00C853] hover:bg-[#00B048] active:scale-[0.98] text-white text-xs font-semibold px-4 py-2.5 rounded-xl transition-all shadow-md shadow-[#00C853]/15 self-start sm:self-auto"
        >
          <Plus size={16} />
          <span>Add Custom Category</span>
        </button>
      </div>

      {/* Feedback Messages */}
      {errorMessage && (
        <div className="flex items-start gap-2.5 p-3.5 text-xs bg-[#EF4444]/15 border border-[#EF4444]/30 text-[#F87171] rounded-xl animate-in fade-in">
          <AlertCircle size={16} className="shrink-0 mt-0.5 text-[#EF4444]" />
          <div className="flex-1 leading-relaxed">{errorMessage}</div>
          <button onClick={() => setErrorMessage(null)} className="text-[#8B949E] hover:text-white">
            <X size={14} />
          </button>
        </div>
      )}

      {successMessage && (
        <div className="flex items-start gap-2.5 p-3.5 text-xs bg-[#00C853]/15 border border-[#00C853]/30 text-[#00C853] rounded-xl animate-in fade-in">
          <CheckCircle2 size={16} className="shrink-0 mt-0.5 text-[#00C853]" />
          <div className="flex-1 leading-relaxed">{successMessage}</div>
          <button onClick={() => setSuccessMessage(null)} className="text-[#8B949E] hover:text-white">
            <X size={14} />
          </button>
        </div>
      )}

      {/* Filter Tabs */}
      <div className="flex items-center gap-2 p-1 bg-[#12161A] border border-[#1F242C] rounded-xl w-fit">
        {(['ALL', 'EXPENSE', 'INCOME'] as const).map((t) => (
          <button
            key={t}
            onClick={() => setTypeFilter(t)}
            className={`px-3.5 py-1.5 text-xs rounded-lg font-medium transition-all ${
              typeFilter === t
                ? 'bg-[#00C853] text-white font-semibold shadow'
                : 'text-[#8B949E] hover:text-white'
            }`}
          >
            {t === 'ALL' ? 'All Categories' : t === 'EXPENSE' ? 'Expense Categories' : 'Income Categories'}
          </button>
        ))}
      </div>

      {/* Categories Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
        {isLoading ? (
          <div className="col-span-full py-12 text-center text-[#8B949E]">
            <Loader2 size={18} className="animate-spin inline-block text-[#00C853] mr-2" />
            <span>Loading categories...</span>
          </div>
        ) : filteredCategories.map((c) => {
          const isSystemDefault = c.isSystem || c.userId === null || c.userId === undefined;

          return (
            <div
              key={c.id}
              className="bg-[#12161A] border border-[#1F242C] rounded-2xl p-4 flex flex-col justify-between hover:border-[#2D3540] transition-colors group"
            >
              <div className="flex items-center justify-between mb-3">
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 rounded-xl bg-[#181D23] border border-[#1F242C] flex items-center justify-center">
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

              {/* Footer row: System lock vs Custom Actions */}
              <div className="flex items-center justify-between pt-2 border-t border-[#1B222B] text-[11px] text-[#6E7681]">
                {isSystemDefault ? (
                  <div className="flex items-center gap-1.5 text-[#6E7681]">
                    <Lock size={12} />
                    <span>System Default</span>
                  </div>
                ) : (
                  <div className="flex items-center justify-between w-full">
                    <span className="text-[#00C853] font-medium text-[10px]">Custom</span>
                    <div className="flex items-center gap-1">
                      <button
                        title="Edit category"
                        onClick={() => openEditForm(c)}
                        className="p-1 text-[#8B949E] hover:text-white rounded hover:bg-[#1E252E] transition-colors"
                      >
                        <Edit2 size={13} />
                      </button>
                      <button
                        title="Delete category"
                        onClick={() => setDeletingId(c.id)}
                        className="p-1 text-[#8B949E] hover:text-[#EF4444] rounded hover:bg-[#EF4444]/10 transition-colors"
                      >
                        <Trash2 size={13} />
                      </button>
                    </div>
                  </div>
                )}
              </div>
            </div>
          );
        })}
      </div>

      {/* Add / Edit Category Modal */}
      {isFormOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/75 backdrop-blur-sm p-4 animate-in fade-in duration-150">
          <div className="w-full max-w-md bg-[#12161A] border border-[#1F242C] rounded-2xl p-6 shadow-2xl space-y-4">
            <div className="flex items-center justify-between border-b border-[#1F242C] pb-3">
              <h3 className="text-white font-semibold text-base">
                {editingCategory ? 'Edit Category' : 'Create Custom Category'}
              </h3>
              <button onClick={() => setIsFormOpen(false)} className="text-[#8B949E] hover:text-white">
                <X size={16} />
              </button>
            </div>

            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-xs font-medium text-[#8B949E] mb-1.5">Category Name</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Healthcare, Freelance, Subscriptions"
                  value={catName}
                  onChange={(e) => setCatName(e.target.value)}
                  className="w-full bg-[#15191E] border border-[#1F242C] rounded-xl px-3.5 py-2.5 text-white text-xs focus:outline-none focus:border-[#00C853]"
                />
              </div>

              <div>
                <label className="block text-xs font-medium text-[#8B949E] mb-1.5">Flow Type</label>
                <select
                  value={catType}
                  onChange={(e) => setCatType(e.target.value as any)}
                  className="w-full bg-[#15191E] border border-[#1F242C] rounded-xl px-3.5 py-2.5 text-white text-xs focus:outline-none focus:border-[#00C853]"
                >
                  <option value="EXPENSE">Expense</option>
                  <option value="INCOME">Income</option>
                </select>
              </div>

              <div className="flex justify-end gap-3 pt-3">
                <button
                  type="button"
                  onClick={() => setIsFormOpen(false)}
                  className="px-4 py-2 text-xs font-medium text-[#8B949E] hover:text-white"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={isSubmitting}
                  className="flex items-center gap-1.5 bg-[#00C853] hover:bg-[#00B048] text-white text-xs font-semibold px-5 py-2.5 rounded-xl shadow"
                >
                  {isSubmitting && <Loader2 size={13} className="animate-spin" />}
                  <span>{isSubmitting ? 'Saving...' : (editingCategory ? 'Save Changes' : 'Create Category')}</span>
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Delete Category Confirmation Dialog */}
      {deletingId && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/75 backdrop-blur-sm p-4 animate-in fade-in duration-150">
          <div className="w-full max-w-sm bg-[#12161A] border border-[#1F242C] rounded-2xl p-6 shadow-2xl space-y-4">
            <div className="flex items-center gap-3 text-[#EF4444]">
              <AlertCircle size={22} />
              <h3 className="text-white font-semibold text-base">Delete Custom Category</h3>
            </div>
            <p className="text-xs text-[#8B949E] leading-relaxed">
              Are you sure? Custom categories linked to existing transactions cannot be deleted until those transactions are re-assigned.
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
                onClick={() => handleDelete(deletingId)}
                className="flex items-center gap-1.5 px-4 py-2 bg-[#EF4444] hover:bg-[#DC2626] text-white font-semibold text-xs rounded-xl shadow"
              >
                {isDeleting && <Loader2 size={13} className="animate-spin" />}
                <span>{isDeleting ? 'Deleting...' : 'Confirm Delete'}</span>
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
