# EarthLivingCore

EarthLivingCore is the foundation plugin for Earth Living.

Version: `0.7.1`

Status: V1 foundation live on Earth Living Main.

## Purpose

EarthLivingCore is the backend/hub layer behind:

- EarthOS
- server events
- reports
- passports/countries
- notifications
- Discord integration
- future transport and economy modules

## Current V1 Scope

- Clean Paper plugin bootstrap.
- `/earthliving` and `/earthos` commands.
- Module registry/status.
- Notification service.
- EarthOS hotbar item on join.
- EarthOS inventory menu with first click actions.
- BlueMap link, server status, events, passport, wallet, reports and settings placeholders.
- Reports app with category menu, chat notes, and report storage in `reports.yml`.
- Reports hub with Create Report, My Reports, and Admin Reports views.
- Read-only panel export in `reports-panel.json`.
- Optional Discord webhook notification when a new report is created.
- DiscordSRV bridge for event announcements and restart countdown messages.
- Discord `!report` import into the same report store as in-game EarthOS reports.
- Permanent staff workflow actions through `reports-actions.queue`.
- Report status transitions: `open`, `repair-approved`, `completed`.
- Staff command: `/earthliving reports set <id> <open|repair-approved|completed> [note]`.
- EarthWebBridge V1 foundation:
  - EarthOS `My EarthLiving` profile link entry.
  - One-time website profile link codes.
  - Read-only JSON exports for server status, linked player profiles and player report summaries.
  - Read-only player activity stats: playtime, blocks broken/placed, deaths, mob kills and walked distance.
- Future builder brain direction:
  - ArchitectModule generates `.schem` files.
  - EarthLivingCore owns approval, build queue state, permissions and EarthOS screens.
  - Citizens, Constructor and Denizen can perform the visible slow builder-NPC workflow where stable.

## Build

```bash
gradle build
```

The output jar is expected under:

```text
build/libs/EarthLivingCore-0.7.1.jar
```

## Notes

- Built against Paper API `1.21.11-R0.1-SNAPSHOT`.
- Java source target is 21 for broad Paper plugin compatibility while the server can still run on Java 25.
- The real custom item texture belongs in a future resource pack using the EarthOS hotbar icon asset.
- Future feature work should be tracked as smaller EarthOS apps/modules instead of keeping the broad foundation task open forever.
