import Link from "next/link";
import type { AnchorHTMLAttributes, ReactNode } from "react";

type ButtonProps = AnchorHTMLAttributes<HTMLAnchorElement> & {
  children: ReactNode;
  href: string;
  variant?: "primary" | "secondary";
};

export function Button({
  children,
  className = "",
  href,
  variant = "primary",
  ...props
}: ButtonProps) {
  const styles =
    variant === "primary"
      ? "bg-earth-accent text-earth-background shadow-green-glow"
      : "border border-white/10 bg-white/5 text-earth-text hover:border-earth-accent/50";

  return (
    <Link
      className={`inline-flex min-h-11 items-center justify-center rounded-full px-5 py-2.5 text-sm font-bold transition hover:-translate-y-0.5 ${styles} ${className}`}
      href={href}
      {...props}
    >
      {children}
    </Link>
  );
}
