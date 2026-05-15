import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { LOGIN_PATH, CONTENT_PATH, REGISTER_PATH } from "./path";
import { LoginPage, ContentPage, RegisterPage } from "./page";
import PrivateLayout from "./layout/PrivateLayout";
import { AlertContainer } from "./context/AlertContext";

function App() {
  return (
    <BrowserRouter>
      <AlertContainer />
      <Routes>
        <Route path={REGISTER_PATH} element={<RegisterPage />} />
        <Route path={LOGIN_PATH} element={<LoginPage />} />

        <Route element={<PrivateLayout />}>
          <Route path={CONTENT_PATH} element={<ContentPage />} />
        </Route>

        <Route path="*" element={<Navigate to={CONTENT_PATH} replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
