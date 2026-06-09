# EarthLiving Web Portal V1 Export Schema

Status: V1 scope, 2026-05-24.

The first portal bridge should export read-only JSON from server systems. The website can consume the data, but it should not edit Minecraft files directly.

## Public Server Status

```json
{
  "generatedAt": "2026-05-24T12:00:00Z",
  "server": "Earth Living Main",
  "phase": "private-test",
  "onlinePlayers": 0,
  "maxPlayers": 20,
  "coreVersion": "0.7.1",
  "mapStatus": "foundation-live",
  "linkedProfiles": 0
}
```

## Linked Player Profile

```json
{
  "profileId": "website-profile-id",
  "minecraftUuid": "00000000-0000-0000-0000-000000000000",
  "playerName": "PlayerName",
  "linkedAt": "2026-05-24T12:00:00Z",
  "lastSeenAt": "2026-05-24T12:00:00Z",
  "online": false,
  "playtimeSeconds": 0,
  "blocksBroken": 0,
  "blocksPlaced": 0,
  "deaths": 0,
  "mobKills": 0,
  "distanceWalkedMeters": 0,
  "openReports": 0,
  "totalReports": 0
}
```

## Player Activity Stats

```json
{
  "profileId": "website-profile-id",
  "minecraftUuid": "00000000-0000-0000-0000-000000000000",
  "playerName": "PlayerName",
  "playtimeSeconds": 12345,
  "blocksBroken": 420,
  "blocksPlaced": 380,
  "deaths": 2,
  "mobKills": 19,
  "distanceWalkedMeters": 1200
}
```

## Player Report Summary

```json
{
  "minecraftUuid": "00000000-0000-0000-0000-000000000000",
  "playerName": "PlayerName",
  "openReports": 1,
  "totalReports": 1,
  "reports": [
    {
      "id": 1,
      "source": "minecraft",
      "category": "bug",
      "categoryTitle": "Bug",
      "status": "open",
      "createdAt": "2026-05-20T18:00:00Z",
      "updatedAt": "2026-05-24T12:00:00Z",
      "closedAt": "",
      "staffNote": ""
    }
  ]
}
```

## Security Boundary

- Public status can be shown on `earthliving.earth`.
- The static website may mirror a sanitized public status file as `docs/data/webportal-server-status.json`.
- Linked player data requires a future authenticated portal session.
- Staff-only report details stay in the panel Report Center.
- API tokens and private config stay only on the live server.
