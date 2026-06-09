import { Button } from "@/components/Button";
import { Card } from "@/components/Card";
import { Footer } from "@/components/Footer";
import { Navbar } from "@/components/Navbar";
import { panelUrl } from "@/data/navigation";

const servers = [
  {
    name: "Earth Living Main",
    status: "Production",
    text: "Live Paper server. Do not modify during Fabric migration work.",
  },
  {
    name: "Earth Living Fabric Test",
    status: "Testing",
    text: "Isolated Minecraft 1.20.1 Fabric test server with Java 21 and modpack foundation.",
  },
  {
    name: "Backups & Migration",
    status: "Protected",
    text: "Use Pterodactyl for controlled stops, backups and server administration.",
  },
];

export default function PanelPage() {
  return (
    <main className="min-h-screen bg-[#020912] text-white">
      <Navbar />
      <section className="relative overflow-hidden border-b border-white/10 px-6 py-20">
        <div className="absolute right-0 top-0 h-96 w-96 rounded-full bg-earth-accent/10 blur-3xl" />
        <div className="absolute inset-0 opacity-20 [background-image:linear-gradient(rgba(255,255,255,0.07)_1px,transparent_1px),linear-gradient(90deg,rgba(255,255,255,0.05)_1px,transparent_1px)] [background-size:48px_48px]" />

        <div className="relative mx-auto grid max-w-7xl gap-8 lg:grid-cols-[1fr_0.72fr] lg:items-center">
          <div>
            <p className="text-xs font-black uppercase tracking-[0.35em] text-earth-accent">
              Owner access
            </p>
            <h1 className="mt-5 max-w-4xl text-5xl font-black uppercase leading-none tracking-[-0.06em] md:text-7xl">
              Earth Living control panel.
            </h1>
            <p className="mt-6 max-w-3xl text-lg leading-8 text-white/68">
              Owner portal til Pterodactyl server administration. Brug panelet til at starte, stoppe, tage backups og styre Fabric testserveren.
            </p>
            <div className="mt-8 flex flex-col gap-3 sm:flex-row">
              <Button href={panelUrl} target="_blank" rel="noreferrer">
                Log ind på panel
              </Button>
              <Button href="/roadmap" variant="secondary">
                Se roadmap
              </Button>
            </div>
          </div>

          <Card className="relative overflow-hidden bg-[#071522]/90 backdrop-blur-xl">
            <div className="absolute -right-20 -top-20 h-44 w-44 rounded-full bg-earth-accent/15 blur-3xl" />
            <p className="relative text-xs font-black uppercase tracking-[0.28em] text-earth-accent">
              Admin backend
            </p>
            <h2 className="relative mt-4 text-3xl font-black uppercase tracking-[-0.04em]">
              Pterodactyl
            </h2>
            <p className="relative mt-4 text-sm leading-7 text-white/66">
              Panelet åbner i en ny fane for at holde login-sessionen isoleret fra den offentlige hjemmeside.
            </p>
            <div className="relative mt-6 rounded-2xl border border-white/10 bg-black/25 p-4 text-sm text-white/70">
              Current panel URL:
              <br />
              <span className="break-all font-bold text-white">{panelUrl}</span>
            </div>
          </Card>
        </div>
      </section>

      <section className="px-6 py-14">
        <div className="mx-auto grid max-w-7xl gap-5 md:grid-cols-3">
          {servers.map((server) => (
            <Card className="transition duration-300 hover:-translate-y-1 hover:border-earth-accent/45" key={server.name}>
              <span className="rounded-full border border-earth-accent/30 bg-earth-accent/10 px-3 py-1 text-[0.68rem] font-black uppercase tracking-[0.14em] text-lime-300">
                {server.status}
              </span>
              <h2 className="mt-5 text-xl font-black uppercase tracking-[-0.02em]">{server.name}</h2>
              <p className="mt-3 text-sm leading-7 text-white/66">{server.text}</p>
            </Card>
          ))}
        </div>
      </section>
      <Footer />
    </main>
  );
}
