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
- Pterodactyl and Blueprint Framework can be used for admin panels and dashboards
- No passwords, SSH keys, API keys, recovery codes, or private credentials in repo files

---

# 1. EarthLivingCore

## Goal
Create the central plugin/core layer for EarthLiving.

## Purpose
EarthLivingCore should connect all major systems:

- EarthOS menu/device
- transport
- economy
- player profile data
- notifications
- region data
- architect/blueprint tools
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
- Build/blueprint app
- Settings app

## Visual direction

- Dark navy/black base
- Gold and cyan highlights
- EarthLiving logo branding
- MMO-style inventory GUI first
- Later custom font/overlay UI

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

# 4. ArchitectModule

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

# 5. ArchitectPreviewModule

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

# 6. BuilderNPCModule

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

# 7. Premium Plugin Licensing System

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

# 8. Pterodactyl + Blueprint Framework Integration

## Goal
Create admin tools inside Pterodactyl using Blueprint Framework.

## Purpose
EarthLiving should have a custom admin dashboard for project/server management.

## Possible pages

- EarthLiving dashboard
- AI assistant page
- Codex task panel
- Transport dashboard
- Architect job dashboard
- Server status widgets
- Logs/crash analyzer
- Backup/deploy buttons

---

# 9. ChatGPT + Codex Collaboration Workflow

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

1. EarthLivingCore foundation
2. EarthOS Version 1 inventory menu
3. TransportModule basics
4. ArchitectModule V1 with manual/simple schematic generation
5. ArchitectPreviewModule
6. BuilderNPCModule
7. Pterodactyl Blueprint admin dashboard
8. AI/web lookup for ArchitectModule
9. Licensing system for future public plugins

---

# Open decisions

- Which economy plugin or internal economy API should EarthLiving use?
- Should ArchitectModule use FAWE first or WorldEdit first?
- Should NPC building use Citizens, FancyNpcs, or a custom NPC system?
- Should EarthOS be DeluxeMenus first or fully custom GUI first?
- Which plugin ideas are private EarthLiving-only and which could become public/premium resources?
