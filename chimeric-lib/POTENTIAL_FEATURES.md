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
  The per-mod `TEST_PLAN.md` files (July 2026) identified the concrete helpers below; each name lists
  its known consumers so the highest-leverage ones can be built first.
  * **Container fill/assert helpers** — `fillContainer(helper, pos, stacks...)` and
    `assertContainerExactly(helper, pos, stacks...)`, replacing the slot-by-slot `if` blocks in
    Hopper X-Treme's `SixSlotTransferTest`. *(hopper-xtreme, minekea crates/barrels/jars,
    shulker-stuff, jdcrafte)*
  * **Menu/screen-handler test harness** — open a screen handler server-side with a mock player,
    manipulate/shift-click slots, assert slot validation and output. Probably the single
    highest-leverage helper in the suite. *(banner-tweaks loom, shulker-stuff dye station,
    hopper-xtreme filter screens, minekea crates, chimeric-lib's own screens)*
  * **Config override fixture** — `withConfig(handler, mutator, testBody)` that snapshots a YACL
    config, mutates it for the test, and guarantees restoration. *(banner-tweaks,
    beacon-conduit-tweaks, villager-tweaks, shulker-stuff, hopper-xtreme, miniblock-merchants)*
  * **Loot-table test kit** — `assertLootTableExists/Contains(server, id, predicate)` structural
    assertions plus `rollLootTable(server, id, seed, n)` seeded sampling. *(athenaeum,
    miniblock-merchants, archaeology-tweaks, shulker-stuff, pannotia-companion)*
  * **Registry loop-test scaffolding** — "for every entry of registry R in namespace M, assert
    predicate P" with per-entry failure reporting: every block has an item / loot table / recipe /
    creative tab. *(minekea ~230 blocks, miniblock-merchants ~1000 trades, cobblicious,
    artificial-heart, sponj)*
  * **Mock-player interaction wrappers** — "use item I on block face F", "use item I on entity E",
    "shift-right-click", and "hold-use for N ticks" (brushing). *(artificial-heart axe/shears,
    archaeology-tweaks brush, villager-tweaks bundle, miniblock-merchants conversion items,
    minekea wrench/painter, hopper-xtreme wrench)*
  * **Villager fixture builder** — `spawnVillager(helper, pos).profession(X).level(3).offers(...)`
    with age/employment control; plus gossip/reputation assertion helpers. *(villager-tweaks,
    miniblock-merchants)*
  * **Block-update detector fixture** — observer + lamp watching a position, with
    `assertNeighborUpdated` / `assertNoNeighborUpdate`, self-validated by negative controls.
    *(houdini-block — its entire feature, minekea beam toggling, hopper-xtreme redstone mixin)*
  * **Redstone start-gate fixture** — the "destroy redstone block → run → re-place to freeze"
    pattern from Hopper X-Treme's timing tests, as `RedstoneGate.open/close(helper, pos)`.
    *(hopper-xtreme, any timed machine)*
  * **Inventory diffing** — snapshot player + container inventories, run an action, assert the
    exact diff; includes `assertNothingDropped(helper, box)`. *(shulker-stuff vacuum/void/refill,
    hopper-xtreme deprecation "no dupes" check, minekea)*
  * **Entity-absence watcher** — `assertNoEntitySpawns(helper, type, ticks)` for spawn-suppression
    promises. *(artificial-heart "no creaking", villager-tweaks zombie conversion controls)*
  * **Fluid-region helpers** — fill/count/assert-absent fluid in a box.
    *(sponj absorption radius/capacity, minekea jars)*
  * **Tool-use conversion assertion** — "use tool on block ⇒ block became B with properties P
    preserved, tool damaged by N". *(artificial-heart, hopper-xtreme deprecation-adjacent,
    minekea block painter)*
  * **BE NBT round-trip helper** — `assertSurvivesReload(blockEntity)` write/read cycle.
    *(every block-entity mod: hopper-xtreme, shulker-stuff, minekea, archaeology-tweaks)*
  * **Networking round-trip helper** — encode/decode assertions for custom payloads to pin wire
    formats. *(banner-tweaks layer-limit sync, minekea network package)*
  * **Reload-idempotence harness** — "reload datapacks twice, assert registries/loot stable".
    *(athenaeum, chimeric-lib LootTableModifier, miniblock-merchants)*
  * **Test-datapack fixture conventions** — a documented way to ship datapack fixtures in the
    fabric test source set and assert on their load results. *(athenaeum, archaeology-tweaks
    loot overrides, pannotia-companion)*
  * **Mock-player-at-distance status-effect assertion** — spawn player N blocks away, assert
    effect present/absent after M ticks. *(beacon-conduit-tweaks)*
  * **Chunk-scan / dimension helpers** — generate a far-away or other-dimension chunk and run
    per-column layer assertions. *(flat-bedrock, future world-gen features)*
  * **Time-of-day and difficulty fixtures** — safely force/restore day-night or difficulty within
    a test batch. *(artificial-heart eyeblossoms, villager-tweaks zombie conversion)*
  * **Furnace fixture** — preload fuel/input, fast-forward burn state, assert slots.
    *(sponj wet lava sponj fuel, future fuel items)*
  * **Crop test helpers** — bonemeal/random-tick a crop to stage X, assert drops per stage.
    *(minekea warped wart, jdcrafte's planned crops)*
  * **JUnit wiring blueprint** — the canonical Gradle `test` source set + `fabric-loader-junit`
    setup for Architectury projects, pioneered here and copied by other mods.
    *(enchantment-numbers-fix RomanNumeralUtil, all pure-helper unit tests)*
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
