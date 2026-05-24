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

Updated 2026-05-24:

- Discord integration foundation is now complete: chat/status/player count, account linking verification, staff alerts and shared in-game/Discord report flow.
- EarthLivingCore `0.6.3` is live on Main with permanent Report Center status actions through `reports-actions.queue`.
- EarthLivingCore `0.7.0` is live on Main with the first EarthWebBridge/Web Portal foundation.
- Panel Report Center can now set reports to `repair-approved`, `completed`, or back to `open` without editing `reports.yml` directly.
- EarthLivingCore foundation V1 is now considered complete; future work should be split into smaller EarthOS apps/modules.
- EarthLiving Web Portal + EarthWebBridge has started implementation: EarthOS profile link entry, one-time link code command, read-only server/profile/report exports and no Microsoft/Minecraft password handling.
- Current public roadmap estimate: 27 completed, 4 in progress, 66 planned, 28% overall.

Updated 2026-05-20:

- Domain `earthliving.earth` is live.
- HTTPS is enabled.
- Website is hosted through Nginx on the EarthLiving server.
- Website source lives under `docs/` in GitHub.
- Landing page now has EarthLiving logo branding, animated green/cyan title, fixed centered background logo, automatic mobile layout, desktop mobile-preview toggle, and English/Danish language toggle.
- The website is still considered in progress because content pages, screenshots, Discord CTA, map/status links, and future devlog/news pages are not finished yet.

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
