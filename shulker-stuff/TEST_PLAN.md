# Test Plan — Shulker Stuff

Shulker Stuff upgrades shulker boxes with three enchantments — **Refill** (tops up your active
stack from the box), **Vacuum** (picked-up items route into the box;
`ShulkerStuff$PlayerInventoryMixin.addStackForVacuum`), **Void** (picked-up matching items are
destroyed; `addStackForVoid`) — plus the **Shulker Dyeing Station** block
(`DyeStationBlock(Entity)`, `DyeStationScreenHandler`), **Netherite Plating** via smithing template
(`ShulkerStuffPlatedComponent`, blast-resistance mixin `ShulkerStuff$AbstractBlockMixin`), custom
dyed-color component (`ShulkerStuffDyedColorComponent`), and client rendering mixins for items and
placed boxes. Loot availability comes via `SSLootTableModifier` (Fabric) /
`ShulkerStuffLootModifier` (NeoForge). Note the README's Known Issue: the config screen currently
contains copy-pasted Miniblock Merchants options — config tests are deferred until that's fixed.

## Test conventions

Follow the Hopper X-Treme pattern: GameTest classes in
`fabric/src/main/java/com/chimericdream/shulkerstuff/fabric/test/`, registered under the
`fabric-gametest` entrypoint, structures under
`common/src/main/resources/data/shulkerstuff/gametest/structure/`. Enchantment behaviors hang off
player inventory pickup, so mock players are central. Enchantments are data-driven — tests must
fetch them from the registry via `ModEnchantments` keys.

## Manual test plan

Fabric full pass, NeoForge full pass (loot + rendering are loader-forked). Setup: creative world,
shulker boxes, enchanting setup or `/enchant`-style component commands, dyes, smithing table +
netherite.

1. **Refill** — Refill-enchanted box in inventory containing cobblestone; place cobblestone from
   your hotbar until the stack empties: it should top back up from the box. Verify: partial stacks,
   multiple candidate boxes (which wins?), box empty of that item (no refill), and that
   non-enchanted boxes never refill.
2. **Vacuum** — Vacuum I and II boxes: walk over items matching box contents; they should enter the
   box instead of the main inventory. Pin the level-I vs level-II difference (range? item types?
   whatever the design is) and verify both. Full box: overflow goes to player inventory, nothing
   deleted.
3. **Void** — Void-enchanted box (presumably configured with filter items — verify the UX for
   choosing what to void): matching pickups are destroyed, non-matching pickups behave normally.
   Void + Vacuum on the same box: define precedence (code checks vacuum first — confirm intended).
4. **Enchantment acquisition** — enchanting table offers these on shulker boxes; enchanted books
   apply via anvil; treasure/trade sources per the enchantment JSON; verify Deep Storage does NOT
   appear anywhere (unregistered).
5. **Dyeing Station** — craft/place; GUI opens; insert box + dye → recolored box out, dye consumed
   correctly, contents and enchantments preserved. Un-dyeing (if supported) and re-dyeing an
   already-dyed box. Hopper interaction with the station (can hoppers feed it? pin intent).
6. **Netherite Plating** — smithing table: box + Plated Shulker Upgrade template + netherite ingot →
   "Netherite Plated" tooltip. Plated box placed and blown up by TNT: must survive (blast
   resistance mixin). Plated box as item entity in fire/lava: does it survive like netherite items?
   Pin and verify.
7. **Rendering** — dyed and plated boxes render correctly: in inventory, in hand, in item frames,
   and placed (open/close animation keeps color). Check both loaders — item rendering is
   implemented separately per loader (`ShulkerBoxModelLoadingPlugin` vs `ShulkerBoxSpecialModel`).
8. **Round-trips** — dyed+plated+enchanted box: break/re-place, world reload, put through the
   dyeing station again; nothing (contents, color, plating, enchantment) may be lost at any step.
9. **Ender chest / bundles / nesting rules** — unchanged vanilla restrictions (boxes don't nest).

## Recommended automated tests

### GameTests — enchantment behaviors

* **`vacuumRoutesPickupIntoBox`** — mock survival player with a Vacuum-enchanted box (containing 1
  dirt) in inventory; spawn a dirt item entity on them; after pickup, assert box contains 2 dirt
  and main inventory has none. Negative control: unenchanted box.
* **`vacuumLevelDifference`** — encode whatever II does beyond I.
* **`vacuumOverflowFallsBack`** — box full: assert item lands in player inventory, count exact.
* **`voidDestroysMatchingPickup`** — Void box configured to void dirt; spawn dirt on player; assert
  dirt is nowhere (not in box, not in inventory, no item entity).
* **`vacuumBeatsVoid`** — box with both (if legal): pin precedence per the mixin's if/else-if.
* **`refillTopsUpActiveStack`** — mock player, hotbar stack of 3 cobblestone selected, Refill box
  with 64; consume/place blocks via the interaction manager until the stack would empty; assert it
  refilled and box count decreased accordingly.

### GameTests — dye station

* **`dyeStationRecolorsBox`** — open `DyeStationScreenHandler` server-side with a mock player;
  insert box + red dye; take output; assert `ShulkerStuffDyedColorComponent` matches red, dye
  consumed, contents/enchantments preserved (pre-load the input box with items + Vacuum).
* **`dyeStationSlotValidation`** — non-box in the box slot rejected; non-dye in dye slot rejected;
  shift-click routing sane.
* **`dyeStationBEPersistence`** — put items in the station, save/load BE NBT
  (`saveAdditional`/`loadAdditional` round-trip); contents intact.

### GameTests — plating

* **`platedBoxSurvivesExplosion`** — structure with a plated box + adjacent TNT; ignite; assert box
  block still present (or dropped intact per design) while a control unplated box in the same
  structure is destroyed; uses the `AbstractBlockMixin` blast-resistance path.
* **`smithingRecipeAppliesPlating`** — resolve the smithing recipe via `RecipeManager` with
  template+box+ingot inputs; assert output has `ShulkerStuffPlatedComponent`.

### GameTests — loot & data

* **`enchantmentsInRegistry`** — REFILLING/VACUUM/VOID resolve; Deep Storage key absent.
* **`lootModifierInjects`** — resolve targeted loot tables and assert the injected entries
  (Fabric); when NeoForge tests exist, mirror with the global loot modifier.
* **`enchantmentAvailability`** — assert tags: which enchantments are in
  `treasure`/`tradeable`/`in_enchanting_table` per the design.

### Unit tests

* `ContainerComponentBuilder` (`addStackForVacuum`/`addStackForVoid` feeding logic): pure-ish —
  table-test merging into existing stacks, respecting max stack size, full-container behavior,
  and component-sensitive stacking (two dirt stacks with different components must not merge).

## ChimericLib helper opportunities

* **Mock-player pickup fixture** — "give player P inventory I, spawn item entity E on them, tick
  until pickup, return resulting inventory diff." Reused for every Vacuum/Void/Refill test and by
  any future magnet-like feature.
* **Inventory diffing** — snapshot player+container inventories, apply action, assert exact diff
  (already on ChimericLib's GameTest wishlist; this mod is the strongest motivation).
* **Menu/screen-handler harness** — shared with Banner Tweaks (loom), Hopper X-Treme (filter
  screens), Minekea (crates): open handler server-side, manipulate slots, assert.
* **Explosion fixture** — "detonate TNT at pos, assert survivors" for the plating test; also useful
  for future blast-resistant content.
* **Component assertion helpers** — `assertHasComponent(stack, type, expected)` with pretty
  failure output.

## Open questions

* How does a player choose what Void discards (dedicated filter UI? box contents as implicit
  filter?) — determines the `voidDestroysMatchingPickup` setup. (ChimericLib's POTENTIAL_FEATURES
  notes reusing Hopper X-Treme's filter API here eventually.)
* Refill's exact trigger (block placement only? any stack shrink?) and multi-box priority order.
* Config screen must be fixed (Known Issue) before config tests are written — currently all options
  belong to Miniblock Merchants.
