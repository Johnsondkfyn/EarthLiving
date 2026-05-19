# EarthBackpackSystem

## Priority
High priority.

## Goal
Create a custom backpack, carry-weight and equipment system integrated with EarthOS, logistics, mining, transport and survival-style gameplay.

## Purpose
Backpacks should not just provide more inventory slots. They should become part of player identity, logistics, travel, mining and equipment management.

The system should help make the world feel physical and believable without becoming frustrating hardcore survival gameplay.

## Core systems

- custom backpacks
- carry weight system
- specialized backpack types
- logistics integration
- transport integration
- mining integration
- HUD integration
- future equipment slots
- future visible backpack models

## Backpack examples

| Backpack | Purpose |
|---|---|
| Civilian Backpack | normal everyday use |
| Travel Bag | tourism and transport |
| Mining Pack | mining and heavy resources |
| Logistics Crate | cargo transport |
| VIP Suitcase | luxury travel |
| Emergency Backpack | disaster/public service equipment |
| Company Equipment Bag | work/company equipment |

## Gameplay effects

Different backpack types can provide:

- higher carry capacity
- reduced movement penalties
- mining bonuses later
- logistics bonuses later
- travel/tourism utility
- item category bonuses
- future temperature or preservation bonuses

## Carry-weight system

Players should not be able to carry unrealistic amounts of material without consequences.

Example:

```text
63 / 80 kg
```

If overloaded:

```text
OVERWEIGHT
Movement reduced
```

## HUD integration

The EarthOS HUD should display:

- current backpack
- current weight
- carry limit
- overload warnings

Example:

```text
Backpack: Mining Pack MK1
Weight: 63 / 80kg
```

## EarthOS integration

```text
EarthOS -> Inventory -> Backpack
```

Possible menu sections:

- backpack
- equipment
- carry weight
- storage
- loadout

## Visual direction

### V1

- GUI system
- item lore/icons
- inventory-based system

### V2

- ItemsAdder textures
- custom icons
- custom backpack visuals

### V3

- visible backpack models on player backs
- equipment visuals
- animations later

## Integration targets

- EarthOS
- EarthOSHudModule
- EarthMiningSystem
- TransportModule
- EarthLivingLogisticsSystem
- CompanyCorporationSystem
- Tourism systems
- DynamicWeatherDisasterSystem later

## Performance rule

Avoid heavy constantly-updating inventory scans. Use cached weight calculations where possible.

## Recommended implementation order

1. Basic backpack GUI
2. Carry-weight calculation
3. HUD integration
4. Backpack types and balancing
5. Logistics integration
6. ItemsAdder textures
7. Visible backpack models later
