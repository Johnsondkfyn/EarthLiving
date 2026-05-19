# EarthLiving Future Plugin Plan

This document is the shared future plan for plugin ideas connected to the EarthLiving Minecraft server project.

## Current technical base

| Area | Choice |
|---|---|
| Server | Paper 26.1.2 |
| Java | Java 25 |
| Panel | Pterodactyl |
| Panel framework | Blueprint Framework |
| Repository | Johnsondkfyn/EarthLiving |
| AI workflow | ChatGPT + Codex + GitHub + Notion |
| Main project concept | EarthLiving, a living Earth-map MMO-style Minecraft server |

## Project principle

EarthLiving should feel like a living world, not just a collection of disconnected plugins. Each plugin/module should connect into EarthLivingCore where possible.

Core ideas:

- Modular architecture
- No huge single manager classes
- Async database and generation tasks
- Admin tools first, player systems later
- Strong visual feedback in-game
- GUI-first gameplay: players should use EarthOS instead of commands
- Pterodactyl and Blueprint Framework can be used for admin panels and dashboards
- No passwords, SSH keys, API keys, recovery codes, or private credentials in repo files

---

# 1. EarthLivingCore

## Goal
Create the central plugin/core layer for EarthLiving.

## Current implementation

EarthLivingCore v0.4.0 has been started under `plugins/earthlivingcore/`.

Current scope:

- Paper plugin bootstrap
- Module registry
- Notification service
- `/earthliving status`
- `/earthliving modules`
- `/earthliving reload`
- `/earthos`
- EarthOS hotbar item on join
- EarthOS inventory GUI
- First EarthOS click actions:
  - World Map sends a clickable BlueMap link
  - Server Events shows current event status/planned Discord event feed
  - Passport explains the upcoming country/passport integration
  - Wallet explains the upcoming economy integration
  - Reports points players toward the Discord bug-reports flow for now
  - Server Status shows test server/runtime status
  - Settings refreshes the EarthOS hotbar device
- First real EarthOS app:
  - Reports opens a Report Center hub
  - Create Report opens a category menu
  - Player chooses a category and then writes a note in chat
  - The next chat message is captured privately and saved as the report note
  - Players can type `cancel` to stop the report draft
  - My Reports shows the player's latest reports in EarthOS
  - Admin Reports shows the latest reports for admins/operators
  - Clicking a report sends details in chat
  - Reports are saved to `reports.yml`
  - Reports are exported read-only to `reports-panel.json` for the Pterodactyl panel
  - Saved data includes category, note, player, UUID, world, coordinates, timestamp and status
  - `/earthliving reports` shows the open report count for admins

The first version is intentionally small. It is the backend/hub layer, while EarthOS is the player-facing interface.

## Purpose
EarthLivingCore should connect all major systems:

- EarthOS menu/device
- transport
- economy
- player profile data
- notifications
- region data
- architect/blueprint tools
- report/support lifecycle
- update/restart lifecycle
- world simulation systems
- admin tools
- future AI integrations

## Notes
EarthLivingCore should act as the internal API layer so other modules do not directly depend on every external plugin.

---

# 2. EarthOS

## Goal
Create the main in-game interface for players and admins.

## Purpose
EarthOS is the digital operating system for EarthLiving. It should replace command-heavy workflows with a branded in-game UI.

## Features

- Hotbar item/device
- Server menu
- Transport app
- Wallet/economy app
- World map app
- News app
- Profile app
- Passport app
- Region/city status app
- Build/blueprint app
- Support & reports app
- My reports app
- Server status app
- Changelog/update notifications
- Admin panel screens
- Settings app

## Hotbar icon decision

EarthOS should use the gold/cyan globe-compass icon as the hotbar menu device.

Repository asset notes:

- `docs/assets/earthos/earthos-hotbar-icon-original.png`
- `docs/assets/earthos/earthos-hotbar-icon-128.png`
- `docs/assets/earthos/earthos-hotbar-icon-64.png`
- `docs/assets/earthos/earthos-hotbar-icon-32.png`
- `docs/assets/earthos/earthos-hotbar-icon-16.png`

Recommended item model:

- Base item: `COMPASS` or `CLOCK`
- Display name: `EarthOS`
- Purpose: opens the Earth Living main menu from the hotbar

## Visual direction

- Dark navy/black base
- Gold and cyan highlights
- EarthLiving logo branding
- MMO-style inventory GUI first
- Later custom font/overlay UI

## GUI-first rule

Players should not need commands for normal gameplay. EarthOS is the visible interface layer for reports, transport, economy, settings, notifications, and server status. Admin debug commands can exist, but the main workflow should be GUI-based.

---

# 3. TransportModule

## Goal
Build the foundation for immersive transport.

## Purpose
EarthLiving should avoid teleport-only travel. Transport should feel like a living world system.

## Features

- Stations
- Routes
- Tickets
- Departures boards
- Train/metro/fly/ship categories
- Travel classes
- Economy integration
- Future NPC passengers
- Future dynamic delays/events

## Recommended model

| Transport | Method |
|---|---|
| Metro | Physical or semi-physical automated trains |
| Regional trains | Semi-physical route system |
| Long-distance trains | Fake interior + moving scenery |
| Flights | Instanced travel |
| Ships | Simulation + harbor visuals |

---

# 4. EarthPulseModule

## Goal
Create a world-consequence engine where cities and regions react to player actions.

## Purpose
EarthPulse should make EarthLiving feel alive by connecting player activity, transport, reports, blueprints, tourism, property value, infrastructure, economy, NPC activity, and city history into one world-state system.

## Main idea

Actions should create consequences.

Example:

```text
Hotel completed in Copenhagen
-> tourism_score rises
-> commerce_score rises
-> transport demand rises
-> hotel/shop income rises
-> EarthOS shows Copenhagen as a tourism hotspot
```

## Region/city scores

| Score | Meaning |
|---|---|
| population_score | activity and city life |
| commerce_score | shops, companies and trade |
| transport_score | stations, routes and accessibility |
| safety_score | reports, crime, deaths and stability |
| infrastructure_score | roads, utilities, stations and services |
| tourism_score | landmarks, hotels, events and visitors |
| pollution_score | industry, traffic and environmental pressure |
| reputation_score | overall region reputation |

## Tourists

Use mostly simulated tourists and only a small number of visible NPC tourists near players.

Recommended model:

```text
90% simulated tourists
10% visible NPC tourists when players are nearby
```

Tourists can affect:

- hotel income
- shop demand
- ticket sales
- transport load
- city popularity
- region growth

Visible tourist NPCs can walk near stations, landmarks and hotels, wait for trains, visit shops and use small dialogue lines.

## EarthOS integration

Players should see region status, tourism, commerce, safety, transport pressure and current effects through EarthOS.

---

# 5. EarthPassportSystem

## Goal
Create passport, citizenship, visa and border gameplay.

## Purpose
EarthLiving is an Earth-map server, so travel and identity should feel connected to countries and regions without becoming boring paperwork cosplay. Humanity already invented enough of that.

## Features

- Player passport profile
- Citizenship / home country
- Visa types
- Travel permissions
- Border checkpoint integration
- Customs/import rules later
- Country reputation
- EarthOS Passport app

## EarthOS example

```text
Passport
Country: Denmark
Status: Citizen
Travel Visa: EU Zone
Reputation: Trusted
```

---

# 6. DynamicMediaNetwork

## Goal
Generate live server news from actual world events.

## Purpose
EarthLiving should have a news system that reports what is happening in the world instead of static admin posts only.

## Sources

- EarthPulse score changes
- transport delays
- reports
- city growth
- new landmarks
- economy changes
- events
- maintenance/update messages

## Example news

```text
Breaking News: Tourism rises in Copenhagen after a new harbor district opens.
Transport Alert: Berlin Central has heavy train delays.
Economy: Tokyo commercial activity increased by 12%.
```

## Integration

- EarthOS News app
- Discord announcement feed
- Pterodactyl dashboard feed

---

# 7. InfrastructureStressSystem

## Goal
Make cities require proper infrastructure as they grow.

## Purpose
Large cities should not grow forever without pressure. More players, transport, shops and tourism should create load that must be solved with stations, routes, utilities and services.

## Effects

- overloaded stations
- transport delays
- lower region satisfaction
- more reports
- increased maintenance costs
- need for upgrades

## Example

```text
Copenhagen Metro overloaded
-> train delay chance increases
-> commerce bonus drops near affected stations
-> EarthOS recommends metro expansion
```

---

# 8. RealEstateValueEngine

## Goal
Create dynamic property and land values.

## Purpose
Land should have value based on the living world around it, not just arbitrary admin pricing.

## Value factors

- nearby transport
- nearby landmarks
- tourism score
- safety score
- pollution score
- commerce score
- region reputation
- reports/problems nearby

## Example

```text
Apartment near major station
-> high property value

Shop in polluted low-traffic area
-> low property value
```

---

# 9. EarthLivingEventSimulator

## Goal
Generate world events that affect gameplay.

## Purpose
The server should occasionally create global or regional events that change transport, tourism, economy and city behavior.

## Event examples

- snowstorm in Northern Europe
- summer festival in Tokyo
- port strike in Rotterdam
- power issue in New York
- tourism boom in Paris
- transport outage in Berlin

## Effects

- transport delays
- tourism changes
- shop demand changes
- region alerts
- EarthOS notifications
- Discord news posts

---

# 10. DigitalTwinSystem

## Goal
Create an admin simulation view of the world state.

## Purpose
Admins should be able to see EarthLiving as a living system, almost like a Cities Skylines style management layer inside EarthOS Admin and Pterodactyl.

## Dashboard ideas

- city health map
- traffic pressure
- tourism hotspots
- economy flow
- report heatmap
- infrastructure problems
- transport bottlenecks
- active events

## Integration

- EarthOS Admin
- Pterodactyl Blueprint dashboard
- future AI city planning suggestions

---

# 11. LivingPowerGrid

## Goal
Add a simplified power/utility grid to cities.

## Purpose
Growing cities should need power and utilities, but not in an annoying micromanagement way. The system should create planning needs and world consequences.

## Features

- power demand per region
- power supply structures
- outages
- utility buildings
- solar/wind/fuel plant support
- transport and business effects during outages

## Example

```text
Power demand exceeds supply in Tokyo
-> metro reliability drops
-> business bonus reduced
-> EarthOS recommends utility upgrade
```

---

# 12. DynamicNPCWorkforce

## Goal
Create simulated and visible NPC workers that make cities feel active.

## Purpose
NPCs should not only stand still. Cities should have workers, commuters and service NPCs that reflect region activity.

## Model

Use simulated workforce as the main system and small visible NPC groups only near players.

## Possible NPC behaviors

- commute to stations
- visit workplaces
- go to shops
- appear near factories, offices, hotels and transport hubs
- create visual rush-hour effects

## Performance rule

Do not spawn huge permanent NPC populations. Use simulation first and visible NPCs only as local ambience.

---

# 13. EarthLivingLogisticsSystem

## Goal
Create a cargo and supply-chain system across the Earth map.

## Purpose
Containers, ships, trains, trucks, ports and warehouses should matter. Goods should move between regions and affect prices and business availability.

## Example flow

```text
Shanghai factory
-> container ship
-> Rotterdam harbor
-> freight train
-> Berlin warehouse
-> local shop
```

## Effects

- shop stock depends on supply chains
- port problems increase prices
- freight routes become valuable
- warehouses and harbors gain purpose
- transport infrastructure affects economy

---

# 14. MemoryHistorySystem

## Goal
Let the world remember major events.

## Purpose
EarthLiving should build history over time. Cities should have timelines players can read through EarthOS.

## Stored history examples

- first harbor opened
- major train failure
- tourism boom
- city founded
- landmark completed
- major event/festival
- large update/restart
- famous player project completed

## EarthOS example

```text
Copenhagen History
- Harbor District opened
- First InterCity line launched
- Tourism boom reached 72%
```

---

# 15. ArchitectModule

## Goal
Create an admin-only system for generating and managing building blueprints/schematics.

## Purpose
Admins should be able to generate or manage structures for EarthLiving cities, infrastructure, stations, landmarks, ports, airports, and special builds.

## Main idea

Admin command example:

```text
/architect generate "Copenhagen Central Station" preset:landmark style:minecraft_clean
```

System flow:

```text
Admin command
-> Async job
-> Building information lookup or manual metadata
-> Blueprint plan
-> Minecraft structure generation
-> .schem output
-> Preview placement
-> WorldEdit/FAWE paste or NPC builder construction
```

## Scale system for 1:326 Earth map

Do not use real map scale directly for buildings. Use gameplay/cinematic presets.

| Preset | Use case |
|---|---|
| compact | small landmarks or dense cities |
| landmark | normal landmark builds |
| cinematic | important large builds |
| mega | rare centerpiece projects |

## Notes
Generated structures should be Minecraft interpretations, not guaranteed exact replicas.

---

# 16. ArchitectPreviewModule

## Goal
Fix the WorldEdit placement problem with visual schematic preview.

## Purpose
Admins should be able to see where a schematic will be placed before confirming.

## Features

- Ghost preview
- Bounding box
- Move up/down/north/south/east/west
- Rotate
- Terrain detection
- Collision warnings
- Confirm/cancel workflow
- Never place real blocks before confirmation

## Commands

```text
/architect preview <schem>
/architect move up 1
/architect move down 1
/architect rotate 90
/architect confirm
/architect cancel
```

---

# 17. BuilderNPCModule

## Goal
Allow NPCs to build schematics slowly block-by-block.

## Purpose
Instead of instantly pasting structures, EarthLiving can show construction happening visually in the world.

## Features

- Builder NPC job
- Schematic input
- Block-by-block building
- Progress tracking
- Pause/resume/cancel
- Build speed config
- Future material delivery integration

## Example

```text
/architect buildnpc <schemId>
```

---

# 18. ReportModule

## Goal
Create a GUI-only reporting and support system for EarthLiving.

## Purpose
Players should be able to report bugs, player issues, transport problems, economy problems, building/blueprint problems, and suggestions through EarthOS. Reports from Discord should also enter the same system.

## Player workflow

```text
EarthOS
-> Support & Reports
-> Choose category
-> Temporary chat input, Anvil GUI, or Sign GUI for message
-> Report is created
-> Player can track status under My Reports
```

## Player-visible report status

Players should be able to see their own reports and status in EarthOS.

Statuses:

| Status | Meaning |
|---|---|
| open | Report has been received |
| reviewing | Admin/dev is reviewing |
| waiting | More information or testing is needed |
| sent_to_codex | Sent to Codex for investigation |
| fixed_pending_deploy | Fix exists but needs deploy/restart |
| deploying | Update is being deployed |
| resolved | Issue has been resolved |
| rejected | Report was rejected |
| archived | Closed and stored |

## Admin workflow

Admins should manage reports through EarthOS Admin GUI and Pterodactyl Blueprint dashboard.

Admin actions:

- Set status
- Set priority
- Add admin reply
- Assign report
- Analyze with AI
- Send to Codex
- Create GitHub issue
- Mark as resolved/rejected/archive

## Data to store

- Report ID
- Source: Minecraft or Discord
- Category
- Player name
- UUID
- Discord user ID if relevant
- Server/world
- Coordinates
- Message
- Status
- Priority
- Assigned admin
- Admin reply
- Created/updated timestamps

---

# 19. AI Report Pipeline

## Goal
Allow report investigation through the panel AI and Codex workflow.

## Purpose
A report should not just sit in a list. Admins should be able to ask the panel AI to analyze it, collect relevant context, and prepare a structured Codex handoff.

## Flow

```text
Player report or Discord report
-> Report Center database
-> Pterodactyl Blueprint dashboard
-> Panel AI analyzes report
-> AI suggests possible cause and solution
-> Admin approves
-> Send to Codex
-> Codex checks code/config/logs
-> Codex creates patch/commit/PR
-> Report status updates
```

## AI context package

When analyzing a report, the AI should receive safe, filtered context:

- Report ID
- Category
- Player message
- Location
- Nearest relevant system if available, such as station or region
- Relevant module
- Recent safe logs
- Recent changes if available
- No secrets or private credentials

## Codex handoff from panel

Use the standard handoff format:

```md
## Goal
Investigate and fix the report.

## Why
Explain why the issue matters.

## Details
Include report ID, module, logs, constraints, and safety rules.

## Expected output
Diagnosis, fix suggestion, code/config changes if safe, build/test result, and report update.
```

---

# 20. UpdateManagerModule

## Goal
Create update, maintenance, countdown, restart, and deploy management for EarthLiving.

## Purpose
When Codex or an admin creates a fix, players should be warned before deployment/restart. The system should connect report fixes with maintenance flow and EarthOS notifications.

## Flow

```text
Codex creates fix
-> Build/test
-> Admin reviews in panel
-> Admin schedules deployment/restart
-> EarthOS warns players
-> Countdown starts
-> Auto-save
-> Maintenance mode
-> Server restart/deploy
-> Server comes online
-> Report status updates
-> Changelog/status message is sent
```

## Player-facing EarthOS features

- Server status screen
- Maintenance warning
- Countdown display
- Update reason
- Expected downtime
- Changelog after restart
- Notification when a player's report is resolved

## Admin-facing EarthOS/Pterodactyl features

- Schedule restart
- Start countdown
- Cancel restart
- Enable maintenance mode
- Send server-wide message
- Deploy update
- Review Codex fix
- Mark related reports as resolved after deploy

## Countdown intervals

Default warning points:

- 10 minutes
- 5 minutes
- 3 minutes
- 1 minute
- 30 seconds
- 10 seconds
- 5 to 1 seconds

## Report integration

ReportModule should support deployment-related statuses:

- fixed_pending_deploy
- deploying
- resolved

---

# 21. Premium Plugin Licensing System

## Goal
Plan future protection for plugins that may be sold publicly.

## Purpose
If EarthLiving tools are later released as premium Spigot/BuiltByBit resources, they should discourage unauthorized redistribution.

## Protection ideas

- Obfuscation
- License API
- Watermarked builds
- Online premium features
- Reasonable offline fallback
- No destructive DRM

## Important limitation

No Java Minecraft plugin can be made impossible to crack. The goal is to make cracking inconvenient while giving paying users better support and updates.

---

# 22. Pterodactyl + Blueprint Framework Integration

## Goal
Create admin tools inside Pterodactyl using Blueprint Framework.

## Purpose
EarthLiving should have a custom admin dashboard for project/server management.

## Possible pages

- EarthLiving dashboard
- AI assistant page
- Codex task panel
- Report Center
- EarthPulse / region dashboard
- Digital Twin dashboard
- Transport dashboard
- Architect job dashboard
- Update Manager dashboard
- Server status widgets
- Logs/crash analyzer
- Backup/deploy buttons

---

# 23. ChatGPT + Codex Collaboration Workflow

## Goal
Keep ChatGPT and Codex aligned on the project.

## Shared context

Use `PROJECT_CONTEXT.md` when available.

## Handoff format

When ChatGPT has an idea for Codex, use:

```md
## Goal
What should Codex do?

## Why
Why is this useful?

## Details
Important details, links, constraints, or risks.

## Expected output
What should Codex produce or change?
```

## Security reminder

Do not include passwords, SSH keys, API keys, recovery codes, or private credentials.

---

# Suggested development order

1. EarthLivingCore foundation - started as `plugins/earthlivingcore`
2. EarthOS Version 1 inventory menu - started inside EarthLivingCore
3. ReportModule GUI-only basics
4. Player My Reports GUI and admin Report Center GUI
5. EarthPulseModule V1 region scores and simulated tourists
6. DynamicMediaNetwork V1 news feed from reports/events
7. TransportModule basics
8. EarthPassportSystem basics
9. ArchitectModule V1 with manual/simple schematic generation
10. InfrastructureStressSystem V1
11. RealEstateValueEngine V1
12. ArchitectPreviewModule
13. BuilderNPCModule
14. EarthLivingLogisticsSystem V1
15. Pterodactyl Blueprint admin dashboard
16. AI Report Pipeline
17. UpdateManagerModule
18. DigitalTwinSystem dashboard
19. AI/web lookup for ArchitectModule
20. Licensing system for future public plugins

---

# Open decisions

- Which economy plugin or internal economy API should EarthLiving use?
- Should EarthPulse use custom regions, WorldGuard regions, or a dedicated region database?
- How many visible tourist NPCs should be allowed per loaded region?
- Should logistics use physical crates/items, simulated stock values, or both?
- Should DynamicMediaNetwork post to Discord automatically or wait for admin approval?
- Should the power grid be light simulation only or tied to actual redstone/custom blocks?
- Should ArchitectModule use FAWE first or WorldEdit first?
- Should NPC building use Citizens, FancyNpcs, or a custom NPC system?
- Should EarthOS be DeluxeMenus first or fully custom GUI first?
- Should report text input use chat-input, Anvil GUI, Sign GUI, or multiple options?
- Should Codex create direct commits, branches, or pull requests for AI report fixes?
- Which plugin ideas are private EarthLiving-only and which could become public/premium resources?
