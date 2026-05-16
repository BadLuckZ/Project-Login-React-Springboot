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

interface AuthContextType {
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | null>(null);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [accessToken, setAccessToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);

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
      } catch (e) {
        setAccessToken(null);
      } finally {
        setIsLoading(false);
      }
    };

    verify();
  }, []);

  const login = async (email: string, password: string) => {
    const { accessToken } = await authLogin(email, password);
    setAccessToken(accessToken || "");
  };

  const logout = () => {
    setAccessToken(null);
  };

  return (
    <AuthContext.Provider
      value={{
        isAuthenticated: !!accessToken,
        isLoading,
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
