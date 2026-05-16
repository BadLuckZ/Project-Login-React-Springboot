import axios from "axios";
import { BASE_API } from "../utils/env";
import { authMe } from "../service/auth";
import { LOGIN_PATH } from "../path";

const api = axios.create({
  baseURL: BASE_API,
  withCredentials: true,
});

let _getAccessToken: (() => string | null) | null = null;
let _refreshPromise: Promise<string | null> | null = null;

api.interceptors.request.use((config) => {
  const token = _getAccessToken?.();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const initTokenHandlers = (getter: () => string | null) => {
  _getAccessToken = getter;
};

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401 && !error.config._retry) {
      error.config._retry = true;

      // ถ้ามี refresh อยู่แล้ว ให้รอ promise เดิม ไม่เรียกซ้ำ
      if (!_refreshPromise) {
        _refreshPromise = authMe()
          .then((data) => data.accessToken ?? null)
          .finally(() => {
            _refreshPromise = null;
          });
      }

      try {
        const token = await _refreshPromise;
        if (!token) throw new Error("No token");
        error.config.headers.Authorization = `Bearer ${token}`;
        return api(error.config);
      } catch {
        window.location.href = LOGIN_PATH;
      }
    }
    return Promise.reject(error);
  },
);

export default api;
