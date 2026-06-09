"use client";

import { useState } from "react";

export function ContactForm() {
  const [submitted, setSubmitted] = useState(false);

  return (
    <form
      className="grid gap-4"
      onSubmit={(event) => {
        event.preventDefault();
        setSubmitted(true);
      }}
    >
      <input className="rounded-2xl border border-white/10 bg-black/30 px-5 py-4 text-white outline-none transition placeholder:text-white/35 focus:border-earth-accent/60 focus:ring-4 focus:ring-earth-accent/10" name="name" placeholder="Name" required />
      <input className="rounded-2xl border border-white/10 bg-black/30 px-5 py-4 text-white outline-none transition placeholder:text-white/35 focus:border-earth-accent/60 focus:ring-4 focus:ring-earth-accent/10" name="email" placeholder="Email" required type="email" />
      <textarea className="min-h-36 rounded-2xl border border-white/10 bg-black/30 px-5 py-4 text-white outline-none transition placeholder:text-white/35 focus:border-earth-accent/60 focus:ring-4 focus:ring-earth-accent/10" name="message" placeholder="Message" required />
      <button className="rounded-2xl bg-earth-accent px-5 py-4 text-sm font-black uppercase tracking-[0.16em] text-[#06100b] shadow-green-glow transition hover:-translate-y-0.5" type="submit">
        Submit
      </button>
      {submitted ? (
        <p className="rounded-2xl border border-earth-accent/30 bg-earth-accent/10 px-4 py-3 text-sm font-bold text-lime-200">
          Message received. Thanks for contacting Earth Living.
        </p>
      ) : null}
    </form>
  );
}
