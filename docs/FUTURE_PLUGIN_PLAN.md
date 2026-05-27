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
- Nightlife app
- Build/blueprint app
- Support & reports app
- My reports app
- Server status app
- Changelog/update notifications
- Admin panel screens
- Settings app

## GUI-first rule

Players should not need commands for normal gameplay. EarthOS is the visible interface layer for reports, transport, economy, language, settings, notifications, mining, nightlife, and server status.

---

# 3. EarthLanguageSystem

## Goal
Create a GUI-first language and localization system.

## Purpose
New players should choose their language through a GUI on first join, and EarthOS/messages should use that language.

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

---

# 4. TransportModule

## Goal
Build the foundation for immersive transport.

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

---

# 5. EarthPulseModule

## Goal
Create a world-consequence engine where cities and regions react to player actions.

## Region/city scores

| Score | Meaning |
|---|---|
| population_score | activity and city life |
| commerce_score | shops, companies and trade |
| transport_score | stations, routes and accessibility |
| safety_score | reports, crime, deaths and stability |
| infrastructure_score | roads, utilities, stations and services |
| tourism_score | landmarks, hotels, events and visitors |
| nightlife_score | bars, clubs, music venues and night tourism |
| mining_score | mining activity and resource production |
| culture_score | local identity and region character |
| public_service_score | police, fire, medical and maintenance coverage |
| pollution_score | industry, traffic and environmental pressure |
| reputation_score | overall region reputation |

---

# 6. EarthMiningSystem

## Goal
Create a balanced, realistic mining and resource distribution system for the Earth map.

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

# 7. EarthNightlifeSystem

## Goal
Create bars, clubs, discos, DJ areas, dancing NPCs and nightlife events.

## Purpose
EarthLiving cities should feel alive at night. Bars and discos should support tourism, culture, economy, social areas and city identity.

## Best technical solution

Use a mixed approach:

| Feature | Recommended solution |
|---|---|
| Dancers | Armor stand animation loops or lightweight custom NPC animation |
| Bartenders | FancyNpcs/custom NPCs |
| DJ NPCs | Static NPC with animation, particles and sound triggers |
| Music | Resource pack/custom sounds where possible |
| Lighting | Particles, redstone/custom blocks and timed light patterns |
| Smoke/fog | Particles only near players |
| Popularity | EarthPulse nightlife_score and tourism integration |

## Dancing NPC options

- Armor stands can animate arms, legs, head, rotation and small movement loops.
- Player/villager-style NPCs can fake dance by looking around, crouching, jumping, rotating and moving in small loops.
- Custom models can provide better dance animations later, but should be treated as a later upgrade.

## Performance rule

Do not spawn large permanent dancing crowds. Use a small number of local visible dancers when players are nearby, and simulate the rest.

## EarthOS integration

```text
EarthOS -> Nightlife
```

Can show:

- bars
- clubs/discos
- events
- VIP tickets
- popularity
- opening hours
- entry fee

## Example club status

```text
Las Vegas Club
Status: Open
Music: Electronic
NPC dancers: 12
Popularity: 84%
Entry fee: 150 coins
```

## Integration

- EarthPulse nightlife_score
- TourismAttractionRatingSystem
- DynamicMediaNetwork
- Economy/company systems
- Time simulation
- Pterodactyl dashboard later

---

# 8. EarthCultureSystem

## Goal
Let regions develop culture and identity over time.

---

# 9. DynamicWeatherDisasterSystem

## Goal
Create persistent climate and disaster systems.

---

# 10. GovernmentCityAdministrationSystem

## Goal
Create city administration, budgets and public decision systems.

---

# 11. PublicServicesSystem

## Goal
Add police, ambulance, fire, garbage and maintenance service simulation.

---

# 12. DynamicTrafficSystem

## Goal
Simulate traffic and congestion around cities, stations and logistics routes.

---

# 13. AirportSystem

## Goal
Create airport-specific systems for flights and international travel.

---

# 14. EarthInternetSystem

## Goal
Make EarthOS function as the in-world digital internet.

---

# 15. CompanyCorporationSystem

## Goal
Allow players to create and manage companies.

---

# 16. FinancialMarketSystem

## Goal
Create optional financial and commodity market gameplay.

---

# 17. CrimeInvestigationSystem

## Goal
Create deeper crime, smuggling and investigation gameplay.

---

# 18. HealthDiseaseSystem

## Goal
Create light health and disease/world condition simulation.

---

# 19. EducationResearchSystem

## Goal
Add universities, research and technology bonuses.

---

# 20. DynamicAdvertisingSystem

## Goal
Allow companies/cities to buy in-world and EarthOS advertising.

---

# 21. SpaceSatelliteSystem

## Goal
Add satellites as high-level utility infrastructure.

---

# 22. UndergroundInfrastructureSystem

## Goal
Add underground infrastructure layers for cities.

---

# 23. TourismAttractionRatingSystem

## Goal
Rate attractions and tourist areas dynamically.

---

# 24. DynamicConstructionEconomy

## Goal
Make construction depend on materials, permits, workers and delays.

---

# 25. EarthLivingTimeSimulation

## Goal
Create day/night and schedule-based city behavior.

---

# 26. AICityPlanner

## Goal
Use AI to suggest improvements for cities and regions.

---

# 27. ReputationGlobalInfluenceSystem

## Goal
Give cities and regions identity and influence.

---

# 28. EarthPassportSystem

## Goal
Create passport, citizenship, visa and border gameplay.

## Current V1

EarthLivingCore `0.7.2` now owns the first passport foundation:

- EarthOS Passport app
- player passport profile
- citizenship/home country
- visa records
- country reputation
- staff admin commands
- read-only JSON export for the future web profile

The older `PassportBorders` plugin remains the polygon/border prototype. Future work should connect it to EarthLivingCore passport data instead of letting both systems own separate passport rules.

---

# 29. DynamicMediaNetwork

## Goal
Generate live server news from actual world events.

---

# 30. InfrastructureStressSystem

## Goal
Make cities require proper infrastructure as they grow.

---

# 31. RealEstateValueEngine

## Goal
Create dynamic property and land values.

---

# 32. EarthLivingEventSimulator

## Goal
Generate world events that affect gameplay.

---

# 33. DigitalTwinSystem

## Goal
Create an admin simulation view of the world state.

---

# 34. LivingPowerGrid

## Goal
Add a simplified power/utility grid to cities.

---

# 35. DynamicNPCWorkforce

## Goal
Create simulated and visible NPC workers that make cities feel active.

---

# 36. EarthLivingLogisticsSystem

## Goal
Create a cargo and supply-chain system across the Earth map.

---

# 37. MemoryHistorySystem

## Goal
Let the world remember major events.

---

# 38. ArchitectModule

## Goal
Create an admin-only system for generating and managing building blueprints/schematics.

## Current plugin test

Hub test server has Citizens and Constructor installed for evaluation:

- Citizens `2.0.42-SNAPSHOT build 4160`
- Constructor `3.5`

Citizens is a good fit for hub guides, passport/verification staff NPCs, departure hall NPCs and later city ambience.

Constructor is useful only as an experimental builder-NPC prototype. It depends on Citizens and WorldEdit and loaded cleanly on the hub, but it is older (`api-version: 1.13`) and should not become a core production dependency until it has passed an in-game builder test on the current Paper version.

Update check on 2026-05-27 found `Constructor 3.5` as the newest visible release/changelog entry. Do not spend time looking for a newer jar unless an official upstream source appears; test the current jar on the hub first.

---

# 39. ArchitectPreviewModule

## Goal
Fix the WorldEdit placement problem with visual schematic preview.

---

# 40. BuilderNPCModule

## Goal
Allow NPCs to build schematics slowly block-by-block.

## Current test direction

Use Citizens as the NPC foundation. Test Constructor on the hub only:

- create one builder NPC
- load one small schematic
- preview the build
- test material donation or creative/admin build mode
- cancel and clean up safely
- confirm no console errors, stuck NPC loops or chunk issues

If Constructor behaves poorly, replace it with a custom EarthLiving BuilderNPCModule later instead of depending on the old plugin.

## Placement preview V1

EarthLivingCore `0.7.3` adds a safe hub/test preview command:

```text
/el preview <width> <height> <depth> [yOffset] [seconds]
/el preview clear
```

This draws a temporary particle wireframe from the player's current block position. Use it before Constructor or WorldEdit placement to verify footprint and vertical alignment. This is not a full schematic parser yet; future V2 should read `.schem` dimensions automatically and hand the chosen origin to Constructor or a custom BuilderNPCModule.

---

# 41. ReportModule

## Goal
Create a GUI-only reporting and support system for EarthLiving.

---

# 42. AI Report Pipeline

## Goal
Allow report investigation through the panel AI and Codex workflow.

---

# 43. UpdateManagerModule

## Goal
Create update, maintenance, countdown, restart, and deploy management for EarthLiving.

---

# 44. Premium Plugin Licensing System

## Goal
Plan future protection for plugins that may be sold publicly.

---

# 45. Pterodactyl + Blueprint Framework Integration

## Goal
Create admin tools inside Pterodactyl using Blueprint Framework.

Possible pages:

- EarthLiving dashboard
- AI assistant page
- Codex task panel
- Report Center
- EarthPulse / region dashboard
- Mining Authority dashboard
- Nightlife dashboard
- Digital Twin dashboard
- Language/Localization dashboard
- Transport dashboard
- Architect job dashboard
- Update Manager dashboard
- Server status widgets
- Logs/crash analyzer
- Backup/deploy buttons

---

# 46. ChatGPT + Codex Collaboration Workflow

## Goal
Keep ChatGPT and Codex aligned on the project.

Use `PROJECT_CONTEXT.md` when available.

---

# Suggested development order

1. EarthLivingCore foundation - started as `plugins/earthlivingcore`
2. EarthOS Version 1 inventory menu - started inside EarthLivingCore
3. EarthLanguageSystem first-join language GUI
4. ReportModule GUI-only basics
5. Player My Reports GUI and admin Report Center GUI
6. EarthPulseModule V1 region scores and simulated tourists
7. EarthMiningSystem V1 region profiles, deposits, mining zones and EarthOS Mining Authority
8. EarthNightlifeSystem V1 bars, clubs, dancing NPC loops and nightlife score
9. DynamicMediaNetwork V1 news feed from reports/events/mining/nightlife
10. TransportModule basics
11. EarthPassportSystem basics
12. CompanyCorporationSystem basics
13. ArchitectModule V1 with manual/simple schematic generation
14. InfrastructureStressSystem V1
15. RealEstateValueEngine V1
16. PublicServicesSystem V1
17. DynamicTrafficSystem V1
18. ArchitectPreviewModule
19. BuilderNPCModule
20. EarthLivingLogisticsSystem V1
21. Pterodactyl Blueprint admin dashboard
22. AI Report Pipeline
23. UpdateManagerModule
24. DigitalTwinSystem dashboard
25. AICityPlanner
26. AI/web lookup for ArchitectModule
27. Licensing system for future public plugins

---

# Open decisions

- Which economy plugin or internal economy API should EarthLiving use?
- Which languages should be supported at launch?
- Should later language changes cost coins, and what should the default fee be?
- Should EarthNightlifeSystem use armor stands first, FancyNpcs first, or custom model animations later?
- How many dancing NPCs should be allowed per club while players are nearby?
- Should clubs use custom resource-pack music or vanilla note/sound loops first?
- Should bars/clubs be player-owned companies later?
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
