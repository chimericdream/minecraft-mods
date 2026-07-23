# Code Review Plan — July 2026

Full-repo review of the 15 active mods (~42k lines of Java) plus build config. Items are ordered
by priority; each lists the evidence, the proposed fix, and a confidence level. **Confirmed** =
verified by reading the code path end-to-end; **Verify first** = strong suspicion, but runtime
behavior should be confirmed (e.g., with a gametest) before fixing.

Status: **Phase 1 is complete.** Each fix is on its own branch off `main` (not yet merged); Phase 2+
has not been started.

| Item | Status | Branch |
|------|--------|--------|
| 1.1 | ✅ Done (2026-07-21) | `fix/glass-jar-container-contract` |
| 1.2 | ✅ Done (2026-07-22) | `fix/hopper-filter-geometry` |
| 1.3 | ✅ Done (2026-07-22) | `fix/hopper-filter-geometry` |
| 1.4 | ✅ Done (2026-07-22) | `fix/glass-jar-item-cache` |
| 1.5 | ✅ No fix needed — already resolved | — |
| 1.6 | ✅ Done (2026-07-22) | `fix/implemented-inventory-partial-merge` |

---

## Phase 1 — Critical bugs (crash, data loss, client/server desync)

### 1.1 `GlassJarBlockEntity.getItems()` returns `null` → NPE via the Container API — **✅ Done (2026-07-21)**
`minekea/.../entity/block/containers/GlassJarBlockEntity.java:605`

**Resolution:** Backed the jar with a real single-slot `NonNullList` and implemented the container
contract for real (`getItems`, `canPlaceItem`, `removeItem`, `removeItemNoUpdate`, `setItem`,
`clearContent`). `removeItem` cascades a compressed reserve stack down into the active slot so
automation can drain the whole jar; a `pendingCascadePutback` guard makes vanilla's "put the last
item back" extraction path (`setItem` after a one-item `removeItem`) lossless. `canPlaceItem` routes
hopper insertion through `canAcceptItem` so automation can't bypass the jar's rules. Covered by
`GlassJarContainerGameTest` (minekea fabric gametests): direct container-API smoke test, reserve
cascade + putback conservation, and live vanilla hoppers draining/feeding the jar.

The jar implements `ImplementedInventory` (and therefore `Container`) but returns `null` from
`getItems()`. Its own overrides (`isEmpty`, `tryInsert`, `removeStack`) avoid the list, but every
*inherited* default — `getContainerSize()`, `getItem(int)`, `setItem(...)`, `removeItem(...)`,
`clearContent()` — dereferences `getItems()`. Any vanilla hopper, hopper minecart, or other
automation that finds the jar via `blockEntity instanceof Container` will call those and crash the
server.

**Fix:** Don't expose the jar as a `Container` at all (drop `ImplementedInventory`, keep the
`tryInsert`/`removeStack` API as plain methods), or implement the container contract for real over
a 1-slot backing list. Add a gametest: hopper feeding into/out of a jar must not crash.

### 1.2 `NonFilterSlot` reads the wrong slot as the "filter" on the server — **✅ Done (2026-07-22, `fix/hopper-filter-geometry`)**
`hopper-xtreme/.../client/screen/NonFilterSlot.java:16`

**Resolution:** `NonFilterSlot` now takes an explicit `filterSlotIndex` instead of deriving it from
`getContainerSize()`; the filtered handlers pass the fixed index (5 / 1). Covered by
`FilterSlotGeometryTest` (fabric gametest): a real filtered-hopper BE with an ordinary item in the
last storage slot still accepts inserts into the storage slots.

```java
this.container.getItem(this.container.getContainerSize() - 1)
```

Per the block-entity invariant (see `XtremeHopperBlockEntity.getContainerSize()`), a filtered
hopper's `getContainerSize()` returns the *storage-only* count (5, or 1 for glazed) — the filter
slot is `getContainerSize()`, not `getContainerSize() - 1`. On the server (real BE) this reads the
**last storage slot** and treats its contents as the filter:

- If that storage slot holds any ordinary item, `matchesFilter` returns `false` (no CONTAINER
  component) and `mayPlace` rejects everything — the GUI refuses inserts.
- On the client the dummy `SimpleContainer(6)` makes the same expression evaluate to the *correct*
  index, so client prediction and server behavior disagree.

**Fix:** Pass the filter-slot index (or the filter stack supplier) into `NonFilterSlot` explicitly
instead of deriving it from `getContainerSize()`. Extend `PreventFilterExtractionTest` (or add a
menu-level test) to cover GUI insertion with a full last storage slot.

### 1.3 Filtered screen-handler `quickMoveStack` boundary is wrong on the client — **✅ Done (2026-07-22, `fix/hopper-filter-geometry`)**
`hopper-xtreme/.../client/screen/FilteredHopperScreenHandler.java:76`,
`FilteredGlazedHopperScreenHandler.java:72`

**Resolution:** Both handlers now use a fixed `HOPPER_SLOT_COUNT` constant (6 / 2) for the
hopper/player shift-click boundary instead of `getContainerSize() + 1`. Covered by
`FilterSlotGeometryTest`, which exercises the client-geometry dummy container and asserts the first
player slot shift-clicks into the hopper.

`int hopperSize = this.hopper.getContainerSize() + 1;` gives 6 on the server (BE hides the filter
slot) but **7 on the client** (dummy `SimpleContainer(6)` doesn't) — the client thinks the first
player slot belongs to the hopper. Shift-click prediction desyncs. Same root cause as 1.2: the
client dummy container and the server BE disagree about `getContainerSize()` semantics.

**Fix:** Make the handler hold the explicit slot count (it knows it added 6 or 2 slots) instead of
asking the container. Fixing 1.2 and 1.3 together with one "filter geometry" object would be ideal.

### 1.4 `GlassJarItemEntityCache` — unbounded client memory leak + very hot registry rebuild — **✅ Done (2026-07-22, `fix/glass-jar-item-cache`)**
`minekea/.../client/render/item/GlassJarItemEntityCache.java:21`

**Resolution:** The cache is now keyed on a record of the render-relevant components
(`CUSTOM_DATA` / `ENTITY_DATA` / `CUSTOM_NAME`, all value-equal) inside a `LinkedHashMap` with LRU
eviction (256 entries). `VanillaRegistries.createLookup()` in the jar's item-stack deserialization
paths was replaced with the level's `registryAccess()`.

`HashMap<ItemStack, RenderState>` — `ItemStack` does not implement `equals`/`hashCode`, so the map
is identity-keyed. Item stacks are recreated constantly (sync, GUI copies), so nearly every lookup
misses, inserts a new entry, and **nothing is ever evicted**. While a jar item is visible this
grows every frame. Each miss also calls `GlassJarBlockEntity.fromItemStack`, which calls
`VanillaRegistries.createLookup()` (GlassJarBlockEntity.java:106/122) — that builds the *entire*
vanilla registry set; it's a datagen utility, not a runtime lookup, and it also can't see modded
registry contents.

**Fix:** Key the cache on the stack's relevant components (e.g., the CUSTOM_DATA/ENTITY_DATA
component values) with a bounded/weak cache, and replace `VanillaRegistries.createLookup()` with
`world.registryAccess()` (a `Level` is already passed in).

### 1.5 Armoire armor stands likely duplicate on every chunk reload — **✅ No fix needed — already resolved (verified 2026-07-22)**
`minekea/.../entity/block/furniture/ArmoireBlockEntity.java:57–128`

**Resolution:** The current `ArmoireBlockEntity` no longer spawns real armor stands — armor is
rendered directly in `ArmoireBlockEntityRenderer` (via `ArmorStandRenderState`/`ArmorStandArmorModel`,
render-only). The `setLevel`/`clearRemoved`/`destroyArmorStands` spawning code described below no
longer exists; the only stand code left is `cleanUpLegacyArmorStands`, a load-time ticker that
*discards* leftover marker stands from pre-refactor world saves (matching them by
marker/small/invisible/no-gravity + `disabledSlots == 16191`) so they can't accumulate. This is
exactly the plan's suggested "render in the BER" fix, applied in an earlier refactor. No code change
made.

<details><summary>Original report (stale)</summary>

The BE spawns four real (invisible, marker) `ArmorStand` entities and re-creates them in
`setLevel`/`clearRemoved`. Armor stands persist with the chunk, and `destroyArmorStands()` only
discards the instances tracked in the current session's list — the copies loaded from chunk NBT
are orphaned. Expected symptom: invisible armor stands accumulating at armoire positions after
repeated reloads.

**Verify:** place an armoire with armor in it, reload the world several times, run
`/execute if entity @e[type=armor_stand]` near it. **Fix options:** mark the stands non-persistent
(`setPersistenceRequired` is the opposite; need `discard` on save or a non-persisting entity), tag
them with the BE position and sweep matching stands in `initializeArmorStands`, or render the
armor in the BER instead of using real entities.

</details>

### 1.6 `ImplementedInventory.isMatchingPartialStack` uses `ItemStack.matches` — **✅ Done (2026-07-22, `fix/implemented-inventory-partial-merge`)**
`chimeric-lib/.../inventories/ImplementedInventory.java:111`

**Resolution:** `isMatchingPartialStack` now uses `ItemStack.isSameItemSameComponents` (count-
independent). `ShelfBlockEntity.tryInsert` detects insertion by snapshotting the incoming count and
checking whether the returned remainder is smaller, rather than the unreliable `ItemStack.matches`
self-comparison. Covered by `ImplementedInventoryTest` (chimeric-lib fabric JUnit).

`ItemStack.matches` compares **counts** as well as item+components, so two partial stacks merge
only when their counts happen to be equal. Everything built on `tryInsert` (minekea shelves,
glass jars via callers, block painter, hopper filter inventory) silently fails to merge partial
stacks. Also: `ShelfBlockEntity.tryInsert` (ShelfBlockEntity.java:94–101) detects "did anything
insert" with `!ItemStack.matches(ret, stack)`, but on a partial merge `tryInsert` returns the
*same mutated instance*, so the comparison is always true and the insert sound never plays.

**Fix:** use `ItemStack.isSameItemSameComponents` in `isMatchingPartialStack`; have `tryInsert`
return a distinct remainder (or a result record) so callers can detect partial success.

---

## Phase 2 — Logic bugs (wrong behavior, no crash)

### 2.1 Sponj connected-sponge search compares squared distance to unsquared limit — **Confirmed**
`sponj/.../BlockUtils.java:66–70`

`start.distSqr(end) <= distance` with `distance = 32` limits the flood fill to √32 ≈ 5.7 blocks,
not 32. Fix: `distSqr(end) <= distance * distance` (and rename the param to make units explicit).

Also in `SponjBlock.absorbWater` (SponjBlock.java:100): the `canBeReplaced` branch queues with a
hardcoded `j < 6` while the other branches use `j < absorptionRadius` — the multi-sponge radius
bonus doesn't apply to washable blocks (kelp, seagrass). Align them.

### 2.2 `GlassJarBlockEntity.getBottle()` — unreachable branch, silent no-op for other fluids — **Confirmed**
`GlassJarBlockEntity.java:252–279`

Two consecutive `if (!this.hasFluid())` checks; the second (returning an empty glass bottle) is
unreachable. Milk has no bottle path (by design?), but if the jar holds milk/lava the method
returns `null` after doing nothing — the caller guards on water/honey today, so this is latent;
simplify and make the contract explicit.

### 2.3 Villager global reputation is ignored when bad reputation is enabled — **Confirmed**
`villager-tweaks/.../mixin/VTVillagerEntityMixin.java:48–58`

`injected()` computes `playerId = enableGlobalReputation ? GLOBAL_UUID : player.getUUID()` but
then returns early when `config.enableBadReputation` is true — so with both options on, the
GLOBAL_UUID substitution never applies to reputation reads (writes still go to GLOBAL_UUID via
`onReputationEventFrom`, making reads/writes inconsistent). Fix: when global rep is enabled and
bad rep is also enabled, still return `gossips.getReputation(GLOBAL_UUID, all types)`.

### 2.4 `BlockConfig.getTexture()` eagerly evaluates its fallback — **Confirmed**
`chimeric-lib/.../blocks/BlockConfig.java` (`getTexture()`)

`textures.getOrDefault("default", TextureMapping.getBlockTexture(this.getIngredient()).sprite())`
— `getOrDefault` evaluates the default argument unconditionally, so a config with an explicit
default texture but **no ingredient** throws `IllegalStateException` (or NPEs inside
`getBlockTexture`) even though the texture exists. Fix: check `textures.containsKey("default")`
first / compute the fallback lazily.

### 2.5 `DyeStationBlockEntity` opener-counter checks the wrong menu class — **Confirmed**
`shulker-stuff/.../block/entity/DyeStationBlockEntity.java:57–64`

`isOwnContainer` tests `player.containerMenu instanceof ChestMenu`, but the dye station opens a
`DyeStationScreenHandler` — copy-paste from the barrel implementation. `recheckOpeners` therefore
always sees zero legitimate viewers. Currently masked because `onOpen`/`onClose` are no-ops; fix
before anyone adds open/close behavior.

### 2.6 Menus call `startOpen` but never `stopOpen` — **Confirmed, repo-wide pattern**
- `chimeric-lib/.../screen/SimpleInventoryScreenHandler.java:27`
- `chimeric-lib/.../screen/DoubleWideInventoryScreenHandler.java:27`
- `hopper-xtreme` FilteredHopper/FilteredGlazedHopper/GlazedHopper/HopperItemFilter handlers (line 30 each)
- `shulker-stuff/.../DyeStationScreenHandler.java:43`

None of these override `removed(Player)` to call `container.stopOpen(player)` (vanilla `ChestMenu`
does). For BEs using `ContainerOpenersCounter` this leaves the open-counter unbalanced until the
recheck kicks in; for anything relying on `stopOpen` side effects it never fires. Fix once in the
shared base class (see 3.2).

### 2.7 Dye station output-slot bookkeeping — **Confirmed (UX), verify the edge cases**
`shulker-stuff/.../client/screen/DyeStationScreenHandler.java` (`OutputSlot`, `quickMoveStack`)

- Shift-clicking the output slot does nothing: `invSlot < getContainerSize()` (7) is false for slot
  index 7, so it's treated as a player slot and `moveItemStackTo(0..7)` finds no legal target.
- Taking the output calls `OutputSlot.remove(amount)`, which shrinks **all seven** input slots and
  never calls `updateOutput()`, so with stacked dyes the output stays empty until another slot is
  touched.
- Input consumption in `remove()` rather than `onTake()` is fragile against other click paths
  (drag, swap, number keys). Audit each click type for "output taken without inputs consumed".

**Fix:** move consumption to `onTake`, call `updateOutput()` after consumption, and special-case
the output slot in `quickMoveStack`.

### 2.8 Houdini block spawns ghost item entities on the client — **Confirmed**
`houdini-block/.../blocks/HoudiniBlock.java` (`replaceWithBlockInHand`, `playerWillDestroy`,
`spawnHoudiniBlockItem`)

`world.addFreshEntity(itemEntity)` runs on both sides (no `isClientSide` guard), producing
client-only ghost items that pop when the server syncs. `playerWillDestroy` has the same issue.
Fix: gate entity spawning (and `setBlockAndUpdate` in the replace path) to the server.

### 2.9 Off-hand interactions use the wrong hand — **Confirmed pattern**
- `minekea/.../ShelfBlock.java:236` — `player.getMainHandItem()` / `setItemInHand(MAIN_HAND, …)`
- `minekea/.../ArmoireBlock.java:439` — same
- `minekea/.../GlassJarBlock.java:469` area — inserts `stack` (the hand param) but writes the
  result back to `MAIN_HAND`

`useItemOn` receives `hand`; when it fires for the off-hand these write the remainder into the
main hand (potentially deleting/duplicating the main-hand item). Fix: thread `hand` through.

### 2.10 `GlassJarBlock.useItemOn` — duplicated condition + client-side state mutation — **Confirmed**
`GlassJarBlock.java:464–466` has the identical `!stack.isEmpty() && entity.canAcceptItem(stack)`
check nested inside itself (copy-paste). More broadly the whole method mutates BE state on both
sides; the sounds are client-gated but the inventory math isn't. Consider gating mutations to the
server and returning `SUCCESS`/`CONSUME` appropriately.

### 2.11 `XtremeHopperBlockEntity.canExtract` casts unconditionally — **Confirmed, latent**
`XtremeHopperBlockEntity.java:403` — `((XtremeHopperBlockEntity) hopper).withFilter` inside a
static helper whose public entry point (`extract(Level, Hopper)`) accepts any `Hopper`. Safe today
because only the own BE calls it; one future caller (e.g., a hopper minecart integration) away
from a `ClassCastException`. Same pattern in all six BE copies. Fold into the Phase 3 base class
with an `instanceof` check.

### 2.12 `isFull()` counts the hidden filter slot — **Confirmed, minor**
`XtremeHopperBlockEntity.java:189–197` iterates the full backing list including the filter slot,
so a filtered hopper with an empty filter never reports full and always attempts extraction.
Cosmetic/perf only; fix in the base-class refactor (iterate `getContainerSize()` slots).

---

## Phase 3 — Duplication reduction (highest-leverage refactors)

### 3.1 hopper-xtreme: six ~600-line block entities that are ~90% identical — **the big one**
`entity/XtremeHopper|XtremeHupper|XtremeMultiHopper|XtremeMultiHupper|GlazedHopper|GlazedMultiHopper BlockEntity.java`
(~3,700 lines total; diff-verified: they differ only in BE type, inventory size, suck direction
(up/down, `getLevelY()±1`, `SUCK_AABB`), insert-vs-drop behavior, and screen handler choice)

Extract `AbstractXtremeHopperBlockEntity` with the vanilla-fork logic (tick, insert/extract,
transfer, inventory-at helpers, cooldown, filter handling) parameterized by:
- storage size / filter index
- pull direction (`Direction.UP`/`DOWN` + suck AABB)
- output behavior (insert into facing container vs drop)
- menu factory

Estimated net deletion: **~2,500–3,000 lines**, and bugs 1.2/2.11/2.12 get fixed in one place
instead of six. The same treatment applies to the block classes
(`XtremeHopperBlock`/`GlazedHopperBlock`/… are diff-identical modulo names) and the four screen
handlers + four screens (differ only in slot layout constants).

**Sequencing note:** do Phase 1 items 1.2/1.3 first as minimal fixes, then this refactor; the
fabric gametests (`SixSlotTransferTest`, `TransferSpeedTest`, `PreventFilterExtractionTest`,
`DeprecatedBlockConversionTest`) are the safety net — run them before and after.

### 3.2 chimeric-lib: unify the screen handlers
`SimpleInventoryScreenHandler` and `DoubleWideInventoryScreenHandler` are identical except column
count (9 vs 18) and x-offsets. Merge into one class with a `columns` parameter; add the missing
`removed()` → `stopOpen()` override (fixes 2.6 for every consumer); consider having the
hopper-xtreme and dye-station handlers extend it instead of re-implementing `quickMoveStack`
(currently 6 near-identical copies of that method across the repo).

### 3.3 sponj: merge the four sponge blocks
`SponjBlock` vs `LavaSponjBlock` (and the wet variants) are ~95% identical. One
`AbstractSponjBlock` parameterized by fluid tag, wet block, and the "washable blocks" branch
(water only). Fixes the 2.1 radius inconsistency once.

### 3.4 WrenchItem is duplicated across mods
`minekea/.../item/tools/WrenchItem.java` and `hopper-xtreme/.../item/WrenchItem.java` are the same
file with cosmetic divergence (the hopper copy gained `useItemDescriptionPrefix()`, the minekea
copy didn't — visible inconsistency in tooltips). Move the shared implementation to chimeric-lib;
keep per-mod registration thin.

### 3.5 ContainerOpenersCounter boilerplate
`CrateBlockEntity`, `MinekeaBarrelBlockEntity` (minekea), and `DyeStationBlockEntity`
(shulker-stuff) each hand-roll the same anonymous `ContainerOpenersCounter` (~40 lines). Extract a
lib helper taking `(menuClass, soundEvents, openProperty)` — this also prevents recurrences of bug
2.5 (wrong menu class) by making the menu type a required parameter.

### 3.6 NeoForge loot-modifier plumbing
`shulker-stuff` and `miniblock-merchants` have diff-identical `LootModifierRegistry` classes (and
similar modifier shells). Small, but a lib-level `registerLootModifier(modId, name, codec)` helper
removes the copy-paste and keeps future mods consistent.

### 3.7 ColorHelpers cleanup (chimeric-lib)
- `public static int[] WHITE …` — mutable public static state; make `final` (ideally an
  `EnumMap<DyeColor, int[]>`).
- `getName`/`getDye`/`getWool` — three 16-arm string switches; replace with `DyeColor`-keyed maps
  or vanilla lookups (`DyeItem.byColor`, wool-by-color map). `getColors()` duplicates
  `DyeColor.values()` with a different ordering guarantee — pick one source of truth (several mods
  iterate colors; ordering differences between call sites are a latent data-gen hazard).
- `RGB.getColor()` and `RGB.toInt()` are identical; both encode alpha 0 — audit call sites that
  feed these into tint APIs expecting opaque (0xFF) alpha.
- `getTint` doesn't guard negative `tintIndex`.

### 3.8 Inventory-abstraction consistency
Three different BE inventory styles coexist, sometimes in the same mod:
- extend `RandomizableContainerBlockEntity` (hopper-xtreme BEs, barrel)
- extend `RandomizableContainerBlockEntity` **and** implement `ImplementedInventory`
  (`CrateBlockEntity` — the interface defaults are dead weight there, and crate skips
  `trySaveLootTable` while barrel uses it: crates silently don't support loot tables)
- extend `BlockEntity` + `ImplementedInventory` (shelf, armoire, display case, glass jar)

Pick a rule (suggested: `RandomizableContainerBlockEntity` for anything loot-table-capable;
`ImplementedInventory` only for non-BE inventories like item-backed ones) and document it in
CLAUDE.md. Also normalize `setItems`: `DyeStationBlockEntity.setItems` does `clear()+addAll` on a
fixed-size `NonNullList` (size can drift; other BEs assign the list reference).

---

## Phase 4 — Consistency, hygiene, docs

4.1 **CLAUDE.md is stale** — says Minecraft 1.21.5 and "only archaeology-tweaks, banner-tweaks,
    chimeric-lib enabled"; reality is MC 26.2 (gradle.properties) with 15 active projects
    (settings.gradle). Regenerate the affected sections; consider generating the active-mod list
    from `project-list.json` so it can't drift.

4.2 **Dead code:** commented-out `writeNbt`/`getItemsOnBreak` blocks in `GlassJarBlockEntity`
    (~60 lines), leftover `int k = 4;` in `ATBrushableBlockEntity.scheduledTick`, commented
    `OccupantData.create/loadEntity`. Delete; git history keeps them.

4.3 **`@Overwrite` mixins** (`ENFEnchantmentMixin.getFullname`, `MapStateMixin.addDecoration`/
    `toggleBanner`) — maximum incompatibility with other mods touching the same methods. Where
    practical convert to `@Inject`/`@ModifyArg` (the enchantment one is a good candidate:
    inject-at-return and rewrite only the level suffix). Low urgency, note in each file why
    overwrite was needed if kept.

4.4 **`HopperItemFilterItem.use`** — redundant nested client checks
    (`player.level() != null && !player.level().isClientSide()` then `if (!world.isClientSide())`
    inside), and `FilterSlot.mayPlace` allocates `new ItemStack(...)` per call where
    `stack.is(ModItems.HOPPER_ITEM_FILTER_ITEM.get())` is used elsewhere for the same test —
    normalize on `stack.is(...)`.

4.5 **`RomanNumeralUtil.toRoman`** NPEs for values < 1 (`map.floorKey` returns null). Guard and
    fall back to Arabic numerals (also covers enchant levels > 3999 from other mods' commands).

4.6 **`ArmoireBlockEntity` uses `oshi.util.tuples.Triplet`** — a hardware-info library dragged in
    for a tuple. Replace with a small record or `Vec3`.

4.7 **`ImplementedInventory.clearContent`** calls `getItems().clear()`, which on a
    `NonNullList.withSize` list either throws or breaks the fixed-size invariant depending on the
    backing implementation — verify against current mappings and switch to filling with
    `ItemStack.EMPTY` if needed.

4.8 **`ItemStorageBlock`** — `world.blockEntityChanged(pos)` on a block with no BE; leather/shears
    interactions neither consume leather nor damage shears (confirm intended); `FACING` property
    has no default in `registerDefaultState`.

4.9 **`ShulkerStuffConfig` is Miniblock Merchants' config, copy-pasted — Confirmed**
    `shulker-stuff/.../config/ShulkerStuffConfig.java`. The entire screen exposes ~26 drop-chance
    options (`ancientShellChance`, `wagyuBeefChance`, `buddingCactusChance`, … — the Miniblock
    Merchants conversion-item drop rates) that have nothing to do with shulker boxes. Two problems,
    both verified: (a) none of the `*Chance` fields are read anywhere outside this file — they're
    dead config bound to nothing; (b) the option/title `text.config.shulkerstuff.*` translation keys
    don't exist in `assets/shulkerstuff/lang/en_us.json`, so the screen renders raw key strings. Fix:
    replace the body with real Shulker Stuff options (e.g., enchantment tuning) or remove the config
    screen entirely, and add the matching lang keys for whatever remains. (Surfaced during the README
    functionality review.)

---

## Suggested execution order

| Step | Items | Risk | Notes |
|------|-------|------|-------|
| 1 | 1.1, 1.2, 1.3 | Low | Small, surgical; add/extend gametests first |
| 2 | 1.4, 1.6 | Low-Med | Lib change (1.6) affects several mods — run full build + minekea manual smoke test |
| 3 | 1.5 verification, then fix | Med | Needs in-game verification before choosing a fix |
| 4 | Phase 2 items | Low each | Independent; batch by mod |
| 5 | 3.1 (hopper BE base class) | Med-High | Biggest payoff; gametests are the gate |
| 6 | 3.2–3.8 | Low-Med | Each is self-contained |
| 7 | Phase 4 | Low | Opportunistic / alongside the above |

**Verification baseline for every step:** `./gradlew build`, plus the hopper-xtreme fabric
gametests for anything touching chimeric-lib inventories or hopper-xtreme. There are currently no
tests outside hopper-xtreme — items 1.1, 1.6, and 2.7 are good candidates for the first gametests
in minekea and shulker-stuff respectively.
