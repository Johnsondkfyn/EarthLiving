import { Button } from "@/components/Button";
import { Footer } from "@/components/Footer";
import { Navbar } from "@/components/Navbar";
import { SectionContainer } from "@/components/SectionContainer";
import { StatusBadge } from "@/components/StatusBadge";

export default function HomePage() {
  return (
    <main className="min-h-screen overflow-hidden bg-earth-background text-earth-text">
      <Navbar />
      <section className="relative isolate min-h-[calc(100vh-77px)]">
        <div className="absolute inset-0 -z-10 bg-[radial-gradient(circle_at_20%_20%,rgba(34,197,94,0.22),transparent_34rem),radial-gradient(circle_at_80%_10%,rgba(34,197,94,0.12),transparent_28rem),linear-gradient(180deg,rgba(11,15,20,0)_0%,#0B0F14_100%)]" />
        <div className="absolute left-1/2 top-24 -z-10 h-72 w-72 -translate-x-1/2 rounded-full bg-earth-accent/20 blur-[120px]" />
        <div className="absolute inset-x-0 bottom-0 -z-10 h-px bg-gradient-to-r from-transparent via-earth-accent/40 to-transparent" />

        <SectionContainer className="flex min-h-[calc(100vh-77px)] items-center py-24">
          <div className="mx-auto max-w-5xl text-center">
            <p className="text-sm font-semibold uppercase tracking-[0.45em] text-earth-accent">
              Minecraft Earth Project
            </p>
            <h1 className="mt-7 text-6xl font-black uppercase leading-[0.85] tracking-[-0.08em] text-earth-text md:text-8xl lg:text-9xl">
              EARTH LIVING
            </h1>
            <h2 className="mx-auto mt-6 max-w-3xl text-4xl font-black leading-none tracking-[-0.05em] text-earth-text md:text-6xl">
              Build the Future.
              <br />
              Together.
            </h2>
            <p className="mx-auto mt-7 max-w-2xl text-lg leading-8 text-earth-muted md:text-xl">
              A living Earth server where transport, economy, cities and people create a dynamic society.
            </p>

            <div className="mt-10 flex flex-col items-center justify-center gap-4 sm:flex-row">
              <Button className="w-full sm:w-auto" href="/roadmap">
                View Roadmap
              </Button>
              <Button className="w-full sm:w-auto" href="/newsletter" variant="secondary">
                Join Newsletter
              </Button>
            </div>

            <div className="mt-10 flex flex-wrap items-center justify-center gap-3">
              <StatusBadge label="Server In Development" />
              <StatusBadge label="Beta Not Open" />
            </div>
          </div>
        </SectionContainer>
      </section>
      <Footer />
    </main>
  );
}
