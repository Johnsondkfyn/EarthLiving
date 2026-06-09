"use client";

import { useState } from "react";

export function NewsletterForm() {
  const [submitted, setSubmitted] = useState(false);

  return (
    <form
      className="mt-8 space-y-4"
      onSubmit={(event) => {
        event.preventDefault();
        setSubmitted(true);
      }}
    >
      <label className="block text-left text-sm font-bold text-white/76" htmlFor="newsletter-email">
        Email
      </label>
      <input
        className="w-full rounded-2xl border border-white/10 bg-black/30 px-5 py-4 text-white outline-none transition placeholder:text-white/35 focus:border-earth-accent/60 focus:ring-4 focus:ring-earth-accent/10"
        id="newsletter-email"
        name="email"
        placeholder="you@example.com"
        required
        type="email"
      />
      <button className="w-full rounded-2xl bg-earth-accent px-5 py-4 text-sm font-black uppercase tracking-[0.16em] text-[#06100b] shadow-green-glow transition hover:-translate-y-0.5" type="submit">
        Subscribe
      </button>
      {submitted ? (
        <p className="rounded-2xl border border-earth-accent/30 bg-earth-accent/10 px-4 py-3 text-sm font-bold text-lime-200">
          Thanks for subscribing.
        </p>
      ) : null}
    </form>
  );
}
