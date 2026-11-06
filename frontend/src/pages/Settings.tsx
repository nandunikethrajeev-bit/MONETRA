import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  User, 
  Lock, 
  Sliders, 
  Check, 
  AlertCircle, 
  Loader2, 
  LogOut,
  Eye,
  EyeOff
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export const Settings: React.FC = () => {
  const { user, updateProfile, changePassword, logout } = useAuth();
  const navigate = useNavigate();

  const [activeTab, setActiveTab] = useState<'profile' | 'security' | 'preferences'>('profile');

  // Profile fields
  const [fullName, setFullName] = useState<string>('');
  const [email, setEmail] = useState<string>('');
  const [isSavingProfile, setIsSavingProfile] = useState<boolean>(false);
  const [profileSuccess, setProfileSuccess] = useState<string | null>(null);
  const [profileError, setProfileError] = useState<string | null>(null);

  // Security fields
  const [currentPassword, setCurrentPassword] = useState<string>('');
  const [newPassword, setNewPassword] = useState<string>('');
  const [confirmPassword, setConfirmPassword] = useState<string>('');
  const [showCurrentPassword, setShowCurrentPassword] = useState<boolean>(false);
  const [showNewPassword, setShowNewPassword] = useState<boolean>(false);
  const [isChangingPassword, setIsChangingPassword] = useState<boolean>(false);
  const [passwordSuccess, setPasswordSuccess] = useState<string | null>(null);
  const [passwordError, setPasswordError] = useState<string | null>(null);

  // Preferences
  const [currency, setCurrency] = useState<string>('INR');
  const [isSavingPreferences, setIsSavingPreferences] = useState<boolean>(false);
  const [preferencesSuccess, setPreferencesSuccess] = useState<string | null>(null);

  useEffect(() => {
    if (user) {
      setFullName(user.fullName || '');
      setEmail(user.email || '');
      setCurrency(user.currency || 'INR');
    }
  }, [user]);

  const handleSaveProfile = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!fullName.trim()) {
      setProfileError('Full name cannot be empty');
      return;
    }
    if (!email.trim() || !email.includes('@')) {
      setProfileError('Please provide a valid email address');
      return;
    }

    setIsSavingProfile(true);
    setProfileError(null);
    setProfileSuccess(null);

    try {
      await updateProfile({
        fullName: fullName.trim(),
        email: email.trim(),
      });
      setProfileSuccess('Profile details updated successfully');
      setTimeout(() => setProfileSuccess(null), 4000);
    } catch (err: any) {
      setProfileError(err.message || 'Failed to update profile');
    } finally {
      setIsSavingProfile(false);
    }
  };

  const handleChangePassword = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!currentPassword) {
      setPasswordError('Please enter your current password');
      return;
    }
    if (!newPassword || newPassword.length < 6) {
      setPasswordError('New password must be at least 6 characters');
      return;
    }
    if (newPassword !== confirmPassword) {
      setPasswordError('New passwords do not match');
      return;
    }

    setIsChangingPassword(true);
    setPasswordError(null);
    setPasswordSuccess(null);

    try {
      await changePassword({
        currentPassword,
        newPassword,
      });
      setPasswordSuccess('Password changed successfully');
      setCurrentPassword('');
      setNewPassword('');
      setConfirmPassword('');
      setTimeout(() => setPasswordSuccess(null), 4000);
    } catch (err: any) {
      setPasswordError(err.message || 'Failed to change password. Verify your current password.');
    } finally {
      setIsChangingPassword(false);
    }
  };

  const handleSaveCurrency = async (newCurr: string) => {
    setCurrency(newCurr);
    setIsSavingPreferences(true);
    setPreferencesSuccess(null);
    try {
      await updateProfile({ currency: newCurr });
      setPreferencesSuccess(`Primary currency set to ${newCurr}`);
      setTimeout(() => setPreferencesSuccess(null), 4000);
    } catch (err: any) {
      console.error('Failed to update currency preference:', err);
    } finally {
      setIsSavingPreferences(false);
    }
  };

  const initials = fullName
    ? fullName.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase()
    : 'NR';

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Settings</h1>
          <p className="text-sm text-[#8B949E] mt-0.5">Manage your personal profile, credentials and preferences</p>
        </div>

        <button
          onClick={() => {
            logout();
            navigate('/login');
          }}
          className="flex items-center gap-2 px-3.5 py-2 text-xs font-semibold text-[#EF4444] bg-[#EF4444]/10 hover:bg-[#EF4444]/20 rounded-xl transition-colors"
        >
          <LogOut size={14} />
          <span>Sign Out</span>
        </button>
      </div>

      {/* Settings Layout: Left sub-navigation & Right content */}
      <div className="grid grid-cols-1 md:grid-cols-12 gap-6 items-start">
        {/* Left Sub-nav */}
        <div className="md:col-span-3 space-y-1.5">
          <button
            onClick={() => setActiveTab('profile')}
            className={`w-full flex items-center gap-3 px-4 py-2.5 rounded-xl text-xs font-semibold transition-all ${
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
            className={`w-full flex items-center gap-3 px-4 py-2.5 rounded-xl text-xs font-semibold transition-all ${
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
            className={`w-full flex items-center gap-3 px-4 py-2.5 rounded-xl text-xs font-semibold transition-all ${
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
        <div className="md:col-span-9 bg-[#12161A] border border-[#1F242C] rounded-2xl p-6 sm:p-8">
          {/* Profile Tab */}
          {activeTab === 'profile' && (
            <div>
              <div className="mb-6">
                <h2 className="text-base font-semibold text-white">Profile Details</h2>
                <p className="text-xs text-[#8B949E] mt-0.5">Manage your personal information</p>
              </div>

              {profileSuccess && (
                <div className="mb-6 flex items-center gap-2 p-3.5 text-xs bg-[#00C853]/15 border border-[#00C853]/30 text-[#00C853] rounded-xl animate-in fade-in">
                  <Check size={16} />
                  <span>{profileSuccess}</span>
                </div>
              )}

              {profileError && (
                <div className="mb-6 flex items-center gap-2 p-3.5 text-xs bg-[#EF4444]/15 border border-[#EF4444]/30 text-[#F87171] rounded-xl animate-in fade-in">
                  <AlertCircle size={16} />
                  <span>{profileError}</span>
                </div>
              )}

              <form onSubmit={handleSaveProfile} className="space-y-6">
                {/* Large Green Avatar */}
                <div className="flex items-center gap-4">
                  <div className="w-16 h-16 rounded-full bg-[#00C853] text-white font-bold flex items-center justify-center text-xl shadow-md">
                    {initials}
                  </div>
                  <div>
                    <div className="text-white text-sm font-semibold">{fullName || 'User'}</div>
                    <div className="text-[#8B949E] text-xs">@{user?.username || 'member'}</div>
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
                    className="w-full bg-[#15191E] border border-[#1F242C] rounded-xl px-4 py-2.5 text-white text-sm focus:outline-none focus:border-[#00C853] transition-colors"
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
                    className="w-full bg-[#15191E] border border-[#1F242C] rounded-xl px-4 py-2.5 text-white text-sm focus:outline-none focus:border-[#00C853] transition-colors"
                  />
                </div>

                {/* Save Changes Button */}
                <div className="pt-2">
                  <button
                    type="submit"
                    disabled={isSavingProfile}
                    className="flex items-center gap-1.5 bg-[#00C853] hover:bg-[#00B048] active:scale-[0.98] disabled:opacity-60 text-white font-semibold text-xs px-5 py-2.5 rounded-xl transition-all shadow-sm"
                  >
                    {isSavingProfile && <Loader2 size={13} className="animate-spin" />}
                    <span>{isSavingProfile ? 'Saving Changes...' : 'Save Changes'}</span>
                  </button>
                </div>
              </form>
            </div>
          )}

          {/* Security Tab */}
          {activeTab === 'security' && (
            <div>
              <div className="mb-6">
                <h2 className="text-base font-semibold text-white">Security & Password</h2>
                <p className="text-xs text-[#8B949E] mt-0.5">Manage your credentials and protect your financial ledger</p>
              </div>

              {passwordSuccess && (
                <div className="mb-6 flex items-center gap-2 p-3.5 text-xs bg-[#00C853]/15 border border-[#00C853]/30 text-[#00C853] rounded-xl animate-in fade-in">
                  <Check size={16} />
                  <span>{passwordSuccess}</span>
                </div>
              )}

              {passwordError && (
                <div className="mb-6 flex items-center gap-2 p-3.5 text-xs bg-[#EF4444]/15 border border-[#EF4444]/30 text-[#F87171] rounded-xl animate-in fade-in">
                  <AlertCircle size={16} />
                  <span>{passwordError}</span>
                </div>
              )}

              <form onSubmit={handleChangePassword} className="space-y-4">
                <div>
                  <label className="block text-xs font-medium text-[#8B949E] mb-1.5">Current Password</label>
                  <div className="relative">
                    <input
                      type={showCurrentPassword ? 'text' : 'password'}
                      required
                      value={currentPassword}
                      onChange={(e) => setCurrentPassword(e.target.value)}
                      placeholder="••••••••"
                      className="w-full bg-[#15191E] border border-[#1F242C] rounded-xl px-4 pr-11 py-2.5 text-white text-sm focus:outline-none focus:border-[#00C853]"
                    />
                    <button
                      type="button"
                      onClick={() => setShowCurrentPassword(!showCurrentPassword)}
                      className="absolute right-3.5 top-1/2 -translate-y-1/2 text-[#6E7681] hover:text-white"
                    >
                      {showCurrentPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                    </button>
                  </div>
                </div>

                <div>
                  <label className="block text-xs font-medium text-[#8B949E] mb-1.5">New Password</label>
                  <div className="relative">
                    <input
                      type={showNewPassword ? 'text' : 'password'}
                      required
                      minLength={6}
                      value={newPassword}
                      onChange={(e) => setNewPassword(e.target.value)}
                      placeholder="••••••••"
                      className="w-full bg-[#15191E] border border-[#1F242C] rounded-xl px-4 pr-11 py-2.5 text-white text-sm focus:outline-none focus:border-[#00C853]"
                    />
                    <button
                      type="button"
                      onClick={() => setShowNewPassword(!showNewPassword)}
                      className="absolute right-3.5 top-1/2 -translate-y-1/2 text-[#6E7681] hover:text-white"
                    >
                      {showNewPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                    </button>
                  </div>
                </div>

                <div>
                  <label className="block text-xs font-medium text-[#8B949E] mb-1.5">Confirm New Password</label>
                  <input
                    type="password"
                    required
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    placeholder="••••••••"
                    className="w-full bg-[#15191E] border border-[#1F242C] rounded-xl px-4 py-2.5 text-white text-sm focus:outline-none focus:border-[#00C853]"
                  />
                </div>

                <div className="pt-2">
                  <button
                    type="submit"
                    disabled={isChangingPassword}
                    className="flex items-center gap-1.5 bg-[#00C853] hover:bg-[#00B048] active:scale-[0.98] disabled:opacity-60 text-white font-semibold text-xs px-5 py-2.5 rounded-xl transition-all shadow-sm"
                  >
                    {isChangingPassword && <Loader2 size={13} className="animate-spin" />}
                    <span>{isChangingPassword ? 'Updating Password...' : 'Update Password'}</span>
                  </button>
                </div>
              </form>
            </div>
          )}

          {/* Preferences Tab */}
          {activeTab === 'preferences' && (
            <div>
              <div className="mb-6">
                <h2 className="text-base font-semibold text-white">Preferences</h2>
                <p className="text-xs text-[#8B949E] mt-0.5">Customize your primary currency and accounting display</p>
              </div>

              {preferencesSuccess && (
                <div className="mb-6 flex items-center gap-2 p-3.5 text-xs bg-[#00C853]/15 border border-[#00C853]/30 text-[#00C853] rounded-xl animate-in fade-in">
                  <Check size={16} />
                  <span>{preferencesSuccess}</span>
                </div>
              )}

              <div className="space-y-5">
                <div>
                  <label className="block text-xs font-medium text-[#8B949E] mb-2">Primary Currency Format</label>
                  <select
                    value={currency}
                    disabled={isSavingPreferences}
                    onChange={(e) => handleSaveCurrency(e.target.value)}
                    className="w-full bg-[#15191E] border border-[#1F242C] rounded-xl px-4 py-2.5 text-white text-sm focus:outline-none focus:border-[#00C853] cursor-pointer"
                  >
                    <option value="INR">INR (₹) — Indian Rupee</option>
                    <option value="USD">USD ($) — US Dollar</option>
                    <option value="EUR">EUR (€) — Euro</option>
                    <option value="GBP">GBP (£) — British Pound</option>
                  </select>
                </div>

                <div className="p-4 rounded-xl bg-[#151A20] border border-[#1F242C] text-xs text-[#8B949E]">
                  <span>Selecting a primary currency automatically updates reports and dashboard ledgers across your active session.</span>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
