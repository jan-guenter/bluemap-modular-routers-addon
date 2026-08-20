# BlueMap Modular Routers Add-on

A narrow Java 21 BlueMap 5.22 add-on that restores Modular Routers' persisted
camouflage in static maps.

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
  clean check build generatePomFileForAddonPublication \
  generateMetadataFileForAddonPublication verifyPinnedArtifact
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
