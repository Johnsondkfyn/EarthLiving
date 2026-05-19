# EarthOS Hotbar Icon

Updated: 2026-05-19

## Decision

EarthOS should use the gold/cyan globe-compass icon as the player hotbar menu device.

Source file from local design export:

- `C:/Users/Johna/Downloads/ChatGPT Image 19. maj 2026, 14.31.27.png`

Repository copies:

- `docs/assets/earthos/earthos-hotbar-icon-original.png`
- `docs/assets/earthos/earthos-hotbar-icon-128.png`
- `docs/assets/earthos/earthos-hotbar-icon-64.png`
- `docs/assets/earthos/earthos-hotbar-icon-32.png`
- `docs/assets/earthos/earthos-hotbar-icon-16.png`

## In-Game Use

The icon represents the EarthOS device/menu item.

Recommended item:

- Material: `CLOCK` or `COMPASS`
- Display name: `EarthOS`
- Custom model data: `260519`
- Lore:
  - `Open your Earth Living menu`
  - `Map, events, passport, reports and settings`

Recommended slot:

- Hotbar slot 8 or 9, depending on other server tools.

## Resource Pack Path

When the custom resource pack is created, use the icon as a custom model texture, for example:

```text
assets/minecraft/textures/item/earthos_device.png
assets/minecraft/models/item/compass.json
```

Recommended texture size:

- Start with `32x32` for Minecraft item texture readability.
- Keep `64x64` and `128x128` for UI/menu assets.

## Plugin Hook

EarthLivingCore v0.1.0 uses this config by default:

```yaml
earthos:
  material: COMPASS
  display-name: "&6EarthOS"
  custom-model-data: 260519
```

## Notes

- The original icon is detailed, so the small versions should be checked in-game.
- If the 16x16 icon becomes too noisy, create a simplified pixel-art version later.
- This asset is for Earth Living project use and should stay aligned with the logo colors: dark base, gold, cyan.
