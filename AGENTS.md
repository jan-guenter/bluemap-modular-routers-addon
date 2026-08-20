# Agent guide for BlueMap Modular Routers Add-on

Read `/root/work/allthemons/AGENTS.md` and this file before changing this
standalone project. This is a plain BlueMap add-on, not a NeoForge mod and not
part of the root orchestration repository.

## Exact baseline

| Component | Identity |
| --- | --- |
| All the Mons | `1.2.0`, pack commit `c7bb230f21d14d26859d0b92548f089b3a493ad9` |
| Minecraft / NeoForge / Java | `1.21.1` / `21.1.248` / `21` |
| BlueMap | backport `5.22-agent.backport-5.22-mc1.21.1-2`, commit `9be321df995a1103808621d529eb72773e719d4d` |
| Modular Routers | `13.2.7`, 1,285,765 bytes, SHA-256 `10f84e7f2d1bc7b655d8398d8c2e7146c4929c3ad2c97408f940ca86c1bf898c` |
| Modular Routers source | tag `v13.2.7`, commit `1df4d085a76c763d8eb2cefd8baeda5f4a4188ed` |

A changed pack, BlueMap build, or Modular Routers artifact begins a new
evidence and visual-review task.

## Frozen route

- Own only `modularrouters:modular_router` and the internal
  `modularrouters:template_frame`, with matching block-entity IDs.
- Redirect only valid persisted camouflage. Ordinary hosts remain on
  BlueMap's stock resource path.
- Decode router camouflage from the unique `Upgrades.Items` entry whose ID is
  `modularrouters:camouflage_upgrade`, then read its
  `components."modularrouters:camouflage"` block state. The client-only
  `BlockStateName` update tag is not a persisted chunk-data route.
- Decode template-frame camouflage only from `CamouflageName`.
- Replace the whole host only with a conservative ordinary default resource
  model. Preserve admitted model-selecting target properties, but reject
  waterlogged states, tint-indexed models, custom renderers, BER/liquid paths
  and anything whose target rendering contract cannot be proven here.
- Reject recursive Modular Routers and `chiselsandbits` targets. Missing,
  malformed, duplicate, empty, unsupported, resource-invalid or
  over-capacity routes must reset partial geometry and map color, then render
  the host atomically.
- Do not render activity, contents, BER effects, animation, or `Mimic`
  physics. The router's rapidly changing active indicator is not a custom
  route.
- Never bundle Modular Routers, Minecraft, NeoForge or BlueMap classes,
  models, textures, source, worlds, screenshots or private fixtures.
- `gallery/**` belongs to the dedicated gallery owner. Do not edit it
  concurrently.

## Validation

Run from this repository with the exact pinned JAR:

```bash
gradle --no-daemon \
  -PmodularRoutersJar=/tmp/modular-routers-13.2.7+mc1.21.1.jar \
  clean check build generatePomFileForAddonPublication \
  generateMetadataFileForAddonPublication verifyPinnedArtifact
```

Inspect the production JAR after the build. Owner visual acceptance is
required before release. A successful build authorizes neither release nor
production deployment.
