# Third-party components

| Component | Version / identity | License | Distribution posture |
| --- | --- | --- | --- |
| BlueMap | 5.22 / workspace Java 21 backport | MIT | Compile-only API; not bundled |
| Modular Routers | 13.2.7, exact SHA-256 in `provenance/upstreams.json` | MIT declared by exact artifact/source | Operator-installed evidence and resource provider; not bundled |
| BlueMap Glassential Add-on | `0.1.0-alpha.1`, exact SHA-256 in `provenance/upstreams.json` | MIT | Optional operator-installed soft dependency and generated-tile provider; not bundled |
| Glassential | 3.4.5, exact SHA-256 in `provenance/upstreams.json` | MIT declared by exact artifact | Operator-installed resource provider behind the Glassential add-on; not bundled |
| Fusion | 1.3.12, exact SHA-256 in `provenance/upstreams.json` | All rights reserved | Operator-installed resource-format owner behind the Glassential add-on; not bundled |
| BlueMap SecurityCraft Add-on | `v0.1.0-alpha.1`, commit `a99e816581b62c71dae3975e1e677a1ff93aec64` | MIT | Owner-authored adapter/fallback structure adapted with attribution |
| Minecraft / NeoForge | 1.21.1 / 21.1.248 | Third-party terms / LGPL-2.1-only portions | Not bundled |

The add-on interprets operator-installed blockstate resources and persisted
NBT. Its own cube model references a runtime-generated Glassential add-on tile;
it copies no third-party texture. No Modular Routers, Glassential, Fusion,
Minecraft, NeoForge, BlueMap or other add-on implementation code or asset is
bundled.
