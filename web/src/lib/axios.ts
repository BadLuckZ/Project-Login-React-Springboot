import axios from "axios";
import { BASE_API } from "../utils/env";

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

export default api;
