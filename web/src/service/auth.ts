import api from "../lib/axios";

export interface AuthRegisterResponse {
  message: string;
}

export interface AuthLoginResponse {
  message: string;
  accessToken?: string;
}

export const authRegister = (
  email: string,
  password: string,
  username: string,
): Promise<AuthRegisterResponse> => {
  return api.post("/auth/register", { email, password, username });
};

export const authLogin = (
  email: string,
  password: string,
): Promise<AuthLoginResponse> => {
  return api
    .post("/auth/login", { email, password })
    .then((response) => response.data);
};
