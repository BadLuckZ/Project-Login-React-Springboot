import {
  createContext,
  useContext,
  useEffect,
  useRef,
  useState,
  type ReactNode,
} from "react";
import { initTokenHandlers } from "../lib/axios";
import { authLogin } from "../service/auth";

interface AuthContextType {
  isAuthenticated: boolean;
  login: (email: string, password: string) => Promise<void>;
}

const AuthContext = createContext<AuthContextType | null>(null);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [accessToken, setAccessToken] = useState<string | null>(null);

  const accessTokenRef = useRef(accessToken);
  accessTokenRef.current = accessToken;

  useEffect(() => {
    initTokenHandlers(() => accessTokenRef.current);
  }, []);

  const login = async (email: string, password: string) => {
    const data = await authLogin(email, password);
    setAccessToken(data.accessToken || "");
  };

  return (
    <AuthContext.Provider
      value={{
        isAuthenticated: !!accessToken,
        login,
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
