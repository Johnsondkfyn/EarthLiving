# GitHub Upload Plan

## Recommended Repository

- Name: `EarthLiving`
- Visibility: private at first
- Default branch: `main`

## First Upload

Upload the contents of `EarthLiving-github`, not the full Codex workspace.

This keeps the repository clean and avoids uploading build output, plugin jars, downloads, world data, or secrets.

## After Upload

Recommended next steps:

1. Install GitHub Desktop or Git for Windows on the PC.
2. Clone the private `EarthLiving` repository locally.
3. Move or copy this clean folder into the cloned repo.
4. Commit with message: `Initial Earth Living source and docs`.
5. Push to GitHub.

## Later Structure

As the project grows, split work into:

- `plugins/` for Paper plugin source code
- `plugins/` for Paper plugins
- `server-config/` for safe example configs
- `docs/` for server plans and operating notes
- `scripts/` for safe setup/backup scripts
