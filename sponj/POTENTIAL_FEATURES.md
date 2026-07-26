# Potential Features — Sponj

Brainstormed, thematically appropriate feature ideas. Nothing here is committed or implemented; these are
starting points for future planning.

The mod's identity: **simple, slightly silly, extremely useful sponges**. Two blocks and a connection
bonus — ideas below keep that "one block, one job" spirit.

## New sponj types

* **Snow Sponj** — absorbs powder snow and snow layers in a radius; comes back as a "cold wet sponj"
  that dries in the nether instantly (with a satisfying *fssss*).
* **Universal Sponj (stretch)** — absorbs *any* fluid, including modded fluids, at the cost of an
  expensive recipe (sponj + lava sponj + something rare). One block that ends all fluid cleanup.
* **Sponj Slab / Sponj Carpet** — thin sponj variants for shallow cleanup jobs (drying a floor after a
  flood without sacrificing full blocks) and for decorative bathroom builds.

## Drying & automation loop

* **Drying Rack** — a block that slowly dries wet sponjes placed on/in it; faster above a campfire,
  instant above soul fire. Gives the drying loop a home base instead of nether round-trips.
* **Squeezing** — right-click a wet sponj block with a glass bottle to get a water bottle (and after
  a few squeezes, a dry-ish sponj). Piston-squeeze for automation: a piston pushing into a wet sponj
  ejects a water bottle item and leaves a dry sponj.
* **Dispenser support** — dispensers can place sponjes (soaking up liquid immediately) and, with shears?
  no — with an empty slot, collect the wet sponj back. Fully automated spill response.
* **Furnace parity note → feature** — the "only smelt one wet lava sponj at a time" gotcha in the README
  could become a feature: make wet lava sponjes dry correctly in batches, or show an in-GUI warning.

## Behavior & configuration

* **Configurable radius math** — expose base radius and per-connected-sponj bonus (and a max cap) in
  config, so packs can tune from "kiddie pool" to "drain the ocean monument."
* **Waterlogging awareness** — absorb the water *inside* waterlogged blocks (stairs, slabs, fences) in
  the radius without breaking the blocks themselves.
* **Rain shield (silly, optional)** — placed dry sponjes very slowly become damp during thunderstorms if
  exposed to rain. Default off; pure flavor for players who like consequences.

## Flavor & fun

* **Sponj Golem (stretch)** — build it like a snow golem (sponj + carved pumpkin); it waddles around
  your base and soaks up puddles/rain-placed water, wringing itself out over farmland. Extremely silly.
  Extremely on-brand for a mod named "sponj."
* **Squishy sounds & walk feel** — custom squish sounds and a slight slow-sink step on sponj blocks,
  like a firm mattress.
* **Advancements** — "Spill Response Team" (absorb 1000 blocks of fluid), "Dry Heat" (dry a wet lava
  sponj in the End as the README describes), "Big Gulp" (absorb with a 20+ connected sponj cluster).

## Compatibility

* **Modded fluid tags** — drive what each sponj absorbs from fluid tags so other mods' fluids can opt
  in via datapack.
