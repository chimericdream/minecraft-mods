# Test Plan — Archaeology Tweaks

Archaeology Tweaks adds eight "suspicious" brushable terrain variants (clay, dirt, mud, packed mud,
red sand, rooted dirt, soul sand, soul soil), each behaving like vanilla suspicious sand/gravel:
brush to reveal loot-table-driven loot piece by piece. Key classes: per-material block classes,
`ATBrushableBlockEntity`, `BrushableFloatingBlock`/`BrushableFloatingNonFullBlock` (gravity
variants), the `BrushItemMixin`/`AbstractBlockMixin` hooks, and a client-side renderer for the
partially-brushed states.

## Test conventions

Follow the Hopper X-Treme pattern: GameTest classes in
`fabric/src/main/java/com/chimericdream/archtweaks/fabric/test/` (match the mod's actual package,
`com.chimericdream.archtweaks`... verify against `ArchaeologyTweaksMod`'s package before creating),
registered under the `fabric-gametest` entrypoint in `fabric.mod.json`, `.snbt` structures under
`common/src/main/resources/data/archtweaks/gametest/structure/`. Brushing is player-driven, so most
GameTests will use a mock player + programmatic brush usage, or directly drive the block entity's
brushing methods where the mixin allows.

## Manual test plan

Setup: creative world; `/give` each suspicious block and a brush. Fabric full pass, NeoForge smoke
pass.

1. **Brushing happy path (per block)** — for each of the 8 blocks: place, hold right-click with a
   brush, verify (a) progressive "dusting" animation and texture stages render, (b) loot item pops
   out after the vanilla-length brush time, (c) block converts to its base terrain block (clay →
   clay, soul sand → soul sand, etc.) when fully brushed.
2. **Brush interruption** — stop brushing mid-way: block should regress to unbrushed after a moment
   (vanilla behavior), with no dropped loot.
3. **Gravity variants** — suspicious red sand (and any other gravity blocks): break the supporting
   block; it should fall like sand, and — mirroring vanilla — the falling version should degrade to
   plain red sand (or whatever behavior is intended; pin it). Non-full gravity blocks
   (`BrushableFloatingNonFullBlock`) need the same check.
4. **Soul sand specifics** — suspicious soul sand: confirm whether it slows entities like real soul
   sand (block behavior parity) and works under water.
5. **Loot customization** — override one block's loot table with a datapack; verify the brushed loot
   changes accordingly (this is an advertised feature).
6. **Non-brush interactions** — mining with a shovel drops nothing special (or per loot table);
   pistons: vanilla suspicious blocks are destroyed when pushed — verify parity.
7. **Brush durability & enchantments** — brushing consumes durability; Unbreaking/Mending behave.
8. **Client rendering** — `ATBrushableBlockEntityRenderer`: the partially-revealed item renders
   inside the block at the correct dusting stages; check on Fabric and NeoForge (renderer
   registration is per-loader).

## Recommended automated tests

### GameTests — brushing behavior

Structure `archtweaks:brushing/single_block` (a 3×3 platform with one suspicious block).

* **`brushingRevealsLootAndConverts_<block>`** (parameterized across all 8 blocks, like
  Hopper X-Treme's per-tier tests): set the block, seed its `ATBrushableBlockEntity` with a known
  item (the BE should expose the vanilla `item` field — set directly rather than relying on the loot
  roll), then simulate brushing. Options for driving the brush, in order of preference:
  1. call the block entity's brush-progress method directly across ticks (deterministic);
  2. mock player + `player.gameMode().useItemOn(...)` repeatedly with a brush.
  Assert: an item entity of the seeded item appears (`assertItemEntityCountIs`), and the block
  becomes the base terrain block.
* **`brushingInterruptedResets`** — advance brushing partway, stop, wait ~40 ticks, assert dusted
  state property returns to 0 and no loot dropped.
* **`lootTableRolledOncePerBlock`** — brush the same block fully; assert exactly one loot drop (no
  duplication when the conversion and the drop race — a classic bug source).

### GameTests — gravity variants

* **`suspiciousRedSandFalls`** — block on a support over a 3-deep hole; remove support; after
  10 ticks assert the landing position holds the expected block and the original position is air.
  Also assert what lands: suspicious block vs plain red sand (pin intended behavior).
* **`fallingOntoTorchBreaksToItem`** — vanilla falling-block edge: assert the drop matches the loot
  table.
* **`nonFullFloatingVariantSurvives`** — whatever `BrushableFloatingNonFullBlock` exists for (verify
  in code), place unsupported and assert intended float/fall behavior.

### GameTests — loot data

* **`allBlocksHaveBrushingLootTables`** — iterate `ModBlocks`; assert each block's brushing loot
  table ID resolves in the server's reloadable registries (catches typos and missing datagen
  output).
* **`datapackOverrideRespected`** — ship a test-only datapack (in the fabric test source set's
  resources) overriding one block's table to a single known item; brush and assert that item drops.

### Unit tests

* None of substance — the logic is engine-coupled. Keep the effort in GameTests.

## ChimericLib helper opportunities

* **Brushing simulation helper** — "advance brushable BE at pos by N brush strokes" +
  `assertDustedLevel(pos, n)`. Only this mod needs it today, but a "tag-first brushable" ChimericLib
  feature is already floated in its POTENTIAL_FEATURES; the helper belongs beside it.
* **Loot-table resolution assertion** — `assertLootTableExists(server, id)` and
  `rollLootTable(server, id, seed, n)` for datapack-driven mods (shared with Athenaeum, Miniblock
  Merchants).
* **Mock-player item-use loop** — "use item X on pos Y for N ticks" wraps the fiddly
  `useItemOn`/`releaseUsing` dance; reused by Artificial Heart (axe/shears), Villager Tweaks
  (bundle on villager), and Minekea (wrench).
* **Falling-block assertions** — `assertBlockFellTo(helper, from, to, expectedBlock)`; shared with
  Houdini Block's sand tests.

## Open questions

* Intended behavior when a suspicious gravity block falls: stay suspicious (keep loot) or degrade to
  the plain block (vanilla suspicious sand degrades)? Tests must pin one.
* Do pistons/explosions destroy the loot (vanilla: yes)? Worth an explicit decision + regression
  test either way.
