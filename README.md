# Earth Living

Earth Living is a realistic Minecraft Earth server project.

This repository is for source code, server notes, and safe configuration examples.
It must not contain world files, backups, server logs with IP addresses, passwords, SSH keys, or private server access details.

## Contents

- `mods/simple-ruby-mod` - Forge ruby mod made during early testing.
- `mods/totem-of-undying-recipe` - Updated Forge recipe mod.
- `plugins/giveaway` - Paper giveaway plugin updated for modern Paper.
- `plugins/earthlogger` - Custom Paper logging plugin made from scratch.
- `server-config/coordinator` - Coordinator plugin config notes for the Earth map.
- `docs` - Project notes and operational guidance.

## Current Server Direction

The server is planned around:

- realistic Earth map gameplay
- whitelist-first testing
- LuckPerms based permissions
- no permanent OP workflow
- CoreProtect/EarthLogger style auditing
- BlueMap for land planning
- GUI-first player interactions
- realistic economy, transport, health, climate, and citizenship systems

## GitHub Safety Rules

Do commit:

- plugin and mod source code
- Gradle build files
- documentation
- safe example configs

Do not commit:

- Minecraft world/map folders
- generated backups
- downloaded plugin jars
- server logs with IP addresses
- SSH keys or passwords
- live `server.properties`
- private player/admin data

## Build Notes

Each mod/plugin folder is currently a separate Gradle project.
Build from inside the relevant folder.

Examples:

```powershell
cd plugins/earthlogger
gradle build
```

or, where a wrapper exists:

```powershell
cd mods/simple-ruby-mod
./gradlew build
```
