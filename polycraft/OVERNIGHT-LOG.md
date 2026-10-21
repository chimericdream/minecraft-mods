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
