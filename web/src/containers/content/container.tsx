import { useState } from "react";
import { Button } from "../../components/ui";
import { useAuth } from "../../context/AuthContext";
import { useAlert } from "../../context/AlertContext";
import type { AuthLogoutResponse } from "../../service/auth";
import type { AxiosError } from "axios";

export const ContentContainer = () => {
  const { user, logout } = useAuth();
  const [loading, setLoading] = useState(false);
  const { showAlert } = useAlert();

  const onLogout = () => {
    setLoading(true);
    try {
      logout();
      showAlert({ variant: "success", title: "Logout successful" });
    } catch (e) {
      const error = e as AxiosError<AuthLogoutResponse>;
      showAlert({
        variant: "error",
        title: "Logout failed",
        description: error.response?.data.message,
      });
    } finally {
      setLoading(false);
    }
  };

  if (!user) {
    return null;
  }

  return (
    <div className="flex w-full flex-col items-center justify-center gap-4">
      <p className="text-2xl font-bold">
        This is Content Page for {user.username}
      </p>
      <Button label="Log out" loading={loading} onClick={onLogout}></Button>
    </div>
  );
};
