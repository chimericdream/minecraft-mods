# Potential Features — Flat Bedrock

Brainstormed, thematically appropriate feature ideas. Nothing here is committed or implemented; these are
starting points for future planning.

The mod's identity: a **predictable floor (and roof)**. It should stay a worldgen tweak — small, boring in
the best way, and server-side only.

## Layer shape options

* **Configurable thickness** — choose 1–5 flat bedrock layers instead of always one, for players who want
  a flat floor but not a *thin* one.
* **Floor/roof independent toggles** — flatten the floor but keep the vanilla nether roof jagged (or vice
  versa), as separate config options per surface.
* **"No roof" option** — remove the nether ceiling bedrock entirely for packs that embrace roof access,
  rather than merely flattening it.
* **Replacement layer** — optionally back the single bedrock layer with a configurable filler (deepslate,
  obsidian, blackstone) where the vanilla noise layers used to be, so the transition band doesn't become
  pure air/ore-space that vanilla never generates.

## Dimension coverage

* **Per-dimension config** — explicit settings for overworld floor, nether floor, nether roof, and a
  wildcard rule for **modded dimensions** that use vanilla-style bedrock surface rules.
* **Datapack-driven rules** — expose the surface-rule adjustment as data, so packs can target custom
  dimension types without waiting on mod updates.

## Existing worlds

* **Retro-flatten command** — an explicit, opt-in `/flatbedrock retrogen` (per-region or radius-limited,
  with a confirmation step and a backup warning) for worlds that installed the mod late. The README's
  "only new chunks" note is the mod's most predictable support question — this answers it.
* **Chunk scan report** — a dry-run companion command that reports how many already-generated chunks have
  non-flat bedrock, so admins know the blast radius before committing.

## Compatibility niceties

* **Ore-above-bedrock safety check** — verify (and if needed, adjust) interactions with mods/datapacks
  that place features in the former bedrock band (Y -60 to -64), documenting the guaranteed-safe Y range.
* **Superflat & amplified sanity** — confirm the surface-rule patch behaves under non-default world
  presets and document it in the README.

## Non-goals worth writing down

* No new blocks, no client requirement, no bedrock-breaking mechanics — "flat" is the whole brand.
