# EarthLiving Web Portal Link Code Flow

Status: V1 scope, 2026-05-24.

## Goal

Let a player link a future EarthLiving website profile to their Minecraft UUID without ever entering Minecraft or Microsoft credentials on the EarthLiving website.

## Recommended V1 Flow

```text
1. Player opens the future My EarthLiving page.
2. Website creates a short one-time link code.
3. Player joins the Minecraft server with their normal account.
4. Player opens EarthOS -> Profile -> Link Website Account.
5. Player enters the one-time code in-game.
6. EarthWebBridge links the website profile id to the player's Minecraft UUID.
7. Website can show read-only profile/report data for that linked player.
```

## Code Rules

- Codes expire quickly, recommended 10 minutes.
- Codes can only be used once.
- Codes are stored hashed or otherwise protected if persistence is needed.
- A code can only link the player who enters it in-game.
- Staff can unlink a profile if a player links the wrong account.

## V1 Non-Goals

- No Minecraft/Microsoft password handling.
- No Microsoft OAuth until the basic link-code model is proven.
- No website write access into live Minecraft data.
- No economy/passport/company private data until permission rules are defined.
