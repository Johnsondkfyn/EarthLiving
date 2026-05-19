# Server Events Plan

Updated: 2026-05-19

## Goal

Earth Living should have automatic event announcements in Discord when a random server event starts.

Discord channel:

- `server-events`
- Channel ID: `1506271254661959761`

## Recommended Event Types

- Random fishing competition.
- Exploration event in a selected country or region.
- Transport discount or route opening.
- City build challenge.
- Weather or world incident.
- Border/checkpoint test event.
- Economy bonus hour.

## Announcement Format

Each automatic event message should include:

- Event name.
- Event type.
- Start time.
- Duration.
- Location or region.
- Reward if any.
- How players join.

Example:

```text
Earth Living Event Started: Denmark Coast Exploration
Type: Exploration
Location: Denmark
Duration: 30 minutes
Objective: Visit the marked coast area and take a screenshot.
Reward: Test currency and event progress.
```

## Implementation Options

### Option A: DiscordSRV Alert Hook

Use DiscordSRV alerts if the event plugin fires console commands or Bukkit events that DiscordSRV can listen for.

Best when:

- We use an existing event plugin.
- We only need announcement messages.
- We do not need complex Discord interaction.

### Option B: Custom Earth Living Event Plugin

Build a custom Paper plugin that chooses events, starts them, tracks progress and sends Discord messages through DiscordSRV or webhook integration.

Best when:

- Events must be realistic and country/region aware.
- Rewards, passports, economy, transport or BlueMap locations are involved.
- We want event history and admin commands later.

## Recommended Path

Start with Option B later, because Earth Living events should use the real-world map, countries, economy, passports and future transport systems.

For now the Discord channel and config placeholder are ready.
