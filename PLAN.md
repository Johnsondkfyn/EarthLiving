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

## I gang

| Projekt | Prioritet | Notion |
| --- | --- | --- |
| Teknisk version rettet til Paper 26 og Java 25 | Høj | https://www.notion.so/3619c23d643a81da90c0ecfd2ee6d5a5 |
| Roadmap/status i Notion | Ikke angivet | https://www.notion.so/3619c23d643a80098efacacb46b4bc28 |
| BlueMap world render | Ikke angivet | https://www.notion.so/3619c23d643a8023aa0af1357584e8ec |
| Landgrænser research | Ikke angivet | https://www.notion.so/3619c23d643a803fae9ce333ca1fe317 |
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
| Earth map importeret | https://www.notion.so/3619c23d643a80e2804fc26b8c4f09b3 |
| BlueMap installeret | https://www.notion.so/3619c23d643a80998678d7a4f7344e65 |
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
| WorldEdit og WorldGuard fundament | Landgrænser | https://www.notion.so/3619c23d643a800b97d6f061e2e98ae3 |
| Landgrænse-system med polygoner | Landgrænser | https://www.notion.so/3619c23d643a808c9a26ceae643ca197 |
| Visuelle grænser/mure | Landgrænser | https://www.notion.so/3619c23d643a805e85e8d584bf539a1e |
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
