# EarthWebBridge

Planned bridge between the EarthLiving Minecraft server and the public/player website.

Status: V1 scope defined, started for the 2026-05-22 Web Portal track and refined on 2026-05-24.

## Goal

EarthWebBridge should expose safe, minimal server data to the EarthLiving website without leaking secrets or letting the website edit live Minecraft files directly.

## V1 Scope

- One-time website account link codes, entered in-game through EarthOS.
- Link a website profile id to a Minecraft UUID.
- Read-only public server status export.
- Read-only linked player profile export.
- Read-only report status export for linked players.
- Private API token stored only in live server config if an HTTP endpoint is added later.
- No write actions from the website into live Minecraft data in V1.

## First Implementation Recommendation

Start with file-based JSON exports beside the existing EarthLivingCore report export, then add an authenticated HTTP service only when the website portal backend exists.

Suggested first files:

- `plugins/EarthWebBridge/server-status.json`
- `plugins/EarthWebBridge/linked-profiles.json`
- `plugins/EarthWebBridge/player-report-summaries.json`

Detailed docs:

- `docs/webportal/LINK_CODE_FLOW.md`
- `docs/webportal/V1_EXPORT_SCHEMA.md`

## Security Rules

- Do not ask for Minecraft/Microsoft passwords.
- Do not store Microsoft credentials.
- Do not commit API keys, tokens, passwords, recovery codes or private player data.
- Prefer read-only exports before any write API.
- Add rate limits before exposing an authenticated endpoint.

## Integration Points

- `plugins/earthlivingcore`
- EarthOS Profile app
- Report Center export
- Website route for "My EarthLiving"

## Not Yet Started

- Website login/session backend.
- In-game EarthOS Profile app UI for entering link codes.
- Public "My EarthLiving" route.
- Token-protected API endpoint.
