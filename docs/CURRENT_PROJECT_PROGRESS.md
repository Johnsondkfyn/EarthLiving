# EarthLiving Current Project Progress

## Overall progress

Estimated overall progress: **28%**.

This is an early project estimate, not a mathematically exact completion percentage. EarthLiving is a large long-term project with many planned systems, so the percentage should be updated as features move from planning into development and testing.

## Current status summary

| Status | Count | Meaning |
|---|---:|---|
| Completed | 27 | Notion roadmap items marked `Færdig` |
| In Progress | 4 | Notion roadmap items marked `I gang` |
| Planned | 66 | Notion roadmap items marked `Ikke startet` |

## Current phase

Current phase:

```text
Core foundation is live, the website is being refined, and the first player portal scope is being planned.
```

## Completed

- GitHub repository structure exists.
- Public website is live on `https://earthliving.earth` with HTTPS.
- Website domain and DNS foundation are configured.
- Core planning documents exist for future systems and server direction.
- EarthLivingCore foundation V1 is live on Main as `0.6.3`.

## In progress

- EarthOS concept and GUI-first direction.
- Basic report flow planning/implementation direction.
- Website content, bilingual copy, branding polish and public presentation.
- EarthLiving Web Portal + EarthWebBridge V1 scope.

## Planned systems

Planned systems include, but are not limited to:

- EarthPulse world consequence engine
- EarthMiningSystem
- EarthNightlifeSystem
- EarthBackpackSystem
- EarthLanguageSystem
- TransportModule
- EarthPassportSystem
- DynamicMediaNetwork
- CompanyCorporationSystem
- PublicServicesSystem
- DynamicTrafficSystem
- EarthLivingLogisticsSystem
- DigitalTwinSystem
- AICityPlanner
- UpdateManagerModule
- AI Report Pipeline
- Pterodactyl Blueprint dashboard integration

## Website status

Updated 2026-05-26:

- Velocity proxy test is online on `159.195.149.253:25567`, currently routing to the former test server as `hub`. Velocity was updated to `3.5.0-SNAPSHOT` build `599` so it supports `26.1.2`/protocol `775`. Main server remains direct on `25565` until the final proxy port swap is intentionally performed.
- The hub backend now runs the uploaded BreadBuilds "Modern Age lobby" world as the first visual hub test. The previous flat hub world is backed up, Paper completed the 1.18+ world migration successfully, Velocity status ping on `159.195.149.253:25567` still works, and player login confirmed the hub loads correctly in-game.
- Hub build plugins are now installed and verified: EarthLivingCore `0.7.2`, LuckPerms, WorldEdit and WorldGuard. WorldEdit is available for cleanup/build work; WorldGuard is ready for later spawn and portal protection.
- Citizens `2.0.42-SNAPSHOT build 4160` and Constructor `3.5` have been added to the hub test server. Citizens loaded cleanly. Constructor also loaded cleanly with Citizens and WorldEdit, but remains experimental because it is an older `api-version: 1.13` plugin.
- Constructor update check found no newer confirmed release than `3.5`; keep it as a hub-only experimental builder-NPC test plugin until an in-game schematic build test passes.
- EarthLivingCore `0.7.8` is deployed on the hub test server with a clearer placement preview command: `/el preview <width> <height> <depth> [yOffset] [seconds]` plus free-roam follow placement via `/el preview look <width> <height> <depth> [yOffset] [seconds] [distance]`. It draws a dense temporary particle box with floor grid, stronger edges, corner markers and an origin marker so schematics can be height/footprint checked before Constructor or WorldEdit placement. The `look` mode follows the block the player is currently aiming at until the player left-clicks to lock the current placement, the timer expires, or `/el preview clear` is used. `/el preview info` shows the locked bounds and `/el preview we` applies those bounds as the current WorldEdit selection.
- Modern Age lobby spawn was set in-game to `405 44 -384` for the current hub test layout.
- EarthPassportSystem V1 is implemented in EarthLivingCore `0.7.2`: EarthOS Passport profile view, citizenship/home country, visas, country reputation, staff commands and read-only passport JSON export.
- Main Discord verification testing found that DiscordSRV sees `minecraft-chat` code messages, but the linking flow does not consume them yet; a temporary owner/dev bypass for `TheKing189` is active so live Passport and hub testing can continue.
- The website now has the first public-safe "My EarthLiving" profile view: not-linked profile state, read-only stat cards, live EarthWebBridge status display and a documented Discord verification/whitelist plan. Main server JSON exports were verified as active and valid, with `0` linked profiles until a player completes the in-game link flow.
- Discord integration foundation is now complete: chat/status/player count, account linking verification, staff alerts and shared in-game/Discord report flow.
- EarthLivingCore `0.6.3` is live on Main with permanent Report Center status actions through `reports-actions.queue`.
- EarthLivingCore `0.7.0` is live on Main with the first EarthWebBridge/Web Portal foundation.
- Panel Report Center can now set reports to `repair-approved`, `completed`, or back to `open` without editing `reports.yml` directly.
- EarthLivingCore foundation V1 is now considered complete; future work should be split into smaller EarthOS apps/modules.
- EarthLiving Web Portal + EarthWebBridge has started implementation: EarthOS profile link entry, one-time link code command, read-only server/profile/report exports and no Microsoft/Minecraft password handling.
- Website next actions from the domain plan are now partly implemented: Discord onboarding placeholder, clear BlueMap link, server status section, visual progress area, devlog/news foundation and a technical hosting note.
- Current public roadmap estimate: 27 completed, 4 in progress, 66 planned, 28% overall.

Updated 2026-05-20:

- Domain `earthliving.earth` is live.
- HTTPS is enabled.
- Website is hosted through Nginx on the EarthLiving server.
- Website source lives under `docs/` in GitHub.
- Landing page now has EarthLiving logo branding, animated green/cyan title, fixed centered background logo, automatic mobile layout, desktop mobile-preview toggle, and English/Danish language toggle.
- The website is still considered in progress because final Discord invite flow, permanent `map.`/`status.` subdomains, approved real screenshots/video, and deeper feature pages are not finished yet.

## Website progress display

The public website should display:

```text
Overall Progress: 28%
Completed: 27
In Progress: 4
Planned: 66
```

Recommended note:

```text
Current phase: Core foundation is live, the website is being refined, and the first player portal scope is being planned.
Progress follows the current Notion roadmap counts and should be refreshed when Notion statuses change.
```

## Update rule

Update this document when:

- a major system moves from planned to in progress
- a major system reaches a usable/testable version
- the website/domain status changes
- EarthOS gains working apps
- public testing begins
- GitHub issues or milestones become the main progress tracker

## Website data source

The website reads current roadmap numbers from:

```text
docs/data/roadmap-status.json
```

Update this JSON when Notion roadmap counts change. The static website will display those values automatically after deployment.
