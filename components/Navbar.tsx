import Link from "next/link";
import { MobileMenu } from "@/components/MobileMenu";
import { Button } from "@/components/Button";
import { discordUrl, navigationLinks } from "@/data/navigation";

export function Navbar() {
  return (
    <header className="sticky top-0 z-50 border-b border-white/10 bg-earth-background/80 px-6 py-4 backdrop-blur-xl">
      <div className="mx-auto flex max-w-7xl items-center justify-between gap-6">
        <div className="flex items-center gap-3">
          <Link className="text-sm font-black uppercase tracking-[0.28em] text-earth-accent" href="/">
            Earth Living
          </Link>
        </div>

        <nav className="hidden items-center gap-6 text-sm font-semibold text-earth-muted md:flex" aria-label="Main navigation">
          {navigationLinks.map((link) => (
            <Link className="transition hover:text-earth-text" href={link.href} key={link.href}>
              {link.label}
            </Link>
          ))}
        </nav>

        <div className="hidden md:block">
          <Button href={discordUrl}>Join Discord</Button>
        </div>

        <MobileMenu />
      </div>
    </header>
  );
}
