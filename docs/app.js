const canvas = document.querySelector("#world-canvas");
const ctx = canvas.getContext("2d");
const viewToggle = document.querySelector(".view-toggle");
const languageToggle = document.querySelector(".language-toggle");

function applyRoadmapStatus(status) {
  if (!status) {
    return;
  }

  const values = {
    progressPercent: `${status.progressPercent}%`,
    completed: String(status.completed),
    inProgress: String(status.inProgress),
    planned: String(status.planned),
  };

  document.querySelectorAll("[data-roadmap-value]").forEach((element) => {
    const key = element.dataset.roadmapValue;
    if (values[key]) {
      element.textContent = values[key];
    }
  });
}

const translations = {
  en: {
    "nav.overview": "Overview",
    "nav.earthos": "EarthOS",
    "nav.systems": "Systems",
    "nav.roadmap": "Roadmap",
    "nav.community": "Community",
    "view.mobile": "Mobil",
    "view.desktop": "Desktop",
    "hero.eyebrow": "Earth-map MMO Minecraft server",
    "hero.lede": "A living civilization sandbox on an Earth map, built around EarthOS, transport, mining, reports, city growth, Discord integration, and long-term world simulation.",
    "hero.primary": "What is EarthLiving?",
    "hero.secondary": "View Roadmap",
    "hero.server.kicker": "Server snapshot",
    "hero.server.title": "Private test world",
    "hero.server.access": "Access",
    "hero.server.accessValue": "Invitation only",
    "hero.server.map": "Map",
    "hero.server.mapValue": "Earth map + BlueMap",
    "hero.server.focus": "Focus",
    "hero.server.focusValue": "Stability before launch",
    "hero.work.item1": "EarthOS player menu",
    "hero.work.item2": "Report flow and staff tools",
    "hero.work.item3": "Public website and roadmap",
    "hero.work.item4": "Player portal foundation",
    "stats.progress": "Progress",
    "stats.core": "Core",
    "stats.server": "Server",
    "stats.private": "Private test",
    "launch.pill": "In development",
    "launch.title": "Private foundation phase",
    "launch.body": "Main server, Discord connection, player reports, live map foundation and EarthOS basics are being shaped before public access opens.",
    "signals.map": "Earth map foundation",
    "signals.private": "Private foundation phase",
    "signals.systems": "Living-world systems",
    "signals.discord": "Discord connected",
    "signals.earthos": "EarthOS first apps",
    "overview.eyebrow": "Overview",
    "overview.title": "A serious world project, not a plugin pile.",
    "overview.body1": "EarthLiving is not just survival with extra plugins. It is planned as a civilization-style Minecraft experience where cities, transport, mining, tourism, nightlife, companies, infrastructure, and player actions all connect together.",
    "overview.body2": "The ambition is to keep developing EarthLiving far beyond a normal Minecraft server. We want to keep expanding the world, systems, and player experience with a level of detail, persistence, and creativity rarely seen in community servers.",
    "overview.card1.kicker": "Cities",
    "overview.card1.title": "Build places that matter",
    "overview.card1.body": "Cities grow through player builds, transport networks, services, businesses, landmarks, nightlife, reports and long-term planning.",
    "overview.card2.kicker": "Simulation",
    "overview.card2.title": "Regions can react",
    "overview.card2.body": "EarthPulse is planned to connect tourism, mining, traffic, reputation, safety, economy and infrastructure into one world state.",
    "overview.card3.kicker": "Earth map",
    "overview.card3.title": "Real geography, playable balance",
    "overview.card3.body": "The world uses Earth as a stage, while systems are tuned so players can enjoy the server without one perfect region dominating everything.",
    "earthos.title": "Your in-game operating system.",
    "earthos.body": "EarthLiving is designed to be GUI-first. Instead of memorizing long command lists, players use EarthOS from a hotbar device to access the server's systems.",
    "earthos.card1.kicker": "Apps",
    "earthos.card1.title": "Everything in one menu",
    "earthos.card1.body": "EarthOS is planned to include transport, maps, economy, reports, mining, nightlife, news, passport, language settings, and server status.",
    "earthos.card2.kicker": "Access",
    "earthos.card2.title": "No command-heavy gameplay",
    "earthos.card2.body": "Normal players should interact through menus, tickets, apps, dashboards, buttons, and visual feedback instead of command spam.",
    "earthos.card3.kicker": "Languages",
    "earthos.card3.title": "International by design",
    "earthos.card3.body": "New players will be able to choose their language on first join, with later changes available through EarthOS settings.",
    "plan.eyebrow": "Current build direction",
    "plan.title": "The next systems are already mapped.",
    "plan.body": "GitHub and Notion now split finished foundation work from the next playable systems, so the public site can show progress without exposing internal tooling.",
    "plan.item1": "Core foundation is live with EarthOS, reports, Discord hooks and panel workflow actions.",
    "plan.item2": "Turn the hotbar device into the normal player interface for reports, map, server status and apps.",
    "plan.item3": "Make reports traceable across Minecraft, panel, staff flow, GitHub and later AI-assisted fixes.",
    "plan.item4": "Start the secure player profile portal with one-time Minecraft account linking.",
    "systems.eyebrow": "Main systems",
    "systems.title": "More like a world than a normal server.",
    "systems.card1.kicker": "Transport",
    "systems.card1.title": "Travel matters",
    "systems.card1.body": "Trains, metros, ships, airports, stations, tickets, and logistics are planned as core parts of the world, not just teleport menus.",
    "systems.card2.kicker": "Mining",
    "systems.card2.title": "Regional resources",
    "systems.card2.body": "Mining uses realistic-inspired resource regions, licenses, deposits, depletion, and logistics while keeping gameplay fair.",
    "systems.card3.kicker": "Economy",
    "systems.card3.title": "Player-driven companies",
    "systems.card3.body": "Players may later operate mines, shops, hotels, clubs, factories, transport companies, airlines, and logistics networks.",
    "systems.card4.kicker": "Tourism",
    "systems.card4.title": "Landmarks and nightlife",
    "systems.card4.body": "Tourism responds to landmarks, transport, safety, culture, events, bars, clubs, DJs, and dancing NPCs in city nightlife areas.",
    "systems.card5.kicker": "Simulation",
    "systems.card5.title": "Regions develop identity",
    "systems.card5.body": "A region can become known for technology, tourism, mining, logistics, culture, business, or public transport.",
    "systems.card6.kicker": "AI",
    "systems.card6.title": "Future AI-assisted planning",
    "systems.card6.body": "Planned AI tools may help analyze reports, suggest city upgrades, detect infrastructure problems, and support development workflows.",
    "roadmap.eyebrow": "Roadmap",
    "roadmap.title": "Development is active and still early.",
    "roadmap.body1": "EarthLiving is being built step by step, starting with the core server foundation, EarthOS, reporting, world simulation planning, transport, mining, and long-term MMO-style systems.",
    "roadmap.body2": "This is intended to be a long-term project, not a one-time launch. The server will continue to evolve with new systems, deeper mechanics, better tools, and ongoing improvements shaped by the world and its players.",
    "roadmap.progress": "overall progress",
    "roadmap.completed": "Completed",
    "roadmap.inProgress": "In Progress",
    "roadmap.planned": "Planned",
    "roadmap.note": "Current phase: Core foundation is live, the website is being refined, and the first player portal scope is being planned. Progress is an early estimate and will change as systems move from planning into development.",
    "community.eyebrow": "Community",
    "community.title": "Follow the project as it grows.",
    "community.body": "Public access is not open yet. The goal right now is to share clear progress, keep the roadmap visible, and build the server properly before wider testing.",
    "community.github": "GitHub Project",
    "community.roadmap": "Roadmap Status",
    "footer.tagline": "Living Earth-map Minecraft civilization sandbox, in development.",
  },
  da: {
    "nav.overview": "Overblik",
    "nav.earthos": "EarthOS",
    "nav.systems": "Systemer",
    "nav.roadmap": "Roadmap",
    "nav.community": "Community",
    "view.mobile": "Mobil",
    "view.desktop": "Desktop",
    "hero.eyebrow": "Earth-map MMO Minecraft-server",
    "hero.lede": "En levende civilisations-sandbox på et Earth map, bygget omkring EarthOS, transport, mining, reports, byudvikling, Discord-integration og langsigtet verdenssimulation.",
    "hero.primary": "Hvad er EarthLiving?",
    "hero.secondary": "Se roadmap",
    "hero.server.kicker": "Serveroverblik",
    "hero.server.title": "Privat testverden",
    "hero.server.access": "Adgang",
    "hero.server.accessValue": "Kun invitationer",
    "hero.server.map": "Kort",
    "hero.server.mapValue": "Earth-kort + live map",
    "hero.server.focus": "Fokus",
    "hero.server.focusValue": "Stabilitet før launch",
    "hero.work.item1": "EarthOS-spillermenu",
    "hero.work.item2": "Spillerrapporter og staff-værktøjer",
    "hero.work.item3": "Offentlig hjemmeside og roadmap",
    "hero.work.item4": "Player portal-fundament",
    "stats.progress": "Fremskridt",
    "stats.core": "Core",
    "stats.server": "Server",
    "stats.private": "Privat test",
    "launch.pill": "Under udvikling",
    "launch.title": "Privat foundation-fase",
    "launch.body": "Main server, Discord-forbindelse, spillerrapporter, live map-fundament og de første EarthOS-funktioner bliver bygget før offentlig adgang.",
    "signals.map": "Earth map-fundament",
    "signals.private": "Privat foundation-fase",
    "signals.systems": "Levende verdenssystemer",
    "signals.discord": "Discord koblet på",
    "signals.earthos": "Første EarthOS-apps",
    "overview.eyebrow": "Overblik",
    "overview.title": "Et seriøst verdensprojekt, ikke bare en bunke plugins.",
    "overview.body1": "EarthLiving er ikke bare survival med ekstra plugins. Det er planlagt som en civilisations-oplevelse i Minecraft, hvor byer, transport, mining, turisme, nightlife, virksomheder, infrastruktur og spillerhandlinger hænger sammen.",
    "overview.body2": "Ambitionen er at udvikle EarthLiving langt ud over en normal Minecraft-server. Verden, systemerne og spilleroplevelsen skal vokse med et niveau af detaljer, vedholdenhed og kreativitet, som sjældent ses på community-servere.",
    "overview.card1.kicker": "Byer",
    "overview.card1.title": "Byg steder der betyder noget",
    "overview.card1.body": "Byer vokser gennem spillerbyggeri, transportnetværk, services, virksomheder, landmarks, nightlife, reports og langsigtet planlægning.",
    "overview.card2.kicker": "Simulation",
    "overview.card2.title": "Regioner kan reagere",
    "overview.card2.body": "EarthPulse er planlagt til at forbinde turisme, mining, trafik, omdømme, sikkerhed, økonomi og infrastruktur i én world state.",
    "overview.card3.kicker": "Earth map",
    "overview.card3.title": "Realistisk geografi, spilbar balance",
    "overview.card3.body": "Verden bruger Earth som scene, mens systemerne balanceres, så én perfekt region ikke kommer til at dominere alt.",
    "earthos.title": "Dit styresystem inde i spillet.",
    "earthos.body": "EarthLiving er designet GUI-first. I stedet for lange kommandolister bruger spillere EarthOS fra et hotbar-device til at åbne serverens systemer.",
    "earthos.card1.kicker": "Apps",
    "earthos.card1.title": "Alt i én menu",
    "earthos.card1.body": "EarthOS skal rumme transport, map, økonomi, reports, mining, nightlife, news, passport, sprogindstillinger og serverstatus.",
    "earthos.card2.kicker": "Adgang",
    "earthos.card2.title": "Ikke command-heavy gameplay",
    "earthos.card2.body": "Almindelige spillere skal bruge menuer, tickets, apps, dashboards, knapper og visuel feedback i stedet for command-spam.",
    "earthos.card3.kicker": "Sprog",
    "earthos.card3.title": "Internationalt fra starten",
    "earthos.card3.body": "Nye spillere skal kunne vælge sprog ved første join, og senere ændringer kan ske via EarthOS Settings.",
    "plan.eyebrow": "Nuværende retning",
    "plan.title": "De næste systemer er allerede kortlagt.",
    "plan.body": "GitHub og Notion skiller nu færdigt foundation-arbejde fra de næste spilbare systemer, så den offentlige side viser fremdrift uden interne værktøjsdetaljer.",
    "plan.item1": "Core foundation er live med EarthOS, reports, Discord hooks og panel workflow-actions.",
    "plan.item2": "Gør hotbar-devicet til spillerens normale interface for reports, map, serverstatus og apps.",
    "plan.item3": "Gør reports sporbare på tværs af Minecraft, panel, staff flow, GitHub og senere AI-assisterede fixes.",
    "plan.item4": "Start den sikre spillerportal med one-time Minecraft account-linking.",
    "systems.eyebrow": "Hovedsystemer",
    "systems.title": "Mere som en verden end en normal server.",
    "systems.card1.kicker": "Transport",
    "systems.card1.title": "Rejser betyder noget",
    "systems.card1.body": "Tog, metro, skibe, lufthavne, stationer, billetter og logistics er planlagt som centrale dele af verdenen, ikke bare teleport-menuer.",
    "systems.card2.kicker": "Mining",
    "systems.card2.title": "Regionale ressourcer",
    "systems.card2.body": "Mining bruger realistisk inspirerede ressource-regioner, licenser, deposits, depletion og logistics, mens gameplay stadig skal være fair.",
    "systems.card3.kicker": "Økonomi",
    "systems.card3.title": "Spillerdrevne virksomheder",
    "systems.card3.body": "Spillere kan senere drive miner, shops, hoteller, clubs, fabrikker, transportfirmaer, airlines og logistics-netværk.",
    "systems.card4.kicker": "Turisme",
    "systems.card4.title": "Landmarks og nightlife",
    "systems.card4.body": "Turisme reagerer på landmarks, transport, sikkerhed, kultur, events, barer, clubs, DJs og dansende NPCs i byernes nightlife-områder.",
    "systems.card5.kicker": "Simulation",
    "systems.card5.title": "Regioner får identitet",
    "systems.card5.body": "En region kan blive kendt for teknologi, turisme, mining, logistics, kultur, business eller offentlig transport.",
    "systems.card6.kicker": "AI",
    "systems.card6.title": "Fremtidig AI-planlægning",
    "systems.card6.body": "Planlagte AI-værktøjer kan hjælpe med reports, byforbedringer, infrastrukturproblemer og udviklingsarbejde.",
    "roadmap.eyebrow": "Roadmap",
    "roadmap.title": "Udviklingen er aktiv og stadig tidlig.",
    "roadmap.body1": "EarthLiving bygges trin for trin, startende med server foundation, EarthOS, reports, world simulation planning, transport, mining og langsigtede MMO-systemer.",
    "roadmap.body2": "Det er tænkt som et langsigtet projekt, ikke en engangslancering. Serveren skal fortsætte med at udvikle sig med nye systemer, dybere mechanics, bedre tools og forbedringer formet af verdenen og spillerne.",
    "roadmap.progress": "samlet fremskridt",
    "roadmap.completed": "Færdig",
    "roadmap.inProgress": "I gang",
    "roadmap.planned": "Planlagt",
    "roadmap.note": "Nuværende fase: Core foundation er live, hjemmesiden bliver finpudset, og første player portal-scope er under planlægning. Fremskridt er et tidligt estimat og ændrer sig, når systemer går fra plan til udvikling.",
    "community.eyebrow": "Community",
    "community.title": "Følg projektet mens det vokser.",
    "community.body": "Offentlig adgang er ikke åben endnu. Målet lige nu er at dele tydelig fremdrift, holde roadmap synligt og bygge serveren ordentligt før bredere testing.",
    "community.github": "GitHub-projekt",
    "community.roadmap": "Roadmap-status",
    "footer.tagline": "Living Earth-map Minecraft civilisations-sandbox, under udvikling.",
  },
};

let width = 0;
let height = 0;
let pointerX = 0;
let pointerY = 0;
let time = 0;

const terrain = [
  { x: 0.08, y: 0.7, r: 0.18, color: "rgba(159, 196, 107, 0.42)" },
  { x: 0.26, y: 0.32, r: 0.28, color: "rgba(127, 177, 200, 0.28)" },
  { x: 0.48, y: 0.55, r: 0.22, color: "rgba(211, 154, 98, 0.26)" },
  { x: 0.72, y: 0.28, r: 0.2, color: "rgba(159, 196, 107, 0.34)" },
  { x: 0.87, y: 0.66, r: 0.25, color: "rgba(127, 177, 200, 0.25)" },
];

function resize() {
  const ratio = Math.min(window.devicePixelRatio || 1, 2);
  width = window.innerWidth;
  height = window.innerHeight;
  canvas.width = Math.floor(width * ratio);
  canvas.height = Math.floor(height * ratio);
  canvas.style.width = `${width}px`;
  canvas.style.height = `${height}px`;
  ctx.setTransform(ratio, 0, 0, ratio, 0, 0);
}

function drawGrid(offsetX, offsetY) {
  ctx.strokeStyle = "rgba(245, 241, 232, 0.065)";
  ctx.lineWidth = 1;

  const spacing = 62;
  for (let x = (offsetX % spacing) - spacing; x < width + spacing; x += spacing) {
    ctx.beginPath();
    ctx.moveTo(x, 0);
    ctx.lineTo(x + 80, height);
    ctx.stroke();
  }

  for (let y = (offsetY % spacing) - spacing; y < height + spacing; y += spacing) {
    ctx.beginPath();
    ctx.moveTo(0, y);
    ctx.lineTo(width, y - 80);
    ctx.stroke();
  }
}

function drawTerrain(offsetX, offsetY) {
  terrain.forEach((shape, index) => {
    const pulse = Math.sin(time * 0.0012 + index) * 18;
    const x = width * shape.x + offsetX * (index + 1) * 0.04;
    const y = height * shape.y + offsetY * (index + 1) * 0.04;
    const radius = Math.max(width, height) * shape.r + pulse;

    const gradient = ctx.createRadialGradient(x, y, 0, x, y, radius);
    gradient.addColorStop(0, shape.color);
    gradient.addColorStop(1, "rgba(16, 23, 21, 0)");

    ctx.fillStyle = gradient;
    ctx.beginPath();
    ctx.arc(x, y, radius, 0, Math.PI * 2);
    ctx.fill();
  });
}

function drawRoutes(offsetX, offsetY) {
  ctx.lineWidth = 2;
  ctx.strokeStyle = "rgba(245, 241, 232, 0.16)";
  ctx.beginPath();
  ctx.moveTo(width * 0.08 + offsetX * 0.02, height * 0.68 + offsetY * 0.02);
  ctx.bezierCurveTo(
    width * 0.28,
    height * 0.42,
    width * 0.44,
    height * 0.78,
    width * 0.64,
    height * 0.5
  );
  ctx.bezierCurveTo(
    width * 0.77,
    height * 0.3,
    width * 0.83,
    height * 0.44,
    width * 0.92 + offsetX * 0.02,
    height * 0.28 + offsetY * 0.02
  );
  ctx.stroke();

  ctx.fillStyle = "rgba(245, 241, 232, 0.58)";
  [
    [0.08, 0.68],
    [0.35, 0.56],
    [0.64, 0.5],
    [0.92, 0.28],
  ].forEach(([x, y]) => {
    ctx.beginPath();
    ctx.arc(width * x + offsetX * 0.02, height * y + offsetY * 0.02, 3.5, 0, Math.PI * 2);
    ctx.fill();
  });
}

function render(now) {
  time = now;
  const offsetX = (pointerX - width / 2) * 0.06 + Math.sin(now * 0.00025) * 24;
  const offsetY = (pointerY - height / 2) * 0.06 + Math.cos(now * 0.00022) * 18;

  ctx.clearRect(0, 0, width, height);
  drawTerrain(offsetX, offsetY);
  drawGrid(offsetX, offsetY);
  drawRoutes(offsetX, offsetY);
  requestAnimationFrame(render);
}

window.addEventListener("resize", resize);
window.addEventListener("pointermove", (event) => {
  pointerX = event.clientX;
  pointerY = event.clientY;
});

if (viewToggle) {
  const viewToggleLabel = viewToggle.querySelector(".view-toggle-label");
  const desktopPreviewQuery = window.matchMedia("(min-width: 681px)");
  const setMobilePreview = (enabled, persist = true) => {
    const language = document.documentElement.lang === "da" ? "da" : "en";
    document.body.classList.toggle("mobile-preview", enabled);
    viewToggle.setAttribute("aria-pressed", String(enabled));
    if (viewToggleLabel) {
      viewToggleLabel.textContent = enabled
        ? translations[language]["view.desktop"]
        : translations[language]["view.mobile"];
    }
    if (persist) {
      window.localStorage.setItem("earthliving-view-mode", enabled ? "mobile" : "desktop");
    }
  };
  const syncResponsiveMode = () => {
    const savedMode = window.localStorage.getItem("earthliving-view-mode");
    setMobilePreview(desktopPreviewQuery.matches && savedMode === "mobile", false);
  };

  syncResponsiveMode();
  viewToggle.addEventListener("click", () => {
    setMobilePreview(!document.body.classList.contains("mobile-preview"));
  });
  desktopPreviewQuery.addEventListener("change", syncResponsiveMode);
}

if (languageToggle) {
  const setLanguage = (language) => {
    const dictionary = translations[language] || translations.en;
    document.documentElement.lang = language;
    languageToggle.dataset.language = language;
    languageToggle.setAttribute("aria-pressed", String(language === "da"));
    document.querySelectorAll("[data-i18n]").forEach((element) => {
      const key = element.dataset.i18n;
      if (dictionary[key]) {
        element.textContent = dictionary[key];
      }
    });
    if (viewToggle) {
      const enabled = document.body.classList.contains("mobile-preview");
      const viewToggleLabel = viewToggle.querySelector(".view-toggle-label");
      if (viewToggleLabel) {
        viewToggleLabel.textContent = enabled ? dictionary["view.desktop"] : dictionary["view.mobile"];
      }
    }
    window.localStorage.setItem("earthliving-language", language);
  };
  const browserLanguage = navigator.language && navigator.language.toLowerCase().startsWith("da") ? "da" : "en";
  setLanguage(window.localStorage.getItem("earthliving-language") || browserLanguage);
  languageToggle.addEventListener("click", () => {
    setLanguage(document.documentElement.lang === "da" ? "en" : "da");
  });
}

fetch("./data/roadmap-status.json", { cache: "no-store" })
  .then((response) => {
    if (!response.ok) {
      throw new Error("Roadmap status not available");
    }
    return response.json();
  })
  .then(applyRoadmapStatus)
  .catch(() => {});

resize();
pointerX = window.innerWidth * 0.5;
pointerY = window.innerHeight * 0.5;
requestAnimationFrame(render);
