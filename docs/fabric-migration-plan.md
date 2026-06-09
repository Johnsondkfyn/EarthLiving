# Fabric Migration Plan

## Goal

Move the paid Earth map into a separate Fabric test server without modifying the live installation.

## Migration Order

1. Create a full backup of the current server data.
2. Create a snapshot before any migration work.
3. Create a new Fabric test server in Pterodactyl.
4. Install Minecraft 1.20.1.
5. Install Fabric Loader.
6. Configure Java 21.
7. Copy world data into the test server:
   - `world`
   - `world_nether`
   - `world_the_end`
8. Start Fabric.
9. Verify world integrity.
10. Test performance.

## Safety Rules

- Do not modify the live installation.
- Do not convert or overwrite the original world folders.
- Copy data only after backup and snapshot gates are complete.
- Keep old servers archived until Fabric has been verified.
- Require explicit confirmation before deleting anything.

## Verification Checklist

- Server starts without world conversion errors.
- Overworld spawn and known player locations load correctly.
- Nether and End folders are detected correctly.
- Chunk loading is stable.
- Region files are present and readable.
- Modded blocks/entities do not corrupt vanilla/Paper world data.
- Performance is measured with realistic view distance and player simulation.

## Risk Assessment

| Risk | Impact | Mitigation |
|---|---:|---|
| Large map copy failure | High | Use checksums, logs and staged copy commands. |
| World format mismatch | High | Test on copied data only. Keep original untouched. |
| Missing dimension mapping | Medium | Validate `world`, `world_nether` and `world_the_end` paths before start. |
| Mod dependency mismatch | Medium | Build modpack manifest before server start. |
| Performance regression | Medium | Benchmark before production decision. |
| Accidental live edits | High | Work only in a new Fabric test server directory. |
