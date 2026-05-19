# DiscordSRV Phase 2 Rollout

## Server-Side Checklist

1. [x] Download the current DiscordSRV release from Modrinth or the official DiscordSRV release link.
2. [x] Put the jar into the test server `plugins/` folder.
3. [x] Start the test server once so DiscordSRV generates its config folder.
4. [x] Stop/restart the server as needed.
5. [x] Copy `config.template.yml` values into `plugins/DiscordSRV/config.yml`.
6. [x] Replace `PUT_REAL_BOT_TOKEN_ON_SERVER_ONLY` with the real Discord bot token on the server only.
7. [ ] Replace `PUT_DISCORD_INVITE_HERE` with the real invite link.
8. [x] Start the test server.
9. [x] Confirm the bot comes online.
10. [ ] Test Minecraft-to-Discord chat in `minecraft-chat` with a real player chat message.
11. [x] Confirm status messages appear in `server-status`.
12. [x] Keep Discord-to-Minecraft disabled until spam/moderation behavior is approved.
13. [x] Connect random event announcements to `server-events` through EarthLivingCore.
14. [x] Deploy tested DiscordSRV config to Earth Living Main.
15. [x] Remove DiscordSRV from Earth Living Test after production deployment.

## Test Server Result - 2026-05-19

- Installed DiscordSRV `1.30.5` on Earth Living Test.
- Bot app used: `Minecraft Monitor`.
- Bot was invited to the Earth Living Discord server with limited chat/status permissions.
- Required Discord gateway intents were enabled: Presence, Server Members, and Message Content.
- `server-status` received the DiscordSRV startup message: `Server has started`.
- `minecraft-chat` is mapped and ready for Minecraft-to-Discord player chat testing.
- Discord-to-Minecraft remains disabled in config for safety.
- Discord console forwarding remains disabled.
- Bot token is stored only in the server-side DiscordSRV config and must not be committed.

## Production Result - 2026-05-19

- Deployed DiscordSRV `1.30.5` to Earth Living Main.
- Deployed EarthLivingCore `0.6.0` to Earth Living Main.
- Confirmed Main starts cleanly on `159.195.149.253:25565`.
- Confirmed DiscordSRV connects on Main: JDA login successful, WebSocket connected and finished loading.
- Confirmed player count presence/status is handled by DiscordSRV.
- Confirmed BlueMap still works through nginx at `http://bluemap.159.195.149.253.nip.io/`.
- Removed the incorrect Main Pterodactyl allocation for public port `8100`; nginx owns that public port and proxies to BlueMap inside the container.
- Earth Living Test has DiscordSRV jar/config removed to prevent duplicate bot connections.
- Discord-to-Minecraft remains disabled.
- Discord console forwarding remains disabled.

## Production Rules

- Use test server first.
- Keep console disabled.
- Keep real token off GitHub.
- Take a backup before copying config to Earth Living Main.
- After production install, document the exact jar version and test result in `PLAN.md`.
