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
- language/localization
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
- Language settings app
- Region/city status app
- Mining Authority app
- Build/blueprint app
- Support & reports app
- My reports app
- Server status app
- Changelog/update notifications
- Admin panel screens
- Settings app

## Hotbar icon decision

EarthOS should use the gold/cyan globe-compass icon as the hotbar menu device.

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

Players should not need commands for normal gameplay. EarthOS is the visible interface layer for reports, transport, economy, language, settings, notifications, mining, and server status. Admin debug commands can exist, but the main workflow should be GUI-based.

---

# 3. EarthLanguageSystem

## Goal
Create a GUI-first language and localization system.

## Purpose
New players should choose their language through a GUI on first join, and EarthOS/messages should use that language. This is important for an Earth-map server with international regions and players.

## First join workflow

```text
First join
-> language selection GUI opens
-> player chooses language
-> language is saved by UUID
-> EarthOS and server messages use the selected language
```

## Suggested languages

- Dansk `da_DK`
- English `en_US`
- Deutsch `de_DE`
- Svenska `sv_SE`
- Norsk `no_NO`
- Français `fr_FR`
- Español `es_ES`

## Language files

Use language files instead of hardcoded text.

```text
plugins/EarthLivingCore/lang/
├── da_DK.yml
├── en_US.yml
├── de_DE.yml
├── sv_SE.yml
└── no_NO.yml
```

Example keys:

```yaml
earthos.title: "EarthOS"
earthos.settings.language: "Sprog"
reports.created: "Din rapport #{id} er oprettet."
transport.ticket_bought: "Du har købt billet til {destination}."
server.restart_warning: "Serveren genstarter om {time}."
```

## Later language changes

First language selection should be free. Later changes through EarthOS Settings can require a configurable fee and cooldown.

Suggested defaults:

| Rule | Default |
|---|---|
| First language selection | Free |
| Grace period after first choice | 10 minutes |
| Later change fee | 250 coins |
| Cooldown | 24 hours |
| Admin override | Free |

The fee should be configurable and integrated with the economy provider.

## Integrations

EarthLanguageSystem should integrate with:

- EarthOS
- ReportModule
- TransportModule
- UpdateManagerModule
- DynamicMediaNetwork
- NPC dialogue
- Discord messages later

---

# 4. TransportModule

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

# 5. EarthPulseModule

## Goal
Create a world-consequence engine where cities and regions react to player actions.

## Purpose
EarthPulse should make EarthLiving feel alive by connecting player activity, transport, reports, blueprints, tourism, property value, infrastructure, mining, economy, NPC activity, and city history into one world-state system.

## Region/city scores

| Score | Meaning |
|---|---|
| population_score | activity and city life |
| commerce_score | shops, companies and trade |
| transport_score | stations, routes and accessibility |
| safety_score | reports, crime, deaths and stability |
| infrastructure_score | roads, utilities, stations and services |
| tourism_score | landmarks, hotels, events and visitors |
| mining_score | mining activity and resource production |
| culture_score | local identity and region character |
| public_service_score | police, fire, medical and maintenance coverage |
| pollution_score | industry, traffic and environmental pressure |
| reputation_score | overall region reputation |

## Tourists

Use mostly simulated tourists and only a small number of visible NPC tourists near players.

Recommended model:

```text
90% simulated tourists
10% visible NPC tourists when players are nearby
```

---

# 6. EarthMiningSystem

## Goal
Create a balanced, realistic mining and resource distribution system for the Earth map.

## Purpose
EarthLiving should have mining that feels connected to the real world without creating unfair resource monopolies. Resources should exist globally, but regions should have realistic bonuses and special mining opportunities.

## Core design

All basic resources should be available globally, but certain regions should be better for specific resources.

```text
Iron can exist globally
Sweden has a strong iron bonus
Chile has a strong copper bonus
Saudi Arabia has a strong oil bonus
South Africa has a gold/diamond bonus
```

## V1 scope

- RegionResourceProfile model
- Deposit model
- MiningZone model
- license system
- deposit quality
- basic depletion
- EarthOS Mining Authority GUI
- admin-created mining zones
- read-only Pterodactyl dashboard export

---

# 7. EarthCultureSystem

## Goal
Let regions develop culture and identity over time.

## Purpose
Different regions should feel distinct through bonuses, visual themes, local events and local identity rather than just coordinates on a map.

## Examples

- Japan-style regions can develop tech/neon/tourism bonuses.
- European city regions can develop public transport and dense property bonuses.
- American regions can develop vehicle/logistics/fuel bonuses.
- Mining regions can develop industrial identity and export bonuses.

## Integration

- EarthPulse culture_score
- DynamicMediaNetwork news
- Tourism systems
- EarthOS region profiles
- AI city planner suggestions

---

# 8. DynamicWeatherDisasterSystem

## Goal
Create persistent climate and disaster systems.

## Purpose
EarthLiving should have regional weather/disaster events that affect transport, tourism, infrastructure, mining, power and public services.

## Examples

- sandstorms in desert regions
- hurricanes in coastal regions
- floods in river/coastal cities
- snowstorms in northern regions
- heatwaves in warm regions
- earthquakes in selected seismic zones

## Effects

- visibility changes
- flight/ship/train delays
- infrastructure damage
- power demand spikes
- tourism drops
- repair contracts/events
- EarthOS warnings

---

# 9. GovernmentCityAdministrationSystem

## Goal
Create city administration, budgets and public decision systems.

## Purpose
Large cities should be manageable by admins or trusted player governments through EarthOS, with budgets, project proposals and public votes.

## Features

- mayor/city admin roles
- city budget
- taxes/fees later
- public project proposals
- voting/polls
- infrastructure spending
- city policy settings

## Integration

- EarthOS City Administration app
- RealEstateValueEngine
- InfrastructureStressSystem
- PublicServicesSystem
- DynamicMediaNetwork

---

# 10. PublicServicesSystem

## Goal
Add police, ambulance, fire, garbage and maintenance service simulation.

## Purpose
Cities should need services to stay safe and functional. Missing public services should affect safety, reputation, tourism and infrastructure.

## Services

- police
- ambulance/hospital
- fire department
- garbage/cleanup
- road/rail maintenance
- utility repair crews

## Effects

- safety_score changes
- public_service_score changes
- event response speed
- tourism/reputation effects
- maintenance jobs and contracts

---

# 11. DynamicTrafficSystem

## Goal
Simulate traffic and congestion around cities, stations and logistics routes.

## Purpose
Transport should react to demand. Busy regions should create congestion, station crowding and delay risk.

## Features

- station load
- route congestion
- rush-hour simulation
- logistics bottlenecks
- NPC crowd ambience near players
- EarthOS traffic warnings

---

# 12. AirportSystem

## Goal
Create airport-specific systems for flights and international travel.

## Purpose
Airports are important for an Earth server and should be more than teleport menus.

## Features

- gates
- arrivals/departures
- boarding workflow
- luggage/cargo later
- customs/passport integration
- runway traffic simulation
- weather delay integration

---

# 13. EarthInternetSystem

## Goal
Make EarthOS function as the in-world digital internet.

## Purpose
Players and companies should use EarthOS for communication, services, ads and business tools.

## Features

- player mail/messages
- company pages
- ads and listings
- service bookings
- market listings
- transport booking
- government announcements

---

# 14. CompanyCorporationSystem

## Goal
Allow players to create and manage companies.

## Purpose
Companies should own and operate mines, logistics, shops, hotels, airlines, factories and construction projects.

## Features

- company creation
- ownership/members
- company wallet
- permissions
- assets
- contracts/jobs
- reputation
- taxes/fees later

---

# 15. FinancialMarketSystem

## Goal
Create optional financial and commodity market gameplay.

## Purpose
EarthLiving can support deeper economy through commodities, company shares and city investments.

## Features

- commodity prices
- mining futures later
- company shares later
- city bonds/investment later
- market news

## Safety note

Keep this as gameplay economy only. Avoid real money gambling or real-world financial implications.

---

# 16. CrimeInvestigationSystem

## Goal
Create deeper crime, smuggling and investigation gameplay.

## Purpose
EarthLiving can support controlled illegal trade, smuggling risk and investigation gameplay without turning the server into chaos.

## Features

- smuggling routes
- customs violations
- illegal cargo flags
- police reports
- investigation cases
- evidence logs
- admin/police EarthOS database

---

# 17. HealthDiseaseSystem

## Goal
Create light health and disease/world condition simulation.

## Purpose
Pollution, disasters, heatwaves and crowded cities can affect health and hospital load without becoming annoying survival micromanagement.

## Features

- pollution effects
- heatwave/cold exposure effects
- flu/outbreak events
- hospital/service load
- EarthOS health warnings

---

# 18. EducationResearchSystem

## Goal
Add universities, research and technology bonuses.

## Purpose
Regions can specialize through universities and research facilities.

## Bonuses

- mining efficiency
- transport efficiency
- power efficiency
- tourism/culture bonuses
- logistics improvements
- AI city planning upgrades

---

# 19. DynamicAdvertisingSystem

## Goal
Allow companies/cities to buy in-world and EarthOS advertising.

## Purpose
Ads can support commerce, events, transport lines and player companies.

## Ad types

- billboards
- station ads
- airport ads
- EarthOS ads
- Discord announcement slots if approved

---

# 20. SpaceSatelliteSystem

## Goal
Add satellites as high-level utility infrastructure.

## Purpose
Satellites can support map scanning, weather prediction, logistics tracking and disaster warnings.

## Features

- satellite launches later
- weather forecast bonuses
- logistics tracking
- mining survey bonuses
- disaster early warning
- map scan overlays

---

# 21. UndergroundInfrastructureSystem

## Goal
Add underground infrastructure layers for cities.

## Purpose
Cities should have depth: metro, power cables, water/sewage, fiber/internet and utility tunnels.

## Features

- metro tunnels
- power lines
- water/sewage simulation later
- fiber/internet network later
- maintenance access
- underground planning view in EarthOS Admin

---

# 22. TourismAttractionRatingSystem

## Goal
Rate attractions and tourist areas dynamically.

## Purpose
Tourism should respond to landmarks, nightlife, transport, safety, weather, pollution and culture.

## Factors

- landmark quality
- nightlife
- transport access
- safety
- weather/events
- pollution
- culture score

---

# 23. DynamicConstructionEconomy

## Goal
Make construction depend on materials, permits, workers and delays.

## Purpose
Large builds should feel like projects with supply chains and planning, especially for cities, stations, mines, factories and landmarks.

## Features

- permits
- material demand
- construction stages
- worker shortages
- logistics dependencies
- delays and progress updates

---

# 24. EarthLivingTimeSimulation

## Goal
Create day/night and schedule-based city behavior.

## Purpose
Cities should feel different depending on time.

## Examples

- morning rush hour
- evening commute
- night tourism/nightlife
- weekend events
- workday logistics peaks

---

# 25. AICityPlanner

## Goal
Use AI to suggest improvements for cities and regions.

## Purpose
Admins should be able to ask for planning suggestions based on EarthPulse, traffic, infrastructure, reports, mining and economy data.

## Example

```text
Copenhagen transport congestion is rising.
Recommended:
- add metro station
- increase rail throughput
- expand bus terminal
```

---

# 26. ReputationGlobalInfluenceSystem

## Goal
Give cities and regions identity and influence.

## Purpose
Regions should become known for what they do well.

## Examples

- Tokyo: tech capital
- Paris: tourism capital
- Sweden: mining/export powerhouse
- Rotterdam: logistics hub

---

# 27. EarthPassportSystem

## Goal
Create passport, citizenship, visa and border gameplay.

## Features

- Player passport profile
- Citizenship / home country
- Visa types
- Travel permissions
- Border checkpoint integration
- Customs/import rules later
- Country reputation
- EarthOS Passport app

---

# 28. DynamicMediaNetwork

## Goal
Generate live server news from actual world events.

## Sources

- EarthPulse score changes
- transport delays
- reports
- city growth
- new landmarks
- economy changes
- mining booms or shortages
- events
- maintenance/update messages

---

# 29. InfrastructureStressSystem

## Goal
Make cities require proper infrastructure as they grow.

## Effects

- overloaded stations
- transport delays
- lower region satisfaction
- more reports
- increased maintenance costs
- need for upgrades

---

# 30. RealEstateValueEngine

## Goal
Create dynamic property and land values.

## Value factors

- nearby transport
- nearby landmarks
- tourism score
- safety score
- pollution score
- commerce score
- mining/industry activity
- region reputation
- reports/problems nearby

---

# 31. EarthLivingEventSimulator

## Goal
Generate world events that affect gameplay.

## Event examples

- snowstorm in Northern Europe
- summer festival in Tokyo
- port strike in Rotterdam
- power issue in New York
- tourism boom in Paris
- mining boom in Sweden
- copper shortage in Chile
- transport outage in Berlin

---

# 32. DigitalTwinSystem

## Goal
Create an admin simulation view of the world state.

## Dashboard ideas

- city health map
- traffic pressure
- tourism hotspots
- mining/resource hotspots
- economy flow
- logistics flow
- report heatmap
- infrastructure problems
- transport bottlenecks
- active events

---

# 33. LivingPowerGrid

## Goal
Add a simplified power/utility grid to cities.

## Features

- power demand per region
- power supply structures
- outages
- utility buildings
- solar/wind/fuel plant support
- mining/industrial power demand
- transport and business effects during outages

---

# 34. DynamicNPCWorkforce

## Goal
Create simulated and visible NPC workers that make cities feel active.

---

# 35. EarthLivingLogisticsSystem

## Goal
Create a cargo and supply-chain system across the Earth map.

---

# 36. MemoryHistorySystem

## Goal
Let the world remember major events.

---

# 37. ArchitectModule

## Goal
Create an admin-only system for generating and managing building blueprints/schematics.

---

# 38. ArchitectPreviewModule

## Goal
Fix the WorldEdit placement problem with visual schematic preview.

---

# 39. BuilderNPCModule

## Goal
Allow NPCs to build schematics slowly block-by-block.

---

# 40. ReportModule

## Goal
Create a GUI-only reporting and support system for EarthLiving.

---

# 41. AI Report Pipeline

## Goal
Allow report investigation through the panel AI and Codex workflow.

---

# 42. UpdateManagerModule

## Goal
Create update, maintenance, countdown, restart, and deploy management for EarthLiving.

---

# 43. Premium Plugin Licensing System

## Goal
Plan future protection for plugins that may be sold publicly.

---

# 44. Pterodactyl + Blueprint Framework Integration

## Goal
Create admin tools inside Pterodactyl using Blueprint Framework.

Possible pages:

- EarthLiving dashboard
- AI assistant page
- Codex task panel
- Report Center
- EarthPulse / region dashboard
- Mining Authority dashboard
- Digital Twin dashboard
- Language/Localization dashboard
- Transport dashboard
- Architect job dashboard
- Update Manager dashboard
- Server status widgets
- Logs/crash analyzer
- Backup/deploy buttons

---

# 45. ChatGPT + Codex Collaboration Workflow

## Goal
Keep ChatGPT and Codex aligned on the project.

Use `PROJECT_CONTEXT.md` when available.

Handoff format:

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

Security reminder: do not include passwords, SSH keys, API keys, recovery codes, or private credentials.

---

# Suggested development order

1. EarthLivingCore foundation - started as `plugins/earthlivingcore`
2. EarthOS Version 1 inventory menu - started inside EarthLivingCore
3. EarthLanguageSystem first-join language GUI
4. ReportModule GUI-only basics
5. Player My Reports GUI and admin Report Center GUI
6. EarthPulseModule V1 region scores and simulated tourists
7. EarthMiningSystem V1 region profiles, deposits, mining zones and EarthOS Mining Authority
8. DynamicMediaNetwork V1 news feed from reports/events/mining
9. TransportModule basics
10. EarthPassportSystem basics
11. CompanyCorporationSystem basics
12. ArchitectModule V1 with manual/simple schematic generation
13. InfrastructureStressSystem V1
14. RealEstateValueEngine V1
15. PublicServicesSystem V1
16. DynamicTrafficSystem V1
17. ArchitectPreviewModule
18. BuilderNPCModule
19. EarthLivingLogisticsSystem V1
20. Pterodactyl Blueprint admin dashboard
21. AI Report Pipeline
22. UpdateManagerModule
23. DigitalTwinSystem dashboard
24. AICityPlanner
25. AI/web lookup for ArchitectModule
26. Licensing system for future public plugins

---

# Open decisions

- Which economy plugin or internal economy API should EarthLiving use?
- Which languages should be supported at launch?
- Should later language changes cost coins, and what should the default fee be?
- Should EarthPulse use custom regions, WorldGuard regions, or a dedicated region database?
- Should EarthMiningSystem use custom regions, biome/geology profiles, or admin-created zones first?
- How many visible tourist NPCs should be allowed per loaded region?
- Should logistics use physical crates/items, simulated stock values, or both?
- Should DynamicMediaNetwork post to Discord automatically or wait for admin approval?
- Should the power grid be light simulation only or tied to actual redstone/custom blocks?
- Should mining use block replacement, custom drops, GUI jobs, or controlled resource nodes?
- Should mega deposits require companies, licenses, transport infrastructure, or admin approval?
- Should government/city administration be admin-only first or player-elected later?
- Should crime/smuggling be limited to admin-controlled events first?
- Should financial markets stay as commodity-only at first?
- Should ArchitectModule use FAWE first or WorldEdit first?
- Should NPC building use Citizens, FancyNpcs, or a custom NPC system?
- Should EarthOS be DeluxeMenus first or fully custom GUI first?
- Should report text input use chat-input, Anvil GUI, Sign GUI, or multiple options?
- Should Codex create direct commits, branches, or pull requests for AI report fixes?
- Which plugin ideas are private EarthLiving-only and which could become public/premium resources?
