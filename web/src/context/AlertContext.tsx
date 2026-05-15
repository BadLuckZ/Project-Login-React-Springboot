import { createContext, useContext, useState, type ReactNode } from "react";

import { Alert, AlertDescription, AlertTitle } from "../components/ui";

type AlertVariant = "success" | "neutral" | "error";

type AlertState = {
  variant: AlertVariant;
  title: string;
  description?: string;
};

type AlertContextType = {
  alert: AlertState | null;
  showAlert: (alert: AlertState) => void;
  clearAlert: () => void;
};

const AlertContext = createContext<AlertContextType | null>(null);

export function AlertProvider({ children }: { children: ReactNode }) {
  const [alert, setAlert] = useState<AlertState | null>(null);

  const showAlert = (data: AlertState) => {
    setAlert(data);
  };

  const clearAlert = () => {
    setAlert(null);
  };

  return (
    <AlertContext.Provider
      value={{
        alert,
        showAlert,
        clearAlert,
      }}
    >
      {children}
    </AlertContext.Provider>
  );
}

export function AlertContainer() {
  const { alert, clearAlert } = useAlert();

  if (!alert) return null;

  return (
    <div className="fixed top-4 right-4 z-50 w-80">
      <Alert
        variant={alert.variant}
        duration={alert.variant !== "error" ? 3000 : undefined}
        onClose={clearAlert}
      >
        <div>
          <AlertTitle>{alert.title}</AlertTitle>

          {alert.description && (
            <AlertDescription>{alert.description}</AlertDescription>
          )}
        </div>
      </Alert>
    </div>
  );
}

export function useAlert() {
  const context = useContext(AlertContext);

  if (!context) {
    throw new Error("useAlert must be used within AlertProvider");
  }

  return context;
}
