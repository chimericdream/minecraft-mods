# Test Plan — Hopper X-Treme

Hopper X-Treme is the suite's **reference implementation for automated testing** — it already ships
four Fabric GameTest classes. This plan documents what exists, the gaps, and the manual checklist.
Feature surface: tiered hoppers (honeyed 20t / copper 8t-redstone-immune / golden 4t / diamond 2t /
netherite 1t), multi-hoppers (multiple output sides), huppers (push up) and multi-huppers, glazed
variants (eject instead of store), built-in item filtering (Hopper Item Filter item, include/exclude
via `HopperXtremeFilterModeComponent`, filter slot = index 5 with `getContainerSize()` reporting 5 —
see the filter-slot convention memory/docs), the Wrench, deprecated `filtered_*` block conversion
(`HopperDeprecation`), and a `RedstoneWireBlockMixin`.

## Existing automated coverage (keep green)

All in `fabric/src/main/java/com/chimericdream/hopperxtreme/fabric/test/`, registered under
`fabric-gametest` in `fabric.mod.json`, structures in
`common/src/main/resources/data/hopperxtreme/gametest/structure/`:

* **`TransferSpeedTest`** — honeyed/copper/golden/diamond/netherite: 4 items move in the expected
  tick window (copper variant also proves redstone immunity by never unlocking it).
* **`SixSlotTransferTest`** — golden/diamond/netherite: all 5 storage slots drain in order into the
  chest below; filter slot untouched.
* **`PreventFilterExtractionTest`** — vanilla + tiered hoppers below a filtered hopper cannot steal
  the installed filter item.
* **`DeprecatedBlockConversionTest`** — deprecated filtered golden hopper converts on first tick to
  the canonical block, preserving inventory, filter, and dropping nothing.

## Manual test plan

Fabric full pass, NeoForge full pass (no NeoForge gametests exist — manual is currently the only
NeoForge coverage).

1. **Tier speeds feel right** — chest→hopper→chest column per tier; compare stopwatch counts to the
   cooldown table in the README.
2. **Multi-hoppers** — configure multiple outputs (wrench?); verify items round-robin/duplicate-free
   across all connected destinations; per-tier variants respect their speeds.
3. **Huppers** — items move upward into a chest above; multi-huppers push up + sideways; check
   pulling: does a hupper pull from below or above? Pin the intended source side and verify.
4. **Glazed variants** — items entering a glazed hopper eject from the output face as item entities
   (dropper-style); honey-glazed keeps the 20t cadence; glazed boxes never retain inventory.
5. **Filtering UX** — install a Hopper Item Filter via the GUI; include mode passes only listed
   items; exclude mode blocks them; toggle modes with the Wrench/GUI; filter persists through break
   → re-place (check the dropped item's components) and world reload.
6. **Filter + machine interactions** — filtered hopper under a furnace/brewing stand; filtered
   hupper; filtered glazed (there are dedicated screens for filtered glazed — exercise each screen
   class); comparator reads: `getContainerSize()` is 5, so comparators should read only storage
   slots — verify signal strength math.
7. **Redstone** — locking works on every non-copper tier (and never on copper); the
   `RedstoneWireBlockMixin`: verify wire connects/points at hoppers as intended.
8. **Wrench** — every advertised wrench interaction on hoppers; wrench on non-mod blocks does
   nothing harmful.
9. **Creative tabs, recipes, Patchouli book** — all four tabs populated; every block craftable per
   recipes (datagen: `XtremeHopperRecipeGenerator`); loot tables drop the right block with
   components intact.
10. **Deprecated block sweep** — place each remaining `filtered_*` block variant (not just golden);
    all convert correctly. Loading a pre-4.0 world save with filtered hoppers is the real-world
    case — keep one such save as a fixture if available.

## Recommended additional automated tests (gaps)

### GameTests — variants with zero current coverage

* **`HupperTransferTest`** — mirror `TransferSpeedTest` upside down: bottom chest → hupper →
  top chest, per tier. New structures `hopperxtreme:hupper/<tier>`.
* **`MultiHopperDistributionTest`** — one multi-hopper with 2–3 output sides, N items in; assert
  the exact expected distribution across destination chests and zero loss/duplication. Include a
  blocked-destination case (one chest full): items must flow to remaining outputs, none vanish.
* **`GlazedEjectionTest`** — glazed hopper fed from a chest: assert item entities appear in front of
  the output face (`assertItemEntityCountIs`) and the glazed hopper's own inventory is empty
  afterward; honey-glazed variant with timing assertion.
* **`FilterIncludeExcludeTest`** — filtered hopper, filter configured programmatically via
  `HopperXtremeFilterModeComponent` + filter inventory: feed a mixed stream (2 allowed, 2 blocked
  item types); include mode → only allowed in bottom chest, blocked remain above; exclude mode →
  inverse. This is the mod's flagship feature and currently has **no filtering-logic test** (only
  filter-extraction protection).
* **`FilterPersistenceTest`** — configure filter, `destroyBlock`, assert the dropped stack carries
  the filter component; re-place and assert behavior resumes. (Deprecation test proves BE survival;
  this proves the item round-trip.)
* **`ComparatorSignalTest`** — comparator against a hopper with known fill; assert signal uses the
  5-slot container size convention.
* **`RedstoneLockTest`** — powered tiered hopper does not transfer; unpowered resumes; copper is
  immune (partially covered by the copper speed test; make the lock case explicit per tier).
* **`WrenchInteractionTest`** — wrench-use on hoppers toggles whatever it toggles (mode/outputs);
  assert state change + no-op on vanilla blocks.
* **`DeprecatedBlockConversionTest` extension** — parameterize across *all* deprecated variants,
  not just filtered golden.

### Screen-handler tests

* **`FilterScreenHandlerTest`** — open `FilteredHopperScreenHandler` server-side with a mock player;
  assert: filter slot rejects non-filter items (`FilterSlot`), storage slots reject filter items if
  that's the convention (`NonFilterSlot`), shift-click routing puts filters in slot 5 and nothing
  else there, and the filter can't be shift-clicked out while... (match
  `PreventFilterExtractionTest`'s protection at the GUI layer).

### Unit tests

* Filter matching logic (component + item list → pass/block decision), if extractable as a pure
  function: table-test include/exclude × empty filter × NBT/component-sensitive stacks.

## ChimericLib helper opportunities

The existing tests hand-roll patterns that the planned ChimericLib GameTest kit should absorb:

* **Redstone start-gate fixture** — every timing test does "destroy redstone block → wait N ticks →
  re-place to freeze" (`TransferSpeedTest`, `SixSlotTransferTest`). Extract as
  `RedstoneGate.open(helper, pos)` / `close(...)`.
* **Container fill/assert helpers** — `fillContainer(helper, pos, stacks...)` and
  `assertContainerExactly(helper, pos, stacks...)` replace the six-fold slot-by-slot `if` blocks in
  `SixSlotTransferTest`.
* **Cooldown math helper** — `ticksForNItems(tier, n)` so per-tier tests share one timing source of
  truth.
* **Typed BE getter with failure message** — `getBlockEntity(helper, pos, Class)` variants already
  exist per-test-class as private helpers; hoist them.
* **`assertNothingDropped(helper, box)`** — the deprecation test's "not dropped (no duplication)"
  assertion, generalized.

## Open questions

* Should NeoForge gametest wiring be added (NeoForge has its own `@GameTest` support) so the
  existing common-logic tests run on both loaders, or does manual NeoForge coverage suffice for now?
* What is the multi-hopper's intended distribution order (round-robin vs. first-available)? The
  distribution test must encode it.
