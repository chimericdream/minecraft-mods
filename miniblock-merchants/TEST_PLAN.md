# Test Plan — Miniblock Merchants

Miniblock Merchants adds 25 villager professions trading ~1000 miniblocks (player heads with custom
textures). Moving parts: `VillagerConversionItem` (25 items, each a textured head-profile item;
right-click an **unemployed adult** villager to convert it — `MMVillagerEntityMixin`),
`ModProfessions` + 25 `*Trades` classes (trade tables), conversion-item drops via loot injection
(`MMLootTables` on Fabric, `VillagerConversionItemsLootModifier` on NeoForge) with **per-item
configurable drop chances** (`MiniblockMerchantsConfig`), miniblock texture data
(`MiniblockTextures`, `SkullTextureData`, `MMPlayerHeadItemMixin`), datapack-migration
(auto-convert villagers from the Miniblock Traders datapack), advancements (`MMAdvancements`),
stats (`ModStats`), and zombie-villager profession retention (`MMZombieVillagerEntityMixin`).

## Test conventions

Follow the Hopper X-Treme pattern: GameTest classes in
`fabric/src/main/java/com/chimericdream/miniblockmerchants/fabric/test/`, registered under the
`fabric-gametest` entrypoint, structures under
`common/src/main/resources/data/miniblockmerchants/gametest/structure/`. With 25 professions ×
5 trade levels, the win here is **loop-driven tests over registries and trade tables**, not
hand-written per-profession tests.

## Manual test plan

Fabric full pass, NeoForge full pass (loot injection differs per loader). Setup: creative world,
village or spawned villagers, conversion items from the creative tab.

1. **Conversion happy path** — right-click an unemployed adult villager with a conversion item:
   profession changes (skin/outfit updates per the custom skins), item consumed, conversion
   sound/feedback, trades available immediately. Spot-check ~5 professions, including one from each
   drop-source category.
2. **Conversion guards** — item must NOT convert: employed villagers, babies, nitwits (pin intent),
   zombie villagers, wandering traders. Item must not be consumed on a failed attempt.
3. **Trades** — for a few professions: trade through all 5 levels; XP bars advance; restocking at a
   job site... note these villagers have no vanilla workstation — verify how restocking works for
   custom professions (this is a classic custom-profession bug area). Prices, stock counts, and
   miniblock outputs match the `*Trades` tables.
4. **Miniblocks** — purchased heads place as miniblocks with correct textures; survive break/
   re-place (texture from profile component persists); stack correctly; work in item frames.
5. **Drop sources** — verify a sample of each acquisition path with configured chances cranked to
   100% where possible: fishing treasure (Crystal Phial et al.), leaf decay/harvest (Cultivated
   Sapling from spruce, Enchanted Red Delicious from oak), chest loot (Igloo → Galilean Spyglass,
   Jungle Temple → Prismatic Honeycomb...), block breaking (Radiating Redstone from redstone ore,
   Sculpting Clay from clay), crop harvest (Shimmering Wheat, Overgrown Carrot), and mob death
   (Stabilized Explosion from creepers).
6. **Config** — change a drop chance; confirm persistence and effect (0% never drops, 100% always).
7. **Zombie cure round-trip** — zombify a converted merchant, cure it: profession and trade level
   must survive (`MMZombieVillagerEntityMixin`).
8. **Datapack migration** — world containing Miniblock Traders datapack villagers: on load with the
   mod, they convert to the matching mod profession with trades intact.
9. **Advancements & stats** — conversion triggers its advancement; stats increment.
10. **Persistence** — converted villager survives world reload, chunk unload/reload, and being
    bagged/unbagged by Villager Tweaks' bundle (cross-mod check worth one manual pass).

## Recommended automated tests

### GameTests — conversion

* **`conversionItemConvertsUnemployedAdult`** — parameterized over all 25 items (loop over
  `ModItems`/profession pairs): spawn unemployed adult villager, mock-player right-click with the
  item, assert profession == expected, item count decremented, villager offers non-empty.
* **`conversionRefusedFor<Employed|Baby|ZombieVillager>`** — assert no profession change AND item
  not consumed.
* **`conversionAdvancementGranted`** — after a conversion, assert the mock player has the
  advancement/criterion.

### GameTests / server tests — trades & professions (loop-driven)

* **`allProfessionsRegistered`** — 25 professions resolve in the villager profession registry.
* **`tradeTablesComplete`** — for each profession: levels 1–5 all have ≥ 1 offer; every offer's
  output stack is valid (non-empty, registered item); every head output carries a profile/texture
  component that resolves in `MiniblockTextures`; costs are within sane bounds (e.g. 1–64
  emeralds). This single loop test guards ~1000 data points.
* **`restockWorks`** — one converted villager; exhaust an offer; run the villager's work schedule
  (or force `restock()`); assert stock resets. This encodes the custom-workstation restocking
  answer from manual test 3.
* **`zombieCureKeepsProfession`** — convert villager → set health low, zombie-convert via code →
  cure via code (or directly construct a zombie villager with the profession and finish conversion)
  → assert profession + offers retained.

### GameTests — drop sources & config

* **`lootTablesInjectConversionItems`** — resolve each targeted vanilla table (igloo, jungle
  temple, desert pyramid, mineshaft, stronghold library, village chests, fishing treasure...) and
  assert the injected entry exists (structural, not probabilistic). Loop from a single
  source-of-truth map {item → tableId} kept next to the test.
* **`dropChanceConfigRespected`** — for one block-break source (e.g. Sculpting Clay from clay):
  set chance to 1.0, break clay 10× with a mock player, assert ≥ 1 drop each time; set 0.0, assert
  none in 10 breaks. Avoid probabilistic mid-values.
* **`fishingTreasureContainsItems`** — resolve the fishing treasure table and assert entries
  (structural).

### Unit tests

* **`SkullTextureData`/`DataUtil.makeGameProfile`** — given an id, the produced GameProfile has a
  well-formed base64 texture payload; decode and sanity-check the JSON inside (URL present). A loop
  over all texture entries catches paste errors in the 1000-texture data set — likely the highest
  bug-per-line-of-test ratio available in this mod.
* **Config round-trip** — serialize/deserialize `MiniblockMerchantsConfig`; all 25 chance fields
  survive.

## ChimericLib helper opportunities

* **Villager fixture builder** — shared with Villager Tweaks (see that plan): spawn with
  age/profession/employment controlled; this mod needs "unemployed adult" constantly.
* **Mock-player entity-interaction helper** — right-click entity with item; shared with Villager
  Tweaks bagging.
* **Loot-table structural assertions + seeded rolling** — shared with Athenaeum, Archaeology
  Tweaks, Shulker Stuff; this mod has the longest list of injection targets, so it's the best
  stress case for the helper's API.
* **Registry loop-test scaffolding** — "for every entry of registry R owned by mod M, assert
  predicate P with readable per-entry failures." Used here for professions/trades/textures, in
  Minekea for its hundreds of blocks, and in every content mod.
* **Villager-profession declarative builder** (already on ChimericLib's POTENTIAL_FEATURES list) —
  when extracted, its own tests move to chimeric-lib and this mod keeps only data-level tests.

## Open questions

* Nitwits: convertible or not? Manual test 2 / the refusal GameTest need the intended rule.
* How do custom professions restock without a vanilla workstation POI? The answer shapes
  `restockWorks` and is worth documenting in the README either way.
