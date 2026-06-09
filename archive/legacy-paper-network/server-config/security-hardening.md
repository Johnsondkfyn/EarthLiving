# Earth Living Security Hardening

Updated: 2026-05-18

## Applied on Earth Living Main

- `online-mode=true`
- `white-list=true`
- `enforce-whitelist=true`
- `enable-query=false`
- `enable-rcon=false`
- `rate-limit=15`
- `bukkit.yml` now has `query-plugins: false`
- Paper Anti-Xray is enabled in `config/paper-world-defaults.yml`
- Paper Anti-Xray uses `engine-mode: 2`
- Paper Anti-Xray has `lava-obscures: true`
- FarmControl 1.3.0 is installed from Modrinth.
- FarmControl is configured as a soft farm governor, not as a ClearLag-style cleaner.
- No FarmControl `kill` profile is enabled by default.
- Hopper checks are reduced from every tick to every 8 ticks.
- Entity collision pressure is reduced.
- Item/XP merge radius is increased slightly.

Backup before live config edit:

`/home/johna/earthliving-security-backups/20260518-182930`

## Current Protection Level

This is a strong private-development baseline:

- Only whitelisted Minecraft accounts can join.
- Mojang online authentication is required.
- RCON is disabled.
- Server query is disabled.
- Plugin names are no longer exposed through Bukkit query config.
- Paper packet limiter is still enabled.
- Paper Anti-Xray is ready after the next server restart.
- FarmControl is ready after the next server restart.
- Server firewall and `fail2ban` are active on the host.

## Farm Limiter Policy

Use FarmControl before considering ClearLag-style cleanup.

Enabled profiles:

- Soft animal farm nerf: remove random movement and collisions near dense animal farms.
- Animal breeding limit: stop breeding when too many same-type animals are close together.
- Villager breeder limit: stop breeding when villagers are too dense.
- Reactive animal/villager nerf: only kicks in when MSPT is high.

Disabled by default:

- Any profile that kills animals or villagers.
- Generic item clearing.
- Scheduled entity deletion.

## Next Security Layers

Before opening the server publicly:

1. Put Velocity in front as the only public Minecraft entry.
2. Restrict backend Paper servers so players cannot connect directly to them.
3. Add anti-bot/rate-limit protection at the proxy layer.
4. Add a real anti-cheat layer for movement/combat/automation checks.
5. Add staff alerts for suspicious mining, fast joins, failed logins, and repeated kicks.
6. Add scheduled backups before every plugin/config/security change.

## Xray Notes

Paper Anti-Xray is the first layer against ore xray. It does not replace staff review or economy/mining monitoring. When the server opens, mining logs should be watched for impossible diamond/ore patterns.
