import type { HTMLAttributes, ReactNode } from "react";

type CardProps = HTMLAttributes<HTMLDivElement> & {
  children: ReactNode;
};

export function Card({ children, className = "", ...props }: CardProps) {
  return (
    <div
      className={`rounded-3xl border border-white/10 bg-earth-card/90 p-6 shadow-2xl shadow-black/30 ${className}`}
      {...props}
    >
      {children}
    </div>
  );
}
