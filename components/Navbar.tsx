import Link from "next/link";

export function Navbar() {
  return (
    <header className="sticky top-0 z-50 border-b border-white/10 bg-earth-background/80 px-6 py-4 backdrop-blur-xl">
      <div className="mx-auto flex max-w-7xl items-center justify-between gap-6">
        <Link className="text-sm font-black uppercase tracking-[0.28em] text-earth-accent" href="/">
          Earth Living
        </Link>
        <nav className="hidden items-center gap-6 text-sm font-semibold text-earth-muted md:flex">
          <Link className="transition hover:text-earth-text" href="/">
            Home
          </Link>
        </nav>
      </div>
    </header>
  );
}
