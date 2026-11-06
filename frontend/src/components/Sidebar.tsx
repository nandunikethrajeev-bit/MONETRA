import React, { useState, useEffect } from 'react';
import { NavLink, useNavigate, useLocation } from 'react-router-dom';
import { 
  LayoutDashboard, 
  ArrowLeftRight, 
  Layers, 
  Wallet, 
  BarChart3, 
  Settings,
  LogOut,
  Menu,
  X
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export const Sidebar: React.FC = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [isMobileOpen, setIsMobileOpen] = useState(false);

  // Auto-close mobile drawer on route change
  useEffect(() => {
    setIsMobileOpen(false);
  }, [location.pathname]);

  // Prevent background scroll when mobile drawer is open
  useEffect(() => {
    if (isMobileOpen) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = '';
    }
    return () => {
      document.body.style.overflow = '';
    };
  }, [isMobileOpen]);

  const navItems = [
    { name: 'Dashboard', path: '/dashboard', icon: LayoutDashboard },
    { name: 'Transactions', path: '/transactions', icon: ArrowLeftRight },
    { name: 'Categories', path: '/categories', icon: Layers },
    { name: 'Budgets', path: '/budgets', icon: Wallet },
    { name: 'Reports', path: '/reports', icon: BarChart3 },
    { name: 'Settings', path: '/settings', icon: Settings },
  ];

  const initials = user?.fullName 
    ? user.fullName.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase()
    : (user?.username ? user.username.substring(0, 2).toUpperCase() : 'US');

  return (
    <>
      {/* Mobile Sticky Top Header Bar */}
      <header className="lg:hidden sticky top-0 left-0 right-0 h-16 bg-[#0E1217]/95 backdrop-blur-md border-b border-[#1B222B] px-4 flex items-center justify-between z-40 select-none">
        <div 
          className="flex items-center gap-2.5 cursor-pointer"
          onClick={() => navigate('/dashboard')}
        >
          <div className="w-8 h-8 rounded-full border-2 border-[#00C853] flex items-center justify-center bg-[#0B1A12] shadow-sm">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
              <path d="M7 17C7 10 12 5 18 5C18 12 13 17 7 17Z" fill="#00C853" />
              <path d="M7 17L15 9" stroke="#0B1A12" strokeWidth="2" strokeLinecap="round" />
            </svg>
          </div>
          <span className="text-white text-base font-bold tracking-wider">MONETRA</span>
        </div>

        <div className="flex items-center gap-3">
          <div 
            onClick={() => navigate('/settings')}
            className="w-8 h-8 rounded-full bg-[#00C853] text-white font-bold flex items-center justify-center text-xs shadow cursor-pointer"
            title="Profile"
          >
            {initials}
          </div>
          <button
            type="button"
            onClick={() => setIsMobileOpen(!isMobileOpen)}
            className="p-2 rounded-lg bg-[#141A21] border border-[#1F2630] text-[#8B949E] hover:text-white transition-colors focus:outline-none"
            aria-label="Toggle navigation menu"
          >
            {isMobileOpen ? <X size={20} /> : <Menu size={20} />}
          </button>
        </div>
      </header>

      {/* Mobile Slide-Out Drawer Overlay */}
      {isMobileOpen && (
        <div 
          className="lg:hidden fixed inset-0 bg-black/70 backdrop-blur-sm z-50 transition-opacity"
          onClick={() => setIsMobileOpen(false)}
        />
      )}

      {/* Mobile Drawer Content */}
      <div 
        className={`lg:hidden fixed top-0 left-0 bottom-0 w-72 max-w-[85vw] bg-[#0E1217] border-r border-[#1B222B] p-5 flex flex-col justify-between z-50 shadow-2xl transition-transform duration-300 ease-out select-none ${
          isMobileOpen ? 'translate-x-0' : '-translate-x-full'
        }`}
      >
        <div>
          {/* Drawer Header */}
          <div className="flex items-center justify-between mb-8">
            <div 
              className="flex items-center gap-3 cursor-pointer" 
              onClick={() => {
                navigate('/dashboard');
                setIsMobileOpen(false);
              }}
            >
              <div className="w-8 h-8 rounded-full border-2 border-[#00C853] flex items-center justify-center bg-[#0B1A12]">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                  <path d="M7 17C7 10 12 5 18 5C18 12 13 17 7 17Z" fill="#00C853" />
                  <path d="M7 17L15 9" stroke="#0B1A12" strokeWidth="2" strokeLinecap="round" />
                </svg>
              </div>
              <span className="text-white text-lg font-bold tracking-wider">MONETRA</span>
            </div>

            <button 
              type="button"
              onClick={() => setIsMobileOpen(false)}
              className="p-1.5 rounded-lg text-[#8B949E] hover:text-white hover:bg-[#141A21]"
              aria-label="Close menu"
            >
              <X size={20} />
            </button>
          </div>

          {/* Navigation Links */}
          <nav className="space-y-1.5">
            {navItems.map((item) => {
              const Icon = item.icon;
              return (
                <NavLink
                  key={item.path}
                  to={item.path}
                  onClick={() => setIsMobileOpen(false)}
                  className={({ isActive }) =>
                    `flex items-center gap-3.5 px-4 py-3 rounded-lg text-sm font-medium transition-colors ${
                      isActive
                        ? 'bg-[#00C853] text-white shadow-sm font-semibold'
                        : 'text-[#8B949E] hover:text-white hover:bg-[#141A21]'
                    }`
                  }
                >
                  <Icon size={19} className="shrink-0" />
                  <span>{item.name}</span>
                </NavLink>
              );
            })}
          </nav>
        </div>

        {/* User Profile Pill at Bottom with Logout */}
        <div className="pt-4 border-t border-[#1B222B]">
          <div className="flex items-center justify-between p-2 rounded-xl bg-[#12161A] border border-[#1F2630]">
            <div 
              onClick={() => {
                navigate('/settings');
                setIsMobileOpen(false);
              }}
              className="flex items-center gap-3 cursor-pointer truncate flex-1 mr-2"
            >
              <div className="w-9 h-9 rounded-full bg-[#00C853] text-white font-bold flex items-center justify-center text-xs shrink-0 shadow">
                {initials}
              </div>
              <div className="truncate">
                <div className="text-white text-xs font-semibold leading-tight truncate">
                  {user?.fullName || user?.username || 'User'}
                </div>
                <div className="text-[#8B949E] text-[11px] leading-tight truncate mt-0.5">
                  {user?.email || 'Pro Account'}
                </div>
              </div>
            </div>

            <button 
              type="button"
              title="Sign out of MONETRA"
              onClick={(e) => {
                e.stopPropagation();
                logout();
                navigate('/login');
              }}
              className="p-2 text-[#8B949E] hover:text-[#FF5252] hover:bg-[#1C1618] rounded-lg transition-colors shrink-0"
              aria-label="Logout"
            >
              <LogOut size={16} />
            </button>
          </div>
        </div>
      </div>

      {/* Desktop Persistent Left Sidebar */}
      <aside className="hidden lg:flex w-64 min-w-[16rem] bg-[#0E1217] border-r border-[#1B222B] flex-col justify-between h-screen sticky top-0 px-4 py-5 select-none z-30">
        <div>
          {/* Brand Header */}
          <div 
            className="flex items-center gap-3 px-3 mb-8 cursor-pointer group" 
            onClick={() => navigate('/dashboard')}
            title="MONETRA Home"
          >
            <div className="w-8 h-8 rounded-full border-2 border-[#00C853] flex items-center justify-center bg-[#0B1A12] shadow-sm group-hover:border-[#10B981] transition-colors">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
                <path d="M7 17C7 10 12 5 18 5C18 12 13 17 7 17Z" fill="#00C853" />
                <path d="M7 17L15 9" stroke="#0B1A12" strokeWidth="2" strokeLinecap="round" />
              </svg>
            </div>
            <span className="text-white text-lg font-bold tracking-wider">MONETRA</span>
          </div>

          {/* Navigation Links */}
          <nav className="space-y-1.5">
            {navItems.map((item) => {
              const Icon = item.icon;
              return (
                <NavLink
                  key={item.path}
                  to={item.path}
                  className={({ isActive }) =>
                    `flex items-center gap-3.5 px-4 py-2.5 rounded-lg text-sm font-medium transition-colors ${
                      isActive
                        ? 'bg-[#00C853] text-white shadow-sm font-semibold'
                        : 'text-[#8B949E] hover:text-white hover:bg-[#141A21]'
                    }`
                  }
                >
                  <Icon size={18} className="shrink-0" />
                  <span>{item.name}</span>
                </NavLink>
              );
            })}
          </nav>
        </div>

        {/* User Profile Pill at Bottom with Dedicated Logout Action */}
        <div className="pt-4 border-t border-[#1B222B]">
          <div className="flex items-center justify-between p-2 rounded-xl bg-[#12161A] border border-[#1F2630] hover:border-[#2D3540] transition-colors">
            <div 
              onClick={() => navigate('/settings')}
              className="flex items-center gap-3 cursor-pointer truncate flex-1 mr-2"
              title="View Profile Settings"
            >
              <div className="w-9 h-9 rounded-full bg-[#00C853] text-white font-bold flex items-center justify-center text-xs shrink-0 shadow">
                {initials}
              </div>
              <div className="truncate">
                <div className="text-white text-xs font-semibold leading-tight truncate">
                  {user?.fullName || user?.username || 'User'}
                </div>
                <div className="text-[#8B949E] text-[11px] leading-tight truncate mt-0.5">
                  {user?.email || 'Pro Account'}
                </div>
              </div>
            </div>

            <button 
              type="button"
              title="Sign out of MONETRA"
              onClick={(e) => {
                e.stopPropagation();
                logout();
                navigate('/login');
              }}
              className="p-2 text-[#8B949E] hover:text-[#FF5252] hover:bg-[#1C1618] rounded-lg transition-colors shrink-0"
              aria-label="Logout"
            >
              <LogOut size={16} />
            </button>
          </div>
        </div>
      </aside>
    </>
  );
};
