import { useForm } from "react-hook-form";
import { Button, Input } from "../../components/ui";
import { Link, useNavigate } from "react-router-dom";
import { CONTENT_PATH, REGISTER_PATH } from "../../path";
import { type AuthLoginResponse } from "../../service/auth";
import type { AxiosError } from "axios";
import { useAlert } from "../../context/AlertContext";
import { useAuth } from "../../context/AuthContext";

type LoginForm = {
  email: string;
  password: string;
};

export const LoginContainer = () => {
  const { showAlert } = useAlert();
  const { login } = useAuth();
  const navigate = useNavigate();

  const onSubmit = async (data: LoginForm) => {
    try {
      await login(data.email, data.password);
      navigate(CONTENT_PATH);
      showAlert({
        variant: "success",
        title: "Login successful",
      });
    } catch (error) {
      const axiosError = error as AxiosError<AuthLoginResponse>;
      showAlert({
        variant: "error",
        title: "Login failed",
        description: axiosError.response?.data.message || "Login failed",
      });
    }
  };

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginForm>();

  return (
    <div className="w-full h-full">
      <h1 className="text-4xl font-bold mb-8 text-center">Login</h1>
      <form
        onSubmit={handleSubmit(onSubmit)}
        className="flex flex-col gap-4 mb-6"
      >
        <Input
          type="text"
          label="Email"
          placeholder="Enter your email"
          required
          {...register("email", {
            required: "Email Address is required",
            pattern: {
              value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
              message: "Invalid email address",
            },
          })}
          errorMessage={errors.email?.message}
        />

        <Input
          type="password"
          label="Password"
          placeholder="Enter your password"
          required
          {...register("password", { required: "Password is required" })}
          errorMessage={errors.password?.message}
        />

        <Button type="submit" label="Login" />
      </form>
      <p className="text-end text-base">
        Don't have an account?{" "}
        <Link to={REGISTER_PATH} className="text-blue-500 hover:underline">
          Register here
        </Link>
      </p>
    </div>
  );
};
