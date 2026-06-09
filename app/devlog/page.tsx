import { Card } from "@/components/Card";
import { Footer } from "@/components/Footer";
import { Navbar } from "@/components/Navbar";
import { devlogEntries } from "@/data/devlog";

const statusStyles: Record<string, string> = {
  Done: "border-earth-accent/40 bg-earth-accent/15 text-lime-300",
  "In Progress": "border-yellow-400/40 bg-yellow-400/15 text-yellow-300",
  Planned: "border-blue-400/40 bg-blue-400/15 text-blue-300",
};

export default function DevlogPage() {
  return (
    <main className="min-h-screen bg-[#020912] text-white">
      <Navbar />
      <section className="relative overflow-hidden border-b border-white/10 px-6 py-20">
        <div className="absolute left-10 top-8 h-72 w-72 rounded-full bg-earth-accent/10 blur-3xl" />
        <div className="relative mx-auto max-w-7xl">
          <p className="text-xs font-black uppercase tracking-[0.35em] text-earth-accent">Devlog</p>
          <h1 className="mt-5 max-w-4xl text-5xl font-black uppercase leading-none tracking-[-0.06em] md:text-7xl">
            Development journal.
          </h1>
          <p className="mt-6 max-w-3xl text-lg leading-8 text-white/68">
            Korte opdateringer fra arbejdet med website, Fabric test server, Earth map migration og modpack prototype.
          </p>
        </div>
      </section>

      <section className="px-6 py-14">
        <div className="mx-auto grid max-w-7xl gap-5 md:grid-cols-2">
          {devlogEntries.map((entry) => (
            <Card className="group relative overflow-hidden transition duration-300 hover:-translate-y-1 hover:border-earth-accent/45" key={entry.title}>
              <div className="absolute -right-16 -top-16 h-36 w-36 rounded-full bg-earth-accent/10 blur-3xl transition group-hover:bg-earth-accent/20" />
              <div className="relative flex items-start justify-between gap-4">
                <div>
                  <p className="text-xs font-black uppercase tracking-[0.22em] text-earth-accent">{entry.date}</p>
                  <h2 className="mt-3 text-2xl font-black uppercase tracking-[-0.02em]">{entry.title}</h2>
                </div>
                <span className={`rounded-full border px-3 py-1 text-[0.68rem] font-black uppercase tracking-[0.14em] ${statusStyles[entry.status]}`}>
                  {entry.status}
                </span>
              </div>
              <p className="relative mt-5 text-sm leading-7 text-white/68">{entry.text}</p>
            </Card>
          ))}
        </div>
      </section>
      <Footer />
    </main>
  );
}
