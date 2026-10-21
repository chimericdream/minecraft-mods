# PolyCraft — Overnight log

Newest milestone last. Decompiled MC 26.2 sources are cached under `.sources/minecraft/26.2/` (gitignored); every
class named below was read there, not guessed.

## M0 — Ground verified (2026-10-21)

### Recipe lookup call sites (the mixin targets)

| Station | Where the recipe is chosen | Notes |
|---|---|---|
| Crafting table, player 2x2 | `CraftingMenu.slotChangedCraftingGrid(menu, level, player, container, resultSlots, recipeHint)` (protected static; `InventoryMenu` calls it too) -> `RecipeManager.getRecipeFor(type, input, level, recipeHint)` | Has the `Player`. A matching `recipeHint` wins over the first match, so we can pass the chosen recipe as the hint. |
| Taking the crafting result | `ResultSlot.getRemainingItems` -> `recipeAccess().getRecipeFor(CRAFTING, input, level)` (3-arg, **first match**, ignores any choice) | **Bug risk:** crafter-remainders (buckets, bottles) would come from the wrong recipe. Must be wrapped too; `ResultSlot` has `this.player`. |
| Furnace / smoker / blast furnace | `AbstractFurnaceBlockEntity.serverTick` and `getTotalCookTime` -> `entity.quickCheck.getRecipeFor(input, level)`; `quickCheck` is a private final `RecipeManager.CachedCheck` | `createCheck` keeps `lastRecipe` as a hint, so vanilla already sticks with a recipe while it still matches. |
| Crafter | `CrafterBlock.dispenseFrom(state, level, pos)` -> `CrafterBlock.getPotentialResults(level, input)` (public static, uses a static `RecipeCache(10)`) | `dispenseFrom` has the block entity; remainders come from the picked recipe, so they stay consistent. |
| Smithing table | `SmithingMenu.createResult()` -> `recipeAccess().getRecipeFor(SMITHING, input, level)` | Menu has no `Player` field; `ItemCombinerMenu` should have one (check in M5). |
| Campfire | `CampfireBlockEntity.placeFood` (3-arg lookup, for cooking time) and `cookTick` (`recipeCache.getRecipeFor`) | No player or GUI, so pack default only, as designed. |
| Stonecutter | `StonecutterMenu` / synced `SelectableRecipe` list | Untouched, as designed. |

`RecipeManager.getRecipeFor(type, input, level)` is `recipes.getRecipesFor(type, input, level).findFirst()`, and
`RecipeMap.getRecipesFor` returns a **Stream of every recipe that matches the actual input**. See "Design
refinements" below.

### Furnace internals (needed for mid-cook switching)

- State: `cookingTimer`, `cookingTotalTime`, `litTimeRemaining`, `litTotalTime` (private ints), synced through
  `dataAccess` (indexes 0 to 3), saved as `cooking_time_spent`, `cooking_total_time`, `lit_time_remaining`,
  `lit_total_time`.
- Completion test is **`cookingTimer == cookingTotalTime`** (equality, not `>=`). If a switch leaves
  `cookingTimer > cookingTotalTime` the furnace would never finish. On a switch we must set the total to the new
  recipe's `cookingTime()` and, if `cookingTimer >= total`, set `cookingTimer = total - 1` so it completes next tick.
- The input is only shrunk in `burn()` at completion; fuel is consumed when it lights. So switching wastes nothing
  as long as we only touch the timer/total.
- `setItem(0, ...)` with a different item resets `cookingTimer = 0` and recomputes the total via
  `getTotalCookTime` (which goes through `quickCheck`), so our wrap covers it.
- `canBurn` is the output-slot merge check; it is a private static, so for the greyed-out option rule we replicate
  it (same item and components, count within `min(maxStackSize, burnResult.getMaxStackSize())`).
- XP/recipes-used: `setRecipeUsed(recipe)` at completion, so XP follows the completing recipe for free.

### Recipe book and client knowledge

- The client does not hold full recipes. `RecipeManager` syncs `RecipeDisplayEntry` objects, addressed by
  `RecipeDisplayId` (an index into `allDisplays`); `ServerboundPlaceRecipePacket(containerId, RecipeDisplayId, useMaxItems)`.
- Server side: `RecipeManager.getRecipeFromDisplay(id)` returns `ServerDisplayInfo(display, parent RecipeHolder)`, so a
  recipe-book click already resolves to a `RecipeHolder`. `CraftingMenu.finishPlacingRecipe(level, recipe)` passes
  that holder as the `recipeHint`. **M7 is feasible**: hook `finishPlacingRecipe` (and the furnace equivalent) to record
  the player's choice when the recipe is in a conflict group. Keep it optional per the runbook.
- The popup therefore needs the server to send, for each option: the recipe id (as a string/`Identifier`), the output
  `ItemStack`, and the source namespace. The client cannot compute any of it.

### UI drawing and sprites (verified in the jar and in this repo)

- The GUI class is `net.minecraft.client.gui.GuiGraphicsExtractor` (not `GuiGraphics`). Plain textures:
  `context.blit(RenderPipelines.GUI_TEXTURED, identifier, x, y, u, v, w, h, texW, texH)`, as in
  `chimeric-lib/.../SimpleInventoryScreen.java`. Screens use `extractRenderState` / `extractBackground`, widgets
  `extractWidgetRenderState`-style names; confirm the exact widget method names when writing the widget.
- The vanilla sprites DESIGN.md hoped for **exist** in 26.2: `minecraft:recipe_book/page_forward`,
  `page_backward`, `page_forward_highlighted`, `page_backward_highlighted`
  (`assets/minecraft/textures/gui/sprites/recipe_book/`). Reuse them by id; no placeholders needed for the arrows.
- `textures/gui/container/crafting_table.png` and `furnace.png` exist (screen backgrounds for positioning the button
  relative to the result slot and the progress arrow). Crafting result slot is at (124, 35) in the menu, furnace result
  at (116, 35), furnace input at (56, 17).

### Persistence and networking patterns already used in this repo

- **Per-player choices**: a `SavedData` with a `SavedDataType`/codec, fetched with
  `server.overworld().getDataStorage().computeIfAbsent(TYPE)`; see `pannotia-companion/.../home/BackData.java` and
  `all-hallows-steve/.../PumpkinFaceData.java`. Loader-agnostic, so no Fabric attachment / NeoForge attachment code.
- **Networking**: Architectury `NetworkManager.registerReceiver(NetworkManager.c2s()/s2c(), ID, CODEC, handler)`
  with `CustomPacketPayload` records, registered once from common init; see `PumpkinFaceNetworking`. Use
  `context.queue(...)` to hop to the main thread.
- **Block-entity pins**: no attachment API in common, so mix a duck-typed interface plus a field into the furnace
  and Crafter block entities and hook `saveAdditional`/`loadAdditional` (`ValueOutput`/`ValueInput` in 26.2, e.g.
  `output.putShort`, `input.read(key, codec)`).
- Mixins: `polycraft.mixins.json` is in place with empty lists; mixins are written against Mojang names directly.

### Design refinements the real API suggests (no DESIGN.md decision changed)

1. **Detect at runtime from the live input, not from precomputed signatures alone.** `RecipeMap.getRecipesFor` already
   returns every recipe that matches the exact grid (mirroring, 2x2-in-3x3 offset and tags all handled by
   `Recipe.matches`). Plan: run vanilla's lookup first; only if it finds a recipe, stream all matches; collapse recipes
   whose **assembled result** has the same item, components and count; two or more survivors = a conflict.
2. **Identical vs overlapping**: per-recipe canonical signatures (computed once per reload, cached per recipe id) decide
   which matches count. Default (identical only): the options are the matches whose signature equals the signature of
   the vanilla winner. `detectOverlappingInputs=true`: all matches. The group key used for stored choices and
   `rules.json` is `type + signature of the vanilla winner`, so adding an unrelated overlapping recipe doesn't
   invalidate saved choices. Recipes whose ingredients can't be canonicalised get a unique signature (never grouped).
3. **Cost**: a conflict check is one full stream over a type's recipes per input change (about 10k `matches` calls in
   the worst case, roughly a millisecond). The `PerfTest` budget still applies to reload-time signature building.
   Add a small LRU keyed by the input items, like vanilla's `RecipeCache`.
4. Because vanilla furnaces re-query `quickCheck` every tick with the previous recipe as a hint, the furnace
   wrap should be cheap: if the block's remembered active recipe still matches the input, return it without scanning.

### Still to verify later (not blocking)

- Who the "acting player" is for a furnace: `AbstractFurnaceMenu` keeps its `container` private and has no block-entity
  reference; plan is an accessor mixin plus recording the viewer when a server-side furnace menu is created and
  removing it in `removed(player)`, and noting which viewer last changed slot 0.
- `ItemCombinerMenu`'s player field name (smithing, M5).
- Exact `AbstractWidget` rendering method names in 26.2 (M3, from a small decompile of `AbstractWidget`/`Button`).
- Crafter screen layout (`CrafterScreen`, `CrafterMenu`) for button placement (M5).

## M1 — Pure-Java core (2026-10-21)

Added `polycraft/common/.../core/`: `Signature`, `SignatureBuilder`, `RecipeInfo`, `ConflictDetector`,
`ChoiceResolver`, `Rules`, `IdFilter`, `ChoiceStore`, `PinMap`, `CookMath`. No Minecraft types (Gson only, for rules).

Tests (`:polycraft:fabric:test`, 62 passing): `SignatureBuilderTest`, `ConflictDetectorTest`, `ChoiceResolverTest`,
`RulesParserTest` (also covers `IdFilter`), `PlayerChoicesTest` (also covers `PinMap` and `CookMath`), `PerfTest`
(10k recipes grouped in about 55 ms, budget 250 ms). Both loaders compile.

Decisions made while implementing (all inside DESIGN.md's lines; flagging so you can overrule):
- **Merge precedence**: `config.merge(datapack)`: the first argument wins on a group default, priorities are
  concatenated first-then-second without duplicates, excludes are unioned. Intended use is local config over datapacks.
- **`Rules.packOrder`**: group-key default first, then a default given under any option's recipe id, then the global
  `priority`. Excluded recipes are not filtered inside `Rules`; the caller removes them from the option list (and if
  that leaves no options, uses vanilla).
- **Choices are never auto-pruned on reload.** `ChoiceStore.prune` exists for `/polycraft reset` style cleanup; the
  per-player cap (LRU) is the only automatic eviction, so choices survive a datapack being off for a while.
- **Ingredient keys**: a tag and an explicit equal item list give the same key (sorted item set, hashed when larger
  than one). The caller passes `null` for anything it can't express (item-data matchers), which makes the recipe's
  signature unique so it's never grouped.
- **Group key** = `type|canonical` of the vanilla winner's signature (see M0 refinements 1 and 2).
- `Rules` ids are validated with a lenient lowercase `namespace:path` pattern; invalid entries warn and are dropped.
- `CookMath.canMergeIntoOutput` replicates vanilla's private `canBurn` rule for greying out options.

## M2 — Config, rules, commands, player data (2026-10-21)

Added: `config/PolyCraftConfig` (YaclConfig, all keys from DESIGN.md's table except the removed `allowPlayerChoice`),
`core/ConfigLimits`, `data/PolyCraftChoices` (SavedData wrapping `ChoiceStore`), `rules/PolyCraftRules` (config file +
datapack reload listener), `recipe/RecipeIndex` (per-reload index; signatures; synthetic results for static listings),
`command/PolyCraftCommand`, `en_us.json`. `PolyCraftMod.init()` now initialises config, rules and the command.

Checks: unit tests 65 passing (adds `ConfigTest`); 10 GameTests passing on `:polycraft:fabric:runGameTest`
(`CommandGameTest`: datapack rules load, conflict listing, export contents, op-only commands, reset self/other,
`rememberChoices` off, save/load round trip); both loaders compile; `bun run verify:gametests` clean.

Fixture datapack (`fabric/src/gametest/resources/data/polycraft_test/`): four 2-cactus crafting recipes (a: 3 green dye,
b: 5 green dye, c: 2 lime dye, copy: 3 green dye) and two gunpowder smelting recipes (charcoal/200 ticks, coal/100 ticks),
plus a rules file that prefers recipe b and excludes recipe c. The export test proves the full pipeline: the copy
collapses into a, c is excluded, b is kept because its count differs.

Decisions and notes:
- **Command permissions**: `conflicts` and `reset` (self) are open to everyone; `export`, `reload` and `reset <player>` need
  gamemaster (level 2), matching how `opus` checks permissions in 26.2 (`Permission.HasCommandLevel`).
- **`/polycraft conflicts`** lists identical-input conflicts only (it works from the recipes alone). Overlapping conflicts
  depend on what is in a station, so they can't be listed. Excluded recipes are left out of the listing and export.
- **`reload`** re-reads `config/polycraft/rules.json` only; datapack rules reload with `/reload`.
- `rules.json` is created with an empty starter body the first time the mod runs.
- **`rememberChoices` off** stops both reading and writing choices but leaves stored ones in the world.
- Per-player cap is 256 stored choices (constant `PolyCraftChoices.MAX_PER_PLAYER`, not a config option).
- **Loader-specific ingredients**: 26.2's `Ingredient` is a final class over a `HolderSet<Item>`, so common code sees
  plain item sets. If NeoForge or Fabric API add custom ingredient types that expose extra predicates, common code
  can't see them; two recipes differing only by such a predicate could be grouped as "identical". Runtime matching
  still uses the real predicates, so a grouped recipe that doesn't actually match the grid never appears as an option.
  Not worth special handling for v1; noted for the README's known limitations.
- **GameTest quirk**: every `makeMockServerPlayerInLevel()` player has the name `test-mock-player`, and `EntityArgument.player()`
  rejects UUID selectors, so the reset-other test resolves the player via the server's name lookup.
  `makeMockServerPlayerInLevel` is deprecated for removal (the opus test uses it too); a replacement should be adopted
  repo-wide when one is chosen.
- You added `polycraft/common/src/main/resources/assets/polycraft/textures/gui/toggle_list_button.png`
  (untracked, not mine). I haven't committed it. M3 uses that file name for the main button texture and won't overwrite it.

## M3 — Crafting table end to end (2026-10-21)

Added: `recipe/ConflictService` (server-side selection), `network/` (`ConflictOptionsPayload` S2C, `SelectRecipePayload`
C2S, `PolyCraftNetworking`), mixins `PCCraftingMenuMixin` and `PCResultSlotMixin`, and the client picker
(`client/ConflictOverlay`, `ClientConflictState`, `PolyCraftClient`). Loader entrypoints call `PolyCraftClient.init()`.
Access widener now exposes `AbstractContainerScreen.leftPos/topPos/imageWidth/imageHeight`.

How it works:
- `PCCraftingMenuMixin` wraps the `RecipeManager.getRecipeFor(..., hint)` call inside `CraftingMenu.slotChangedCraftingGrid`
  (shared by the crafting table and the player's 2x2 grid). Vanilla still finds its pick first; `ConflictService.select` then
  confirms or replaces it, and the server tells the player's client what the options are (only when they change).
- `PCResultSlotMixin` wraps the second lookup that `ResultSlot.getRemainingItems` does when you take the result, so leftovers
  come from the recipe that was actually chosen.
- The server only accepts a selection that matches what it last offered that menu (checked by container id, group key and
  recipe id), so forged or stale requests are ignored.
- A choice applies for the current menu immediately and is saved per player unless `rememberChoices` is off. With it off,
  the choice still lasts while that menu stays open.
- The picker is drawn and handled by `ConflictOverlay` through Architectury's screen events (no screen mixins).
- `ConflictService.select` also demotes a recipe the rules exclude: if vanilla would pick an excluded recipe and another
  recipe matches, the other one is used even when there is nothing to offer. An excluded recipe is still used when it is the
  only match (we never remove recipes).

Verification:
- Unit tests: 65 passing. GameTests: 19 passing on `:polycraft:fabric:runGameTest` (`CraftingGameTest` adds 9: vanilla default,
  pack default beats vanilla, player choice beats pack default and persists into a new menu, per-player isolation, forged
  selections ignored, excluded and duplicate recipes not offered, a lone recipe makes no offer, taking the result uses up the
  inputs, shift-click crafts everything with the chosen recipe). That proves both mixins apply and work on Fabric.
- NeoForge: compiles, and a dev `runServer` boot loaded PolyCraft, accepted the mixin config, and force-loading `CraftingMenu`
  and `ResultSlot` (a temporary check, since reverted) applied both mixins with no errors. NeoForge has no GameTest runner
  in this repo, so its behaviour is covered by that boot check plus the shared common code.
- Visual smoke test (temporary client GameTest, since deleted): real client, crafting table, a conflict with 8 options.
  Click on the button opens the list; right arrow scrolls; clicking the third visible option crafted paper (asserted on the
  server). Screenshots: `C:\Users\bill\AppData\Local\Temp\claude\C--Users-bill-code-minecraft-mods\41e2c44a-9693-42ab-b379-7baf94eacc17\scratchpad\m3-screenshots\`
  (`0001_popup-1-button.png` closed, `0002_popup-2-open.png`, `0003_popup-3-scrolled.png`, `0004_popup-4-hover.png`,
  `0005_popup-5-chosen.png`). They looked right to me; the feel of the interaction is for your morning check.

Notes and things to look at in the morning:
- **Art**: the code uses your `toggle_list_button`, `toggle_list_button_pressed`, `result_list_item` and `result_list_item_active`
  textures (all untracked/uncommitted except the list items, which you committed). I did not commit `toggle_list_button*.png`
  or the modified `icon.png`; they are yours. Without them the button draws as the missing-texture checkerboard.
- **Popup position**: it is centred above the GUI, and goes below it when there is no room above. At GUI scale 3 on a 720p
  window it sits just under the table and overlaps the hotbar area slightly. Fine functionally; tell me if you'd rather it
  anchor beside the button.
- **Badge**: the count is drawn as plain text in the button's upper-right corner (white with shadow, no scaling).
- **Fixture growth**: the test datapack now has an eight-option group (`two_shards_1` to `_8`) so scroll arrows can be tested;
  `CommandGameTest` expects four groups in the fixture.
- **Not done in M3**: the inventory screen's 2x2 grid uses the same code path and button (above the result slot), but is not
  covered by a test yet. Recipe-book placement is M7.
- `makeMockServerPlayerInLevel` deprecation warning is unchanged.

## M4 — Furnace family and the pin (2026-10-21)

Added: `recipe/FurnaceState`, `FurnaceResolver`, `StationBlock` (duck interface), `PolyCraftEpoch`; `network/StationViewers`,
`FurnaceOffers`, `SetPinPayload`; mixins `PCAbstractFurnaceBlockEntityMixin`, `PCAbstractFurnaceMenuAccessor`,
`PCAbstractContainerMenuMixin`; client lock toggle, disabled options, and furnace-screen support in `ConflictOverlay`;
placeholder lock icons from `polycraft/design/make_placeholders.py` (`lock_toggle.png`, `lock_toggle_on.png`, 12x12).

How it works:
- The furnace, blast furnace and smoker share one mixin on `AbstractFurnaceBlockEntity`. It wraps the two places vanilla looks
  up the recipe (`serverTick`, and `getTotalCookTime` when the input item changes). Pins and the recipe in use are saved on the
  block (`polycraft_pins`, `polycraft_active_input`, `polycraft_active_recipe`), so a cook survives a restart unchanged.
- Order: block pin, then the acting player's choice, then pack default, then vanilla. The recipe in use is remembered and only
  chosen again when the input item changes, someone changes the selection or a pin, or the recipes reload. Closing the screen
  does not change it.
- Switching mid-cook keeps the elapsed ticks against the new recipe's total (`CookMath.switchedProgress`); past the new total
  it finishes on the next tick. No fuel or input is touched. Verified by GameTests, including the exact timer values.
- Offers: a furnace doesn't notify its menu, so `FurnaceOffers` runs on each player tick and recomputes only when a stamp
  (input, output, epoch, acting player, rules, recipe set, config) changes. All viewers see the same offer, because it is
  computed from the furnace's own resolution (including the acting player).
- Options the output slot can't take are listed but disabled (dimmed, red tooltip line); the server rejects selecting them and
  pinning them. An already-selected option that can't merge simply stalls the furnace, like any vanilla mismatch.
- Pin toggle: the client asks, the server pins whatever it last offered as selected for that group on that block. Wrong menu,
  wrong group, or a non-furnace menu are ignored.

Decisions and notes:
- **Acting player** is the viewer who clicked most recently in a furnace menu (a hook on `AbstractContainerMenu.clicked`), not
  strictly "changed the input slot". With one viewer these are the same; with two, the one who last clicked anything wins.
  Tell me if you want it narrowed to input-slot changes only.
- **Hopper-fed furnaces** have no acting player, so they use the pin, then the pack default, then vanilla, as designed.
- The per-tick wrapper returns the remembered recipe without scanning all recipes while nothing has changed, so an idle furnace
  costs the same as vanilla plus one string comparison.
- `makeMockServerPlayerInLevel` players are real entries in `level.players()`, which the furnace GameTests rely on to simulate a
  viewer. They tick furnaces by calling `AbstractFurnaceBlockEntity.serverTick` directly, so whole cooks take no game time.
- A first test of mine tried to cook a second, different item into a filled output slot and failed; that was my test, and the
  furnace was right (vanilla won't mix coal and charcoal). Fixed the test.

Verification: 65 unit tests; 29 GameTests (`FurnaceGameTest` adds 10: vanilla default, pin from the start, viewer choice and pin
beating it, mid-cook switch, switch past the new total, closing the screen, disabled options, pin/unpin validation, save and load,
blast furnace and smoker types). NeoForge: compiles, and a dev server boot force-loaded `AbstractFurnaceBlockEntity`,
`FurnaceBlockEntity`, `AbstractFurnaceMenu`, `AbstractContainerMenu`, `CraftingMenu` and `ResultSlot` with all mixins applied and no
errors (temporary check, reverted).
Visual (temporary client test, deleted): furnace screen with the button, the lock beside it, a disabled option, and the lock
turning gold when clicked; the block really pinned the fast recipe. Screenshots in
`C:\Users\bill\AppData\Local\Temp\claude\C--Users-bill-code-minecraft-mods\41e2c44a-9693-42ab-b379-7baf94eacc17\scratchpad\m4-screenshots\`.

TODO(art): lock icons are placeholders (12x12, off and on); replace with your own files of the same names.
Not covered yet: the lock/pin UI on a smoker or blast furnace screen is the same code path but was only screenshotted on the
furnace; the Crafter's pin comes in M5.
