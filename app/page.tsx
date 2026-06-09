import Image from "next/image";
import Link from "next/link";

const featureCards = [
  {
    icon: TrainIcon,
    title: "Transport",
    text: "Tog, metro, busser og skibsruter forbinder byer og lande.",
  },
  {
    icon: CityIcon,
    title: "Byer",
    text: "Byg, udvikl og administrer smarte byer med butikker, boliger og infrastruktur.",
  },
  {
    icon: FactoryIcon,
    title: "Virksomheder",
    text: "Start din egen virksomhed, producer varer og skab arbejdspladser.",
  },
  {
    icon: EconomyIcon,
    title: "Økonomi",
    text: "Dynamisk marked med udbud og efterspørgsel drevet af spillere.",
  },
  {
    icon: PeopleIcon,
    title: "Mennesker & NPCs",
    text: "NPC-turister, arbejdere og spillere giver verden liv og aktivitet.",
  },
  {
    icon: PhoneIcon,
    title: "Telefon",
    text: "Din in-game telefon giver adgang til alt det vigtige.",
  },
];

const imagePanels = [
  { title: "Transport", src: "/earthliving-panels/transport.png" },
  { title: "Cities", src: "/earthliving-panels/city.png" },
  { title: "Industry", src: "/earthliving-panels/industry.png" },
  { title: "Society", src: "/earthliving-panels/society.png" },
  { title: "Tourism", src: "/earthliving-panels/harbor.png" },
  { title: "EarthPhone", src: "/earthliving-panels/earthphone.png" },
];

const roadmap = [
  {
    phase: "Fase 1",
    items: ["Infrastruktur", "Modpack", "Grundlæggende økonomi", "Hjemmeside"],
    state: "I gang",
    color: "bg-yellow-400",
  },
  {
    phase: "Fase 2",
    items: ["Lukket beta", "Transportsystem", "Jobs & virksomheder"],
    state: "Kommer snart",
    color: "bg-orange-400",
  },
  {
    phase: "Fase 3",
    items: ["Åben beta", "Flere byer", "Avancerede systemer"],
    state: "Planlagt",
    color: "bg-blue-400",
  },
  {
    phase: "Fase 4",
    items: ["NPC-systemer", "Turisme", "Events & aktiviteter"],
    state: "Fremtid",
    color: "bg-slate-400",
  },
];

const statusItems = [
  { label: "Earth Map", value: "Færdig", done: true },
  { label: "Server", value: "Færdig", done: true },
  { label: "Modpack", value: "Under udvikling", active: true },
  { label: "Hjemmeside", value: "Under udvikling", active: true },
  { label: "Telefon-system", value: "Planlagt" },
  { label: "Lukket Beta", value: "Planlagt" },
];

export default function HomePage() {
  return (
    <main className="min-h-screen bg-[#020912] text-white">
      <div className="mx-auto flex min-h-screen max-w-[1440px] flex-col overflow-hidden border-x border-white/10 bg-[#03101b] shadow-2xl shadow-black">
        <HeroHeader />
        <PanelShowcase />
        <FeatureGrid />
        <section className="grid border-y border-white/10 lg:grid-cols-[1.5fr_0.58fr_0.45fr]">
          <RoadmapPanel />
          <StatusPanel />
          <FollowPanel />
        </section>
        <BottomBar />
      </div>
    </main>
  );
}

function HeroHeader() {
  return (
    <header className="grid gap-8 border-b border-white/10 bg-[#020912] px-7 py-6 md:grid-cols-[0.9fr_1.4fr] md:items-center">
      <Link className="flex items-center gap-5" href="/">
        <Image
          alt="Earth Living"
          className="h-24 w-24 rounded-2xl object-contain drop-shadow-[0_0_30px_rgba(34,197,94,0.28)]"
          height={128}
          priority
          src="/earthliving-logo.png"
          width={128}
        />
        <div>
          <p className="text-4xl font-black uppercase leading-[0.85] tracking-[-0.06em] md:text-5xl">
            Earth
            <br />
            Living
          </p>
          <p className="mt-2 text-xs font-black uppercase tracking-[0.2em] text-earth-accent">
            A living Earth server
          </p>
        </div>
      </Link>

      <div className="text-center md:pr-20">
        <p className="bg-gradient-to-r from-earth-accent via-lime-200 to-amber-300 bg-clip-text text-5xl font-black uppercase tracking-[0.08em] text-transparent md:text-6xl">
          Vision 1
        </p>
        <p className="mx-auto mt-3 max-w-3xl text-lg font-semibold leading-7 text-white/90">
          Vi bygger en levende Earth-verden i Minecraft, hvor transport, økonomi, byer og mennesker skaber et dynamisk samfund.
        </p>
      </div>
    </header>
  );
}

function PanelShowcase() {
  return (
    <section className="grid min-h-[345px] grid-cols-1 overflow-hidden border-b border-white/10 md:grid-cols-6">
      {imagePanels.map((panel, index) => (
        <article
          className="group relative min-h-[230px] overflow-hidden bg-slate-900 md:min-h-[345px] md:-skew-x-[8deg] md:border-r md:border-black/70"
          key={panel.title}
        >
          <Image
            alt={`${panel.title} concept art`}
            className="h-full w-full object-cover transition duration-700 group-hover:scale-105 md:skew-x-[8deg] md:scale-125"
            fill
            sizes="(min-width: 768px) 17vw, 100vw"
            src={panel.src}
          />
          <div className="absolute inset-0 bg-[linear-gradient(135deg,rgba(255,255,255,0.18),transparent_35%),radial-gradient(circle_at_50%_30%,rgba(34,197,94,0.16),transparent_18rem)]" />
          <div className="absolute inset-0 bg-black/10" />
          <div className="absolute inset-x-0 bottom-0 h-28 bg-gradient-to-t from-black/80 to-transparent" />
          <div className="absolute inset-0 opacity-20 [background-image:linear-gradient(rgba(255,255,255,0.08)_1px,transparent_1px),linear-gradient(90deg,rgba(255,255,255,0.06)_1px,transparent_1px)] [background-size:42px_42px]" />
          <div className="absolute bottom-5 left-6 md:skew-x-[8deg]">
            <p className="text-xs font-black uppercase tracking-[0.25em] text-white/70">
              0{index + 1}
            </p>
            <h2 className="mt-1 text-2xl font-black uppercase tracking-tight">
              {panel.title}
            </h2>
          </div>
        </article>
      ))}
    </section>
  );
}

function FeatureGrid() {
  return (
    <section className="grid border-b border-white/10 bg-[#05131f] md:grid-cols-2 xl:grid-cols-6">
      {featureCards.map((feature) => {
        const Icon = feature.icon;
        return (
          <article className="border-b border-r border-white/10 p-7 xl:border-b-0" key={feature.title}>
            <Icon className="h-9 w-9 text-white/75" />
            <h3 className="mt-5 text-sm font-black uppercase tracking-[0.12em] text-white">
              {feature.title}
            </h3>
            <p className="mt-3 text-sm leading-6 text-white/72">{feature.text}</p>
          </article>
        );
      })}
    </section>
  );
}

function RoadmapPanel() {
  return (
    <section className="border-r border-white/10 bg-[#04101b] p-8">
      <h2 className="text-xl font-black uppercase tracking-[0.08em]">Roadmap</h2>
      <div className="mt-8 hidden items-center gap-0 md:flex">
        {[0, 1, 2, 3].map((item) => (
          <div className="flex flex-1 items-center" key={item}>
            <span className={`h-4 w-4 rounded-full ${item === 0 ? "bg-yellow-400" : "bg-white/60"}`} />
            <span className="h-1 flex-1 bg-white/25" />
          </div>
        ))}
      </div>
      <div className="mt-6 grid gap-8 md:grid-cols-4">
        {roadmap.map((phase) => (
          <article key={phase.phase}>
            <h3 className="text-lg font-black uppercase text-yellow-300">{phase.phase}</h3>
            <ul className="mt-3 space-y-1 text-sm leading-6 text-white/85">
              {phase.items.map((item) => (
                <li key={item}>- {item}</li>
              ))}
            </ul>
            <p className="mt-5 flex items-center gap-2 text-sm font-black uppercase tracking-[0.12em] text-white/70">
              <span className={`h-3 w-3 rounded-full ${phase.color}`} />
              {phase.state}
            </p>
          </article>
        ))}
      </div>
    </section>
  );
}

function StatusPanel() {
  return (
    <section className="border-r border-white/10 bg-[#06121e] p-8">
      <h2 className="text-xl font-black uppercase tracking-[0.08em]">Status</h2>
      <div className="mt-6 space-y-3">
        {statusItems.map((item) => (
          <div className="grid grid-cols-[1.2rem_1fr_auto] items-center gap-3 text-sm" key={item.label}>
            <span
              className={`grid h-5 w-5 place-items-center rounded-md border ${
                item.done
                  ? "border-earth-accent/50 bg-earth-accent/35 text-white"
                  : item.active
                    ? "border-yellow-400/50 bg-yellow-400/25"
                    : "border-white/20 bg-white/5"
              }`}
            >
              {item.done ? "✓" : ""}
            </span>
            <span className="font-bold text-white/88">{item.label}</span>
            <span className={`text-xs font-black uppercase ${item.active ? "text-yellow-300" : item.done ? "text-lime-300" : "text-white/55"}`}>
              {item.value}
            </span>
          </div>
        ))}
      </div>
    </section>
  );
}

function FollowPanel() {
  return (
    <section className="bg-[#06121e] p-8">
      <h2 className="text-xl font-black uppercase tracking-[0.08em]">Følg udviklingen</h2>
      <p className="mt-5 text-sm leading-6 text-white/72">
        Vi opdaterer løbende med nyheder, devlogs og milepæle.
      </p>
      <div className="mt-7 rounded-2xl border border-white/10 bg-white/5 p-4 text-sm text-white/72">
        Tilmeld dig for at blive beta-tester når vi åbner.
      </div>
      <Link
        className="mt-6 inline-flex w-full items-center justify-center rounded-lg bg-earth-accent px-5 py-4 text-sm font-black uppercase tracking-[0.12em] text-[#07110b] shadow-green-glow transition hover:-translate-y-0.5"
        href="/newsletter"
      >
        Tilmeld dig
      </Link>
    </section>
  );
}

function BottomBar() {
  return (
    <footer className="flex flex-col gap-3 bg-[#020912] px-7 py-4 text-sm text-white/70 md:flex-row md:items-center md:justify-between">
      <span>Earth Living - Byg fremtiden sammen med os.</span>
      <Link className="font-black text-white" href="/">
        earthliving.earth
      </Link>
      <span>En verden. Uendelige muligheder.</span>
    </footer>
  );
}

type IconProps = {
  className?: string;
};

function TrainIcon({ className }: IconProps) {
  return (
    <svg className={className} fill="none" viewBox="0 0 48 48" xmlns="http://www.w3.org/2000/svg">
      <path d="M14 7h20a6 6 0 0 1 6 6v18a6 6 0 0 1-6 6H14a6 6 0 0 1-6-6V13a6 6 0 0 1 6-6Z" stroke="currentColor" strokeWidth="3" />
      <path d="M14 17h20M16 37l-4 6M32 37l4 6" stroke="currentColor" strokeLinecap="round" strokeWidth="3" />
      <circle cx="17" cy="29" r="2.5" fill="currentColor" />
      <circle cx="31" cy="29" r="2.5" fill="currentColor" />
    </svg>
  );
}

function CityIcon({ className }: IconProps) {
  return (
    <svg className={className} fill="none" viewBox="0 0 48 48" xmlns="http://www.w3.org/2000/svg">
      <path d="M8 42V18l12-6v30M20 42V8h14v34M34 42V22l6 4v16" stroke="currentColor" strokeWidth="3" />
      <path d="M14 24h1M14 31h1M26 15h2M26 22h2M26 29h2" stroke="currentColor" strokeLinecap="round" strokeWidth="3" />
    </svg>
  );
}

function FactoryIcon({ className }: IconProps) {
  return (
    <svg className={className} fill="none" viewBox="0 0 48 48" xmlns="http://www.w3.org/2000/svg">
      <path d="M7 42V25l11 6v-6l11 6V13h10v29H7Z" stroke="currentColor" strokeLinejoin="round" strokeWidth="3" />
      <path d="M14 36h3M24 36h3M34 36h3" stroke="currentColor" strokeLinecap="round" strokeWidth="3" />
    </svg>
  );
}

function EconomyIcon({ className }: IconProps) {
  return (
    <svg className={className} fill="none" viewBox="0 0 48 48" xmlns="http://www.w3.org/2000/svg">
      <path d="M8 38h32M13 32l8-8 6 5 10-14" stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="3" />
      <path d="M31 15h6v6" stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="3" />
    </svg>
  );
}

function PeopleIcon({ className }: IconProps) {
  return (
    <svg className={className} fill="none" viewBox="0 0 48 48" xmlns="http://www.w3.org/2000/svg">
      <circle cx="18" cy="17" r="7" stroke="currentColor" strokeWidth="3" />
      <circle cx="33" cy="20" r="5" stroke="currentColor" strokeWidth="3" />
      <path d="M6 40c2-8 7-12 12-12s10 4 12 12M27 32c4 1 7 4 9 8" stroke="currentColor" strokeLinecap="round" strokeWidth="3" />
    </svg>
  );
}

function PhoneIcon({ className }: IconProps) {
  return (
    <svg className={className} fill="none" viewBox="0 0 48 48" xmlns="http://www.w3.org/2000/svg">
      <rect height="34" rx="4" stroke="currentColor" strokeWidth="3" width="20" x="14" y="7" />
      <path d="M21 34h6" stroke="currentColor" strokeLinecap="round" strokeWidth="3" />
    </svg>
  );
}
