import { useForm } from "react-hook-form";
import { Button, Input } from "../../components/ui";
import { Link } from "react-router-dom";
import { REGISTER_PATH } from "../../path";

type LoginForm = {
  email: string;
  password: string;
};

export const LoginContainer = () => {
  const onSubmit = (data: LoginForm) => {
    console.log(data);
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
          {...register("email", { required: "Email Address is required" })}
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
