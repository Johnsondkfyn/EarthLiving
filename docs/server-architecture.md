# Server Architecture

## Target State

Earth Living is now planned as a single persistent Fabric-based Earth world.

```text
Earth Living Fabric Test Server
├─ Minecraft 1.20.1
├─ Fabric Loader
├─ Java 21
├─ Paid Earth map copy
└─ Fabric modpack foundation

Later
└─ Earth Living Production Server
```

## Administration

Pterodactyl remains the technical backend.

```text
panel.earthliving.earth
```

Pterodactyl should be restored to a clean/default role: server lifecycle, console, file access, allocations, backups and startup configuration.

## What Changes In Phase 1

- Active documentation now describes the Fabric single-world direction.
- Old server/plugin/panel material is archived under `archive/legacy-paper-network/`.
- Website content now points to Earth Living as a public project home at `earthliving.earth`.
- No live server migration is performed in Phase 1.

## What Does Not Change Yet

- The live paid Earth map installation remains untouched.
- Old infrastructure remains archived until the Fabric migration is verified.
- Production launch is not planned until the test server passes integrity and performance checks.
