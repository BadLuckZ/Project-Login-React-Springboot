import api from "../lib/axios";

export interface AuthRegisterResponse {
  message: string;
}

export const authRegister = (
  email: string,
  password: string,
  username: string,
): Promise<AuthRegisterResponse> => {
  return api.post("/auth/register", { email, password, username });
};
