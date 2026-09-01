# BlueMap Modular Routers Add-on

[![CI](https://github.com/jan-guenter/bluemap-modular-routers-addon/actions/workflows/ci.yml/badge.svg)](https://github.com/jan-guenter/bluemap-modular-routers-addon/actions/workflows/ci.yml)

A narrow Java 21 add-on for the exact BlueMap 5.23 feature backport that
restores Modular Routers' persisted camouflage in static maps.

Version `0.1.0-alpha.2` is the unpublished BlueMap 5.23 migration candidate.
Its production JAR is 53,593 bytes with SHA-256
`160a3fc276057e634b5c1c2dad7641028c7be811a5a7262d6da53c3936b8dfc2`.
It preserves alpha.1's accepted route and gallery contract. That staging
gallery passed all 8 assertions with zero failures at the immediate, 20-tick
and 100-tick phases on 2026-08-20. Its deterministic gallery ZIP remains
3,825 bytes with SHA-256
`f6011f220590f8ddb6d557e9be01830e3872e3c0ec17cfb8fd3c9816a9b9cd6f`.

The exact profile activates only for Modular Routers `13.2.7`, JAR size
1,285,765 bytes and SHA-256
`10f84e7f2d1bc7b655d8398d8c2e7146c4929c3ad2c97408f940ca86c1bf898c`.
It owns the exact `modularrouters:modular_router` and internal
`modularrouters:template_frame` hosts.

A valid saved camouflage state replaces the whole host through BlueMap's
ordinary default resource-model renderer, retaining admitted model-selecting
properties. Waterlogged states, tint-indexed models and custom-renderer or BER
paths deliberately fall back to the stock host because those would exceed the
exact pinned client behavior proven by this route.

The add-on records the original renderer identity while resources load, then
uses that identity for admission after aggregate add-ons apply late renderer
wrappers. Rendering still delegates through the live wrapped variant. This
keeps the default-model and Glassential policies intact without bypassing
combined-pack renderer behavior.

One custom-renderer exception reproduces Modular Routers' exact client result
for propertyless `minecraft:glass`. When either compatible exact native
BlueMap Glassential add-on `0.1.0-alpha.2` artifact is installed and active,
this add-on delegates a propertyless-glass neighborhood through its exact renderer. The
standalone candidate is 166,871 bytes with SHA-256
`9df99ffba26b1dd5a38452fb020e9a931b6a16a4ab4c374d85dad91cb9437e60`;
the accepted aggregate integration overlay is 166,916 bytes with SHA-256
`1a6b5ec84cd6c1a1bb1f0f711ddec4d6cef4b493b80d8da4d1139ad8a4eba28c`.
That exact delegation preserves
the generated Fusion tile's transparency and contextless isolated-glass
selection. It never reads the full 128-by-128 connected-texture sheet as one
face.
All 49 block IDs routed by that exact Glassential profile are guarded from the
ordinary model lane; the other 48 remain unsupported and render the stock
Modular Routers host. Missing, changed, inactive or resource-invalid
Glassential interop also falls back atomically to that stock host.

Routers are decoded from their saved camouflage-upgrade item component;
template frames use their direct saved camouflage state. Hosts without valid
camouflage remain stock. Any failed decode, unsupported target, missing
resource or renderer failure resets partial output before the stock host is
rendered.

Contents, activity, animations, BER effects and template-frame `Mimic`
physics are deliberately outside this static-map route. Recursive Modular
Routers and Chisels & Bits targets are rejected.

## Build

```bash
git submodule update --init --recursive -- \
  tooling/bluemap-addon-toolkit modules/bluemap-addon-adapter-api
gradle --no-daemon \
  -PbluemapSourcePath=/absolute/path/to/BlueMap-at-7e07f4e7 \
  -PmodularRoutersJar=/tmp/modular-routers-13.2.7+mc1.21.1.jar \
  -PglassentialAddonJar=/absolute/path/bluemap-glassential-addon-0.1.0-alpha.2.jar \
  -PreleaseTag=v0.1.0-alpha.2 \
  clean check build generatePomFileForAddonPublication \
  generateMetadataFileForAddonPublication verifyPublicationArtifacts \
  verifyReleaseCandidate
```

The exact BlueMap checkout is commit
`7e07f4e74ec1e92a6ead9aa1e66054af3e133aac` with API commit
`285c9a60eff3ac2b0cab308ce1058d1565be0971`. The Adapter API source module is
pinned at commit `e81f08bc4bfbf02d810ec8949a019130e2e61634`, source tree
`2f974c9bb2ba13888d69682f86f30f58922d30eb`; exactly four helpers are compiled
as source and no module JAR is installed, bundled, or nested. The production
JAR is a plain BlueMap add-on for BlueMap's `packs` directory, not a NeoForge
mod. Modular Routers remains operator-installed. The optional Glassential
bridge is a soft dependency; neither its add-on, implementation classes, nor
operator textures are bundled.

## Installation and fallback

Place the reviewed add-on JAR in `config/bluemap/packs` and restart the JVM.
An absent or different Modular Routers artifact leaves the add-on inactive.
An absent or different Glassential add-on affects only its guarded camouflage
targets, which render the stock Modular Routers host.
Removing the add-on and restarting restores BlueMap's ordinary resource
rendering without changing world data.

Only the exact All the Mons 1.2.0 profile is supported. Runtime rendering,
owner visual acceptance, release and deployment are separate gates.

## Release

The intended immutable tag is `v0.1.0-alpha.2`, and the Maven coordinate is
`io.github.jan-guenter:bluemap-modular-routers-addon:0.1.0-alpha.2`.
Publication is allowed only after the independently audited pull request and
its final-head CI pass. See [the release procedure](docs/RELEASING.md) and
[recorded candidate provenance](provenance/release.json).
