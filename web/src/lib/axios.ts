import axios from "axios";
import { BASE_API } from "../utils/env";

const api = axios.create({
  baseURL: BASE_API,
  withCredentials: true,
});

export default api;
