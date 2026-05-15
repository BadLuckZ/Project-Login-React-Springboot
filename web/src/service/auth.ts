import api from "../lib/axios";

export const register = (email: string, password: string, username: string) => {
  api.post("/auth/register", { email, password, username });
};
