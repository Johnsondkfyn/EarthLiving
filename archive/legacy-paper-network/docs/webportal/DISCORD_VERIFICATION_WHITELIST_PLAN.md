# Discord Verification and Whitelist Plan

Status: V1 planning, 2026-05-26.

## Goal

Connect Discord verification, Minecraft whitelist access and the future website profile without exposing private player data publicly.

## Recommended Flow

```text
1. Player joins Discord.
2. Player opens the website profile page and starts verification.
3. Website creates a one-time verification code.
4. Player enters the code in Minecraft through EarthOS -> My EarthLiving.
5. EarthWebBridge links website profile id to Minecraft UUID.
6. Discord bot links Discord user id to the same website profile id.
7. Staff or automation grants the verified Discord role.
8. Whitelist is granted only after Minecraft UUID and Discord user id are both verified.
```

## V1 Data Model

```json
{
  "profileId": "website-profile-id",
  "minecraftUuid": "00000000-0000-0000-0000-000000000000",
  "discordUserId": "000000000000000000",
  "verifiedAt": "2026-05-26T12:00:00Z",
  "whitelistStatus": "pending|approved|denied|revoked",
  "staffNote": ""
}
```

## Staff Alerts

- Alert staff when a player completes Minecraft linking.
- Alert staff when Discord verification is ready for whitelist approval.
- Alert staff if one Discord account attempts to link multiple Minecraft accounts.
- Alert staff if one Minecraft UUID attempts to link multiple website profiles.

## Security Rules

- Do not publish Discord user ids, Minecraft UUIDs or profile mappings on the public website.
- Keep private verification mappings on the server or a future authenticated portal backend.
- The public static website can show only bridge readiness and safe aggregate counts.
- Whitelist write actions should be staff-approved until the abuse rules are tested.

## Next Implementation Step

Add a private `verification-links.yml` or database table after the website login/session model is chosen. The current static website should only display the safe profile shell and live bridge status.
