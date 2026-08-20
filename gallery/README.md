# Modular Routers camouflage staging gallery

This directory defines a tiny deterministic datapack for the Modular Routers
persisted-camouflage prototype. It is confined to inclusive x `160..191`, y
`99..108`, z `160..175` and does not touch production or cluster state.

The four logical placements are:

| Cell | Position | Persisted state | Expected appearance |
| --- | --- | --- | --- |
| A1 | `164 100 164` | plain north-facing inactive router | stock router |
| A2 | `170 100 164` | router camouflage-upgrade component with `oak_log[axis=x]` | horizontal oak log |
| A3 | `176 100 164` | template-frame `CamouflageName` = glass | glass block |
| A4 | `182 100 164` | template-frame `CamouflageName` = east/top/straight deepslate-brick stairs | property-rich stair model |

There are deliberately no target references, inventory contents, activity or
LED matrices, BER effects, flying items, beams, or `Mimic` physics. The router
uses only its persisted `Upgrades.Items[].components` camouflage value;
client-only `BlockStateName` is excluded.

## Generate, lint, and package

Run from the repository root:

```text
PYTHONDONTWRITEBYTECODE=1 python3 gallery/generate.py --check
PYTHONDONTWRITEBYTECODE=1 python3 gallery/lint.py
bash gallery/package.sh /tmp/bluemap-modular-routers-gallery.zip
```

Running `gallery/generate.py` without `--check` rewrites only generated
datapack files and `SHA256SUMS`. Packaging uses sorted paths, fixed file modes,
stripped ZIP metadata, and a fixed DOS epoch. No Modular Routers or Minecraft
resource is bundled.

## Staging functions

```text
/function modularrouters_gallery:build
/function modularrouters_gallery:verify
/function modularrouters_gallery:clear
/function modularrouters_gallery:release
```

`build` increments a persistent `#builds` score before clearing and placing
the gallery. For a deliberate fresh run, reset `#builds` in objective
`mr_gallery` to zero first. The verifier runs immediately and at 20 and 100
ticks. Each phase performs eight assertions: four exact host block states,
three persisted camouflage payloads, and the one-build counter. Require:

```text
#immediate_checked = 8   #immediate_failures = 0
#20t_checked       = 8   #20t_failures       = 0
#100t_checked      = 8   #100t_failures      = 0
```

`release` cancels delayed checks and removes only this pad's forceload ticket;
it deliberately retains the four cells for rendering.
