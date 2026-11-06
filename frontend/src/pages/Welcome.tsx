import React from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  ArrowRight, 
  TrendingUp, 
  PieChart, 
  Shield, 
  ChevronDown, 
  Layers, 
  CheckCircle, 
  Sparkles,
  Lock,
  BarChart3
} from 'lucide-react';
import { AnimatedBackground } from '../components/AnimatedBackground';
import { DashboardPreview } from '../components/DashboardPreview';
import { useAuth } from '../context/AuthContext';

export const Welcome: React.FC = () => {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();

  const scrollToFeatures = () => {
    document.getElementById('features')?.scrollIntoView({ behavior: 'smooth' });
  };

  return (
    <div className="relative min-h-screen bg-[#0B0E11] text-white overflow-hidden selection:bg-[#00C853] selection:text-black">
      {/* Ambient Animated Layered Background */}
      <AnimatedBackground showMountains={true} showGlow={true} glowPosition="right" />

      {/* Main Content Overlay */}
      <div className="relative z-10 flex flex-col min-h-screen">
        {/* Navigation Bar */}
        <header className="w-full max-w-7xl mx-auto px-6 lg:px-12 py-6 flex items-center justify-between">
          {/* Logo */}
          <div 
            onClick={() => navigate('/')} 
            className="flex items-center gap-3 cursor-pointer group select-none"
          >
            <div className="w-9 h-9 rounded-full border-2 border-[#00C853] flex items-center justify-center bg-[#0B1A12] shadow-sm group-hover:border-[#10B981] transition-colors">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
                <path d="M7 17C7 10 12 5 18 5C18 12 13 17 7 17Z" fill="#00C853" />
                <path d="M7 17L15 9" stroke="#0B1A12" strokeWidth="2" strokeLinecap="round" />
              </svg>
            </div>
            <span className="text-white text-xl font-bold tracking-wider">MONETRA</span>
          </div>

          {/* Desktop Navigation Links */}
          <nav className="hidden md:flex items-center gap-8 text-sm font-medium text-[#8B949E]">
            <a href="#features" className="hover:text-white transition-colors">
              Features
            </a>
            <a href="#security" className="hover:text-white transition-colors">
              Security
            </a>
            <a href="#about" className="hover:text-white transition-colors">
              About
            </a>
          </nav>

          {/* Top-Right Action Buttons */}
          <div className="flex items-center gap-3">
            <button
              onClick={() => navigate('/login')}
              className="text-xs sm:text-sm font-medium text-[#D1D5DB] hover:text-white px-3 sm:px-4 py-2 rounded-lg hover:bg-[#151A20] transition-colors"
            >
              Sign In
            </button>
            <button
              onClick={() => navigate('/register')}
              className="text-xs sm:text-sm font-semibold text-white bg-[#00C853] hover:bg-[#00B048] active:scale-[0.98] px-4 sm:px-5 py-2 rounded-lg transition-all shadow-md shadow-[#00C853]/20"
            >
              Get Started
            </button>
          </div>
        </header>

        {/* Hero Section */}
        <section className="flex-1 flex flex-col justify-center w-full max-w-7xl mx-auto px-6 lg:px-12 pt-6 pb-20">
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-12 lg:gap-8 items-center">
            {/* Left Hero Column */}
            <div className="lg:col-span-7 space-y-6 text-left">
              {/* Badge */}
              <div className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-[#151D18] border border-[#00C853]/30 text-[#00C853] text-xs font-semibold backdrop-blur-md">
                <Sparkles size={13} className="text-[#00C853]" />
                <span>Next-Generation Personal Finance</span>
              </div>

              {/* Main Hero Headline */}
              <h1 className="text-4xl sm:text-5xl md:text-6xl font-extrabold text-white tracking-tight leading-[1.12]">
                Take Control of <br />
                Your <span className="text-[#00C853] underline decoration-[#00C853]/30 underline-offset-8">Finances</span>
              </h1>

              {/* Supporting Text */}
              <p className="text-base sm:text-lg text-[#8B949E] max-w-xl leading-relaxed">
                Effortlessly track income and expenses, establish disciplined monthly budgets, and uncover powerful cash flow analytics with bank-grade security.
              </p>

              {/* Action Buttons */}
              <div className="flex flex-wrap items-center gap-4 pt-2">
                <button
                  onClick={() => navigate(isAuthenticated ? '/dashboard' : '/register')}
                  className="flex items-center gap-2 bg-[#00C853] hover:bg-[#00B048] active:scale-[0.98] text-white font-semibold text-sm px-6 py-3.5 rounded-xl transition-all shadow-lg shadow-[#00C853]/25 hover:shadow-[#00C853]/40"
                >
                  <span>{isAuthenticated ? 'Open Dashboard' : 'Get Started'}</span>
                  <ArrowRight size={16} />
                </button>

                <button
                  onClick={() => navigate('/dashboard')}
                  className="flex items-center gap-2 bg-[#141A21] hover:bg-[#1A222C] active:scale-[0.98] border border-[#242C37] text-white font-semibold text-sm px-6 py-3.5 rounded-xl transition-all hover:border-[#384353]"
                >
                  <span>View Live Demo</span>
                </button>
              </div>

              {/* Quick Trust Highlights */}
              <div className="pt-4 flex flex-wrap items-center gap-6 text-xs text-[#8B949E]">
                <div className="flex items-center gap-2">
                  <CheckCircle size={14} className="text-[#00C853]" />
                  <span>Real-time Sync</span>
                </div>
                <div className="flex items-center gap-2">
                  <CheckCircle size={14} className="text-[#00C853]" />
                  <span>Zero Ads & Tracking</span>
                </div>
                <div className="flex items-center gap-2">
                  <CheckCircle size={14} className="text-[#00C853]" />
                  <span>Production Ready</span>
                </div>
              </div>
            </div>

            {/* Right Hero Column: Dashboard Preview */}
            <div className="lg:col-span-5 relative">
              <DashboardPreview />
            </div>
          </div>

          {/* Explore MONETRA Indicator */}
          <div className="mt-14 sm:mt-20 flex flex-col items-center justify-center cursor-pointer select-none" onClick={scrollToFeatures}>
            <span className="text-xs font-medium text-[#8B949E] hover:text-white transition-colors mb-2 tracking-wider uppercase">
              Explore MONETRA
            </span>
            <div className="text-[#00C853] animate-bounce-slow">
              <ChevronDown size={20} />
            </div>
          </div>
        </section>

        {/* Feature Highlights Section */}
        <section id="features" className="w-full max-w-7xl mx-auto px-6 lg:px-12 py-20 relative z-10 border-t border-[#1F2630]">
          <div className="text-center max-w-2xl mx-auto mb-14">
            <h2 className="text-2xl sm:text-3xl font-bold text-white tracking-tight">
              Designed for Total Financial Clarity
            </h2>
            <p className="text-sm text-[#8B949E] mt-3">
              Everything you need to master your personal economy without the complexity of traditional accounting software.
            </p>
          </div>

          {/* Three Feature Highlights Cards */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            {/* Feature 1: Track Spending */}
            <div className="bg-[#12161A]/90 backdrop-blur-md border border-[#1F2630] rounded-2xl p-6 hover:border-[#00C853]/40 transition-all group">
              <div className="w-12 h-12 rounded-xl bg-[#00C853]/15 border border-[#00C853]/30 flex items-center justify-center text-[#00C853] mb-5 group-hover:scale-105 transition-transform">
                <TrendingUp size={22} />
              </div>
              <h3 className="text-lg font-bold text-white mb-2">Track Spending</h3>
              <p className="text-sm text-[#8B949E] leading-relaxed">
                Log and categorize all your income and expenses seamlessly. View real-time balances and transaction timelines instantly.
              </p>
            </div>

            {/* Feature 2: Set Budgets */}
            <div className="bg-[#12161A]/90 backdrop-blur-md border border-[#1F2630] rounded-2xl p-6 hover:border-[#00C853]/40 transition-all group">
              <div className="w-12 h-12 rounded-xl bg-[#F59E0B]/15 border border-[#F59E0B]/30 flex items-center justify-center text-[#F59E0B] mb-5 group-hover:scale-105 transition-transform">
                <PieChart size={22} />
              </div>
              <h3 className="text-lg font-bold text-white mb-2">Set Budgets</h3>
              <p className="text-sm text-[#8B949E] leading-relaxed">
                Establish monthly spending thresholds per category. Monitor utilization bars with color-coded alerts to never overspend again.
              </p>
            </div>

            {/* Feature 3: Analyze & Grow */}
            <div className="bg-[#12161A]/90 backdrop-blur-md border border-[#1F2630] rounded-2xl p-6 hover:border-[#00C853]/40 transition-all group">
              <div className="w-12 h-12 rounded-xl bg-[#8B5CF6]/15 border border-[#8B5CF6]/30 flex items-center justify-center text-[#8B5CF6] mb-5 group-hover:scale-105 transition-transform">
                <BarChart3 size={22} />
              </div>
              <h3 className="text-lg font-bold text-white mb-2">Analyze & Grow</h3>
              <p className="text-sm text-[#8B949E] leading-relaxed">
                Deep visual analytics with multi-month cash flow trends, donut breakdowns, and net savings ratios to accelerate wealth creation.
              </p>
            </div>
          </div>
        </section>

        {/* Security & Architecture Highlights */}
        <section id="security" className="w-full max-w-7xl mx-auto px-6 lg:px-12 py-16 relative z-10 border-t border-[#1F2630]">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-10 items-center">
            <div>
              <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-[#121E16] text-[#00C853] text-xs font-semibold mb-4">
                <Shield size={14} />
                <span>Zero Compromise Security</span>
              </div>
              <h2 className="text-2xl sm:text-3xl font-bold text-white tracking-tight">
                Your financial data belongs exclusively to you
              </h2>
              <p className="text-sm text-[#8B949E] mt-3 leading-relaxed">
                MONETRA uses industry-standard BCrypt cryptographic password hashing, stateless JSON Web Tokens (JWT), normalized relational persistence on MySQL, and HTTPS transport encryption.
              </p>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div className="bg-[#141A21] border border-[#212832] rounded-xl p-4">
                <div className="text-white font-bold text-xl mb-1">BCrypt</div>
                <div className="text-xs text-[#8B949E]">Irreversible one-way salted password encryption</div>
              </div>
              <div className="bg-[#141A21] border border-[#212832] rounded-xl p-4">
                <div className="text-white font-bold text-xl mb-1">JWT Bearer</div>
                <div className="text-xs text-[#8B949E]">Cryptographically signed, stateless authentication</div>
              </div>
              <div className="bg-[#141A21] border border-[#212832] rounded-xl p-4">
                <div className="text-white font-bold text-xl mb-1">DECIMAL SQL</div>
                <div className="text-xs text-[#8B949E]">Exact precision currency representation without rounding errors</div>
              </div>
              <div className="bg-[#141A21] border border-[#212832] rounded-xl p-4">
                <div className="text-white font-bold text-xl mb-1">Spring + Flyway</div>
                <div className="text-xs text-[#8B949E]">Deterministic database migrations and enterprise robustness</div>
              </div>
            </div>
          </div>
        </section>

        {/* Footer */}
        <footer id="about" className="w-full border-t border-[#1F2630] bg-[#0E1217]/80 backdrop-blur-md py-8 px-6 lg:px-12 mt-auto">
          <div className="max-w-7xl mx-auto flex flex-col sm:flex-row items-center justify-between gap-4 text-xs text-[#8B949E]">
            <div className="flex items-center gap-2">
              <div className="w-5 h-5 rounded-full border border-[#00C853] flex items-center justify-center bg-[#0B1A12]">
                <div className="w-2 h-2 rounded-full bg-[#00C853]" />
              </div>
              <span className="text-white font-bold tracking-wider">MONETRA</span>
              <span>— Personal Finance Management System</span>
            </div>

            <div className="flex items-center gap-6">
              <span className="hover:text-white transition-colors cursor-pointer" onClick={() => navigate('/login')}>
                Sign In
              </span>
              <span className="hover:text-white transition-colors cursor-pointer" onClick={() => navigate('/register')}>
                Register
              </span>
              <span className="hover:text-white transition-colors cursor-pointer" onClick={() => navigate('/dashboard')}>
                Dashboard
              </span>
            </div>

            <div>
              &copy; {new Date().getFullYear()} MONETRA. All rights reserved.
            </div>
          </div>
        </footer>
      </div>
    </div>
  );
};
