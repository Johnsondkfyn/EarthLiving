# Earth Map Migration

This folder tracks planning for the protected Earth map migration.

The live installation must not be modified here. Work from copied data only after a full backup and snapshot have been created.

See `docs/fabric-migration-plan.md` and `docs/migration-checklist.md`.

Current status as of 2026-06-09:

- Live Paper installation has not been modified.
- Fabric test server has been created separately.
- No Earth map world data has been copied yet.
- Before copying `world`, `world_nether` or `world_the_end`, take a consistent backup/snapshot. The live Paper `world` folder is approximately 177 GB and the Fabric test server disk limit has been raised to 250 GB.
