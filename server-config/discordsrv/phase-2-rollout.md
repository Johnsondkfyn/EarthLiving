# DiscordSRV Phase 2 Rollout

## Server-Side Checklist

1. Download the current DiscordSRV release from Modrinth or the official DiscordSRV release link.
2. Put the jar into the test server `plugins/` folder.
3. Start the test server once so DiscordSRV generates its config folder.
4. Stop the server.
5. Copy `config.template.yml` values into `plugins/DiscordSRV/config.yml`.
6. Replace `PUT_REAL_BOT_TOKEN_ON_SERVER_ONLY` with the real Discord bot token on the server only.
7. Replace `PUT_DISCORD_INVITE_HERE` with the real invite link.
8. Start the test server.
9. Confirm the bot comes online.
10. Test Minecraft-to-Discord chat in `minecraft-chat`.
11. Confirm status messages appear in `server-status`.
12. Keep Discord-to-Minecraft disabled until spam/moderation behavior is approved.

## Production Rules

- Use test server first.
- Keep console disabled.
- Keep real token off GitHub.
- Take a backup before copying config to Earth Living Main.
- After production install, document the exact jar version and test result in `PLAN.md`.
