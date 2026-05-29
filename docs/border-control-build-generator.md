# Earth Living Border Control Build Generator

Developer utility for generating the first compact Border Control building.

## Command

```text
/elbuild bordercontrol
```

Preview the building first:

```text
/elbuild bordercontrol preview
```

In preview mode the outline follows the block you are looking at. Left-click places the building at the preview origin.

If the build area contains existing blocks above the floor, the command asks for confirmation:

```text
/elbuild bordercontrol confirm
```

Permission:

```text
earthliving.build.bordercontrol
```

## Placement

The building is placed relative to the player location.

- The player stands at the center of the neutral hall.
- Size is about `21x21` blocks.
- Height is about `7` blocks.
- Country A is north / negative Z.
- Country B is south / positive Z.
- The border line runs through the neutral hall at the center.

## Layout

The generator creates:

- Country A entrance
- Country B entrance
- Neutral hall
- Passport Check A
- Passport Check B
- Visa Office
- Staff/Admin room
- Exit Gate to Country A
- Exit Gate to Country B
- Modern flat roof with glass skylight
- Roads, lighting, planters and signs

This is only the physical building. It does not implement visa purchase, economy or new border enforcement logic.

## Saving As WorldEdit Schematic

After generating and adjusting the building manually:

```text
//pos1
//pos2
//copy
//schem save earthliving-border-control-v1
```

Recommended selection:

- Include the foundation below the floor.
- Include the roof and road entrances.
- Stand in a stable reference position before `//copy` so future pastes are predictable.
