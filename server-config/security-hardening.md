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
- Server firewall and `fail2ban` are active on the host.

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
