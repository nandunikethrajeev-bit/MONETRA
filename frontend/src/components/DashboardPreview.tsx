import React from 'react';
import { 
  ArrowUpRight, 
  ArrowDownRight, 
  Wallet, 
  TrendingUp, 
  CheckCircle2, 
  ShieldCheck 
} from 'lucide-react';

export const DashboardPreview: React.FC = () => {
  return (
    <div className="relative w-full max-w-lg mx-auto lg:max-w-none group">
      {/* Ambient Emerald Backlight Glow */}
      <div className="absolute -inset-1.5 bg-gradient-to-r from-[#00C853]/25 via-[#10B981]/15 to-[#00C853]/20 rounded-2xl blur-xl opacity-75 group-hover:opacity-100 transition-opacity duration-700 pointer-events-none" />

      {/* Main Preview Container */}
      <div className="relative bg-[#12161A]/95 backdrop-blur-md border border-[#1F2630] rounded-2xl p-5 shadow-2xl transition-transform duration-500 hover:scale-[1.01]">
        {/* Top Header of Preview */}
        <div className="flex items-center justify-between pb-4 mb-4 border-b border-[#1B222B]">
          <div className="flex items-center gap-2.5">
            <div className="w-6 h-6 rounded-full border border-[#00C853] flex items-center justify-center bg-[#0B1A12]">
              <div className="w-2.5 h-2.5 rounded-full bg-[#00C853]" />
            </div>
            <span className="text-white text-xs font-bold tracking-wider">MONETRA</span>
            <span className="text-[10px] px-2 py-0.5 rounded-full bg-[#00C853]/15 text-[#00C853] font-semibold border border-[#00C853]/30">
              PRO
            </span>
          </div>

          <div className="flex items-center gap-2">
            <span className="text-[11px] text-[#8B949E] bg-[#161B22] px-2.5 py-1 rounded-md border border-[#21262D]">
              Nov 2026
            </span>
            <div className="w-2 h-2 rounded-full bg-[#00C853] animate-pulse" />
          </div>
        </div>

        {/* 2 Metric Mini-Cards */}
        <div className="grid grid-cols-2 gap-3 mb-4">
          <div className="bg-[#151A20] border border-[#212832] rounded-xl p-3.5">
            <div className="flex items-center justify-between text-[11px] text-[#8B949E]">
              <span>Total Balance</span>
              <Wallet size={12} className="text-[#00C853]" />
            </div>
            <div className="text-lg font-bold text-white mt-1.5 tracking-tight">₹4,82,300</div>
            <div className="flex items-center gap-1 text-[10px] text-[#00C853] font-medium mt-0.5">
              <ArrowUpRight size={11} />
              <span>+12.4% vs last mo</span>
            </div>
          </div>

          <div className="bg-[#151A20] border border-[#212832] rounded-xl p-3.5">
            <div className="flex items-center justify-between text-[11px] text-[#8B949E]">
              <span>Net Savings</span>
              <TrendingUp size={12} className="text-[#00C853]" />
            </div>
            <div className="text-lg font-bold text-white mt-1.5 tracking-tight">₹77,250</div>
            <div className="flex items-center gap-1 text-[10px] text-[#00C853] font-medium mt-0.5">
              <ArrowUpRight size={11} />
              <span>+18.7% saved</span>
            </div>
          </div>
        </div>

        {/* Mini Chart Area */}
        <div className="bg-[#151A20] border border-[#212832] rounded-xl p-3.5 mb-4">
          <div className="flex items-center justify-between text-[11px] mb-2.5">
            <span className="text-white font-semibold">Income vs Expenses</span>
            <div className="flex items-center gap-3 text-[10px]">
              <span className="flex items-center gap-1 text-[#8B949E]">
                <span className="w-2 h-2 rounded-full bg-[#00C853]" /> Income
              </span>
              <span className="flex items-center gap-1 text-[#8B949E]">
                <span className="w-2 h-2 rounded-full bg-[#EF4444]" /> Expenses
              </span>
            </div>
          </div>

          {/* SVG Line / Area Graph */}
          <div className="h-28 w-full">
            <svg viewBox="0 0 360 100" className="w-full h-full overflow-visible">
              <defs>
                <linearGradient id="prevGreenGrad" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="0%" stopColor="#00C853" stopOpacity="0.35" />
                  <stop offset="100%" stopColor="#00C853" stopOpacity="0.0" />
                </linearGradient>
                <linearGradient id="prevRedGrad" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="0%" stopColor="#EF4444" stopOpacity="0.2" />
                  <stop offset="100%" stopColor="#EF4444" stopOpacity="0.0" />
                </linearGradient>
              </defs>

              {/* Grid lines */}
              <line x1="0" y1="25" x2="360" y2="25" stroke="#212832" strokeDasharray="3 3" />
              <line x1="0" y1="65" x2="360" y2="65" stroke="#212832" strokeDasharray="3 3" />

              {/* Income Area & Line */}
              <path
                d="M 0,75 Q 60,65 120,50 T 240,30 T 360,15 L 360,100 L 0,100 Z"
                fill="url(#prevGreenGrad)"
              />
              <path
                d="M 0,75 Q 60,65 120,50 T 240,30 T 360,15"
                fill="none"
                stroke="#00C853"
                strokeWidth="2.5"
                strokeLinecap="round"
              />

              {/* Expenses Area & Line */}
              <path
                d="M 0,85 Q 60,70 120,78 T 240,65 T 360,58 L 360,100 L 0,100 Z"
                fill="url(#prevRedGrad)"
              />
              <path
                d="M 0,85 Q 60,70 120,78 T 240,65 T 360,58"
                fill="none"
                stroke="#EF4444"
                strokeWidth="2"
                strokeLinecap="round"
              />

              {/* Current Month Highlight Node */}
              <circle cx="360" cy="15" r="4" fill="#00C853" stroke="#12161A" strokeWidth="2" />
            </svg>
          </div>

          <div className="flex justify-between text-[10px] text-[#6E7681] pt-1">
            <span>Jun</span>
            <span>Jul</span>
            <span>Aug</span>
            <span>Sep</span>
            <span>Oct</span>
            <span className="text-[#00C853] font-semibold">Nov</span>
          </div>
        </div>

        {/* Recent Activity Pill */}
        <div className="flex items-center justify-between bg-[#151A20] border border-[#212832] rounded-xl px-3.5 py-2.5 text-xs">
          <div className="flex items-center gap-2.5">
            <div className="w-7 h-7 rounded-lg bg-[#00C853]/15 flex items-center justify-center text-[#00C853]">
              <CheckCircle2 size={14} />
            </div>
            <div>
              <div className="text-white font-medium text-[11px]">Salary Credited</div>
              <div className="text-[#8B949E] text-[10px]">Today, 10:30 AM</div>
            </div>
          </div>
          <span className="text-[#00C853] font-bold text-xs">+ ₹1,20,000</span>
        </div>
      </div>

      {/* Floating Badge 1: Bank-Grade Security */}
      <div className="hidden sm:flex absolute -bottom-4 -left-4 items-center gap-2 bg-[#141A21] border border-[#242C37] rounded-xl px-3.5 py-2 shadow-xl backdrop-blur-md">
        <ShieldCheck size={16} className="text-[#00C853]" />
        <span className="text-xs font-medium text-white">Bank-Grade 256-bit AES</span>
      </div>

      {/* Floating Badge 2: Budget Controlled */}
      <div className="hidden sm:flex absolute -top-3 -right-3 items-center gap-2 bg-[#141A21] border border-[#242C37] rounded-xl px-3.5 py-2 shadow-xl backdrop-blur-md">
        <span className="w-2 h-2 rounded-full bg-[#00C853]" />
        <span className="text-xs font-medium text-white">Budgets 100% on Track</span>
      </div>
    </div>
  );
};
