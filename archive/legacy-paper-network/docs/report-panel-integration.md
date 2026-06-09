# Report Panel Integration Plan

EarthOS and Discord reports should appear in the Pterodactyl panel.

## Current state

EarthLivingCore saves reports in the server plugin data folder as:

```text
plugins/EarthLivingCore/reports.yml
```

EarthLivingCore also exports a read-only panel file:

```text
plugins/EarthLivingCore/reports-panel.json
```

Each report includes:

- id
- status
- source (`minecraft` or `discord`)
- category
- player name
- player UUID
- Discord user fields when created from Discord
- world
- coordinates
- created timestamp
- note

## Recommended panel approach

Use a Blueprint panel page called `Report Center`.

The panel should read a safe exported report file from the server volume, then show:

- Open report count
- Latest reports
- Source: in-game or Discord
- Category
- Player
- Location
- Note
- Status
- Quick actions: AI analysis package, Codex handoff, approve repair, copy player reply, and permanent status updates through EarthLivingCore

## Safe implementation path

1. Keep Minecraft as the source of truth.
2. Let Discord reports enter through EarthLivingCore, not by editing panel files.
3. Let EarthLivingCore export `reports-panel.json`.
4. Let the Blueprint extension read only that exported file.
5. Do not expose secrets, server credentials, private logs, or API tokens to the panel page.
6. Add write actions later through a controlled command/API instead of letting the panel edit raw plugin files directly.

## Current implementation

- In-game reports are created from EarthOS and stored as `source: minecraft`.
- Discord reports are imported from the `bug-reports` Discord channel with `!report`.
- Both flows write to the same `reports.yml` file.
- Both flows are exported to the same `reports-panel.json` file.
- The Blueprint Report Center reads the export and labels each card as `In-game` or `Discord`.
- Staff workflow buttons now help triage each report:
  - `ChatGPT-analyse` copies a clean analysis prompt.
  - `Send til Codex` copies a scoped implementation handoff.
  - `Godkend fix` queues a `repair-approved` status update for EarthLivingCore.
  - `Svar til spiller` copies a short player reply draft.
  - `Luk-pakke` copies the close checklist.
  - `Afsluttet` queues a `completed` status update for EarthLivingCore.
  - `Genåbn` queues an `open` status update for EarthLivingCore.
- EarthLivingCore `0.6.3` processes `reports-actions.queue` and remains the source of truth for report status.

## Why not edit `reports.yml` directly from the panel?

Direct editing can corrupt the file while the server is running. A read-only export is safer first. Later, panel actions should call a controlled endpoint or command queue that EarthLivingCore processes.

The permanent workflow uses a controlled queue file. The panel appends a requested action, and EarthLivingCore applies it to `reports.yml`, updates `reports-panel.json`, and keeps Minecraft as the source of truth.
