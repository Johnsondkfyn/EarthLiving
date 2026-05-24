# EarthLiving Web Portal

## Priority
High priority after the core website and EarthOS foundation.

## Planned start
Planned development start: Friday 22 May.

## Current status
V1 server-side foundation implemented in EarthLivingCore `0.7.0` on 2026-05-24. The public website stays mostly static for now, while the portal foundation starts with safe one-time account linking and read-only exports.

## Goal
Create a web profile system where players can link their Minecraft account to an EarthLiving website profile and view their progress, status and server information online.

## Important security rule
Do not ask for or store Minecraft/Microsoft passwords. Players must never enter their Minecraft/Microsoft login details directly on the EarthLiving website.

Account linking should use a secure linking flow such as an in-game verification code first, with official Microsoft/Minecraft OAuth considered later if needed.

## Purpose
The website should become more than a static information page. It should become a player portal where users can follow their EarthLiving progress, reports, wallet, passport, companies, mining licenses, properties, transport tickets and other server systems.

This helps EarthLiving feel like a real online world/platform instead of only a Minecraft server.

## Recommended V1

- Website account system
- Minecraft UUID linking
- In-game verification code
- Basic player profile
- Player skin/avatar display
- Report status overview
- Server status overview
- Basic progress overview

## Recommended account linking flow

```text
Player creates/logs into website account
-> Website generates a one-time link code
-> Player opens EarthOS in Minecraft
-> EarthOS -> Profile -> Link Website Account
-> Player enters the link code
-> Website profile is linked to the player's Minecraft UUID
```

## Later features

- Wallet/economy overview
- Passport/citizenship status
- Company ownership and jobs
- Mining licenses
- Backpack/carry weight overview
- Transport tickets and travel history
- Property ownership
- Notifications
- Personal news feed
- Admin dashboard
- Microsoft/Minecraft OAuth later if needed

## Example player dashboard

```text
EarthLiving Profile

Player: John
Country: Denmark
Wallet: 12,450 coins
Company: Nordic Rail
Passport: Active
Current Region: Copenhagen
Open Reports: 1
Backpack: Mining Pack MK1
Carry Weight: 63 / 80 kg
```

## Integration targets

- EarthOS
- EarthOS Profile app
- ReportModule
- Economy system
- EarthPassportSystem
- EarthMiningSystem
- EarthBackpackSystem
- TransportModule
- CompanyCorporationSystem
- Website/domain system
- Pterodactyl/Blueprint dashboard later
- EarthWebBridge plugin

## Recommended development order

1. Design account linking flow.
2. Add safe web account/profile concept.
3. Add Minecraft UUID link code flow through EarthOS.
4. Create EarthWebBridge plugin/API layer.
5. Display basic player profile and report status.
6. Add economy/passport/company/mining/backpack/transport data later.
7. Consider Microsoft/Minecraft OAuth later only if it is needed and implemented securely.

## Start scope - 2026-05-22

The first build should stay deliberately small and safe.

### V1 deliverables

- `EarthWebBridge` module/plugin scaffold.
- Private config with API token placeholder only, never a real token in GitHub.
- One-time link code model documented in `docs/webportal/LINK_CODE_FLOW.md`.
- EarthOS Profile entry point design.
- Read-only player profile export.
- Read-only report status export for linked players.
- Public server status export.
- Website-side placeholder route/page for "My EarthLiving".
- Export schema documented in `docs/webportal/V1_EXPORT_SCHEMA.md`.

### Implemented in EarthLivingCore 0.7.0

- EarthOS `My EarthLiving` menu entry.
- Chat-based one-time code input from EarthOS.
- Admin command `/earthliving portal code <website-profile-id>`.
- Admin command `/earthliving portal export`.
- `web-portal.yml` storage for link codes and linked profiles.
- `web-exports/server-status.json`.
- `web-exports/player-profiles.json`.
- `web-exports/player-report-summaries.json`.

### V1 non-goals

- No Microsoft/Minecraft password handling.
- No public account registration until the security model is reviewed.
- No wallet/passport/company/private economy data until the profile link flow is tested.
- No write actions from the website into live Minecraft data.

### Open implementation decision

Choose the web stack before coding the portal backend:

- Static website + JSON exports first: safest and fastest, but limited login/profile support.
- Small server-side app later: needed for real accounts, sessions and private player dashboards.
- Existing Pterodactyl/Blueprint panel only: good for staff tools, not for public player login.

Recommended path: keep the current public site static, add file-based read-only exports first, then add a separate private web service for authenticated player profiles when the link-code flow is ready.

## EarthWebBridge

### Goal
Create the Minecraft plugin/API bridge between EarthLiving server systems and the EarthLiving website.

### Purpose
EarthWebBridge should securely connect Minecraft server systems with the EarthLiving website and player portal.

### Features

- Secure account linking between website profile and Minecraft UUID
- One-time link code support
- EarthOS profile linking menu
- Player profile API
- Report status API
- Server status API
- Future wallet/passport/company/mining/backpack/transport data API
- API token support stored only in private server config
- Rate limiting and permission checks

### Security rule
Never ask for or store Minecraft/Microsoft passwords. Never commit API keys, tokens or private credentials.

## Safety notes

Do not store:

- Minecraft/Microsoft passwords
- Microsoft account credentials
- session tokens in public files
- private player data in GitHub
- API keys
- recovery codes
- server secrets
