# Velocity Hub Setup

Status: V1 proxy test online, 2026-05-26.

## Current Live Layout

| Address | Current target |
|---|---|
| `159.195.149.253:25565` | EarthLiving Main, direct Paper server |
| `159.195.149.253:25567` | Velocity proxy test |
| `159.195.149.253:25566` | Hub backend, blocked for public direct Minecraft ping/login |

## What Was Configured

- Existing Velocity server volume: `89919e61-bbba-44a7-8030-7b0439954c6f`.
- Existing test server volume was converted into the first hub backend: `d554d2b4-ac4b-4b48-b004-35f1b73feadc`.
- Velocity listens on `0.0.0.0:25567`.
- Velocity backend:
  - `hub = "172.18.0.3:25566"`
  - `try = ["hub"]`
- Velocity uses modern player info forwarding.
- Hub Paper config has Velocity forwarding enabled with the same forwarding secret.
- Hub `server.properties` is set to `online-mode=false` as required behind Velocity.
- A persistent systemd firewall unit applies `DOCKER-USER` rules to block public direct access to the hub backend port.

## Verification

2026-05-26 checks:

- `159.195.149.253:25567` responds to Minecraft status ping as Velocity with `EarthLiving Network`.
- `159.195.149.253:25566` times out to external Minecraft status ping, which is expected because direct backend access is blocked.
- `159.195.149.253:25565` still responds as the existing main Paper server.
- Hub server starts with Paper Velocity forwarding enabled.

## Player Test

For now, connect to:

```text
159.195.149.253:25567
```

That should send the player to the hub backend.

## Not Done Yet

- Main is not behind Velocity yet.
- `earthliving.earth:25565` still points directly to Main.
- Hub spawn/build design is not done.
- Main needs a planned maintenance window before moving `25565` to Velocity.

## Recommended Next Step

Build and test the hub on `25567` first. When hub login is confirmed in Minecraft, schedule a short maintenance window to move public `25565` from Main to Velocity and add Main as a backend server.
