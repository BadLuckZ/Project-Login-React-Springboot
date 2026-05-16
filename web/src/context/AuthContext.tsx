import {
  createContext,
  useContext,
  useEffect,
  useRef,
  useState,
  type ReactNode,
} from "react";
import { initTokenHandlers } from "../lib/axios";
import { authLogin, authMe } from "../service/auth";

export interface User {
  email: string;
  username: string;
}

interface AuthContextType {
  isAuthenticated: boolean;
  user: User | null;
  isLoading: boolean;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | null>(null);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [accessToken, setAccessToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [user, setUser] = useState<User | null>(null);

  const accessTokenRef = useRef(accessToken);
  accessTokenRef.current = accessToken;

  useEffect(() => {
    initTokenHandlers(() => accessTokenRef.current);
  }, []);

  useEffect(() => {
    const verify = async () => {
      try {
        const data = await authMe();
        setAccessToken(data.accessToken || "");
        setUser(data.user || null);
      } catch (e) {
        setAccessToken(null);
        setUser(null);
      } finally {
        setIsLoading(false);
      }
    };

    verify();
  }, []);

  const login = async (email: string, password: string) => {
    const { accessToken } = await authLogin(email, password);
    setAccessToken(accessToken || "");

    if (accessToken) {
      try {
        const data = await authMe();
        setUser(data.user || null);
      } catch (e) {
        setUser(null);
      }
    }
  };

  const logout = () => {
    setAccessToken(null);
    setUser(null);
  };

  return (
    <AuthContext.Provider
      value={{
        isAuthenticated: !!accessToken,
        isLoading,
        user,
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used inside AuthProvider");
  return ctx;
};
