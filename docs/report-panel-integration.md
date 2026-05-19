# Report Panel Integration Plan

EarthOS reports should also appear in the Pterodactyl panel.

## Current state

EarthLivingCore saves reports in the server plugin data folder as:

```text
plugins/EarthLivingCore/reports.yml
```

Each report includes:

- id
- status
- category
- player name
- player UUID
- world
- coordinates
- created timestamp
- note

## Recommended panel approach

Use a Blueprint panel page called `Report Center`.

The panel should read a safe exported report file from the server volume, then show:

- Open report count
- Latest reports
- Category
- Player
- Location
- Note
- Status
- Quick actions later: set status, assign, archive, send to Codex

## Safe implementation path

1. Keep Minecraft as the source of truth.
2. Add an export file from EarthLivingCore, for example `reports-panel.yml` or `reports-panel.json`.
3. Let the Blueprint extension read only that exported file.
4. Do not expose secrets, server credentials, private logs, or API tokens to the panel page.
5. Add write actions later through a controlled command/API instead of letting the panel edit raw plugin files directly.

## Why not edit `reports.yml` directly from the panel?

Direct editing can corrupt the file while the server is running. A read-only export is safer first. Later, panel actions should call a controlled endpoint or command queue that EarthLivingCore processes.
