# Earth Living Fabric Test Server

Purpose: validate Minecraft 1.20.1, Fabric Loader, Java 21, the modpack foundation and a copied Earth map before any production decision.

Status as of 2026-06-09:

- Pterodactyl server: `Earth Living Fabric Test`
- UUID: `0435a9c7-8cc7-4a6b-9a90-2b715ea5c7e8`
- Address: `159.195.149.253:25568`
- Runtime image: `ghcr.io/pterodactyl/yolks:java_21`
- Minecraft: `1.20.1`
- Fabric Loader: `0.17.2`
- Startup jar: `fabric-server-launch.jar`
- Memory: `8192 MB`
- Disk limit: `50000 MB`

The server was created as a separate Pterodactyl server and has its own volume:

`/var/lib/pterodactyl/volumes/0435a9c7-8cc7-4a6b-9a90-2b715ea5c7e8`

The live Paper installation has not been modified.

Boot checks:

- Vanilla Fabric boot check passed with Java 21.
- Modpack foundation boot check passed with Java 21 and reached `Done`.

Do not copy world data from the live Paper server until the migration backup and snapshot steps are complete.
