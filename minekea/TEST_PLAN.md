# Test Plan — Minekea

Minekea is the suite's largest content mod (~230 classes): furniture (tables, chairs, foot stools,
armoires, shelves, display cases, bookshelves + secret doors/trapdoors, shutters, pillows), storage
(crates with double-chest capacity, variant barrels, glass jars with fluid support), building blocks
(vertical stairs/slabs, beams, covers, framed/dyed/compressed 1x–9x blocks, basalt bricks, walls),
decorations (lamps, candles, endless end rod), tools (wrench, random block placer, block painter),
a warped wart crop, and network packets. Testing 100% of this by hand-written cases is not
realistic; the strategy is **(a) loop-driven registry/data tests that scale across whole block
families, plus (b) targeted behavioral GameTests for each *mechanic*** (a crate is one mechanic,
regardless of 10 wood types).

## Test conventions

Follow the Hopper X-Treme pattern: GameTest classes in
`fabric/src/main/java/com/chimericdream/minekea/fabric/test/`, registered under the
`fabric-gametest` entrypoint in `fabric.mod.json`, structures under
`common/src/main/resources/data/minekea/gametest/structure/`. Group test classes by family
(`CrateTests`, `SeatTests`, `BeamTests`, ...) mirroring the `block/*` package layout. Datagen
(`FABRIC: data`) produces models/recipes/loot — the completeness tests below verify its output made
it into the runtime registries.

## Manual test plan

Too large for a full per-block pass; do a **per-family** pass on Fabric plus a NeoForge smoke pass
(client init, one block per family). Suggested checklist:

1. **Crates** — open GUI, fill to double-chest capacity, hopper in/out, break: contents behavior
   (drop vs retain — pin intent), label/appearance per wood type, comparator signal.
2. **Barrels** — parity with vanilla barrels per wood type; open/close animation and sounds.
3. **Jars** — store items/food/dusts; fluid fill/empty (bucket interactions), fluid rendering,
   stacking rules, pick-block.
4. **Shelves & display cases** — insert/remove items, displayed item renders (rotation/count),
   break drops contents.
5. **Seating** — chairs/stools/pillows: sit (right-click), dismount lands safely, seat entity
   despawns when block broken while seated, no phantom seat entities accumulate (check
   `/kill @e[type=...]` count after repeated use — uses ChimericLib's `SimpleSeatEntity`).
6. **Secret doors/trapdoors** — camouflage rendering matches neighboring bookshelves, open/close by
   hand and redstone.
7. **Beams** — placement axis, connection shapes, wrench toggles connections on/off, collision
   boxes reasonable.
8. **Covers** — attach to block faces, one per face rules, drop on support break.
9. **Vertical stairs/slabs** — placement orientation logic (half/quarter selection by crosshair),
   waterlogging.
10. **Compressed blocks** — craft 1x→9x and decompress back losslessly for a few items; absurd ones
    (nether star) too; burn times if flammable... pin whether compressed flammables are fuel.
11. **Dyed/framed blocks, basalt bricks, lamps, endless end rod** — placement, models, recipes,
    lighting values (lamps emit light; endless end rod chains).
12. **Warped wart crop** — plant on soul-sand-family (pin the valid substrate), growth stages,
    bonemeal, drops at maturity vs immature.
13. **Tools** — wrench (beam connections + whatever else it rotates/toggles), random block placer
    (places a random block from its configured set?), block painter (repaints without breaking —
    verify contents/state preserved on paintable targets).
14. **Creative tabs & Patchouli book** — every family present and organized; book entries match
    (the repo has a patchouli update script — verify output loads).
15. **Network packets** — whatever the two `network` classes carry (likely tool/GUI sync):
    exercise the corresponding feature on a **dedicated server**, not just single-player.

## Recommended automated tests

### Registry/data completeness (loop-driven — the backbone)

One test class, several loops over Minekea's registered content (use the registry helper's block
list or filter registries by namespace):

* **`everyBlockHasItem`** — each block has a corresponding `BlockItem` (except intentionally
  item-less technical blocks — maintain an explicit exclusion list in the test).
* **`everyBlockHasLootTable`** — loot table id resolves; where trivially possible, assert
  self-drop by rolling with a Silk-Touch-free mining context.
* **`everyBlockHasRecipe`** — each craftable block resolves ≥ 1 recipe producing it (exclusion list
  for drop-only/creative-only items).
* **`everyBlockInACreativeTab`** — no orphaned content.
* **`compressedChainsRoundTrip`** — for every compressed family: 9× base = level-1 recipe exists,
  decompress recipe exists, all 9 levels registered, and compress→decompress is lossless by recipe
  math.
* These loops replace thousands of manual checks and instantly cover newly added variants; on
  failure they must report the offending block id, not just "assertion failed".

### Behavioral GameTests (one per mechanic)

* **`crateStoresDoubleChestCapacity`** — fill via code to capacity; assert 54 slots usable; hopper
  feeds in and drains out (reuse Hopper X-Treme's chest→container→chest timing pattern);
  break-and-reopen semantics pinned.
* **`jarFluidRoundTrip`** — bucket water into jar (mock player use), assert fluid state; empty it
  back; item-storage mode excludes fluid mixing.
* **`shelfDisplaysAndDrops`** — insert item via use; `destroyBlock`; `assertItemEntityCountIs` for
  contents + shelf.
* **`seatMountAndDismount`** — mock player uses chair; assert player vehicle is a
  `SimpleSeatEntity` at the right offset; break block; assert player dismounted and **seat entity
  removed** (regression: entity leaks).
* **`secretDoorOpensByRedstone`** — lever toggles the door state.
* **`beamConnectionsToggleWithWrench`** — place two adjacent beams; assert auto-connect state; use
  wrench; assert connection property flipped; assert **no neighbor update storm** (observer
  fixture from the Houdini plan).
* **`coverAttachesPerFace`** — place covers on multiple faces of one block; break support; covers
  pop with drops.
* **`verticalSlabPlacementOrientation`** — `useOn` with hit vectors on each half of a face; assert
  resulting orientation property.
* **`warpedWartGrows`** — crop on its substrate; apply bonemeal / force random ticks; assert stage
  progression and mature drops.
* **`blockPainterPreservesState`** — paint a stairs block; assert shape/facing/waterlogged
  properties carried to the repainted block.
* **`randomPlacerPlacesFromSet`** — trigger placements; assert placed block ∈ configured set.
* **`endlessEndRodChains`** — placement against another end rod continues the chain orientation.

### Screen-handler tests

* Crate (and any other GUI'd container) via the shared menu harness: slot validation, shift-click
  routing, `getContainerSize` conventions, comparator math.

### Unit tests

* Any pure placement-math (vertical slab quadrant selection, beam connection resolution from
  neighbor states) should be extracted into static methods and table-tested — cheaper and more
  exhaustive than GameTest for orientation matrices.

## ChimericLib helper opportunities

Minekea is the volume consumer of nearly every helper proposed elsewhere; specifically flag:

* **Registry loop-test scaffolding** with per-entry failure reporting (shared with Miniblock
  Merchants) — Minekea is the reason it must be fast and well-reported.
* **Menu/screen-handler harness** (crates here; loom in Banner Tweaks; dye station in Shulker
  Stuff; filters in Hopper X-Treme).
* **Seat-entity test helpers** — mount/dismount/leak assertions belong next to `SimpleSeatEntity`
  in chimeric-lib itself, with Minekea consuming them.
* **Tool-use fixture** (wrench/painter/placer) — shared with Artificial Heart, Hopper X-Treme.
* **Block-family completeness assertions** — "for family F, assert stairs/slab/wall/vertical
  variants + recipes exist" pairs with the planned block-family generator; build the assertion
  helper in chimeric-lib so Cobblicious can reuse it later.
* **Crop test helpers** — "advance crop N random ticks / bonemeal until stage X" (warped wart here;
  jdcrafte's future crops).

## Open questions

* Crates on break: drop contents or retain them item-side (shulker-style)? Pin before writing the
  crate tests.
* Which blocks are intentionally recipe-less or item-less (for the exclusion lists)?
* What exactly do the network packets sync (needed to write a wire-format round-trip test like the
  one planned for Banner Tweaks)?
