import React from 'react';
import { BrowserRouter, Routes, Route, Navigate, Outlet, useLocation } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { Sidebar } from './components/Sidebar';
import { AnimatedBackground } from './components/AnimatedBackground';
import { Welcome } from './pages/Welcome';
import { Auth } from './pages/Auth';
import { Dashboard } from './pages/Dashboard';
import { Transactions } from './pages/Transactions';
import { Categories } from './pages/Categories';
import { Budgets } from './pages/Budgets';
import { Reports } from './pages/Reports';
import { Settings } from './pages/Settings';
import { Loader2 } from 'lucide-react';

const ProtectedRoute: React.FC = () => {
  const { isAuthenticated, isLoading } = useAuth();
  const location = useLocation();

  if (isLoading) {
    return (
      <div className="min-h-screen bg-[#0B0E11] flex items-center justify-center text-white">
        <div className="flex flex-col items-center gap-3">
          <Loader2 className="w-8 h-8 text-[#00C853] animate-spin" />
          <span className="text-xs text-[#8B949E] tracking-wider uppercase font-medium">Verifying Session...</span>
        </div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  return (
    <div className="min-h-screen bg-[#0B0E11] text-white flex flex-col lg:flex-row relative">
      {/* Restrained Atmospheric Dashboard Background */}
      <AnimatedBackground variant="dashboard" />

      {/* Navigation (Sticky Header on Mobile, Sidebar on Desktop) */}
      <Sidebar />

      {/* Main Content Area */}
      <main className="flex-1 min-w-0 p-4 sm:p-6 lg:p-8 overflow-y-auto min-h-[calc(100vh-4rem)] lg:h-screen relative z-10">
        <Outlet />
      </main>
    </div>
  );
};

export const App: React.FC = () => {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          {/* Public Landing Page */}
          <Route path="/" element={<Welcome />} />

          {/* Authentication Pages */}
          <Route path="/login" element={<Auth initialMode="login" />} />
          <Route path="/register" element={<Auth initialMode="register" />} />

          {/* Protected Application Routes */}
          <Route element={<ProtectedRoute />}>
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/transactions" element={<Transactions />} />
            <Route path="/categories" element={<Categories />} />
            <Route path="/budgets" element={<Budgets />} />
            <Route path="/reports" element={<Reports />} />
            <Route path="/settings" element={<Settings />} />
          </Route>

          {/* Catch-all */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
};

export default App;
