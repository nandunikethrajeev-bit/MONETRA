import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { 
  Mail, 
  Lock, 
  Eye, 
  EyeOff, 
  User as UserIcon, 
  ArrowLeft, 
  Loader2, 
  CheckCircle2, 
  AlertCircle 
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { AnimatedBackground } from '../components/AnimatedBackground';

interface AuthProps {
  initialMode?: 'login' | 'register';
}

export const Auth: React.FC<AuthProps> = ({ initialMode = 'login' }) => {
  const location = useLocation();
  const navigate = useNavigate();
  const { login, register, isAuthenticated } = useAuth();

  // Mode state: 'login' or 'register'
  const isRegisterRoute = location.pathname === '/register' || initialMode === 'register';
  const [mode, setMode] = useState<'login' | 'register'>(isRegisterRoute ? 'register' : 'login');

  // Shared / persistent field values between tabs
  const [fullName, setFullName] = useState<string>('');
  const [email, setEmail] = useState<string>('');
  const [password, setPassword] = useState<string>('');
  const [confirmPassword, setConfirmPassword] = useState<string>('');
  const [rememberMe, setRememberMe] = useState<boolean>(true);

  // UI States
  const [showPassword, setShowPassword] = useState<boolean>(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState<boolean>(false);
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [isTransitioning, setIsTransitioning] = useState<boolean>(false);

  // Sync mode with route if changed externally
  useEffect(() => {
    if (location.pathname === '/register' && mode !== 'register') {
      setMode('register');
    } else if (location.pathname === '/login' && mode !== 'login') {
      setMode('login');
    }
  }, [location.pathname]);

  // If already authenticated and not actively submitting, offer redirect
  useEffect(() => {
    if (isAuthenticated && !isSubmitting) {
      // Allow navigation or redirect to dashboard
    }
  }, [isAuthenticated, isSubmitting]);

  // Fluid transition between login and register
  const switchMode = (targetMode: 'login' | 'register') => {
    if (mode === targetMode || isSubmitting) return;
    setErrorMessage(null);
    setSuccessMessage(null);
    setIsTransitioning(true);

    setTimeout(() => {
      setMode(targetMode);
      window.history.replaceState(null, '', targetMode === 'register' ? '/register' : '/login');
      setIsTransitioning(false);
    }, 150);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (isSubmitting) return;

    setErrorMessage(null);
    setSuccessMessage(null);

    // Validation
    const cleanEmail = email.trim();
    if (!cleanEmail || !cleanEmail.includes('@')) {
      setErrorMessage('Please enter a valid email address.');
      return;
    }

    if (!password || password.length < 6) {
      setErrorMessage('Password must be at least 6 characters.');
      return;
    }

    if (mode === 'register') {
      if (!fullName.trim()) {
        setErrorMessage('Please enter your full name.');
        return;
      }
      if (password !== confirmPassword) {
        setErrorMessage('Passwords do not match. Please re-enter.');
        return;
      }
    }

    setIsSubmitting(true);

    try {
      if (mode === 'register') {
        const username = cleanEmail.split('@')[0].replace(/[^a-zA-Z0-9_]/g, '_');
        await register({
          fullName: fullName.trim(),
          email: cleanEmail,
          username,
          password,
          currency: 'INR',
        }, rememberMe);
        setSuccessMessage('Account created successfully! Redirecting...');
      } else {
        await login({
          email: cleanEmail,
          password,
        }, rememberMe);
        setSuccessMessage('Authentication successful! Opening dashboard...');
      }

      // Smooth redirection to dashboard
      setTimeout(() => {
        navigate('/dashboard');
      }, 700);
    } catch (err: any) {
      console.error('Authentication error:', err);
      const msg = err.message || 'Authentication failed. Please check your credentials.';
      setErrorMessage(msg);
      setIsSubmitting(false);
    }
  };

  return (
    <div className="relative min-h-screen w-full flex bg-[#0B0E11] text-white overflow-hidden selection:bg-[#00C853] selection:text-black">
      {/* Background Ambience */}
      <AnimatedBackground showMountains={false} showGlow={true} glowPosition="center" />

      {/* Main Container */}
      <div className="relative z-10 w-full flex flex-col lg:flex-row min-h-screen">
        {/* Left Side: Animated Form Card */}
        <div className="w-full lg:w-1/2 flex flex-col justify-between px-6 sm:px-12 md:px-20 py-8 lg:py-12 max-w-xl mx-auto lg:mx-0">
          {/* Top Brand & Back to Home */}
          <div className="flex items-center justify-between">
            <div 
              onClick={() => navigate('/')} 
              className="flex items-center gap-3 cursor-pointer group select-none"
            >
              <div className="w-8 h-8 rounded-full border-2 border-[#00C853] flex items-center justify-center bg-[#0B1A12] shadow-sm group-hover:border-[#10B981] transition-colors">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                  <path d="M7 17C7 10 12 5 18 5C18 12 13 17 7 17Z" fill="#00C853" />
                  <path d="M7 17L15 9" stroke="#0B1A12" strokeWidth="2" strokeLinecap="round" />
                </svg>
              </div>
              <span className="text-white text-lg font-bold tracking-wider">MONETRA</span>
            </div>

            <button
              onClick={() => navigate('/')}
              className="flex items-center gap-1.5 text-xs text-[#8B949E] hover:text-white transition-colors px-3 py-1.5 rounded-lg hover:bg-[#151A20]"
            >
              <ArrowLeft size={14} />
              <span>Back to Home</span>
            </button>
          </div>

          {/* Form Container with Smooth Crossfade & Slide */}
          <div className="my-auto py-8">
            <div
              className={`transition-all duration-300 transform ${
                isTransitioning
                  ? 'opacity-0 translate-y-2 scale-[0.98]'
                  : 'opacity-100 translate-y-0 scale-100'
              }`}
            >
              {/* Heading */}
              <h1 className="text-3xl sm:text-4xl font-extrabold text-white tracking-tight mb-2">
                {mode === 'login' ? 'Welcome Back' : 'Create Your Account'}
              </h1>
              <p className="text-sm text-[#8B949E] mb-6">
                {mode === 'login'
                  ? 'Sign in to manage your finances'
                  : 'Master your income, expenses, and wealth goals'}
              </p>

              {/* Error Alert */}
              {errorMessage && (
                <div className="mb-5 flex items-start gap-2.5 p-3.5 text-xs bg-[#EF4444]/15 border border-[#EF4444]/30 text-[#F87171] rounded-xl animate-in fade-in duration-200">
                  <AlertCircle size={16} className="shrink-0 mt-0.5 text-[#EF4444]" />
                  <div className="flex-1 leading-relaxed">{errorMessage}</div>
                </div>
              )}

              {/* Success Alert */}
              {successMessage && (
                <div className="mb-5 flex items-start gap-2.5 p-3.5 text-xs bg-[#00C853]/15 border border-[#00C853]/30 text-[#00C853] rounded-xl animate-in fade-in duration-200">
                  <CheckCircle2 size={16} className="shrink-0 mt-0.5 text-[#00C853]" />
                  <div className="flex-1 leading-relaxed">{successMessage}</div>
                </div>
              )}

              {/* Form */}
              <form onSubmit={handleSubmit} className="space-y-4">
                {/* Full Name Field (Register Mode only) */}
                {mode === 'register' && (
                  <div className="space-y-1">
                    <label className="block text-xs font-medium text-[#8B949E]">Full Name</label>
                    <div className="relative">
                      <UserIcon size={17} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-[#6E7681]" />
                      <input
                        type="text"
                        required
                        placeholder="Niketh Rajeev"
                        value={fullName}
                        onChange={(e) => setFullName(e.target.value)}
                        className="w-full bg-[#13171C] border border-[#21262D] rounded-xl pl-11 pr-4 py-2.5 text-white placeholder-[#586069] text-sm focus:outline-none focus:border-[#00C853] focus:ring-1 focus:ring-[#00C853]/30 transition-all"
                      />
                    </div>
                  </div>
                )}

                {/* Email Address */}
                <div className="space-y-1">
                  <label className="block text-xs font-medium text-[#8B949E]">Email Address</label>
                  <div className="relative">
                    <Mail size={17} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-[#6E7681]" />
                    <input
                      type="email"
                      required
                      placeholder="name@example.com"
                      value={email}
                      onChange={(e) => setEmail(e.target.value)}
                      className="w-full bg-[#13171C] border border-[#21262D] rounded-xl pl-11 pr-4 py-2.5 text-white placeholder-[#586069] text-sm focus:outline-none focus:border-[#00C853] focus:ring-1 focus:ring-[#00C853]/30 transition-all"
                    />
                  </div>
                </div>

                {/* Password */}
                <div className="space-y-1">
                  <label className="block text-xs font-medium text-[#8B949E]">Password</label>
                  <div className="relative">
                    <Lock size={17} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-[#6E7681]" />
                    <input
                      type={showPassword ? 'text' : 'password'}
                      required
                      placeholder="••••••••"
                      value={password}
                      onChange={(e) => setPassword(e.target.value)}
                      className="w-full bg-[#13171C] border border-[#21262D] rounded-xl pl-11 pr-11 py-2.5 text-white placeholder-[#586069] text-sm focus:outline-none focus:border-[#00C853] focus:ring-1 focus:ring-[#00C853]/30 transition-all"
                    />
                    <button
                      type="button"
                      onClick={() => setShowPassword(!showPassword)}
                      className="absolute right-3.5 top-1/2 -translate-y-1/2 text-[#6E7681] hover:text-white transition-colors"
                      title={showPassword ? 'Hide password' : 'Show password'}
                    >
                      {showPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                    </button>
                  </div>
                </div>

                {/* Confirm Password (Register Mode only) */}
                {mode === 'register' && (
                  <div className="space-y-1">
                    <label className="block text-xs font-medium text-[#8B949E]">Confirm Password</label>
                    <div className="relative">
                      <Lock size={17} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-[#6E7681]" />
                      <input
                        type={showConfirmPassword ? 'text' : 'password'}
                        required
                        placeholder="••••••••"
                        value={confirmPassword}
                        onChange={(e) => setConfirmPassword(e.target.value)}
                        className="w-full bg-[#13171C] border border-[#21262D] rounded-xl pl-11 pr-11 py-2.5 text-white placeholder-[#586069] text-sm focus:outline-none focus:border-[#00C853] focus:ring-1 focus:ring-[#00C853]/30 transition-all"
                      />
                      <button
                        type="button"
                        onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                        className="absolute right-3.5 top-1/2 -translate-y-1/2 text-[#6E7681] hover:text-white transition-colors"
                        title={showConfirmPassword ? 'Hide password' : 'Show password'}
                      >
                        {showConfirmPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                      </button>
                    </div>
                  </div>
                )}

                {/* Remember Me Option (Login Mode) */}
                {mode === 'login' && (
                  <div className="flex items-center justify-between text-xs py-1">
                    <label className="flex items-center gap-2 cursor-pointer text-[#8B949E] select-none hover:text-white transition-colors">
                      <input
                        type="checkbox"
                        checked={rememberMe}
                        onChange={(e) => setRememberMe(e.target.checked)}
                        className="w-4 h-4 rounded bg-[#13171C] border-[#21262D] text-[#00C853] focus:ring-0 focus:ring-offset-0 cursor-pointer accent-[#00C853]"
                      />
                      <span>Remember me</span>
                    </label>

                    <button
                      type="button"
                      onClick={() => setErrorMessage('Password recovery is managed by your administrator. Contact support or use registered credentials.')}
                      className="text-[#00C853] hover:underline font-medium"
                    >
                      Forgot password?
                    </button>
                  </div>
                )}

                {/* Primary Action Button */}
                <button
                  type="submit"
                  disabled={isSubmitting}
                  className="w-full flex items-center justify-center gap-2 bg-[#00C853] hover:bg-[#00B048] active:scale-[0.98] disabled:opacity-60 disabled:cursor-not-allowed text-white font-semibold py-3 px-4 rounded-xl text-sm transition-all shadow-lg shadow-[#00C853]/20 mt-3"
                >
                  {isSubmitting ? (
                    <>
                      <Loader2 size={16} className="animate-spin" />
                      <span>{mode === 'login' ? 'Authenticating...' : 'Creating Profile...'}</span>
                    </>
                  ) : (
                    <span>{mode === 'login' ? 'Sign In' : 'Create Account'}</span>
                  )}
                </button>
              </form>

              {/* Mode Toggle Switch Navigation */}
              <div className="mt-8 text-center text-xs text-[#8B949E]">
                {mode === 'login' ? (
                  <span>
                    Don't have an account?{' '}
                    <button
                      type="button"
                      onClick={() => switchMode('register')}
                      className="text-[#00C853] font-semibold hover:underline transition-colors"
                    >
                      Create an account
                    </button>
                  </span>
                ) : (
                  <span>
                    Already have an account?{' '}
                    <button
                      type="button"
                      onClick={() => switchMode('login')}
                      className="text-[#00C853] font-semibold hover:underline transition-colors"
                    >
                      Sign In
                    </button>
                  </span>
                )}
              </div>
            </div>
          </div>

          {/* Footer */}
          <div className="text-[11px] text-[#586069] flex items-center justify-between">
            <span>&copy; {new Date().getFullYear()} MONETRA Inc.</span>
            <span>Bank-Grade Encryption</span>
          </div>
        </div>

        {/* Right Side: Atmospheric Mountain Art & Moving Mist Layer */}
        <div className="hidden lg:block lg:w-1/2 relative bg-[#090D0B] overflow-hidden border-l border-[#1B222B]">
          <img
            src="/mountain-bg.svg"
            alt="Dark Atmospheric Mountains"
            className="w-full h-full object-cover object-center filter brightness-90 contrast-105"
          />

          {/* Subtle Emerald Ambient Light Shifter */}
          <div className="absolute inset-0 bg-gradient-to-t from-[#0B0E11] via-transparent to-[#0B0E11]/40" />

          {/* Continuous Floating Mist Accent on Mountain Ridge */}
          <div className="absolute bottom-[20%] left-[-20%] right-[-20%] h-48 bg-[#00C853]/8 blur-[60px] animate-mist-1 pointer-events-none" />

          {/* Right Side Atmospheric Quote */}
          <div className="absolute bottom-12 left-12 right-12 p-6 rounded-2xl bg-[#0D1210]/70 backdrop-blur-md border border-[#1E2922]">
            <p className="text-white text-sm font-medium italic leading-relaxed">
              "Financial peace isn't the acquisition of stuff. It's learning to live on less than you make, so you can give money back and have money to invest."
            </p>
            <div className="mt-2 text-xs text-[#00C853] font-semibold">
              MONETRA Precision Core
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

