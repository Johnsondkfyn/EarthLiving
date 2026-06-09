# Migration Checklist

## Phase 1: Project Restructure

- [x] Create repository branch.
- [x] Create repository snapshot.
- [x] Archive old direction files without deleting them.
- [x] Rebuild active documentation around the Fabric single-world vision.
- [x] Create website foundation pages.
- [x] Create Fabric migration and modpack documentation.

## Phase 2: Backup Gate

- [ ] Confirm available disk space for a full world backup.
- [ ] Create full server backup.
- [ ] Create independent snapshot.
- [ ] Verify backup size and restore path.
- [ ] Record backup location and timestamp.

## Phase 3: Fabric Test Server

- [ ] Create new Pterodactyl server.
- [ ] Install Minecraft 1.20.1.
- [ ] Install Fabric Loader.
- [ ] Configure Java 21.
- [ ] Add minimal Fabric modpack.
- [ ] Start once with a clean test world.

## Phase 4: Earth Map Copy

- [ ] Stop the source server before copying, if required.
- [ ] Copy `world`.
- [ ] Copy `world_nether`.
- [ ] Copy `world_the_end`.
- [ ] Validate file counts and copy logs.
- [ ] Start Fabric against copied data only.

## Phase 5: Verification

- [ ] Check startup log.
- [ ] Join server.
- [ ] Check spawn.
- [ ] Check known locations.
- [ ] Check dimensions.
- [ ] Run performance profiling.
- [ ] Decide whether to continue, rebuild or roll back.
