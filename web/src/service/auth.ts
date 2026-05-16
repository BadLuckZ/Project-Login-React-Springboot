import type { AxiosResponse } from "axios";
import type { User } from "../context/AuthContext";
import api from "../lib/axios";

export interface AuthRegisterResponse {
  message: string;
}

export interface AuthLoginResponse {
  message: string;
  accessToken?: string;
}

export interface AuthMeResponse {
  message: string;
  accessToken?: string;
  user?: User;
}

export interface AuthLogoutResponse {
  message: string;
}

export const authRegister = async (
  email: string,
  password: string,
  username: string,
): Promise<AuthRegisterResponse> => {
  return api.post("/auth/register", { email, password, username });
};

export const authLogin = async (
  email: string,
  password: string,
): Promise<AuthLoginResponse> => {
  return api
    .post("/auth/login", { email, password })
    .then((response: AxiosResponse<AuthLoginResponse>) => response.data);
};

export const authMe = async (): Promise<AuthMeResponse> => {
  return api
    .get("/auth/me")
    .then((response: AxiosResponse<AuthMeResponse>) => response.data);
};

export const authLogout = async (): Promise<AuthLogoutResponse> => {
  return api.post("/auth/logout");
};
