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

EarthLivingCore v0.2.0 has been started under `plugins/earthlivingcore/`.

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
  - Reports opens a category menu
  - Quick reports are saved to `reports.yml`
  - Saved data includes category, player, UUID, world, coordinates, timestamp and status
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

# 7. ReportModule

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

# 8. AI Report Pipeline

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

# 9. UpdateManagerModule

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

# 10. Premium Plugin Licensing System

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

# 11. Pterodactyl + Blueprint Framework Integration

## Goal
Create admin tools inside Pterodactyl using Blueprint Framework.

## Purpose
EarthLiving should have a custom admin dashboard for project/server management.

## Possible pages

- EarthLiving dashboard
- AI assistant page
- Codex task panel
- Report Center
- Transport dashboard
- Architect job dashboard
- Update Manager dashboard
- Server status widgets
- Logs/crash analyzer
- Backup/deploy buttons

---

# 12. ChatGPT + Codex Collaboration Workflow

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
5. TransportModule basics
6. ArchitectModule V1 with manual/simple schematic generation
7. ArchitectPreviewModule
8. BuilderNPCModule
9. Pterodactyl Blueprint admin dashboard
10. AI Report Pipeline
11. UpdateManagerModule
12. AI/web lookup for ArchitectModule
13. Licensing system for future public plugins

---

# Open decisions

- Which economy plugin or internal economy API should EarthLiving use?
- Should ArchitectModule use FAWE first or WorldEdit first?
- Should NPC building use Citizens, FancyNpcs, or a custom NPC system?
- Should EarthOS be DeluxeMenus first or fully custom GUI first?
- Should report text input use chat-input, Anvil GUI, Sign GUI, or multiple options?
- Should Codex create direct commits, branches, or pull requests for AI report fixes?
- Which plugin ideas are private EarthLiving-only and which could become public/premium resources?
