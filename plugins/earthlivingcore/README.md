# EarthLivingCore

EarthLivingCore is the foundation plugin for Earth Living.

Version: `0.1.1`

## Purpose

EarthLivingCore is the backend/hub layer behind:

- EarthOS
- server events
- reports
- passports/countries
- notifications
- Discord integration
- future transport and economy modules

## Current v1 Scope

- Clean Paper plugin bootstrap.
- `/earthliving` and `/earthos` commands.
- Module registry/status.
- Notification service.
- EarthOS hotbar item on join.
- EarthOS inventory menu with first click actions.
- BlueMap link, server status, events, passport, wallet, reports and settings placeholders.

## Build

```bash
gradle build
```

The output jar is expected under:

```text
build/libs/EarthLivingCore-0.1.1.jar
```

## Notes

- Built against Paper API `1.21.11-R0.1-SNAPSHOT`.
- Java source target is 21 for broad Paper plugin compatibility while the server can still run on Java 25.
- The real custom item texture belongs in a future resource pack using the EarthOS hotbar icon asset.
