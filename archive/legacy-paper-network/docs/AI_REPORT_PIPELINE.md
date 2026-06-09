# EarthLiving AI Report Pipeline

Status: planned server integration, manual panel handoff live.
Updated: 2026-05-22.

## Goal

Reports from Minecraft and Discord should be easy for staff to triage, analyze, and hand off to Codex when code, config, or server fixes are needed.

## Current flow

- EarthLivingCore exports both in-game reports and Discord `!report` messages into the same report feed.
- The Panel Report Center shows the combined report feed.
- Each report has staff buttons for:
  - `Analyze with ChatGPT`: copies a clean analysis package for ChatGPT.
  - `Send to Codex`: copies a scoped handoff prompt for Codex.
  - `View package`: shows the generated package before copying.

This keeps the first version safe because no OpenAI key or personal ChatGPT login is stored in the panel.

## Recommended server-side flow

When an OpenAI API key is available, add a small server-side report analyzer:

1. Store `OPENAI_API_KEY` only on the server, outside the repository.
2. Add a panel action that sends a selected report to a backend endpoint.
3. Backend calls the OpenAI Responses API and stores the analysis result beside the report export.
4. Staff reviews the analysis in Report Center.
5. If a fix is needed, staff sends a scoped Codex handoff.

## Safety rules

- Do not put API keys in GitHub, panel JavaScript, Minecraft config, or Discord messages.
- Do not let AI deploy or restart production automatically.
- Codex should work in a repo/worktree context first, then a human reviews and deploys.
- Reports may include player names and coordinates, so prompts should include only what staff needs to solve the issue.
- Staff alerts can summarize reports, but detailed logs and secrets stay server-side.

## Future additions

- `Analyze` button that stores AI analysis directly in Report Center.
- `Create Codex task` button for a reviewed handoff.
- Staff alert when a report is high severity or repeated.
- Filters for source, status, category, reporter, and severity.
