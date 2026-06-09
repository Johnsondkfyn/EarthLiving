import { Button } from "@/components/Button";
import { Card } from "@/components/Card";
import { Footer } from "@/components/Footer";
import { Navbar } from "@/components/Navbar";
import { SectionContainer } from "@/components/SectionContainer";

export default function HomePage() {
  return (
    <main className="min-h-screen bg-earth-background text-earth-text">
      <Navbar />
      <SectionContainer className="py-24">
        <Card className="max-w-3xl">
          <p className="text-sm font-semibold uppercase tracking-[0.35em] text-earth-accent">
            Phase 1 Foundation
          </p>
          <h1 className="mt-6 text-5xl font-black tracking-tight md:text-7xl">
            Earth Living
          </h1>
          <p className="mt-5 max-w-2xl text-lg text-earth-muted">
            Build the Future. Together.
          </p>
          <div className="mt-8">
            <Button href="#">Foundation Ready</Button>
          </div>
        </Card>
      </SectionContainer>
      <Footer />
    </main>
  );
}
