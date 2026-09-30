# GameTest harness helpers

> Source: legacy backlog, [§ Developer & testing tools](../brainstorms/2026-08-27-legacy/potential-features.md#developer--testing-tools) · Status: **Exploring**

## Description

Shared fixtures for spinning up test worlds with suite blocks placed,
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
  minekea wrench/painter, hopper-xtreme wrench)* — **partially done**: `testkit.gametest.GameTestPlayers`
  (added for log-all-the-things's lava-logging tests) has the two lowest-level building blocks,
  `makeFacingPlayer` (positioned + oriented mock player) and `useItem` (calls `Item#use`, applies the
  result to the held item) — covers general-`use()` items like buckets, which
  `GameTestHelper#useBlock` can't reach at all. Still open: a `useOn`-specific convenience (block
  face targeting), use-on-entity, shift-click, and hold-for-N-ticks.
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

## Decisions

_None yet._

## Related

- [`docs/TESTING.md`](../../../docs/TESTING.md) — how the GameTest harness is wired today.
- Each mod's `TEST_PLAN.md` — where the consumers listed above were identified.

## Open questions

_None yet._
