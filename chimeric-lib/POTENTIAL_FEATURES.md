# Potential Features — ChimericLib

Brainstormed, thematically appropriate feature ideas. Nothing here is committed or implemented; these are
starting points for future planning.

ChimericLib is **developer-facing plumbing**, so these suggestions are library features: shared systems
that multiple mods in the suite would otherwise each reinvent. Several are extracted from patterns that
already exist in individual mods.

## Extract & generalize existing suite patterns

* **Block migration framework** — a general "deprecated block converts itself on placement/load" system
  (the pattern Hopper X-Treme uses for its filtered hoppers), so any mod can rename or merge blocks across
  versions without DataFixerUpper: old ID → new block + component/NBT mapping, contents preserved.
* **Shared Wrench API** — Minekea and Hopper X-Treme both ship wrench items. A common wrench capability
  (`WrenchableBlock` interface + a default wrench item) would let one wrench work across the whole suite
  and keep interaction behavior consistent.
* **Item Filter API** — Hopper X-Treme's include/exclude filter logic as a reusable component, so Shulker
  Stuff's Vacuum/Void enchantments (and future mods) can offer the same filtering UX with the same UI
  widgets.
* **Villager profession helper** — Miniblock Merchants' "convert villager with item, register profession,
  build trade tables" flow as a declarative builder.

## Registration & data generation

* **Block family generator** — declare a base block once and auto-generate stairs, slabs, walls, vertical
  variants, plus their models, blockstates, recipes, loot tables, and tags (Minekea's bread and butter,
  useful to every content mod).
* **Data-driven creative tab builder** — ordering, icons, and per-mod tab conventions in one helper.
* **Tag-first behavior registry** — register behavior against block/item tags rather than concrete
  registries, so datapacks can opt blocks into suite mechanics (e.g. "any block in this tag is brushable").

## Cross-cutting infrastructure

* **Config sync layer** — a small channel that syncs server-authoritative YACL config values to clients
  on join, with a consistent "server value overrides local" indicator in config screens. Nearly every mod
  in the suite with server-side config wants this.
* **Networking wrapper** — a thin, versioned packet abstraction over Fabric/NeoForge networking so common
  code never touches loader-specific channels directly.
* **Component/NBT compatibility helpers** — utilities for reading old NBT and new data components through
  one API, easing multi-version maintenance.

## Developer & testing tools

* **GameTest harness helpers** — shared fixtures for spinning up test worlds with suite blocks placed,
  hopper-line assertions, inventory diffing, and "place → break → assert no updates" style checks.
* **Registry dump command** — `/chimericlib dump <blocks|items|tags>` writing a report to disk, for
  debugging cross-mod registration issues.
* **Debug overlay hooks** — an opt-in F3-style overlay section where suite mods can publish debug lines
  (hopper cooldowns, filter state, seat entity positions).

## Small shared conveniences

* **SimpleSeatEntity polish** — configurable seat height offsets, dismount position safety checks, and
  support for multi-seat blocks (benches/couches for Minekea).
* **Sound & particle helpers** — one-liners for "play this sound/particles at block, both sides handled."
* **Patchouli book datagen** — generate book structure from registered content so in-game docs stay in
  sync with registries automatically (the repo already has a Patchouli update script; this would move the
  guarantee into datagen).
