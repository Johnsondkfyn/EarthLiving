# EarthWebBridge

Planned bridge between the EarthLiving Minecraft server and the public/player website.

Status: planning scaffold, started for the 2026-05-22 Web Portal track.

## Goal

EarthWebBridge should expose safe, minimal server data to the EarthLiving website without leaking secrets or letting the website edit live Minecraft files directly.

## V1 Scope

- One-time website account link codes.
- Link a website profile to a Minecraft UUID through EarthOS.
- Read-only player profile export.
- Read-only report status export for linked players.
- Server status export.
- Private API token stored only in live server config.

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

