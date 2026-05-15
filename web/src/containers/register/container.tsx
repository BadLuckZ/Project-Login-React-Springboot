import { useForm } from "react-hook-form";
import { Button, Input } from "../../components/ui";
import { Link, useNavigate } from "react-router-dom";
import { LOGIN_PATH } from "../../path";
import { authRegister, type AuthRegisterResponse } from "../../service/auth";
import { AxiosError } from "axios";
import { useAlert } from "../../context/AlertContext";

type RegisterForm = {
  username: string;
  email: string;
  password: string;
};

export const RegisterContainer = () => {
  const { showAlert } = useAlert();
  const navigate = useNavigate();

  const onSubmit = async (data: RegisterForm) => {
    try {
      await authRegister(data.email, data.password, data.username);
      navigate(LOGIN_PATH);
      showAlert({
        variant: "success",
        title: "Registration successful",
      });
    } catch (error) {
      const axiosError = error as AxiosError<AuthRegisterResponse>;
      showAlert({
        variant: "error",
        title: "Registration failed",
        description: axiosError.response?.data.message || "Registration failed",
      });
    }
  };

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterForm>();

  return (
    <div className="w-full h-full">
      <h1 className="text-4xl font-bold mb-8 text-center">Register</h1>

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
          {...register("password", {
            required: "Password is required",
          })}
          errorMessage={errors.password?.message}
        />

        <Input
          type="text"
          label="Username"
          placeholder="Enter your username"
          required
          {...register("username", {
            required: "Username is required",
          })}
          errorMessage={errors.username?.message}
        />

        <Button type="submit" label="Create Account" />
      </form>

      <p className="text-end text-base">
        Already have an account?{" "}
        <Link to={LOGIN_PATH} className="text-blue-500 hover:underline">
          Login here
        </Link>
      </p>
    </div>
  );
};
