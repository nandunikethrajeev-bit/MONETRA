import React, { createContext, useContext, useState, useEffect } from 'react';
import { User } from '../types';
import { api } from '../api/client';

interface AuthContextType {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (credentials: { email?: string; username?: string; password: string }) => Promise<void>;
  register: (data: { username: string; email: string; password: string; fullName: string; currency?: string }) => Promise<void>;
  logout: () => void;
  updateProfile: (data: Partial<User>) => void;
}

const DEFAULT_USER: User = {
  id: 1,
  username: 'niketh',
  fullName: 'Niketh Rajeev',
  email: 'nandunikethrajeev@gmail.com',
  currency: 'INR',
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(() => {
    const saved = localStorage.getItem('monetra_user');
    return saved ? JSON.parse(saved) : DEFAULT_USER;
  });
  const [token, setToken] = useState<string | null>(() => localStorage.getItem('monetra_token'));
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
          console.warn('Backend session expired or unreachable, using active profile:', err);
          // Keep current user
        }
      } else {
        if (!user) {
          setUser(DEFAULT_USER);
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
    } catch (err) {
      // In offline/demo fallback mode, still sign in with provided info
      const mockUser: User = {
        id: 1,
        username: credentials.username || (credentials.email ? credentials.email.split('@')[0] : 'user'),
        email: credentials.email || 'nandunikethrajeev@gmail.com',
        fullName: 'Niketh Rajeev',
        currency: 'INR',
      };
      setUser(mockUser);
      setToken('demo-token');
      localStorage.setItem('monetra_user', JSON.stringify(mockUser));
      localStorage.setItem('monetra_token', 'demo-token');
      throw err;
    } finally {
      setIsLoading(false);
    }
  };

  const register = async (data: { username: string; email: string; password: string; fullName: string; currency?: string }) => {
    setIsLoading(true);
    try {
      const res = await api.register(data);
      setUser(res.user);
      setToken(res.token);
      localStorage.setItem('monetra_user', JSON.stringify(res.user));
    } catch (err) {
      const fallbackUser: User = {
        id: Date.now(),
        username: data.username,
        email: data.email,
        fullName: data.fullName,
        currency: data.currency || 'INR',
      };
      setUser(fallbackUser);
      setToken('demo-token');
      localStorage.setItem('monetra_user', JSON.stringify(fallbackUser));
      localStorage.setItem('monetra_token', 'demo-token');
      throw err;
    } finally {
      setIsLoading(false);
    }
  };

  const logout = () => {
    api.setToken(null);
    setToken(null);
    localStorage.removeItem('monetra_token');
    localStorage.removeItem('monetra_user');
    setUser(null);
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
        isAuthenticated: !!token || !!user,
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
