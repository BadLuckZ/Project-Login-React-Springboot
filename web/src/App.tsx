import { BrowserRouter, Route, Routes } from "react-router-dom";
import { LOGIN_PATH, REGISTER_PATH } from "./path";
import { LoginPage, RegisterPage } from "./page";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path={REGISTER_PATH} element={<RegisterPage />} />
        <Route path={LOGIN_PATH} element={<LoginPage />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
