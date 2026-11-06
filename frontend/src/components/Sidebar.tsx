import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { 
  LayoutDashboard, 
  ArrowLeftRight, 
  Layers, 
  Wallet, 
  BarChart3, 
  Settings,
  LogOut
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export const Sidebar: React.FC = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const navItems = [
    { name: 'Dashboard', path: '/dashboard', icon: LayoutDashboard },
    { name: 'Transactions', path: '/transactions', icon: ArrowLeftRight },
    { name: 'Categories', path: '/categories', icon: Layers },
    { name: 'Budgets', path: '/budgets', icon: Wallet },
    { name: 'Reports', path: '/reports', icon: BarChart3 },
    { name: 'Settings', path: '/settings', icon: Settings },
  ];

  // Derive initials
  const initials = user?.fullName 
    ? user.fullName.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase()
    : 'NR';

  return (
    <aside className="w-64 min-w-[16rem] bg-[#0E1217] border-r border-[#1B222B] flex flex-col justify-between h-screen sticky top-0 px-4 py-5 select-none z-30">
      <div>
        {/* Brand Header */}
        <div className="flex items-center gap-3 px-3 mb-8 cursor-pointer" onClick={() => navigate('/dashboard')}>
          <div className="w-8 h-8 rounded-full border-2 border-[#00C853] flex items-center justify-center bg-[#0B1A12]">
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
                      ? 'bg-[#00C853] text-white shadow-sm'
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

      {/* User Profile Pill at Bottom */}
      <div className="pt-4 border-t border-[#1B222B]">
        <div 
          onClick={() => navigate('/settings')}
          className="flex items-center justify-between p-2 rounded-lg hover:bg-[#141A21] cursor-pointer transition-colors group"
        >
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-full bg-[#00C853] text-white font-semibold flex items-center justify-center text-sm shrink-0">
              {initials}
            </div>
            <div className="truncate">
              <div className="text-white text-sm font-medium leading-tight truncate">
                {user?.fullName || 'Niketh Rajeev'}
              </div>
              <div className="text-[#8B949E] text-xs leading-tight">
                Pro Account
              </div>
            </div>
          </div>
          <button 
            title="Sign out"
            onClick={(e) => {
              e.stopPropagation();
              logout();
              navigate('/login');
            }}
            className="text-[#8B949E] hover:text-[#EF4444] p-1.5 rounded opacity-0 group-hover:opacity-100 transition-opacity"
          >
            <LogOut size={16} />
          </button>
        </div>
      </div>
    </aside>
  );
};
