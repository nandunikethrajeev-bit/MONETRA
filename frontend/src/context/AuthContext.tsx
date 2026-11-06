import React, { createContext, useContext, useState, useEffect } from 'react';
import { User } from '../types';
import { api } from '../api/client';

interface AuthContextType {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (credentials: { email?: string; username?: string; password: string }) => Promise<void>;
  register: (data: { username?: string; email: string; password: string; fullName: string; currency?: string }) => Promise<void>;
  logout: () => void;
  updateProfile: (data: Partial<User>) => void;
}

const DEFAULT_REFERENCE_USER: User = {
  id: 1,
  username: 'niketh',
  fullName: 'Niketh Rajeev',
  email: 'nandunikethrajeev@gmail.com',
  currency: 'INR',
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [token, setToken] = useState<string | null>(() => localStorage.getItem('monetra_token'));
  const [user, setUser] = useState<User | null>(() => {
    const saved = localStorage.getItem('monetra_user');
    if (saved) {
      try {
        return JSON.parse(saved);
      } catch {
        return null;
      }
    }
    return token ? null : DEFAULT_REFERENCE_USER;
  });
  const [isLoading, setIsLoading] = useState<boolean>(true);

  useEffect(() => {
    const verifyUser = async () => {
      const savedToken = localStorage.getItem('monetra_token');
      if (savedToken) {
        try {
          const remoteUser = await api.getCurrentUser();
          setUser(remoteUser);
          localStorage.setItem('monetra_user', JSON.stringify(remoteUser));
        } catch (err) {
          console.warn('Backend session check:', err);
          // If token expired, clear invalid session
          localStorage.removeItem('monetra_token');
          localStorage.removeItem('monetra_user');
          setToken(null);
          setUser(DEFAULT_REFERENCE_USER);
        }
      }
      setIsLoading(false);
    };

    verifyUser();
  }, []);

  const login = async (credentials: { email?: string; username?: string; password: string }) => {
    setIsLoading(true);
    try {
      const res = await api.login(credentials);
      setUser(res.user);
      setToken(res.token);
      localStorage.setItem('monetra_user', JSON.stringify(res.user));
      localStorage.setItem('monetra_token', res.token);
    } finally {
      setIsLoading(false);
    }
  };

  const register = async (data: { username?: string; email: string; password: string; fullName: string; currency?: string }) => {
    setIsLoading(true);
    try {
      const res = await api.register({
        username: data.username || data.email.split('@')[0],
        email: data.email,
        password: data.password,
        fullName: data.fullName,
        currency: data.currency || 'INR',
      });
      setUser(res.user);
      setToken(res.token);
      localStorage.setItem('monetra_user', JSON.stringify(res.user));
      localStorage.setItem('monetra_token', res.token);
    } finally {
      setIsLoading(false);
    }
  };

  const logout = () => {
    api.setToken(null);
    setToken(null);
    localStorage.removeItem('monetra_token');
    localStorage.removeItem('monetra_user');
    setUser(DEFAULT_REFERENCE_USER);
  };

  const updateProfile = (updated: Partial<User>) => {
    if (user) {
      const newUser = { ...user, ...updated };
      setUser(newUser);
      localStorage.setItem('monetra_user', JSON.stringify(newUser));
    }
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        isAuthenticated: !!token,
        isLoading,
        login,
        register,
        logout,
        updateProfile,
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
