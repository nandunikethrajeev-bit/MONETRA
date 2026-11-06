import React, { createContext, useContext, useState, useEffect } from 'react';
import { User } from '../types';
import { api } from '../api/client';

interface AuthContextType {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (credentials: { email?: string; username?: string; password: string }, rememberMe?: boolean) => Promise<void>;
  register: (data: { username?: string; email: string; password: string; fullName: string; currency?: string }, rememberMe?: boolean) => Promise<void>;
  logout: () => void;
  updateProfile: (data: { fullName?: string; email?: string; currency?: string }) => Promise<User>;
  changePassword: (data: { currentPassword: string; newPassword: string }) => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [token, setToken] = useState<string | null>(() => api.getToken());
  const [user, setUser] = useState<User | null>(() => {
    const saved = localStorage.getItem('monetra_user') || sessionStorage.getItem('monetra_user');
    if (saved) {
      try {
        return JSON.parse(saved);
      } catch {
        return null;
      }
    }
    return null;
  });
  const [isLoading, setIsLoading] = useState<boolean>(true);

  const logout = () => {
    api.setToken(null);
    setToken(null);
    localStorage.removeItem('monetra_user');
    sessionStorage.removeItem('monetra_user');
    setUser(null);
  };

  useEffect(() => {
    api.setOnUnauthorized(() => {
      logout();
    });

    const verifyUser = async () => {
      const activeToken = api.getToken();
      if (activeToken) {
        try {
          const remoteUser = await api.getCurrentUser();
          setUser(remoteUser);
          if (localStorage.getItem('monetra_token')) {
            localStorage.setItem('monetra_user', JSON.stringify(remoteUser));
          } else {
            sessionStorage.setItem('monetra_user', JSON.stringify(remoteUser));
          }
          setToken(activeToken);
        } catch (err) {
          console.warn('Session verification failed:', err);
          logout();
        }
      } else {
        setUser(null);
      }
      setIsLoading(false);
    };

    verifyUser();
  }, []);

  const login = async (credentials: { email?: string; username?: string; password: string }, rememberMe: boolean = true) => {
    setIsLoading(true);
    try {
      const res = await api.login(credentials, rememberMe);
      setUser(res.user);
      setToken(res.token);
      if (rememberMe) {
        localStorage.setItem('monetra_user', JSON.stringify(res.user));
      } else {
        sessionStorage.setItem('monetra_user', JSON.stringify(res.user));
      }
    } finally {
      setIsLoading(false);
    }
  };

  const register = async (data: { username?: string; email: string; password: string; fullName: string; currency?: string }, rememberMe: boolean = true) => {
    setIsLoading(true);
    try {
      const res = await api.register(data, rememberMe);
      setUser(res.user);
      setToken(res.token);
      if (rememberMe) {
        localStorage.setItem('monetra_user', JSON.stringify(res.user));
      } else {
        sessionStorage.setItem('monetra_user', JSON.stringify(res.user));
      }
    } finally {
      setIsLoading(false);
    }
  };

  const updateProfile = async (data: { fullName?: string; email?: string; currency?: string }): Promise<User> => {
    const updatedUser = await api.updateProfile(data);
    setUser(updatedUser);
    if (localStorage.getItem('monetra_token')) {
      localStorage.setItem('monetra_user', JSON.stringify(updatedUser));
    } else {
      sessionStorage.setItem('monetra_user', JSON.stringify(updatedUser));
    }
    return updatedUser;
  };

  const changePassword = async (data: { currentPassword: string; newPassword: string }): Promise<void> => {
    await api.changePassword(data);
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        isAuthenticated: !!token && !!user,
        isLoading,
        login,
        register,
        logout,
        updateProfile,
        changePassword,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
