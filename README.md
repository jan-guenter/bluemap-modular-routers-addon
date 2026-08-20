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
  clean check build generatePomFileForAddonPublication \
  generateMetadataFileForAddonPublication verifyPinnedArtifact
```

The build uses the sibling `../bluemap-backport` checkout by default. The
production JAR is a plain BlueMap add-on for BlueMap's `packs` directory; it
is not a NeoForge mod. Modular Routers remains operator-installed and is not
bundled.

## Installation and fallback

Place the reviewed add-on JAR in `config/bluemap/packs` and restart the JVM.
An absent or different Modular Routers artifact leaves the add-on inactive.
Removing the add-on and restarting restores BlueMap's ordinary resource
rendering without changing world data.

Only the exact All the Mons 1.2.0 profile is supported. Runtime rendering,
owner visual acceptance, release and deployment are separate gates.
