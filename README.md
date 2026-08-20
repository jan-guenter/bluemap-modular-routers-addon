# BlueMap Modular Routers Add-on

[![CI](https://github.com/jan-guenter/bluemap-modular-routers-addon/actions/workflows/ci.yml/badge.svg)](https://github.com/jan-guenter/bluemap-modular-routers-addon/actions/workflows/ci.yml)

A narrow Java 21 BlueMap 5.22 add-on that restores Modular Routers' persisted
camouflage in static maps.

Version `0.1.0-alpha.1` is the owner-accepted release candidate. Its final
production JAR is 46,780 bytes with SHA-256
`c8e0c591169e12334abc85c3a917caffb5c82e4dd000acf92d0ff101b8f97a31`.
The accepted staging gallery passed all 8 assertions with zero failures at
the immediate, 20-tick and 100-tick phases on 2026-08-20. Its deterministic
gallery ZIP is 3,825 bytes with SHA-256
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

One custom-renderer exception reproduces Modular Routers' exact client result
for propertyless `minecraft:glass`. When the exact released BlueMap
Glassential add-on `0.1.0-alpha.1` is installed and active, this add-on renders
its generated 16-by-16 Fusion contextless cell 0 through a project-owned cube
model. It never reads the full 128-by-128 connected-texture sheet as one face.
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
gradle --no-daemon \
  -PmodularRoutersJar=/tmp/modular-routers-13.2.7+mc1.21.1.jar \
  -PglassentialAddonJar=../bluemap-glassential-addon/build/libs/bluemap-glassential-addon-0.1.0-alpha.1.jar \
  -PreleaseTag=v0.1.0-alpha.1 \
  clean check build generatePomFileForAddonPublication \
  generateMetadataFileForAddonPublication verifyPublicationArtifacts \
  verifyReleaseCandidate
```

The build uses the sibling `../bluemap-backport` checkout by default. The
production JAR is a plain BlueMap add-on for BlueMap's `packs` directory; it
is not a NeoForge mod. Modular Routers remains operator-installed and is not
bundled. The optional Glassential bridge is a soft dependency and neither its
add-on, implementation classes nor operator textures are bundled.

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

The intended immutable tag is `v0.1.0-alpha.1`, and the Maven coordinate is
`io.github.jan-guenter:bluemap-modular-routers-addon:0.1.0-alpha.1`.
Publication is allowed only after the independently audited pull request and
its final-head CI pass. See [the release procedure](docs/RELEASING.md) and
[recorded candidate provenance](provenance/release.json).
