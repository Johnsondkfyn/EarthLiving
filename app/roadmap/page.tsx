import { Footer } from "@/components/Footer";
import { Navbar } from "@/components/Navbar";
import { roadmapItems, type RoadmapStatus } from "@/data/roadmap";

const statusStyles: Record<RoadmapStatus, string> = {
  Done: "border-earth-accent/40 bg-earth-accent/15 text-lime-300",
  "In Progress": "border-yellow-400/40 bg-yellow-400/15 text-yellow-300",
  Planned: "border-blue-400/40 bg-blue-400/15 text-blue-300",
  Future: "border-white/15 bg-white/5 text-white/55",
};

const dotStyles: Record<RoadmapStatus, string> = {
  Done: "bg-earth-accent shadow-[0_0_24px_rgba(34,197,94,0.55)]",
  "In Progress": "bg-yellow-400 shadow-[0_0_24px_rgba(250,204,21,0.45)]",
  Planned: "bg-blue-400 shadow-[0_0_24px_rgba(96,165,250,0.45)]",
  Future: "bg-slate-500",
};

export default function RoadmapPage() {
  return (
    <main className="min-h-screen bg-[#020912] text-white">
      <Navbar />
      <section className="relative overflow-hidden border-b border-white/10 px-6 py-20">
        <div className="absolute left-1/2 top-0 h-96 w-96 -translate-x-1/2 rounded-full bg-earth-accent/10 blur-3xl" />
        <div className="relative mx-auto max-w-7xl">
          <p className="text-xs font-black uppercase tracking-[0.35em] text-earth-accent">
            Earth Living roadmap
          </p>
          <h1 className="mt-5 max-w-4xl text-5xl font-black uppercase leading-none tracking-[-0.06em] md:text-7xl">
            From foundation to public beta.
          </h1>
          <p className="mt-6 max-w-3xl text-lg leading-8 text-white/68">
            Roadmapet viser den planlagte udvikling fra website foundation til Fabric infrastructure, closed beta og public beta.
          </p>
        </div>
      </section>

      <section className="px-6 py-14">
        <div className="mx-auto max-w-7xl">
          <div className="grid gap-6 lg:grid-cols-[0.34fr_1fr]">
            <aside className="rounded-3xl border border-white/10 bg-white/[0.04] p-6 shadow-2xl shadow-black/25">
              <h2 className="text-xl font-black uppercase tracking-[0.1em]">Status</h2>
              <div className="mt-6 grid gap-3">
                {(["Done", "In Progress", "Planned", "Future"] as RoadmapStatus[]).map((status) => (
                  <div className="flex items-center justify-between gap-3 rounded-2xl border border-white/10 bg-black/20 px-4 py-3" key={status}>
                    <span className="flex items-center gap-3 text-sm font-bold text-white/76">
                      <span className={`h-3 w-3 rounded-full ${dotStyles[status]}`} />
                      {status}
                    </span>
                    <span className={`rounded-full border px-3 py-1 text-[0.68rem] font-black uppercase tracking-[0.14em] ${statusStyles[status]}`}>
                      {roadmapItems.filter((item) => item.status === status).length}
                    </span>
                  </div>
                ))}
              </div>
            </aside>

            <div className="relative">
              <div className="absolute bottom-6 left-6 top-6 hidden w-px bg-gradient-to-b from-earth-accent via-blue-400/50 to-white/10 md:block" />
              <div className="grid gap-5">
                {roadmapItems.map((item) => (
                  <article
                    className="group relative rounded-3xl border border-white/10 bg-[#071522]/86 p-6 shadow-2xl shadow-black/25 transition duration-300 hover:-translate-y-1 hover:border-earth-accent/45 hover:bg-[#0b1b2a]"
                    key={`${item.version}-${item.title}`}
                  >
                    <span className={`absolute left-[-0.15rem] top-8 hidden h-4 w-4 rounded-full ring-8 ring-[#020912] md:block ${dotStyles[item.status]}`} />
                    <div className="flex flex-col gap-4 md:flex-row md:items-start md:justify-between md:pl-8">
                      <div>
                        <p className="text-sm font-black uppercase tracking-[0.24em] text-earth-accent">
                          Version {item.version}
                        </p>
                        <h2 className="mt-3 text-2xl font-black uppercase tracking-[-0.02em] text-white md:text-3xl">
                          {item.title}
                        </h2>
                        <p className="mt-3 max-w-2xl text-sm leading-6 text-white/66">{item.description}</p>
                      </div>
                      <span className={`w-fit rounded-full border px-4 py-2 text-xs font-black uppercase tracking-[0.16em] ${statusStyles[item.status]}`}>
                        {item.status}
                      </span>
                    </div>
                  </article>
                ))}
              </div>
            </div>
          </div>
        </div>
      </section>
      <Footer />
    </main>
  );
}
