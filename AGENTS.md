# Agent guide for BlueMap Modular Routers Add-on

Read `/root/work/allthemons/AGENTS.md` and this file before changing this
standalone project. This is a plain BlueMap add-on, not a NeoForge mod and not
part of the root orchestration repository.

## Exact baseline

| Component | Identity |
| --- | --- |
| All the Mons | `1.2.0`, pack commit `c7bb230f21d14d26859d0b92548f089b3a493ad9` |
| Minecraft / NeoForge / Java | `1.21.1` / `21.1.248` / `21` |
| BlueMap | feature backport `5.22-feature.backport-5.23-stateless-java-web-server-46`, commit `7e07f4e74ec1e92a6ead9aa1e66054af3e133aac`, API `285c9a60eff3ac2b0cab308ce1058d1565be0971` |
| Adapter API | `0.1.0-alpha.2`, commit `e81f08bc4bfbf02d810ec8949a019130e2e61634`, source tree `2f974c9bb2ba13888d69682f86f30f58922d30eb` |
| Modular Routers | `13.2.7`, 1,285,765 bytes, SHA-256 `10f84e7f2d1bc7b655d8398d8c2e7146c4929c3ad2c97408f940ca86c1bf898c` |
| Modular Routers source | tag `v13.2.7`, commit `1df4d085a76c763d8eb2cefd8baeda5f4a4188ed` |
| BlueMap Glassential add-on | Native `0.1.0-alpha.2`: standalone candidate 166,871 bytes / SHA-256 `9df99ffba26b1dd5a38452fb020e9a931b6a16a4ab4c374d85dad91cb9437e60`; accepted aggregate overlay 166,916 bytes / SHA-256 `1a6b5ec84cd6c1a1bb1f0f711ddec4d6cef4b493b80d8da4d1139ad8a4eba28c` |

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
- Snapshot original renderer identity during resource loading so a later
  aggregate wrapper cannot make an ordinary model look custom. Admission uses
  that snapshot, while rendering continues through the live wrapped variant.
- Reject recursive Modular Routers and `chiselsandbits` targets. Missing,
  malformed, duplicate, empty, unsupported, resource-invalid or
  over-capacity routes must reset partial geometry and map color, then render
  the host atomically.
- Treat all 49 exact routes owned by either compatible native Glassential artifact as custom
  renderer routes. Only propertyless `minecraft:glass` is admitted through
  the exact optional bridge: require one of the two byte-pinned artifacts, its active
  synthetic dispatch and its generated 16-by-16 Fusion cell-0 tile, then
  delegate a propertyless-glass neighborhood through that exact renderer. Every
  unavailable, inactive or invalid interop state and the other 48 routes fall
  back atomically to the stock Modular Routers host.
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
git submodule update --init --recursive -- \
  tooling/bluemap-addon-toolkit modules/bluemap-addon-adapter-api
gradle --no-daemon \
  -PbluemapSourcePath=/absolute/path/to/BlueMap-at-7e07f4e7 \
  -PmodularRoutersJar=/tmp/modular-routers-13.2.7+mc1.21.1.jar \
  -PglassentialAddonJar=/absolute/path/bluemap-glassential-addon-0.1.0-alpha.2.jar \
  -PreleaseTag=v0.1.0-alpha.2 \
  clean check build generatePomFileForAddonPublication \
  generateMetadataFileForAddonPublication verifyPinnedArtifact
```

Inspect the production JAR after the build. Owner visual acceptance is
required before release. A successful build authorizes neither release nor
production deployment.
