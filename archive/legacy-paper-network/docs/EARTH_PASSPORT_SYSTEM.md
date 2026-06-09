# EarthPassportSystem

Status: V1 foundation implemented in EarthLivingCore `0.7.2`.

EarthPassportSystem is the Earth Living identity, citizenship, visa and border-access layer. It should feel like a real EarthOS app first, then become gameplay as countries, transport, borders and reputation mature.

## V1 Scope

Implemented in EarthLivingCore:

- Player passport profile created on join.
- EarthOS Passport app with read-only player view.
- Citizenship/home country fields.
- Visa records per country.
- Country reputation values from `-100` to `100`.
- Staff commands for basic passport administration.
- Read-only JSON export for the website/player portal:
  - `plugins/EarthLivingCore/web-exports/player-passports.json`

## Commands

Player:

```text
/earthliving passport
/earthliving passport info
```

Staff:

```text
/earthliving passport setcitizenship <player> <country> [status]
/earthliving passport addvisa <player> <country> <visitor|work|resident|event> [status] [expiresAt]
/earthliving passport reputation <player> <country> <-100..100>
/earthliving passport export
```

Examples:

```text
/earthliving passport setcitizenship TheKing189 denmark active
/earthliving passport addvisa TheKing189 germany visitor active 2026-06-30
/earthliving passport reputation TheKing189 denmark 25
```

## Data Model

Passport data is stored in:

```text
plugins/EarthLivingCore/passports.yml
```

Per player:

- Minecraft UUID
- player name
- passport issue timestamp
- citizenship country
- citizenship status
- visa records
- reputation per country

## Gameplay Direction

V1 is intentionally staff-managed while rules are tested. Players can view their passport, but cannot self-issue citizenship or visas.

V2 should add:

- Passport office workflow in hub/main cities.
- Country application/review flow.
- Border checkpoint integration.
- Visa expiry and renewal.
- Discord/staff alerts for denied crossings.
- Website "My EarthLiving" passport view.

V3 should add:

- Customs/import rules.
- Country reputation effects.
- Transport ticket/passport checks.
- City/nation diplomacy and sanctions.
- Automated events affecting visa access.

## Relationship To PassportBorders

`PassportBorders` remains the older border/polygon prototype. It already knows country polygons and can block movement without permission/passport. EarthPassportSystem should become the source of player passport data, while PassportBorders or a future border adapter reads that data for checkpoint and border decisions.

Recommended next integration:

1. Keep EarthLivingCore as the passport profile/data owner.
2. Add a lightweight border adapter that asks EarthLivingCore whether a player may enter a country.
3. Keep hard border blocking disabled until the map and country rules are tested.
