/**
 * Format currency in Indian numbering system matching MONETRA's visual reference (₹4,82,300)
 */
export function formatCurrency(amount: number | string | undefined | null): string {
  if (amount === undefined || amount === null || isNaN(Number(amount))) {
    return '₹0';
  }
  const num = typeof amount === 'string' ? parseFloat(amount) : amount;
  
  // Format with INR locale
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    maximumFractionDigits: 0,
    minimumFractionDigits: 0,
  }).format(num).replace('INR', '₹').trim();
}

/**
 * Format currency with explicit +/- sign for transactions
 */
export function formatSignedCurrency(amount: number, type: 'INCOME' | 'EXPENSE'): string {
  const formatted = formatCurrency(Math.abs(amount));
  return type === 'INCOME' ? `+ ${formatted}` : `- ${formatted}`;
}

/**
 * Format dates e.g. "Nov 6, 2026"
 */
export function formatDate(dateStr: string | Date | undefined): string {
  if (!dateStr) return '';
  const date = typeof dateStr === 'string' ? new Date(dateStr) : dateStr;
  if (isNaN(date.getTime())) return String(dateStr);
  return date.toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric'
  });
}

