# Design notes

Non-obvious design decisions and invariants in specific mods. Read the relevant section before changing the
code it describes.

## Hopper X-Treme: filter-slot convention

Every hopper/hupper is filter-capable (`withFilter` is derived from the block, not NBT). Invariants:

- The filter slot is the **last** inventory slot: index 5 for the 6-slot families (standard, hupper,
  multi-hopper, multi-hupper: 5 storage + 1 filter) and index 1 for the glazed families (1 storage + 1 filter).
- `getContainerSize()` returns `inventory.size() - 1` when `withFilter`, hiding the filter slot from the
  vanilla `Container` API and sided automation. These block entities are not `WorldlyContainer`, so available
  slots derive from `getContainerSize()`.
- The loop bound `i < getContainerSize()` already excludes the filter slot, so push/transfer loops must
  **not** also `continue` on `i == getContainerSize() - 1`. That skipped a real storage slot (fixed during the
  deprecation work; multi-hupper also used to fail to hide its filter slot).

A filter-capable hopper with an empty filter slot behaves like a plain hopper
(`HopperItemFilterItem.matchesFilter(EMPTY, stack)` returns true). Covered by the `SixSlotTransferTest` and
`PreventFilterExtractionTest` GameTests. For deprecating or renaming blocks without breaking worlds, see
[BLOCK-MIGRATION.md](BLOCK-MIGRATION.md).

## Minekea: glass jar container

`GlassJarBlockEntity` stores one item type as an active stack (slot 0) plus `fullItemStacks`, a count of extra
full stacks compressed into a reserve. Item identity lives only on the active stack, so the active stack stays
non-empty whenever the jar holds items.

- **Two-slot `Container`.** `getContainerSize() == 2`. Slot 0 (`STORAGE_SLOT`) is the real active stack: what
  renders (`getStoredItem`) and what a hopper drains. Slot 1 (`OVERFLOW_SLOT`) is a virtual, always-empty
  input: `getItem(1)` is EMPTY, and `removeItem` / `removeItemNoUpdate` on it yield EMPTY.
- **Why two slots.** A hopper (and the Hopper X-Treme fork) calls `isInventoryFull` *before* `canPlaceItem`,
  and a one-slot container reads as full the moment it holds 64, so the hopper gave up before it could
  overflow into the reserve. Slot 1 reading "not full" keeps the hopper pushing. `setItem(1, x)` routes `x`
  into the reserve via `tryInsert`. `canPlaceItem(1, x)` requires a **full fit** (`remainingItemCapacity`)
  because `setItem` cannot return a leftover and a partial accept would delete the excess.
- **Phantom-full slot 1.** `getItem(1)` reads EMPTY while the jar has capacity, but a full phantom stack once
  item storage is completely full (`isItemStorageFull`). A hopper pushing at a full jar then trips
  `isInventoryFull`'s cheap early-out instead of re-probing `canPlaceItem` (tag lookup plus capacity math)
  every cooldown. The phantom is a full stack (never a valid merge target) and `canTakeItem` is false for
  slot 1, so it can't be pulled or duplicated. It doesn't help the fluid/mob case, where slot 0 is legitimately
  empty.
- **Extraction cascade and putback guard.** `removeItem(0, ...)` drains the active slot, and when it empties
  while the reserve is non-empty it refills the active slot from the reserve, so automation drains the whole
  jar. The vanilla extract path puts the last item back via `setItem(0, original)` when the slot held exactly 1
  and the destination was full; `pendingCascadePutback` makes that putback hand the borrowed reserve stack back
  instead of deleting it.
- Zeroing the active slot inside `canPlaceItem` was tried and rejected: it worked for pulls but not pushes,
  because `isInventoryFull` gates the push before `canPlaceItem` runs.

Tests: `GlassJarContainerGameTest` in minekea's gametest source set (see [TESTING.md](TESTING.md)).

## Villager Tweaks: trade preview

Vanilla facts (26.2): a villager's offers are `null` until the first `getOffers()` (first player interaction,
not job acquisition; a profession change only nulls them). `updateTrades` rolls only the current level's
`TradeSet` via `LootContext.Builder.create(randomSequence)`, where the sequence is
`trade_set/<profession>/level_N` from `server.getRandomSequence`. That sequence is world-shared and stateful
(believed, not verified in source), so a villager's level-4 trades depend on how many same-profession villagers
rolled before it. Trades are not deterministic per villager.

Design:

- `VTVillagerTradePreviewMixin` pre-rolls the levels above the current one at `startTrading` (this covers
  villagers that predate the mod), stores them in `vt$futureOffers` / NBT `VTFutureTrades`, and uses them in
  `updateTrades` at level-up. Only missing future levels are filled for existing villagers.
- The client receives locked offers in a `LockedTradesPayload` sent after vanilla's offers packet and appends
  them to its `MerchantMenu` offers. `LockableOffer` is implemented onto `MerchantOffer` by `VTTradeOfferMixin`
  (which also makes locked offers fail `satisfiedBy`). `VTMerchantScreenMixin` draws the grey overlay, replaces
  the tooltip and ignores clicks. In 26.2 `setTooltipForNextFrame` keeps the first tooltip unless
  `replaceExisting` is true, so use the 8-arg overload.
- **Live level-up.** 26.2 only ticks `updateMerchantTimer` while `!isTrading()`, so level-up would wait for the
  screen to close. The mixin runs the countdown during a trade, then `resetSpecialPrices` +
  `updateSpecialPrices` + vanilla `resendOffersToTradingPlayer`, and re-sends the locked payload on every
  resend (restock included).

Later Minecraft versions may add this natively. The screen rendering was checked in-game by the maintainer, not
by the headless smoke test.

## chimeric-lib: armor trim datagen

`com.chimericdream.lib.trims` (in `chimeric-lib/common`) automates the parts of a custom armor-trim material
that Minecraft's datagen supports. `trim_material` JSON is an ordinary dynamic registry, so it is fully
generated. The `assets/minecraft/atlases/armor_trims.json` / `items.json` overrides are not covered by any
Fabric or vanilla datagen API (vanilla's `AtlasProvider` and `ItemModelGenerators.TRIM_MATERIAL_MODELS` are
internal and hardcoded to vanilla's materials), so chimeric-lib assembles them itself. Palette PNGs must still
be drawn by hand.

- `TrimMaterialConfig`: record of namespace, id, color, palette texture path and display name.
- `TrimMaterialRegistryHelper.bootstrap(BootstrapContext<TrimMaterial>, List<TrimMaterialConfig>)`: used as-is
  by both loaders (Fabric's `buildRegistry`, NeoForge's `RegistrySetBuilder`).
- `ArmorTrimAtlasProvider implements DataProvider`: holds the vanilla 18-pattern / 4-item-texture lists
  (hand-maintained; update them if a future Minecraft version changes vanilla's trim patterns) and writes both
  atlas overrides.

Reference consumer: `effective-gear`'s ender pearl trim (`Trims.MATERIALS`). A mod adding a trim copies its
wiring: a thin `fabric/.../worldgen/<Mod>TrimProvider extends FabricDynamicRegistryProvider` (unavoidable on
Fabric, see the datagen notes in [MC-26.2-NOTES.md](MC-26.2-NOTES.md)); each loader's `ModDataGenerator`
registering `TrimMaterialRegistryHelper.bootstrap` and `ArmorTrimAtlasProvider`; each loader's lang provider
looping the material list; the `"fabric-datagen"` entrypoint in `fabric.mod.json`; `runs { datagen {...} }`
(fabric) / `runs { data {...} }` (neoforge) blocks in the platform `build.gradle`s; and
`sourceSets { main { resources.srcDir 'src/main/generated' } }` in `common/build.gradle`. `artificial-heart`
has the template datagen wiring. Generated output is committed under `<mod>/common/src/main/generated/`.
