import type { HTMLAttributes, ReactNode } from "react";

type SectionContainerProps = HTMLAttributes<HTMLElement> & {
  children: ReactNode;
};

export function SectionContainer({
  children,
  className = "",
  ...props
}: SectionContainerProps) {
  return (
    <section className={`mx-auto w-full max-w-7xl px-6 ${className}`} {...props}>
      {children}
    </section>
  );
}
