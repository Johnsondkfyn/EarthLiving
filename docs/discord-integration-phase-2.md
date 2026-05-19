# Discord Integration Phase 2

Updated: 2026-05-19

## Goal

Connect Earth Living's Minecraft server and Discord in a controlled way without opening unsafe remote console access or leaking secrets.

## Completed Today

- Added `server-status` for online/offline, restart and maintenance updates.
- Added `minecraft-chat` for future Minecraft and Discord chat bridge.
- Added `staff-alerts` for private staff warnings and future automation alerts.
- Posted short purpose messages in the new channels.
- Made `staff-alerts` private for the owner while the bot setup is being prepared.
- Captured the real Discord channel IDs for configuration.

## Discord Channel IDs

| Purpose | Channel | Discord channel ID |
| --- | --- | --- |
| Minecraft chat bridge | `minecraft-chat` | `1506268822813016134` |
| Server status | `server-status` | `1506268769314541639` |
| Staff alerts | `staff-alerts` | `1506268923895484477` |
| Bug reports | `bug-reports` | `1505977375907643502` |

## Recommended Plugin

Use DiscordSRV for phase 2.

Reason:

- It supports Paper/Purpur/Spigot-style servers and Modrinth lists compatibility with Minecraft `26.1.x`.
- It handles Minecraft <-> Discord chat bridging.
- It can send status/start/stop/join/leave/death messages to dedicated Discord channels.
- It supports account linking, role synchronization and staff/admin use cases later.
- It can forward console access, but Earth Living should keep remote console disabled at first for security.

Sources checked:

- DiscordSRV Modrinth page: https://modrinth.com/plugin/discordsrv
- DiscordSRV docs: https://docs.discordsrv.com/
- DiscordSRV config reference: https://docs.discordsrv.com/config/

## Safe Rollout

### Step 1: Prepare Discord bot

- Create an Earth Living Discord application and bot in the Discord Developer Portal.
- Enable only the intents needed for DiscordSRV.
- Keep the bot token private and store it only on the server.
- Do not commit the real token to GitHub, Notion or chat.

### Step 2: Install on test server first

- Install DiscordSRV on the test server first.
- Start with Minecraft-to-Discord chat enabled.
- Keep Discord-to-Minecraft chat disabled until spam and moderation behavior is confirmed.
- Keep remote console disabled.

### Step 3: Connect production

- After the test server works, copy the cleaned config to Earth Living Main.
- Turn on two-way chat only after staff confirms message formatting and moderation flow.
- Add status messages to `server-status`.
- Add staff alerts to `staff-alerts`.

### Step 4: Later features

- Whitelist/verification via linked Discord accounts.
- Role sync between LuckPerms and Discord roles.
- Player count bot presence.
- Report and staff alert automation.

## Security Rules

- Never commit `BotToken`.
- Never enable Discord remote console until staff permissions are fully locked down.
- Never put console output into public channels.
- Staff alerts should stay private.
- Test with a normal non-owner Discord account before public launch.
