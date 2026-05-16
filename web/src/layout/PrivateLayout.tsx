import { Navigate, Outlet, useLocation } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { LOGIN_PATH } from "../path";

const PrivateLayout = () => {
  const { isAuthenticated, isAuthLoading: isLoading } = useAuth();
  const location = useLocation();

  if (isLoading) return <div>Loading...</div>;

  if (!isAuthenticated) {
    return <Navigate to={LOGIN_PATH} state={{ from: location }} replace />;
  }

  return <Outlet />;
};

export default PrivateLayout;
