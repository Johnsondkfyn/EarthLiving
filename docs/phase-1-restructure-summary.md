# Phase 1 Restructure Summary

## 1. Updated Project Structure

```text
EarthLiving/
├─ README.md
├─ archive/legacy-paper-network/
├─ docs/
├─ migration/
├─ modpack/fabric-1.20.1/
└─ server/
   ├─ fabric-test/
   └─ production-future/
```

## 2. Migration Checklist

See `docs/migration-checklist.md`.

Current status:

- Repository branch created.
- Repository snapshot created.
- Old direction archived without deletion.
- Fabric-first docs and website foundation created.
- Live server migration not started.

## 3. Website Structure

```text
docs/index.html
docs/vision.html
docs/roadmap.html
docs/devlog.html
docs/newsletter.html
docs/contact.html
docs/styles.css
docs/app.js
```

## 4. Server Architecture Diagram

```text
Earth Living Fabric Test Server
└─ Minecraft 1.20.1
   ├─ Fabric Loader
   ├─ Java 21
   ├─ Paid Earth map copy
   └─ Fabric modpack foundation

Later
└─ Earth Living Production Server
```

Pterodactyl remains the backend at `panel.earthliving.earth`.

## 5. Fabric Migration Plan

See `docs/fabric-migration-plan.md`.

Short form:

1. Full backup.
2. Snapshot.
3. New Fabric test server.
4. Minecraft 1.20.1.
5. Fabric Loader.
6. Java 21.
7. Copy `world`, `world_nether`, `world_the_end`.
8. Start Fabric.
9. Verify world integrity.
10. Test performance.

## 6. Recommended Folder Structure

```text
archive/legacy-paper-network/  preserved legacy material
docs/                          website and active documentation
migration/                     migration notes and future scripts
modpack/fabric-1.20.1/         modpack manifest planning
server/fabric-test/            test server setup notes
server/production-future/      production planning after verification
```

## 7. Files Modified

- `README.md`
- `docs/index.html`
- `docs/styles.css`
- `docs/app.js`
- `docs/vision.html`
- `docs/roadmap.html`
- `docs/devlog.html`
- `docs/newsletter.html`
- `docs/contact.html`
- `docs/server-architecture.md`
- `docs/fabric-migration-plan.md`
- `docs/fabric-modpack.md`
- `docs/migration-checklist.md`
- `docs/archive-index.md`
- `docs/phase-1-restructure-summary.md`
- `migration/README.md`
- `modpack/fabric-1.20.1/README.md`
- `server/fabric-test/README.md`
- `server/production-future/README.md`

## 8. Files Archived

Archived into `archive/legacy-paper-network/`:

- old plugin source folders
- old server configuration folders
- old panel override folders
- old map and border generation tools
- old operational docs
- old root planning docs
- untracked root artifacts and jars

## 9. Risk Assessment Before Migration

| Risk | Level | Response |
|---|---:|---|
| Paid Earth map data loss | High | Full backup and snapshot before copy. |
| Accidental live server modification | High | Work only in separate Fabric test server. |
| Dimension folder mismatch | Medium | Verify all three world folders before first start. |
| Mod incompatibility | Medium | Start with minimal modpack and documented dependencies. |
| Performance problems with 200 GB map | Medium | Profile with Spark and test view distance/chunk loading. |
| Cleanup too early | High | Do not delete old infrastructure until verified and confirmed. |
