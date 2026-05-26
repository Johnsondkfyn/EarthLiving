# Velocity Hub Setup

Status: V1 proxy + flat hub test online, 2026-05-26.

## Current Velocity Build

Velocity was updated on 2026-05-26 after client login showed:

```text
Incompatible client! Please use 1.7.2-1.21.11
```

The old proxy jar was `Velocity 3.4.0-SNAPSHOT`, which only advertised protocol `771`. The proxy now runs:

```text
Velocity 3.5.0-SNAPSHOT build 599
Status ping: Velocity 1.7.2-26.1.2
Protocol: 775
```

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

## Hub World Setup

Updated 2026-05-26:

- The old normal hub world was backed up before reset:
  - `.earthliving-backups/hub-world-before-flat-20260526-140103.tar.gz`
- Hub was regenerated as a flat world:
  - `level-type=minecraft\:flat`
  - grass surface, dirt base and bedrock floor
- Hub defaults are set for building:
  - `gamemode=creative`
  - `difficulty=peaceful`
  - `pvp=false`
  - `spawn-monsters=false`
  - `spawn-animals=false`
  - `allow-flight=true`
- Spawn is set to `0 80 0`.
- A `500` block worldborder is centered at `0 0`.
- The spawn chunks around `0 0` are force-loaded for a stable first login area.
- `TheKing189` is operator level `4` on the hub.
- RCON was used only temporarily for setup and was disabled again afterwards.

## Verification

2026-05-26 checks:

- `159.195.149.253:25567` responds to Minecraft status ping as Velocity with `EarthLiving Network`.
- `159.195.149.253:25567` advertises protocol `775` and version range `Velocity 1.7.2-26.1.2`.
- `159.195.149.253:25566` times out to external Minecraft status ping, which is expected because direct backend access is blocked.
- `159.195.149.253:25565` still responds as the existing main Paper server.
- Hub server starts with Paper Velocity forwarding enabled.
- Hub server starts as a flat creative/peaceful build world for the first hub design pass.

## Player Test

For now, connect to:

```text
159.195.149.253:25567
```

That should send the player to the hub backend.

## Not Done Yet

- Main is not behind Velocity yet.
- `earthliving.earth:25565` still points directly to Main.
- Hub spawn/build design is not done, but the flat build world is ready.
- Main needs a planned maintenance window before moving `25565` to Velocity.

## Recommended Next Step

Build and test the hub on `25567` first. When hub login is confirmed in Minecraft, schedule a short maintenance window to move public `25565` from Main to Velocity and add Main as a backend server.
