import React, { useState } from 'react';
import { User, Lock, Sliders, Camera, Check } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export const Settings: React.FC = () => {
  const { user, updateProfile } = useAuth();
  const [activeTab, setActiveTab] = useState<'profile' | 'security' | 'preferences'>('profile');
  const [fullName, setFullName] = useState<string>(user?.fullName || 'Niketh Rajeev');
  const [email, setEmail] = useState<string>(user?.email || 'nandunikethrajeev@gmail.com');
  const [currentPassword, setCurrentPassword] = useState<string>('');
  const [newPassword, setNewPassword] = useState<string>('');
  const [confirmPassword, setConfirmPassword] = useState<string>('');
  const [currency, setCurrency] = useState<string>(user?.currency || 'INR');
  const [showSavedToast, setShowSavedToast] = useState<boolean>(false);

  const handleSaveProfile = (e: React.FormEvent) => {
    e.preventDefault();
    updateProfile({ fullName, email });
    setShowSavedToast(true);
    setTimeout(() => setShowSavedToast(false), 3000);
  };

  const initials = fullName
    ? fullName.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase()
    : 'NR';

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-white tracking-tight">Settings</h1>
      </div>

      {/* Settings Layout: Left sub-navigation & Right content */}
      <div className="grid grid-cols-1 md:grid-cols-12 gap-6 items-start">
        {/* Left Sub-nav */}
        <div className="md:col-span-3 space-y-1.5">
          <button
            onClick={() => setActiveTab('profile')}
            className={`w-full flex items-center gap-3 px-4 py-2.5 rounded-lg text-xs font-semibold transition-all ${
              activeTab === 'profile'
                ? 'bg-[#00C853] text-white shadow-sm'
                : 'text-[#8B949E] hover:text-white hover:bg-[#141A21]'
            }`}
          >
            <User size={16} />
            <span>Profile</span>
          </button>

          <button
            onClick={() => setActiveTab('security')}
            className={`w-full flex items-center gap-3 px-4 py-2.5 rounded-lg text-xs font-semibold transition-all ${
              activeTab === 'security'
                ? 'bg-[#00C853] text-white shadow-sm'
                : 'text-[#8B949E] hover:text-white hover:bg-[#141A21]'
            }`}
          >
            <Lock size={16} />
            <span>Security</span>
          </button>

          <button
            onClick={() => setActiveTab('preferences')}
            className={`w-full flex items-center gap-3 px-4 py-2.5 rounded-lg text-xs font-semibold transition-all ${
              activeTab === 'preferences'
                ? 'bg-[#00C853] text-white shadow-sm'
                : 'text-[#8B949E] hover:text-white hover:bg-[#141A21]'
            }`}
          >
            <Sliders size={16} />
            <span>Preferences</span>
          </button>
        </div>

        {/* Right Content Card */}
        <div className="md:col-span-9 bg-[#12161A] border border-[#1F242C] rounded-xl p-6 sm:p-8">
          {activeTab === 'profile' && (
            <div>
              <div className="mb-6">
                <h2 className="text-base font-semibold text-white">Profile Details</h2>
                <p className="text-xs text-[#8B949E] mt-0.5">Manage your personal information</p>
              </div>

              {showSavedToast && (
                <div className="mb-6 flex items-center gap-2 p-3 text-xs bg-[#00C853]/15 border border-[#00C853]/30 text-[#00C853] rounded-lg">
                  <Check size={16} />
                  <span>Changes saved successfully!</span>
                </div>
              )}

              <form onSubmit={handleSaveProfile} className="space-y-6">
                {/* Large Green Avatar with Camera Badge */}
                <div className="flex items-center gap-4">
                  <div className="relative">
                    <div className="w-16 h-16 rounded-full bg-[#00C853] text-white font-bold flex items-center justify-center text-xl shadow-md">
                      {initials}
                    </div>
                    <button
                      type="button"
                      title="Update avatar"
                      className="absolute bottom-0 right-0 w-6 h-6 rounded-full bg-[#181D23] border border-[#2D3540] text-[#8B949E] hover:text-white flex items-center justify-center transition-colors"
                    >
                      <Camera size={12} />
                    </button>
                  </div>
                </div>

                {/* Full Name */}
                <div>
                  <label className="block text-xs font-medium text-[#8B949E] mb-2">Full Name</label>
                  <input
                    type="text"
                    required
                    value={fullName}
                    onChange={(e) => setFullName(e.target.value)}
                    className="w-full bg-[#15191E] border border-[#1F242C] rounded-lg px-4 py-2.5 text-white text-sm focus:outline-none focus:border-[#00C853] transition-colors"
                  />
                </div>

                {/* Email Address */}
                <div>
                  <label className="block text-xs font-medium text-[#8B949E] mb-2">Email Address</label>
                  <input
                    type="email"
                    required
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    className="w-full bg-[#15191E] border border-[#1F242C] rounded-lg px-4 py-2.5 text-white text-sm focus:outline-none focus:border-[#00C853] transition-colors"
                  />
                </div>

                {/* Save Changes Button */}
                <div className="pt-2">
                  <button
                    type="submit"
                    className="bg-[#00C853] hover:bg-[#00B048] text-white font-semibold text-xs px-5 py-2.5 rounded-lg transition-colors shadow-sm"
                  >
                    Save Changes
                  </button>
                </div>
              </form>
            </div>
          )}

          {activeTab === 'security' && (
            <div>
              <div className="mb-6">
                <h2 className="text-base font-semibold text-white">Security & Password</h2>
                <p className="text-xs text-[#8B949E] mt-0.5">Manage your account credentials and password</p>
              </div>

              <form onSubmit={(e) => { e.preventDefault(); setShowSavedToast(true); setTimeout(() => setShowSavedToast(false), 3000); }} className="space-y-4">
                <div>
                  <label className="block text-xs font-medium text-[#8B949E] mb-1.5">Current Password</label>
                  <input
                    type="password"
                    value={currentPassword}
                    onChange={(e) => setCurrentPassword(e.target.value)}
                    placeholder="••••••••"
                    className="w-full bg-[#15191E] border border-[#1F242C] rounded-lg px-4 py-2.5 text-white text-sm focus:outline-none focus:border-[#00C853]"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-[#8B949E] mb-1.5">New Password</label>
                  <input
                    type="password"
                    value={newPassword}
                    onChange={(e) => setNewPassword(e.target.value)}
                    placeholder="••••••••"
                    className="w-full bg-[#15191E] border border-[#1F242C] rounded-lg px-4 py-2.5 text-white text-sm focus:outline-none focus:border-[#00C853]"
                  />
                </div>
                <div>
                  <label className="block text-xs font-medium text-[#8B949E] mb-1.5">Confirm New Password</label>
                  <input
                    type="password"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    placeholder="••••••••"
                    className="w-full bg-[#15191E] border border-[#1F242C] rounded-lg px-4 py-2.5 text-white text-sm focus:outline-none focus:border-[#00C853]"
                  />
                </div>
                <div className="pt-2">
                  <button
                    type="submit"
                    className="bg-[#00C853] hover:bg-[#00B048] text-white font-semibold text-xs px-5 py-2.5 rounded-lg transition-colors shadow-sm"
                  >
                    Update Password
                  </button>
                </div>
              </form>
            </div>
          )}

          {activeTab === 'preferences' && (
            <div>
              <div className="mb-6">
                <h2 className="text-base font-semibold text-white">Preferences</h2>
                <p className="text-xs text-[#8B949E] mt-0.5">Customize your currency, locale and notification settings</p>
              </div>

              <div className="space-y-4">
                <div>
                  <label className="block text-xs font-medium text-[#8B949E] mb-1.5">Primary Currency</label>
                  <select
                    value={currency}
                    onChange={(e) => {
                      setCurrency(e.target.value);
                      updateProfile({ currency: e.target.value });
                    }}
                    className="w-full bg-[#15191E] border border-[#1F242C] rounded-lg px-4 py-2.5 text-white text-sm focus:outline-none focus:border-[#00C853]"
                  >
                    <option value="INR">INR (₹) — Indian Rupee</option>
                    <option value="USD">USD ($) — US Dollar</option>
                    <option value="EUR">EUR (€) — Euro</option>
                    <option value="GBP">GBP (£) — British Pound</option>
                  </select>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
