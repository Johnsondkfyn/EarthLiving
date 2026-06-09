"use client";

import Link from "next/link";
import { useState } from "react";
import { discordUrl, navigationLinks } from "@/data/navigation";
import { Button } from "@/components/Button";

export function MobileMenu() {
  const [isOpen, setIsOpen] = useState(false);

  return (
    <div className="md:hidden">
      <button
        aria-expanded={isOpen}
        aria-label="Toggle navigation menu"
        className="inline-flex h-11 w-11 items-center justify-center rounded-full border border-white/10 bg-white/5 text-earth-text"
        type="button"
        onClick={() => setIsOpen((value) => !value)}
      >
        <span className="sr-only">Menu</span>
        <span className="flex flex-col gap-1.5">
          <span className="block h-0.5 w-5 rounded-full bg-current" />
          <span className="block h-0.5 w-5 rounded-full bg-current" />
          <span className="block h-0.5 w-5 rounded-full bg-current" />
        </span>
      </button>

      {isOpen ? (
        <div className="absolute left-4 right-4 top-20 rounded-3xl border border-white/10 bg-earth-card/95 p-4 shadow-2xl shadow-black/50 backdrop-blur-xl">
          <nav className="grid gap-1 text-sm font-semibold text-earth-muted" aria-label="Mobile navigation">
            {navigationLinks.map((link) => (
              <Link
                className="rounded-2xl px-4 py-3 transition hover:bg-white/5 hover:text-earth-text"
                href={link.href}
                key={link.href}
                onClick={() => setIsOpen(false)}
              >
                {link.label}
              </Link>
            ))}
          </nav>
          <Button className="mt-4 w-full" href={discordUrl}>
            Join Discord
          </Button>
        </div>
      ) : null}
    </div>
  );
}
