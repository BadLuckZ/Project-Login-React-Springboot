import { createContext, useContext, useEffect, useState } from "react";
import api from "../lib/axios";

interface User {
  email: string;
  username: string;
}

interface AuthContextType {
  isAuthenticated: boolean;
  isLoading: boolean;
  user: User | null;
}

const AuthContext = createContext<AuthContextType | null>(null);

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
  const [state, setState] = useState<AuthContextType>({
    isAuthenticated: false,
    isLoading: true,
    user: null,
  });

  useEffect(() => {
    const verify = async () => {
      try {
        const { data } = await api.get("/auth/me");
        setState({ isAuthenticated: true, isLoading: false, user: data });
      } catch {
        setState({ isAuthenticated: false, isLoading: false, user: null });
      }
    };

    verify();
  }, []);

  return <AuthContext.Provider value={state}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used inside AuthProvider");
  return ctx;
};
