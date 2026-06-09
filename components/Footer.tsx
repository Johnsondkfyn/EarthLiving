import Link from "next/link";
import { discordUrl, navigationLinks } from "@/data/navigation";
import { Button } from "@/components/Button";

export function Footer() {
  return (
    <footer className="border-t border-white/10 px-6 py-10 text-sm text-earth-muted">
      <div className="mx-auto grid max-w-7xl gap-8 md:grid-cols-[1fr_auto] md:items-start">
        <div>
          <Link className="font-black uppercase tracking-[0.28em] text-earth-accent" href="/">
            Earth Living
          </Link>
          <p className="mt-3 max-w-xl">
            A single persistent Fabric-based Earth world. Build the Future. Together.
          </p>
        </div>

        <div className="flex flex-col gap-5 md:items-end">
          <nav className="flex flex-wrap gap-x-5 gap-y-3 font-semibold" aria-label="Footer navigation">
            {navigationLinks.map((link) => (
              <Link className="transition hover:text-earth-text" href={link.href} key={link.href}>
                {link.label}
              </Link>
            ))}
          </nav>
          <Button href={discordUrl} variant="secondary">
            Join Discord
          </Button>
        </div>
      </div>
    </footer>
  );
}
