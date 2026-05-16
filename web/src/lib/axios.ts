import axios from "axios";
import { BASE_API } from "../utils/env";
import { authMe } from "../service/auth";
import { LOGIN_PATH } from "../path";

const api = axios.create({
  baseURL: BASE_API,
  withCredentials: true,
});

let _getAccessToken: (() => string | null) | null = null;

export const initTokenHandlers = (getter: () => string | null) => {
  _getAccessToken = getter;
};

api.interceptors.request.use((config) => {
  const token = _getAccessToken?.();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401 && !error.config._retry) {
      error.config._retry = true;
      try {
        const data = await authMe();
        error.config.headers.Authorization = `Bearer ${data.accessToken}`;
        return api(error.config);
      } catch {
        window.location.href = LOGIN_PATH;
      }
    }
    return Promise.reject(error);
  },
);

export default api;
