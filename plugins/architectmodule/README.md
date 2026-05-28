# ArchitectModule

Admin-only EarthLiving blueprint and schematic generator.

## Goal

ArchitectModule helps admins create Minecraft-style interpretations of real-world buildings, landmarks, stations, ports and airports. V1 is the safe local generator. V2 adds optional public real-world metadata lookup.

Generated builds are not exact replicas. They are blocky Minecraft interpretations meant to speed up city-building and planning.

## Commands

```text
/architect search <building>
/architect generate <building> [scale] [style]
/architect preview <id>
/architect paste <id>
/architect undo
/architect list
/architect reload
```

Permission:

```text
earthliving.architect.admin
```

Runtime:

```text
Paper 26.1.2
Java 25
WorldEdit or FAWE with WorldEdit API
```

## Workflow

1. Search/planning:
   ```text
   /architect search copenhagen central station
   ```
2. Generate a schematic:
   ```text
   /architect generate copenhagen central station 2 modern
   ```
3. Preview metadata:
   ```text
   /architect preview arch-abc123
   ```
4. Start visual preview:
   ```text
   /architect preview arch-abc123 look
   ```
   While the visual preview is active, use the mouse wheel/hotbar scroll to rotate 90 degrees at a time. Left-click places the schematic. Cancel with:
   ```text
   /architect cancel
   ```
5. Or stand at the target paste origin and paste directly:
   ```text
   /architect paste arch-abc123
   ```
   Or paste onto the block you are looking at:
   ```text
   /architect paste arch-abc123 look
   ```
6. If the last paste was wrong, undo it:
   ```text
   /architect undo
   ```

## V1 Complete Criteria

- Runs on Paper 26.1.2 / Java 25.
- Loads with WorldEdit on hub/test and Main.
- Generates local `.schem` files without web or AI secrets.
- Supports visual client-side preview with look placement.
- Supports mouse wheel/hotbar rotation during preview.
- Supports left-click placement from preview.
- Supports direct paste and look paste.
- Supports `/architect undo` through WorldEdit history for the last paste.

## V2 Public Lookup

ArchitectModule `0.2.2` can use public Wikipedia/MediaWiki data when enabled:

```yaml
generation:
  allow-web-lookup: true
  web-provider: "wikipedia"
  web-endpoint: "https://en.wikipedia.org"
```

Lookup flow:

```text
building name
-> Wikipedia search
-> Wikipedia page summary
-> title/description/extract
-> Minecraft-style blueprint interpretation
```

This does not create exact replicas. It uses public metadata to choose a better structure type and proportions, then creates a blocky EarthLiving interpretation.

V2.1 adds a dedicated lattice/spire tower template for Eiffel Tower-like landmarks, so those generate as open metal tower structures instead of closed block buildings.

V2.1.1 also routes generic `tower`/`tårn`/`taarn` requests into the open tower generator unless the metadata clearly says `skyscraper` or `high-rise`. This avoids the old fallback where `/architect generate test tower 1 modern` produced a massive closed box.

V2.2 adds the first specialist local templates:

- Eiffel/iron/lattice tower
- generic open tower
- clock/bell tower
- skyscraper/high-rise
- obelisk

## Generated Files

Schematics are saved under:

```text
plugins/ArchitectModule/generated/
```

Each generated ID creates:

```text
<id>.schem
<id>.yml
```

## Current Limitations

- Generates Minecraft interpretations, not exact real-world replicas.
- Public lookup uses title, description and summary metadata only.
- If lookup fails, generation falls back to the local generator.
- Schematic generation is async.
- Schematic file loading before paste is async.
- Pasting is run through WorldEdit on the main thread because Bukkit world edits must be synchronous unless a safe FAWE integration is added later.
- Citizens, Constructor and Denizen are installed on hub/test and Main for builder-NPC experiments.
- EarthLivingCore should own the long-term build approval/queue workflow; ArchitectModule should continue to generate `.schem` files and provide visual placement tools.

## Safety

Do not put API keys, SSH keys, recovery codes, panel tokens or other secrets in this plugin config. Web/AI calls must be optional and configured later through safe server-side secret handling.
