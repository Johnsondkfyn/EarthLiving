# Earth Living Plan

Importeret fra Notion-databasen **Earth Living Roadmap** den 2026-05-16.

Kilde: https://www.notion.so/2e79c23d643a806eb703c25882182865

## Teknisk grundlag

- Serverplanen skal bruge **Paper 26** og **Java 25**.
- Tidligere omtale af Paper 1.21.4 og Java 21 er forældet.
- Plugin-valg, serveropsætning, testmiljø og dokumentation skal tjekkes mod Paper 26 og Java 25.

## Statusoversigt fra Notion

| Status | Antal i Notion |
| --- | ---: |
| I gang | 5 |
| Færdig | 10 |
| Ikke startet | 70 |
| I alt | 85 |

Opdatering 2026-05-17: Velocity proxy med servervalg er tilføjet i Notion som `I gang`. Den nye faktiske status er derfor mindst **6 I gang** og **86 rækker i alt**, hvis alle tidligere Notion-rækker stadig findes.

Opdatering 2026-05-18: Flere tekniske fundament-opgaver er nu udført på live-serveren. Tallene i Notion bør opdateres efter næste fulde Notion-sync, men den faktiske status er ændret for Pterodactyl, BlueMap, WorldGuard/landgrænser og PassportBorders.

Opdatering 2026-05-18 aften: Discord-serveren er nu genopbygget til Earth Living-brandet. Den gamle serverstruktur er ryddet væk, nye Earth Living-kanaler og roller er oprettet, logoet er sat ind, og regler/velkomst/roadmap/BlueMap-info er skrevet ind som foundation.

Opdatering 2026-05-19: Discord fase 2 er startet. Kanalerne `server-status`, `minecraft-chat`, `server-events` og `staff-alerts` er oprettet, kanal-ID'er er dokumenteret, `staff-alerts` er gjort privat for owner, og DiscordSRV rollout/config-skabeloner er lagt i repoet uden bot-token.

Opdatering 2026-05-19: EarthOS hotbar-ikonet er valgt og gemt som projekt-asset i flere størrelser under `docs/assets/earthos/`.

Opdatering 2026-05-19: EarthLivingCore v0.1.0 er startet som Paper-plugin under `plugins/earthlivingcore/`. Første version indeholder module registry, notification service, `/earthliving`, `/earthos`, hotbar EarthOS item og en placeholder EarthOS inventory menu.

Opdatering 2026-05-19 aften: DiscordSRV `1.30.5` er installeret og forbundet på Earth Living Test med botten `Minecraft Monitor`. `server-status` modtager nu start/status-beskeder. `minecraft-chat` er mappet til Minecraft-to-Discord chat, mens Discord-to-Minecraft og console forwarding fortsat er slået fra for sikkerhed.

Opdatering 2026-05-19 aften: DiscordSRV og EarthLivingCore `0.6.0` er flyttet fra test til **Earth Living Main**. Main booter rent på `25565`, DiscordSRV logger ind på Discord, player count presence/status er aktiv, og BlueMap kører fortsat bag nginx på `bluemap.159.195.149.253.nip.io`. Testserverens DiscordSRV-jar og config er fjernet, så botten kun forbindes fra main.

Opdatering 2026-05-20: Main-spillertest er gennemført med `TheKing189`. Minecraft-to-Discord chat virker i `minecraft-chat`, danske tegn (`æøå`) kommer korrekt igennem, join/leave-beskeder vises i `server-status`, og EarthLivingCore report flow oprettede `Report #1` på main. `rate-limit` på main blev sat til `0`, fordi den tidligere værdi `15` kickede klienten ved login.

Opdatering 2026-05-20: Domænet `earthliving.earth` er købt, DNS er sat op, HTTPS er aktiveret, og den offentlige hjemmeside er live via Nginx på EarthLiving-serveren. Hjemmesiden har nu EarthLiving-logo, animeret grøn/cyan hero-title, fast centreret logo-baggrund, automatisk mobil-layout, desktop mobil-preview-knap og engelsk/dansk sprogskift. Website-opgaven er derfor flyttet fra `Ikke startet` til `I gang`, men er ikke færdig før indholdssider, screenshots, Discord CTA, map/status-links og devlog/news er på plads.

Opdatering 2026-05-20: Notion-roadmapet blev ryddet op efter gennemgang af faktisk status. `Teknisk version rettet til Paper 26 og Java 25` og `Landgrænser research` er flyttet til `Færdig`, fordi serveren kører Paper 26.1.2/Java 25 og landgrænsefundamentet er genereret/dokumenteret. Aktuel status er derfor **22 Færdig**, **7 I gang** og **67 Ikke startet**. Hjemmesidens roadmap-tal læses fra `docs/data/roadmap-status.json`, så status kan opdateres ét sted i repoet.

## Udført 2026-05-18

### Pterodactyl og serverdrift

- Pterodactyl Panel og Wings er sat op og bruges nu til Earth Living-serveren.
- Den rigtige Earth Living-server er flyttet ind i Pterodactyl som **Earth Living Main**.
- Serveren kører på **Paper 26.1.2 build 64** og **Java 25**.
- Den gamle `minecraft.service` er stoppet/deaktiveret, og serverdata ligger nu i Pterodactyl volume.
- Pterodactyl-panelet har fået Earth Living carbon-style, logo, forbedrede knapper, dashboard-grafer og admin Marketplace.
- Notion: https://www.notion.so/3649c23d643a81308047c91c35ad1168

### BlueMap og kort

- BlueMap-render er færdig og fungerer offentligt på `http://159.195.149.253:8100/`.
- BlueMap er proxyet via Nginx, så webappen kan åbnes udefra.
- Hele verden har fået visuelt **Country Borders** overlay på BlueMap.
- Starter Europe-landene er highlightet med tydelig cyan outline.
- Antarktis/dateline-artefakten er filtreret væk fra overlayet.

### Landgrænser og regioner

- WorldEdit og WorldGuard er installeret på Earth Living Main.
- Danmark blev først testet manuelt og derefter genereret automatisk som polygoner.
- Starter Europe blev oprettet som WorldGuard country regions.
- Hele verden er nu oprettet som **673 WorldGuard country regions** uden flags.
- Regionerne fungerer som teknisk fundament og låser ikke spillere endnu.

### PassportBorders

- Det tidligere lavede plugin **PassportBorders 0.3.2** er fundet i chat-backup.
- Plugin-koden er lagt ind i repoen under `plugins/passportborders/`.
- Der er genereret ny `countries.yml` til PassportBorders med rigtige starter-Europe polygoner.
- Pluginet er ikke aktiveret på live-serveren endnu, fordi pas/adgang først bør kobles på efter checkpoints og grænseposter.
- Notion: https://www.notion.so/3649c23d643a815e8f4cc21a90834ca8

### GitHub/repo

- Nye generator-værktøjer er tilføjet under `tools/`.
- BlueMap marker config og WorldGuard region config er gemt som repo-artefakter.
- PassportBorders-kildekode og konfiguration er gemt i repoen, så pluginet ikke kun ligger i gammel chat-backup.

### Discord foundation

- Discord-serveren er omdøbt og brandet som **Earth Living**.
- Earth Living-logoet er sat på serveren.
- De gamle kanaler blev ryddet væk, bortset fra Discords beskyttede community-kanaler.
- Ny kanalstruktur er oprettet: start/info, Earth Living, community, support, voice og staff.
- Regler, velkomsttekst, server-info, BlueMap-link, roadmap og launch/update-tekst er skrevet ind.
- Nye roller er oprettet: Owner, Admin, Moderator, Builder, Tester og Member.
- Gamle job/theme-roller blev slettet: Trucker og Miner.
- `Minecraft Server Status` blev bevaret, fordi den ligner en bot/integration-rolle.
- Næste Discord-fase er chat-sync, serverstatus/player count, whitelist/verification og staff alerts.

## I gang 2026-05-19: Discord integration phase 2

- Nye Discord-kanaler:
  - `minecraft-chat`: `1506268822813016134`
  - `server-status`: `1506268769314541639`
  - `server-events`: `1506271254661959761`
  - `staff-alerts`: `1506268923895484477`
- `staff-alerts` er sat som privat kanal for owner under opsætning.
- DiscordSRV er valgt som anbefalet plugin til chat bridge/status/account linking.
- Config-skabelon er gemt i `server-config/discordsrv/config.template.yml`.
- Rollout-checkliste er gemt i `server-config/discordsrv/phase-2-rollout.md`.
- `server-events` er reserveret til automatiske random events, konkurrencer og timed world incidents.
- Real bot-token er oprettet og lagt i testserverens private DiscordSRV config. Token må aldrig lægges i GitHub, Notion eller chat.
- DiscordSRV `1.30.5` blev testet på Earth Living Test og flyttet til Earth Living Main efter godkendt test.
- EarthLivingCore `0.6.0` kører på Main med DiscordSRV bridge, random event announcements og restart countdown-beskeder.
- `server-status` modtager server start/status og restart countdowns.
- `server-events` modtager `/earthliving event <message>` announcements.
- `minecraft-chat` er mappet til Minecraft-to-Discord player chat og testet med en rigtig spillerbesked på main.
- Danske tegn i Minecraft-to-Discord chat er testet med `æøå`.
- Discord-to-Minecraft er fortsat slået fra.
- Discord console forwarding er fortsat slået fra.
- Real bot-token ligger kun i main-serverens private DiscordSRV config. Token må aldrig lægges i GitHub, Notion eller chat.

## I gang 2026-05-19: EarthOS v1

- EarthOS er planlagt som serverens primære in-game menu/device.
- Hotbar-ikonet er valgt: gold/cyan globe-compass.
- Asset-kopier er gemt under `docs/assets/earthos/`.
- Første tekniske version bør være en inventory GUI med et `COMPASS` eller `CLOCK` item som EarthOS-device.
- Første apps bør være Server Status, World Map/BlueMap, Events, Passport/Countries, Support & Reports og Settings.

## I gang 2026-05-19: EarthLivingCore v0.1.0

- Plugin scaffold er oprettet i `plugins/earthlivingcore/`.
- EarthLivingCore fungerer som backend/hjerne for EarthOS, events, reports, passports, notifications og Discord hooks.
- Module registry er tilføjet med modulerne `earthos`, `notifications`, `events`, `reports`, `passports` og `discord`.
- `/earthliving status`, `/earthliving modules`, `/earthliving reload` og `/earthos` er tilføjet.
- EarthOS-device gives på join via hotbar slot `8`.
- EarthOS-device bruger `COMPASS` med custom model data `260519`, så resource packen senere kan koble hotbar-ikonet på.
- EarthOS placeholder-menu har knapper til World Map, Server Events, Passport, Wallet, Reports, Server Status og Settings.
- Main-spillertest 2026-05-20 bekræftede EarthOS/kommando-flow og oprettede `Report #1`.

## Sikkerhedsfokus 2026-05-18

- Earth Living Main er hærdet som privat udviklingsserver: whitelist, enforce-whitelist og online-mode er aktivt.
- RCON og query er slået fra.
- Bukkit plugin-query er slået fra, så pluginlisten ikke eksponeres via query.
- `rate-limit=15` er sat som mild join-spam beskyttelse.
- Paper Anti-Xray er slået til med `engine-mode: 2` og `lava-obscures: true`.
- FarmControl 1.3.0 er lagt på serveren som blød farm limiter.
- FarmControl er sat op uden aktive kill-profiler og uden ClearLag-style item/entity cleanup.
- Hopper-check, item/XP merge og entity-collision er justeret for mindre farm-lag.
- Firewall og `fail2ban` er aktive på hosten.
- Det næste store sikkerhedslag bør være Velocity som eneste offentlige Minecraft-indgang, anti-bot på proxy-laget og direkte backend-adgang lukket.

## I gang

| Projekt | Prioritet | Notion |
| --- | --- | --- |
| Roadmap/status i Notion | Ikke angivet | https://www.notion.so/3619c23d643a80098efacacb46b4bc28 |
| Ekstra I gang-række | Ikke angivet | Vises i Notion, men blev ikke returneret med navn af connector-søgningen. |
| Velocity proxy med servervalg | Høj | https://www.notion.so/3639c23d643a8152a370dc0aa3c373df |

## I gang: Velocity proxy og servervalg

Formaalet er at bruge **Velocity** som faelles indgang til Earth Living-netvaerket, saa spillere senere kan joine samme IP/domane og vaelge mellem forskellige servertyper.

### Foerste struktur

- **Velocity proxy:** faelles front door og routing.
- **Earth Living:** realistisk Paper-server med Earth map, realisme-systemer, BlueMap og roadmap-funktioner.
- **Standard Survival:** separat klassisk survival-server, saa almindelig survival ikke blandes sammen med Earth Living-regler og tunge realism features.
- **Test server:** bruges til plugin-test, Paper/Java-kompatibilitet og staging foer produktion.

### Pterodactyl status

- Velocity egg er importeret i Pterodactyl under Minecraft nest.
- Server oprettet: **Earth Living Velocity**.
- Server UUID: `89919e61-bbba-44a7-8030-7b0439954c6f`.
- Panel short id: `89919e61`.
- Allocation: `earthliving-node:25567`.
- Image: `ghcr.io/pterodactyl/yolks:java_25`.
- RAM: `768 MiB`, Disk: `1 GiB`, CPU limit: `50%`.
- Install status: installeret i Pterodactyl `2026-05-17 19:31:55`.
- Velocity jar: rettet manuelt til PaperMC Downloads API, version `3.4.0-SNAPSHOT` build `559`.
- Status: oprettet som in-progress infrastruktur uden at aendre den nuvaerende Paper testserver.

### Naeste skridt

1. Start Velocity og bekraeft at proxyen booter rent.
2. Saet `velocity.toml` op med forwarding, MOTD og serverliste.
3. Opret senere Standard Survival som separat Paper-server.
4. Opret eller flyt Earth Living real-server bag Velocity, naar BlueMap/render og migration er klar.
5. Skift offentlig spillerindgang til Velocity, naar proxy, forwarding og backend-servere er testet.

## Færdig

| Projekt | Notion |
| --- | --- |
| Remote server installeret på Netcup | https://www.notion.so/3619c23d643a80c79542d73732646d09 |
| Paper 26.1.2 kører | https://www.notion.so/3619c23d643a806d9beecdd829910a6c |
| Java 25 installeret | https://www.notion.so/3619c23d643a809e8752f42854b05446 |
| Teknisk version rettet til Paper 26 og Java 25 | https://www.notion.so/3619c23d643a81da90c0ecfd2ee6d5a5 |
| Earth map importeret | https://www.notion.so/3619c23d643a80e2804fc26b8c4f09b3 |
| BlueMap installeret | https://www.notion.so/3619c23d643a80998678d7a4f7344e65 |
| BlueMap world render gennemført | https://www.notion.so/3619c23d643a8023aa0af1357584e8ec |
| WorldEdit og WorldGuard fundament | https://www.notion.so/3619c23d643a800b97d6f061e2e98ae3 |
| Landgrænse-system med polygoner | https://www.notion.so/3619c23d643a808c9a26ceae643ca197 |
| Landgrænser research | https://www.notion.so/3619c23d643a803fae9ce333ca1fe317 |
| Pterodactyl panel customisering og Marketplace | https://www.notion.so/3649c23d643a81308047c91c35ad1168 |
| PassportBorders fundet og forberedt til test | https://www.notion.so/3649c23d643a815e8f4cc21a90834ca8 |
| Security hardening: whitelist, query, RCON og Anti-Xray | https://www.notion.so/3649c23d643a81a28e47c4fadc3160a4 |
| Farm limiter og performance hardening | https://www.notion.so/3649c23d643a81b0a04fc5d38536b30c |
| Discord server foundation og branding | https://www.notion.so/3649c23d643a814a922efa0b5d9386f3 |
| EarthLogger v1 | https://www.notion.so/3619c23d643a805b9437cb695f716eea |
| LuckPerms admin setup | https://www.notion.so/3619c23d643a80d68b72f2a4a4381f9d |
| Coordinator plugin installeret | https://www.notion.so/3619c23d643a808a9e1cd59dcc6088e3 |
| Whitelist og security foundation | https://www.notion.so/3619c23d643a8070acb2d7c8b116106d |
| Ekstra færdig-række | Vises i Notion, men blev ikke returneret med navn af connector-søgningen. |

## Ikke startet

Notion viser **70** rækker som `Ikke startet`. Nedenfor er de navngivne rækker, som Notion-connectoren returnerede ved eksporten.

| Projekt | Område | Notion |
| --- | --- | --- |
| Serverregler og spillerguide | Regler | https://www.notion.so/2e79c23d643a808b8c97e2b68b8e6e5a |
| Regler for voksenindhold og server-rating | Regler | https://www.notion.so/3619c23d643a80bcaef6d41cb56a3088 |
| Rus-effekter med gameplay-risiko | Regler | https://www.notion.so/3619c23d643a80c3a3a7fe7ee181209f |
| Alkohol-system med barer og licenser | Regler | https://www.notion.so/3619c23d643a80af9503e10b930b0ae6 |
| Visuelle grænser/mure | Landgrænser | https://www.notion.so/3619c23d643a805e85e8d584bf539a1e |
| Checkpoints og pas-grænseposter | Landgrænser | https://www.notion.so/3649c23d643a8196ada7c6304dd127dd |
| Velocity som eneste offentlige Minecraft-indgang | Sikkerhed | https://www.notion.so/3649c23d643a81f5b67bde3012f88bf2 |
| Anti-bot og anti-cheat lag til offentlig launch | Sikkerhed | https://www.notion.so/3649c23d643a81718908dff900ff4e2a |
| FarmControl test og balance efter første farms | Performance | https://www.notion.so/3649c23d643a8188a083f77b73dcec5a |
| Klima forskelligt fra land til land | Klima | https://www.notion.so/3619c23d643a807eae00e7f0ac6a755b |
| Region-baserede sæsoner | Klima | https://www.notion.so/3619c23d643a80d2b3e7d5e5e63dc3f2 |
| Vejrzoner for Grønland, Island og andre lande | Klima | https://www.notion.so/3619c23d643a804f9cd2dd823bc34936 |
| Temperatur påvirker spiller og gameplay | Klima | https://www.notion.so/3619c23d643a80ecac10c9effe85b2fd |
| Transport-system med tog og skibe | Transport | https://www.notion.so/3619c23d643a8008b121fa32236c9d80 |
| Jernbaner og stationer mellem lande | Transport | https://www.notion.so/3619c23d643a80ae9088ecbc30b32785 |
| Billetter og transport-økonomi | Transport | https://www.notion.so/3619c23d643a8067bf7ce37de1943bf4 |
| Bankkonto som primær økonomi | Økonomi | https://www.notion.so/3619c23d643a804ba4afe0cc74bc0b3b |
| Fysiske kontanter kan hæves og indsættes | Økonomi | https://www.notion.so/3619c23d643a804ebff9efdd22795e6b |
| Kontanter kan tabes ved død | Økonomi | https://www.notion.so/3619c23d643a8082a6dbcd9dbc9e92fa |
| Skat, løn og transportbetaling via bankkonto | Økonomi | https://www.notion.so/3619c23d643a80a8a7e9e9fb2a0d4e95 |
| Aktiv spilletid giver lille borgerløn | Økonomi | https://www.notion.so/3619c23d643a8059890ecfcfffee694b |
| Economy/jobs baseline | Økonomi | https://www.notion.so/3619c23d643a802da559ef2ace4d273f |
| GUI-menuer til økonomi, jobs og transport | Økonomi | https://www.notion.so/3619c23d643a80af9562dedb7dc753a4 |
| Casinoer i kendte storbyer | Casino | https://www.notion.so/3619c23d643a80f9b898f5b719620b1c |
| Casino-økonomi med indsatsgrænser | Casino | https://www.notion.so/3619c23d643a80049d57f52e8ecf8280 |
| Bybaserede casino-licenser | Casino | https://www.notion.so/3619c23d643a803b84a8e0db94e2e5a6 |
| Automatiske fiskekonkurrencer | Events | https://www.notion.so/3619c23d643a80c4a9b3cdfaeab9bb88 |
| Fiskekonkurrencer i forskellige verdensregionerMusiksystem til lobby, casinoer og byområder | Events | https://www.notion.so/3619c23d643a80afb322e32776bbc042 |
| Chat-games med små præmier | Events | https://www.notion.so/3619c23d643a806494e5f0274010f6df |
| Lobby-games til ventetid og socialt spil | Events | https://www.notion.so/3619c23d643a8035af33f04cae2569a6 |
| Custom resource pack til musik og lyde | Lyd | https://www.notion.so/3619c23d643a80a5b422f07c938432ed |
| Spillerindstillinger for musik og lydstyrke | Lyd | https://www.notion.so/3619c23d643a8086aed7c62dd2d71f30 |
| Voice lines til bank, hospital, casino og transport | Lyd | https://www.notion.so/3619c23d643a801abe9bf2e5af7453aa |
| Region- og stedbaseret baggrundsmusik | Lyd | https://www.notion.so/3619c23d643a800c9e0ed6c433d431b4 |
| Faste NPC-stemmer via custom resource pack | Lyd | https://www.notion.so/3619c23d643a8044a247cbba0a341348 |
| NPC voice lines med engelsk tale | Lyd | https://www.notion.so/3619c23d643a8047b4f8d038f9f39260 |
| AI/TTS NPC-stemmer som fremtidig v2 | Lyd | https://www.notion.so/3619c23d643a80029e58d352c2fc9057 |
| Discord integration | Discord | https://www.notion.so/3619c23d643a809d84ebefd67d56d860 |
| Minecraft chat synkroniseret med Discord | Discord | https://www.notion.so/3619c23d643a80188eabe2c32c8d80e1 |
| Serverstatus og player count i Discord | Discord | https://www.notion.so/3619c23d643a803280f1d68ca61b226b |
| Discord roller koblet til LuckPerms/supporter | Discord | https://www.notion.so/3619c23d643a80c89516c09404521f1f |
| Whitelist eller verification via Discord | Discord | https://www.notion.so/3619c23d643a8044bfb0e88bf48435d0 |
| Rapporter og staff alerts til Discord | Discord | https://www.notion.so/3619c23d643a80338344e8d486319d1f |
| Rapport-log med beviser og status | Moderation | https://www.notion.so/3619c23d643a802091d7e6a608484fc4 |
| Rapport-kategorier for snyd, griefing, chat og bugs | Moderation | https://www.notion.so/3619c23d643a80f4a3ecccc4fe0829de |
| Staff dashboard til rapporter | Moderation | https://www.notion.so/3619c23d643a80268f20fc72e1535139 |
| Rapport-system for spillere | Moderation | https://www.notion.so/3619c23d643a80788b26d3f54ed7cdd6 |
| Sygehus og læge-system | Jobs | https://www.notion.so/3619c23d643a800aa5a1c7832462b3f1 |
| Lægejob og hospital-roller | Jobs | https://www.notion.so/3619c23d643a805eaddce7a0ef425b57 |
| Backup automation v2 | Drift | https://www.notion.so/3619c23d643a803c8d2ec6dbe07b0274 |
| Remote drift/monitorering | Drift | https://www.notion.so/3619c23d643a8017943ffcb0b6936e19 |
| Admin-commands skjules fra almindelige spillere | Drift | https://www.notion.so/3619c23d643a80b68fb4f5d1d3715cf9 |
| Website til serveren | Web | https://www.notion.so/3619c23d643a8020b74be8af46263f37 |
| Donation/support-side med Tebex | Web | https://www.notion.so/3619c23d643a802ea199e086dab7c6eb |
| Towny eller claim-system | Gameplay | https://www.notion.so/3619c23d643a809d856ad1d79e569d6c |
| Flere realistiske gameplay plugins | Gameplay | https://www.notion.so/3619c23d643a801cbe8ccf814a6c221b |

## Senere plan: Custom Pterodactyl Panel

Startdato: **2026-05-18**

Formaalet er at lave et Earth Living admin/control panel ovenpaa Pterodactyl, saa serverdrift, roadmap, BlueMap og sikkerhed kan samles et sted uden at aendre Pterodactyl-kernen unodigt.

### Fase 1: Pterodactyl foundation

- Installere eller gennemgaa standard Pterodactyl Panel og Wings.
- Sikre at Minecraft-serveren kan styres via Pterodactyl: start, stop, restart og console.
- Tjekke Java/Paper startup, RAM, disk, allocations og server variables.
- Saette sikre roller/adgange op til owner/admin/staff.
- Dokumentere login, domane, HTTPS og backup-placering.

### Fase 2: Earth Living branding

- Tilfoeje Earth Living navn, logo, favicon og farver.
- Lave simpelt custom login-look, der matcher websitet.
- Undgaa tunge tredjeparts-themes, der kan goere updates og sikkerhed svaerere.
- Holde Pterodactyl saa standard som muligt under motorhjelmen.

### Fase 3: Nyttige drift-vaerktoejer

- Server health dashboard: online/offline, CPU, RAM, disk, uptime, TPS/MSPT hvis muligt.
- Backup monitor: seneste backup, backup-stoerrelse, backup-alder og fejl-advarsler.
- Restart/crash log: seneste restart, crashes, downtime og vigtige haendelser.
- Plugin overview: installerede plugins, versioner, status og korte noter.
- Security checklist: whitelist, OP-liste, RCON, firewall, SSH, permissions og backups.
- Config/deployment log: hvad der sidst er aendret paa serveren.

### Fase 4: Map view og BlueMap

- Tilfoeje tydeligt link til BlueMap.
- Lave en Map-side, hvor BlueMap kan vises direkte i panelet eller dashboardet.
- Vise BlueMap status: online/offline, sidste render-aktivitet, tile count og diskforbrug.
- Senere: landgraenser, region notes, coordinates bookmarks og steder der skal bygges.

### Fase 5: Roadmap og community tools

- Roadmap widget med Notion/GitHub-tal: completed, in progress og not started.
- Current focus-widget med de vigtigste opgaver lige nu.
- Changelog/devlog fra GitHub commits eller manuelle updates.
- Whitelist manager til testspillere.
- Discord/staff overview senere: reports, alerts, roller og whitelist/verification.

### Foerste prioritet den 18. maj

1. Afgor om vi bruger standard Pterodactyl med branding eller et separat Earth Living dashboard ovenpaa API'et.
2. Tjek serverens krav og sikkerhed foer installation.
3. Saet basis-panel op og forbind Minecraft-serveren.
4. Tilfoej BlueMap-link og backup-status som de foerste custom elementer.
5. Dokumenter alle credentials/hemmeligheder uden for GitHub.

## Importnote

Notion-visningen viser 85 rækker: 5 `I gang`, 10 `Færdig` og 70 `Ikke startet`. Connector-søgningen returnerede ikke alle 85 rækker med navn i én samlet eksport, så denne fil bruger de korrekte Notion-tal og inkluderer alle navngivne rækker, der blev returneret under eksporten.
