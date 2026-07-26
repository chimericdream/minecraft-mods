# Potential Features — Beacon & Conduit Tweaks

Brainstormed, thematically appropriate feature ideas. Nothing here is committed or implemented; these are
starting points for future planning.

The mod's identity: **configurable reach** for beacons and conduits. The natural next step is making the
*rest* of their behavior just as configurable, while staying server-side wherever possible.

## Beyond range: configurable effects

* **Amplifier control** — configure the effect level each pyramid tier grants (e.g. Haste II at level 2,
  Haste III at level 4), instead of vanilla's fixed primary/secondary split.
* **Effect pool via datapack** — drive the beacon's selectable effects from a data-defined list per
  pyramid level, so packs can offer Speed at level 1 but hold Resistance for level 4 — or add modded
  effects entirely.
* **Configurable effect duration/reapply window** — how long the effect lingers after leaving range
  (great for large bases where players skirt the boundary).

## Pyramid & frame materials

* **Mixed-material bonuses** — optional rule where a uniform pyramid (all one material) earns a small
  range or amplifier bonus over a patchwork one.
* **Conduit frame material weights** — same idea underwater: sea lanterns vs. prismarine bricks vs. dark
  prismarine each contribute a configurable amount of range.

## Shape & coverage controls

* **Range shape option** — sphere, cylinder (full world height), or vanilla-style box, per beacon config.
  Cylinder is the classic "my whole base, every Y level" request.
* **Vertical range decoupled from horizontal** — separate up/down reach settings, since bases sprawl
  horizontally far more than vertically.
* **Conduit hostile-damage radius as its own setting** — configure the "Conduit Power" buff range and the
  attack-hostile-mobs range independently.

## Feedback & polish

* **Range visualization** — a brief particle shell (or boundary shimmer) when the beacon UI is closed or
  when sneaking near the beacon, so players can *see* the configured reach. Client-optional.
* **Comparator output** — beacons emit a signal proportional to pyramid level; conduits proportional to
  frame completeness. Cheap, vanilla-flavored automation hooks.
* **Beam customization** — config for beam visibility through blocks, beam width, or letting stained
  glass produce gradient beams over a configurable blend distance.
* **HUD hint** — optional small icon showing which beacon/conduit effects currently reach you and from
  roughly how far (client-side nicety; server remains authoritative).

## Config ergonomics

* **Presets** — shippable config presets ("vanilla+", "mega-base", "lite") selectable from the YACL
  screen, since range/level/block interactions are easy to mis-tune.
* **Per-dimension overrides** — different range math in the nether (where distances are compressed 8:1)
  than the overworld.
