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

  const initials = user?.fullName 
    ? user.fullName.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase()
    : (user?.username ? user.username.substring(0, 2).toUpperCase() : 'US');

  return (
    <aside className="w-64 min-w-[16rem] bg-[#0E1217] border-r border-[#1B222B] flex flex-col justify-between h-screen sticky top-0 px-4 py-5 select-none z-30">
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
            className="text-[#8B949E] hover:text-[#EF4444] hover:bg-[#EF4444]/10 p-2 rounded-lg transition-colors shrink-0"
          >
            <LogOut size={16} />
          </button>
        </div>
      </div>
    </aside>
  );
};
