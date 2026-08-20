# Third-party components

| Component | Version / identity | License | Distribution posture |
| --- | --- | --- | --- |
| BlueMap | 5.22 / workspace Java 21 backport | MIT | Compile-only API; not bundled |
| Modular Routers | 13.2.7, exact SHA-256 in `provenance/upstreams.json` | MIT declared by exact artifact/source | Operator-installed evidence and resource provider; not bundled |
| BlueMap SecurityCraft Add-on | `v0.1.0-alpha.1`, commit `a99e816581b62c71dae3975e1e677a1ff93aec64` | MIT | Owner-authored adapter/fallback structure adapted with attribution |
| Minecraft / NeoForge | 1.21.1 / 21.1.248 | Third-party terms / LGPL-2.1-only portions | Not bundled |

The add-on interprets operator-installed blockstate resources and persisted
NBT. No Modular Routers, Minecraft, NeoForge or BlueMap implementation code or
asset is bundled.
