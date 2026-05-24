# EarthLiving Web Portal V1 Export Schema

Status: V1 scope, 2026-05-24.

The first portal bridge should export read-only JSON from server systems. The website can consume the data, but it should not edit Minecraft files directly.

## Public Server Status

```json
{
  "updatedAt": "2026-05-24T12:00:00Z",
  "server": "Earth Living Main",
  "phase": "private-test",
  "onlinePlayers": 0,
  "maxPlayers": 20,
  "coreVersion": "0.6.3",
  "mapStatus": "foundation-live"
}
```

## Linked Player Profile

```json
{
  "profileId": "website-profile-id",
  "minecraftUuid": "00000000-0000-0000-0000-000000000000",
  "playerName": "PlayerName",
  "linkedAt": "2026-05-24T12:00:00Z",
  "status": "linked"
}
```

## Player Report Summary

```json
{
  "minecraftUuid": "00000000-0000-0000-0000-000000000000",
  "openReports": 1,
  "reports": [
    {
      "id": 1,
      "source": "in-game",
      "category": "bug",
      "status": "open",
      "createdAt": "2026-05-20T18:00:00Z",
      "lastUpdatedAt": "2026-05-24T12:00:00Z"
    }
  ]
}
```

## Security Boundary

- Public status can be shown on `earthliving.earth`.
- Linked player data requires a future authenticated portal session.
- Staff-only report details stay in the panel Report Center.
- API tokens and private config stay only on the live server.
