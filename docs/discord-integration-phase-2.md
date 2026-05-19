# Discord Integration Phase 2

Updated: 2026-05-19

## Goal

Connect Earth Living's Minecraft server and Discord in a controlled way without opening unsafe remote console access or leaking secrets.

## Completed Today

- Added `server-status` for online/offline, restart and maintenance updates.
- Added `minecraft-chat` for future Minecraft and Discord chat bridge.
- Added `server-events` for future automatic random event announcements.
- Added `staff-alerts` for private staff warnings and future automation alerts.
- Posted short purpose messages in the new channels.
- Made `staff-alerts` private for the owner while the bot setup is being prepared.
- Captured the real Discord channel IDs for configuration.
- Installed and connected DiscordSRV `1.30.5` on the Earth Living Test server.
- Invited the `Minecraft Monitor` bot to the Earth Living Discord server.
- Enabled the required bot intents for DiscordSRV.
- Confirmed the bot posts server startup status in `server-status`.
- Added EarthLivingCore `0.6.0` DiscordSRV bridge for random event announcements and restart countdown messages.
- Tested event and restart countdown Discord messages on Earth Living Test.
- Deployed DiscordSRV `1.30.5` and EarthLivingCore `0.6.0` to Earth Living Main after testing.
- Removed the DiscordSRV jar and config from Earth Living Test after production deployment to avoid duplicate bot sessions.
- Fixed the Earth Living Main Pterodactyl allocation issue: port `8100` is handled by nginx/BlueMap proxy, not by a Pterodactyl server allocation.

## Discord Channel IDs

| Purpose | Channel | Discord channel ID |
| --- | --- | --- |
| Minecraft chat bridge | `minecraft-chat` | `1506268822813016134` |
| Server status | `server-status` | `1506268769314541639` |
| Random server events | `server-events` | `1506271254661959761` |
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

- DiscordSRV was installed and tested on the test server.
- Minecraft-to-Discord chat is enabled and mapped to `minecraft-chat`.
- Discord-to-Minecraft chat is disabled until spam and moderation behavior is confirmed.
- Remote console is disabled.
- Remaining test: send a real player chat message from the test server and confirm it appears in `minecraft-chat`.

### Step 3: Connect production

- Completed: copied the cleaned DiscordSRV config and jar to Earth Living Main.
- Completed: copied EarthLivingCore `0.6.0` to Earth Living Main.
- Completed: confirmed Earth Living Main starts on `25565`.
- Completed: confirmed DiscordSRV logs in and connects to Discord WebSocket on Earth Living Main.
- Completed: confirmed BlueMap still serves through nginx after removing the incorrect `8100` Pterodactyl allocation from Main.
- Turn on two-way chat only after staff confirms message formatting and moderation flow.
- Status messages are mapped to `server-status`.
- Random events are mapped to `server-events`.
- Add staff alerts to `staff-alerts` in a later pass.

### Step 4: Later features

- Whitelist/verification via linked Discord accounts.
- Role sync between LuckPerms and Discord roles.
- Player count bot presence.
- Report and staff alert automation.
- Automatic random event announcements to `server-events`.

## Production Result - 2026-05-19

- Earth Living Main runs DiscordSRV `1.30.5` and EarthLivingCore `0.6.0`.
- DiscordSRV connected successfully: JDA login, WebSocket connection and finished loading were confirmed in the main server log.
- Bot presence uses player count status through DiscordSRV.
- Minecraft server port `159.195.149.253:25565` is reachable.
- BlueMap is served by nginx at `http://bluemap.159.195.149.253.nip.io/`; ordinary GET requests return `200`.
- Discord-to-Minecraft remains disabled for safety.
- Discord console forwarding remains disabled.

## Security Rules

- Never commit `BotToken`.
- Never enable Discord remote console until staff permissions are fully locked down.
- Never put console output into public channels.
- Staff alerts should stay private.
- Test with a normal non-owner Discord account before public launch.
