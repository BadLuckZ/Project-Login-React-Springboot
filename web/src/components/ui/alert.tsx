import * as React from "react";
import { cva, type VariantProps } from "class-variance-authority";
import { CheckCircle2, AlertCircle, Info, X } from "lucide-react";

import { cn } from "../../lib/utils";

const alertVariants = cva(
  "relative flex w-full items-start gap-3 rounded-2xl border px-4 py-4 text-sm",
  {
    variants: {
      variant: {
        neutral: "border-gray-200 bg-white text-gray-900",
        success: "border-green-200 bg-green-50 text-green-800",
        error: "border-red-200 bg-red-50 text-red-800",
      },
    },
    defaultVariants: {
      variant: "neutral",
    },
  },
);

const iconMap = {
  neutral: Info,
  success: CheckCircle2,
  error: AlertCircle,
};

type AlertProps = React.ComponentProps<"div"> &
  VariantProps<typeof alertVariants> & {
    duration?: number;
    onClose?: () => void;
  };

function Alert({
  className,
  variant = "neutral",
  duration,
  onClose,
  children,
  ...props
}: AlertProps) {
  const currentVariant = variant ?? "neutral";
  const Icon = iconMap[currentVariant];

  return (
    <div
      role="alert"
      className={cn(alertVariants({ variant: currentVariant }), className)}
      {...props}
    >
      <Icon className="mt-0.5 size-5 shrink-0" />

      <div className="flex-1">{children}</div>

      <button
        type="button"
        onClick={() => {
          onClose?.();
        }}
        className="opacity-70 transition hover:opacity-100"
      >
        <X className="size-4" />
      </button>
    </div>
  );
}

function AlertTitle({ className, ...props }: React.ComponentProps<"div">) {
  return <div className={cn("mb-1 font-semibold", className)} {...props} />;
}

function AlertDescription({
  className,
  ...props
}: React.ComponentProps<"div">) {
  return <div className={cn("text-sm opacity-90", className)} {...props} />;
}

export { Alert, AlertTitle, AlertDescription };
