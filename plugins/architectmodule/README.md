# ArchitectModule

Admin-only EarthLiving blueprint and schematic generator.

## Goal

ArchitectModule helps admins create Minecraft-style interpretations of real-world buildings, landmarks, stations, ports and airports. V1 is intentionally simple and command-driven.

Generated builds are not exact replicas. They are blocky Minecraft interpretations meant to speed up city-building and planning.

## Commands

```text
/architect search <building>
/architect generate <building> [scale] [style]
/architect preview <id>
/architect paste <id>
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

## V1 Limitations

- Generates simple blocky structures locally.
- Web/AI lookup is configurable but disabled by default.
- Schematic generation is async.
- Schematic file loading before paste is async.
- Pasting is run through WorldEdit on the main thread because Bukkit world edits must be synchronous unless a safe FAWE integration is added later.
- Constructor integration is planned after one builder-NPC workflow has been validated.

## Safety

Do not put API keys, SSH keys, recovery codes, panel tokens or other secrets in this plugin config. Web/AI calls must be optional and configured later through safe server-side secret handling.
