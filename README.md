# Earth Living

## A single persistent Fabric-based Earth world

Earth Living is a long-term Minecraft project focused on a realistic Earth world where transport, cities, businesses, jobs, tourism, events and a dynamic economy grow into a living society simulation.

The active project direction is:

```text
A single persistent Fabric-based Earth world.
```

The current restructure prepares one Fabric test server first, then a later production server after the Earth map migration has been verified.

## Project Vision

Earth Living should feel like a society being built on top of a Minecraft Earth map:

- realistic Earth world
- transport systems
- cities and infrastructure
- businesses and jobs
- dynamic economy
- tourism and landmarks
- community events
- future custom phone system
- future web dashboard and integrations

## Current Development Focus

1. Website v0.1
2. Fabric Test Server
3. Earth Map Migration
4. Modpack Prototype
5. Closed Beta Preparation

## Active Architecture

```text
Earth Living
└─ Fabric Test Server
   ├─ Minecraft 1.20.1
   ├─ Fabric Loader
   ├─ Java 21
   ├─ Paid Earth map copy
   └─ Fabric modpack foundation

Later
└─ Earth Living Production Server
```

Pterodactyl remains the technical backend for server administration.

Administration domain:

```text
panel.earthliving.earth
```

## Website

The website at `earthliving.earth` is the public project home.

Active website pages:

- Home
- Vision
- Roadmap
- Devlog
- Newsletter
- Contact

The current website is static and uses placeholder form behavior until a backend is added.

## Migration Safety

The paid Earth map is currently treated as a protected production asset. The live Paper installation must not be modified during planning.

Migration rules:

- create a full backup before migration work
- create a snapshot before world copy or server conversion
- build a separate Fabric test server
- copy world data into the test server only
- verify world integrity before production decisions
- keep old servers archived until migration is proven
- do not permanently delete old infrastructure without explicit confirmation

## Repository Structure

```text
archive/legacy-paper-network/  archived old direction and server files
docs/                          public website and active project docs
migration/                     Earth map migration planning
modpack/fabric-1.20.1/         Fabric modpack foundation
server/fabric-test/            Fabric test server notes
server/production-future/      future production notes
```

## Key Documents

- `docs/server-architecture.md`
- `docs/fabric-migration-plan.md`
- `docs/fabric-modpack.md`
- `docs/migration-checklist.md`
- `docs/archive-index.md`

## Do Not Store Here

This repository must not contain:

- live world files
- full backups
- server logs with private IPs or player data
- passwords
- SSH keys
- private credentials
- private player/admin data
