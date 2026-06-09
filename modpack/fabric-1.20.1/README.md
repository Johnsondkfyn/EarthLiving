# Fabric 1.20.1 Modpack Foundation

Target runtime:

- Minecraft `1.20.1`
- Fabric Loader `0.17.2`
- Java `21`

The first server-side foundation was installed on the isolated Fabric test server and boot-tested successfully on 2026-06-09.

Mods installed:

| Mod | Version | Purpose |
| --- | --- | --- |
| Fabric API | `0.92.9+1.20.1` | Required Fabric APIs |
| Create Fabric | `6.0.8.1+build.1744-mc1.20.1` | Transport, industry and mechanical systems |
| Create: Steam 'n' Rails | `1.7.2+fabric-mc1.20.1` | Train and rail expansion |
| Chipped | `3.0.7` | Building block variety |
| Handcrafted | `3.0.6` | Furniture and decoration |
| Resourceful Lib | `2.1.29` | Dependency for Chipped/Handcrafted |
| Athena | `3.1.2` | Dependency for Chipped |
| Flan | `1.20.1-1.11.16-fabric` | Claims/chunks equivalent for early testing |
| Lithium | `mc1.20.1-0.11.4-fabric` | Server performance |
| FerriteCore | `6.0.1` | Memory reduction |
| ModernFix | `5.25.2+mc1.20.1` | Performance and bug fixes |
| Krypton | `0.2.3` | Networking optimization |

Notes:

- FTB Chunks was not installed in this first pass because a Fabric 1.20.1 Modrinth project was not found during the 2026-06-09 lookup. `Flan` is used as the equivalent claims/chunks foundation for testing.
- Fabric Loader `0.19.3` booted vanilla Fabric but failed with Create due to a namespace/class tweaker compatibility issue.
- Fabric Loader `0.16.14` was too old for Create `6.0.8.1`.
- Fabric Loader `0.17.2` passed the full modpack boot check.
