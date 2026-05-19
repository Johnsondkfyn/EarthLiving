# EarthLiving Chat Backup - 2026-05-19

Backup created: 2026-05-19 19:20 Europe/Copenhagen

This is a safe project backup of the working chat context. It does not include passwords, SSH keys, API tokens, recovery codes, or private credentials.

## High-level status

EarthLiving has moved from planning into a working test-server development loop.

Current main themes:

- Pterodactyl panel customization
- Blueprint Framework extension work
- EarthLivingCore plugin foundation
- EarthOS in-game menu
- Reports app
- Report Center inside the Pterodactyl panel
- Discord server foundation
- BlueMap and world/country region planning

## Servers

### Main Server

- Address: `159.195.149.253:25565`
- Pterodactyl server id: `0157164c-e4e1-4979-935c-d703ddd6706e`
- Paper status checked on 2026-05-19:
  - Current version from `version_history.json`: `26.1.2-64-2186e1e`
- Main server should be treated carefully. New plugin work should continue on the test server first.

### Test Server

- Address: `159.195.149.253:25566`
- Pterodactyl server id: `d554d2b4-ac4b-4b48-b004-35f1b73feadc`
- Purpose: safe plugin and panel testing before production
- Runtime:
  - Java 25
  - Paper 26.1.2
- Player `TheKing189` was set as OP for testing.
- `spawn-protection=0` was set on the test server.

## EarthLivingCore

Plugin path:

```text
plugins/earthlivingcore/
```

Current deployed test version:

```text
EarthLivingCore v0.4.0
```

Build output:

```text
plugins/earthlivingcore/build/libs/EarthLivingCore-0.4.0.jar
```

Build command pattern:

```powershell
$jdk='C:\Users\Johna\.gradle\caches\minecraftforge\forgegradle\mavenizer\caches\microsoft-jdk-25.0.3-windows-x64'
$env:JAVA_HOME=$jdk
$env:PATH="$jdk\bin;$env:PATH"
..\giveaway\gradlew.bat build
```

Current plugin systems:

- `/earthliving status`
- `/earthliving modules`
- `/earthliving reload`
- `/earthliving reports`
- `/earthos`
- EarthOS hotbar item
- EarthOS main inventory menu
- Report Center in EarthOS
- Report export for panel

## EarthOS

EarthOS is the in-game command-center menu for EarthLiving.

Current apps/buttons:

- World Map
- Server Events
- Passport
- Wallet
- Reports
- Server Status
- Settings

Working behavior:

- World Map sends a clickable BlueMap link.
- Server Events shows current placeholder/event text.
- Passport explains future country/passport integration.
- Wallet explains future economy integration.
- Server Status shows runtime/status text.
- Settings refreshes the EarthOS hotbar device.
- Reports opens a real Report Center.

## Reports App

Reports started as the first real EarthOS app.

Current flow:

1. Player opens EarthOS.
2. Player clicks Reports.
3. Player sees Report Center.
4. Player can choose Create Report.
5. Player chooses a category.
6. Player writes a note in chat.
7. The chat message is captured privately and not sent publicly.
8. The report is saved with metadata.
9. Player can type `cancel` to cancel report creation.

Report categories:

- Bug Report
- Player Issue
- Region / Border
- Transport
- Build / World
- Suggestion

Saved report data:

- id
- status
- category
- category title
- player name
- player UUID
- world
- x/y/z coordinates
- created timestamp
- note

Report files:

```text
plugins/EarthLivingCore/reports.yml
plugins/EarthLivingCore/reports-panel.json
```

Important design rule:

- `reports.yml` is the plugin source of truth.
- `reports-panel.json` is read-only export for the panel.
- The panel should not directly edit `reports.yml`.

## Pterodactyl Panel

Panel URL:

```text
https://panel.159.195.149.253.nip.io
```

EarthLivingCore panel page:

```text
https://panel.159.195.149.253.nip.io/admin/extensions/earthlivingcore#earthliving-report-center
```

Blueprint extension path in repo:

```text
blueprint-extensions/earthlivingcore/
```

Current panel features:

- Carbon/dark EarthLiving style
- Owner shortcut rail
- Plugin Gate shortcut
- Marketplace view for themes, plugin installers, player managers and admin tools
- Read-only Report Center
- Report Center reads `reports-panel.json` from the test server first

Panel fixes completed:

- Fixed missing stylesheet loading on the EarthLivingCore extension page.
- Fixed Report Center layout overlap.
- Fixed long updated timestamp display.
- Forced marketplace sections into a stable single-column layout to avoid overlap.

## Discord

Discord server was rebuilt/organized for EarthLiving.

Known channels created:

- `server-status`
- `minecraft-chat`
- `staff-alerts`
- `server-events`
- `bug-reports`

Current Discord integration plan:

- Use DiscordSRV later for Minecraft chat/status integration.
- Add server-event announcements later.
- Reports can later notify Discord or create staff alerts.

## BlueMap / Regions

BlueMap completed rendering 100 percent.

BlueMap URL:

```text
http://159.195.149.253:8100/
```

Country region planning started:

- Denmark region points were manually provided.
- BlueMap markers/WorldGuard region work exists in repo.
- World/country foundation is considered more important before deep passport gameplay.

## EarthOS Visual Direction

ChatGPT provided menu concepts.

Chosen direction:

- Command Center / futuristic hologram style
- Dark carbon base
- Gold and cyan accents
- Globe/compass identity
- Inventory GUI first
- Resource pack/custom textures later

Documentation:

```text
docs/earthos-menu-direction.md
```

## Key Git Commits From This Session

```text
24d473e Fix Report Center panel layout
8a202b4 Fix EarthLivingCore panel stylesheet loading
095d715 Add report panel export and view
ff22698 Add report center views
02d40b0 Add report notes via chat
0be4f5d Add EarthOS reports app
260e21c Add EarthOS menu actions
f5838ff Start EarthLivingCore foundation plugin
c4d7980 Add EarthOS hotbar icon asset
27f6300 Add Discord server events channel plan
4aa1ef0 Start Discord integration phase 2
```

## Recommended Next Steps

1. Test creating a new report in EarthOS.
2. Refresh the Pterodactyl Report Center and confirm the report appears.
3. Add panel actions later:
   - mark reviewing
   - mark resolved
   - assign report
   - archive report
4. Add Discord notification for new reports.
5. Add a safer command queue/API for panel actions instead of direct file editing.
6. Continue EarthOS apps:
   - Passport
   - Server Events
   - Wallet/Economy
   - Map/Regions

## Security Notes

- Do not commit passwords, SSH keys, API keys, Discord bot tokens, database credentials, recovery codes, or private server secrets.
- Keep credentials in the private local/server credential files only.
- Keep report panel access read-only until action handling is implemented safely.
