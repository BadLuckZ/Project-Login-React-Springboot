import * as React from "react";

import { cn } from "../../lib/utils";
import { EyeOffIcon, EyeIcon } from "lucide-react";

type InputProps = React.ComponentProps<"input"> & {
  label?: string;
  required?: boolean;
  errorMessage?: string;
};

const Input = React.forwardRef<HTMLInputElement, InputProps>(
  (
    { className, type, label, placeholder, required, errorMessage, ...props },
    ref,
  ) => {
    const [showPassword, setShowPassword] = React.useState(type === "password");
    return (
      <div className="flex flex-col">
        {label && (
          <label className="text-lg font-medium mb-2">
            {label} {required && <span className="text-destructive">*</span>}
          </label>
        )}

        <div className="relative mb-3">
          <input
            ref={ref}
            type={
              type != "password" ? type : showPassword ? "text" : "password"
            }
            data-slot="input"
            placeholder={placeholder}
            className={cn(
              "h-10 w-full min-w-0 placeholder:text-sm rounded-3xl pr-10 border border-transparent bg-input/50 px-3 py-1 text-base transition-[color,box-shadow,background-color] outline-none file:inline-flex file:h-7 file:border-0 file:bg-transparent file:text-sm file:font-medium file:text-foreground placeholder:text-muted-foreground focus-visible:border-ring focus-visible:ring-3 focus-visible:ring-ring/30 disabled:pointer-events-none disabled:cursor-not-allowed disabled:opacity-50 aria-invalid:border-destructive aria-invalid:ring-3 aria-invalid:ring-destructive/20 md:text-sm dark:aria-invalid:border-destructive/50 dark:aria-invalid:ring-destructive/40",
              className,
            )}
            {...props}
          />

          {type === "password" && (
            <span
              className="absolute right-3 top-1/2 -translate-y-1/2 cursor-pointer"
              onClick={() => setShowPassword((prev) => !prev)}
            >
              {showPassword ? <EyeIcon size={20} /> : <EyeOffIcon size={20} />}
            </span>
          )}
        </div>

        {errorMessage && (
          <p className="text-sm font-medium text-destructive">{errorMessage}</p>
        )}
      </div>
    );
  },
);

Input.displayName = "Input";

export { Input };
