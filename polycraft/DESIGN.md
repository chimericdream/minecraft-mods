# PolyCraft — Design

Status: **implemented in the first overnight session** (see `OVERNIGHT-LOG.md` for what was built, how it was checked, and the
small judgment calls). Decisions below are unchanged. Decisions marked ✅ were made by the maintainer; items marked ❓ are open
(see [Open questions](#open-questions)). Anything marked "verify" must be checked against decompiled MC 26.2
source (`mc-source-decompile` skill) before coding against it.

## Pitch

Large modpacks have recipe conflicts: several mods add recipes that consume the same vanilla materials. Today
one recipe silently wins (registry/datapack order). PolyCraft detects those conflicts and lets the player
choose which output they get, at any crafting station. Audience: **modpack makers**, so everything is
configurable, nothing is destructive, and defaults are sane with zero config.

## Positioning and compatibility ✅

Polymorph does something similar but is not on 26.2; the forks the maintainer tried either lack the wanted
behaviour or break on servers. PolyCraft's goals are to be robust, server-correct and well tested, with pack-
author tooling (`rules.json`, conflict export, count-based conflicts, per-block pins). PolyCraft is declared
**explicitly incompatible** with Polymorph and its 26.2 forks in both loaders' metadata (`breaks` in
`fabric.mod.json`, an `incompatible` dependency in `neoforge.mods.toml`), where the loader can express it. Both
mods hook the same recipe lookups, so running them together isn't supported.

## Decisions

1. ✅ **UI**: a cycle button in the station screen. Clicking it opens a **horizontal popup list** of the
   candidate results. When there are more than `visibleOptions` candidates, the list shows scroll arrows
   (left/right) and the mouse wheel scrolls it. The button only appears when 2+ recipes match the current
   inputs.
2. ✅ **Conflict detection**
   - Default: **identical inputs, different outputs.**
   - `detectOverlappingInputs` (config, **default off**): also treat recipes whose inputs can be satisfied by
     the same grid (e.g. tag ingredient that contains another recipe's specific item) as conflicting.
3. ✅ **Persistence**: per-player memory of the last choice per conflict group, stored server-side in player
   data, plus pack-author defaults/priorities/overrides (config and/or datapack).
4. ✅ **Mechanism**: mixins into per-station recipe lookup so the selected recipe is the one used. Recipes are
   **never mutated or removed**, so JEI/REI/EMI and other mods still see everything.

## Core model

- **Conflict group**: a set of recipes of the same `RecipeType` that match the same input state. Identified by
  a stable **group key** = recipe type + canonical input signature (sorted ingredient signature; for shaped
  recipes, the pattern normalised for mirroring and empty rows/columns). Group membership is recomputed on
  every recipe reload (`/reload`, world join, datapack sync).
- **Choice**: `(player, groupKey) -> recipeId`. Stored as ids, not indexes, so reordering is harmless.
- **Block pin** ✅: block-entity stations (furnace, smoker, blast furnace, Crafter) have a **lock toggle**
  beside the main button. Toggling it on pins the currently selected recipe for the current group on the block
  entity: `(groupKey -> recipeId)`, a map, since a furnace sees different inputs over time. Toggling off
  removes that group's pin. Pins are saved with the block entity.
- **Resolution order** when a station needs "the" recipe for the current inputs:
  1. The block's **pin** for this group, if the station is a block entity and one exists. Always wins, even
     over the acting player's own preference.
  2. The **acting player's** remembered choice, if there is one and that recipe is still in the group.
  3. Pack **default/priority** list, first match.
  4. Vanilla behaviour (whatever `RecipeManager` would have returned).
  If a pinned or remembered recipe no longer exists or no longer matches, fall through silently.
- **Acting player**: the player with the station's GUI open (several viewers: the one who most recently changed
  the input slot ✅). For stations with no GUI
  (campfire) or no one watching (hopper-fed furnace, redstone-driven Crafter) there is none, so step 2 is
  skipped. This is what the pin is for. ❓ See open questions.
- **Server authoritative.** The client sends "select recipe X for group G" (validated server-side: X must be
  in G and G must match the player's current station inputs). The server sends the candidate list to the
  client when inputs change (custom payload via Architectury networking).
- **Both sides required** (the UI needs the client). Server-only is not supported; the server checks the client
  has the mod and otherwise behaves exactly like vanilla for that player.

## Stations in scope

| Station | Notes |
|---|---|
| Crafting table / player 2x2 | shaped + shapeless + special recipes; result slot updates live |
| Furnace / blast furnace / smoker | selection applies to the item in the input slot; changes what cooking produces |
| Campfire | no screen, so no button. Uses the pack-configured default for the group if one exists, else normal campfire logic ✅ |
| Smithing table | template + base + addition |
| Furnace / blast furnace / smoker | also get the lock toggle (block pin) ✅ |
| Crafter (1.21+) | has a screen: main button + lock toggle. Crafting is redstone-driven so there is rarely an acting player: the pin is what makes the choice stick; unpinned falls to pack default, then normal crafter rules ✅ |
| Stonecutter | **Out of scope** ✅: it is already a picker, nothing to add |
| Modded stations | any recipe type whose lookup goes through the standard `RecipeManager`/`RecipeManager.getRecipeFor` path; a config allow/deny list by recipe-type id; a documented API for stations that don't |

Brewing is not recipe-driven in vanilla and is **out of scope**.

## Config (YACL via chimeric-lib `YaclConfig`)

| Key | Default | Meaning |
|---|---|---|
| `detectOverlappingInputs` | `false` | Include overlapping (not just identical) inputs in conflict groups |
| `visibleOptions` | `6` | Options shown before the popup list gets scroll arrows |
| `rememberChoices` | `true` | Persist per-player choices; if off, resets to pack default each time |
| `showConflictBadge` | `true` | Small indicator on the button showing how many options exist |
| `enabledRecipeTypes` / `disabledRecipeTypes` | all / none | Per-type allow and deny lists (ids, supports `modid:*`) |
| `debugLogging` | `false` | Log detected groups on reload |

Pack-author rules live in `config/polycraft/rules.json` (hot-reloadable via `/polycraft reload`), with the same
schema also accepted from datapacks at `data/<ns>/polycraft/rules/*.json`:

```json
{
  "priority": ["minecraft:oak_planks_from_logs", "othermod:planks_x4"],
  "exclude": ["badmod:duplicate_recipe"],
  "groups": { "<groupKey or recipe id>": { "default": "mod:recipe_id" } }
}
```

## Commands

- `/polycraft conflicts [modid]` — list detected groups. Any player ✅.
- `/polycraft export` — write `config/polycraft/conflicts.json` so pack authors can copy group keys into rules.
- `/polycraft reload` — reload rules.
- `/polycraft reset [player]` — clear remembered choices.

## Edge cases

Detection
- Shaped recipes: mirrored variants, offset in a larger grid (2x2 recipe in a 3x3 table), `show_notification`.
- Tag ingredients vs concrete items; empty tags; tags that change on reload.
- Recipes with data components / `NBT`-like ingredient predicates (verify: 26.2 ingredient API, custom ingredient
  types from loaders) — if an ingredient can't be canonicalised, treat the recipe as **its own group** (never
  falsely group).
- Special/dynamic recipes (armor dye, firework, map extend, repair): not enumerable; excluded by default.
- Same inputs, same output **and count**, different recipe id: collapse (not a conflict). Same item with a **different count is a conflict** ✅ (primary use case: packs that add higher-yield stair/slab recipes).
- Recipes in different `group` fields (the recipe-book group) — ignore, irrelevant to conflicts.
- Very large packs (10k+ recipes): detection must be O(n) via hashing, done off-thread at reload, and
  lazy-built per recipe type. Budget: <= 250 ms for 10k crafting recipes ✅ (starting point).

Runtime
- Inputs change while popup is open (item moved, hopper feeding a furnace): popup refreshes; if the chosen
  recipe no longer matches, drop to resolution order.
- Shift-click crafting / bulk craft in the table: all crafts in the batch use the selected recipe.
- **Switching mid-cook** ✅ (furnace, smoker, blast furnace): the selection applies to the cook in progress, so
  the new recipe's result is what comes out. Nothing is wasted: the input item is only consumed at completion
  and burning fuel keeps burning. Progress is kept as **elapsed ticks** while the total becomes the new
  recipe's cooking time, so the arrow may jump (forward or back). If elapsed >= the new total, the cook
  completes on the next tick. Experience and the recipes-used bookkeeping follow the recipe that completes.
- **When the active recipe is re-resolved** (so closing a GUI doesn't silently flip it): on input item change,
  on a viewing player changing the selection or pin, and on recipe reload. The station remembers the recipe in
  effect on the block entity; closing the GUI or the acting player leaving does **not** re-resolve. A
  hopper-fed furnace therefore re-resolves per new input item via pin, then pack default, then vanilla.
- Selecting a recipe whose result can't merge into the furnace's output slot (different item, or the stack is
  full): vanilla's "can't smelt" behaviour applies (progress decays). The popup greys such options with a
  tooltip rather than hiding them.
- Crafter: redstone-driven crafting never consults any player's memory; only a pin, then pack default, then
  vanilla crafter rules.
- Hoppers/droppers/dispensers don't craft, but a hopper can feed a furnace/smoker/blast furnace with no player
  present: pin, else pack default/priority, else vanilla. Never "whoever opened it last".
- A pin whose recipe vanishes on reload is ignored (and shown stale in the UI until unpinned).
- Two players viewing one pinned station: both see the pin state; either can toggle it. The pin always wins
  over both players' preferences. We add no permission layer of our own for toggling in v1; claim/protection
  mods already gate access to the block.
- Multiplayer: two players at the same station: choice is per player; the one who takes the result decides.
- Recipe book (**optional for v1**, drop it if mapping display ids to recipes is awkward): clicking a recipe in the book implies a choice; select that recipe as the player's choice.
- Recipe viewer "move items" (JEI/REI/EMI transfer): **post-v1**; same idea, selects that recipe.
- Chosen recipe removed on reload → fall through; player not notified (log at debug).
- `/reload` mid-session: groups rebuilt, open popups refreshed.
- Creative/spectator/lacking client mod: vanilla path.
- Pinned station: lock icon shown in the on state, with a tooltip naming the pinned recipe's output and source.

UI
- Popup must not overflow the screen at any GUI scale; flips side/clamps; keyboard accessible (Tab/arrows,
  Enter, Esc closes without changing).
- Tooltips on options show output item + source mod + recipe id (advanced tooltips).
- Works with other mods that move result slots (position the button relative to the result slot).
- Button hidden in recipe-viewer overlay situations (no overlap with EMI/REI widgets).

## UI art (placeholders for now) ✅

The maintainer will draw the final textures before release. Until then the implementation ships its own
placeholders, and swapping them must be a pure asset change (no code edits).

- Every UI element is a named GUI sprite under `assets/polycraft/textures/gui/sprites/` (verify the 26.2 sprite
  and `.mcmeta` nine-slice conventions), referenced by id from one `PolyCraftSprites` class. No pixel
  coordinates for art live in the widget code beyond each sprite's size.
- **Committed sizes** (maintainer will keep to these): the main button that opens the list is **16x16**; each
  item in the horizontal list is **30x30**, as two textures,
  `assets/polycraft/textures/gui/result_list_item.png` and `result_list_item_active.png` (selected state).
  These are plain fixed-size textures in `textures/gui/` (not nine-slice, and not under `sprites/`), so
  draw them with a direct texture blit; verify the 26.2 call. Placeholders must use exactly these paths and sizes.
- **Lock toggle**: **12x12**, positioned next to the main button (off/on, with hover variants).
- **Scroll arrows**: reuse vanilla sprites by id, no copied textures. First choice is the recipe book's page
  arrows (`minecraft:recipe_book/page_forward`, `page_backward`, and their `_highlighted` variants); not
  confirmed for 26.2 because the cached decompiled sources lack the recipe-book classes, so verify the ids in
  the vanilla assets. They have no disabled state, so hide the arrow when there is nothing to scroll that way.
  If they're missing or unsuitable, fall back to placeholder arrows and say so in the CHANGELOG `TODO(art)`.
- **Popup background**: the "crafting table gray" panel colour (match the vanilla container background; verify
  whether to reuse a vanilla container sprite or fill with the matching colour). No custom texture needed.
- **Conflict-count badge**: no texture. Just the number, drawn as text in the **upper-right corner of the main
  button**, shown when `showConflictBadge` is on.
- **Still placeholder**: the main button's hover/disabled variants.
- Placeholders needed, then: main button (normal / hovered / disabled, 16x16), lock toggle (off / on, each with a
  hovered variant, 12x12), the two 30x30 list-item textures, and the scroll arrows only if no vanilla sprite
  fits. The greyed-out list-item state is drawn by tinting or dimming `result_list_item.png`, not a new texture.
- Placeholders are simple flat-colour PNGs, generated by a small script committed under `polycraft/design/`
  (LF line endings, `encoding="utf-8"` where text is written) so they can be regenerated and are obviously
  temporary. Don't use or imitate vanilla textures.
- Add a `TODO(art)` list to the CHANGELOG's Unreleased section until the real textures land, and a manual
  test item to re-check sizes and nine-slice borders once they do.

## Architecture sketch

```
common/
  core/        pure Java: ConflictDetector, GroupKey, SignatureBuilder, ChoiceResolver, Rules (no MC types in the
               signature math where possible; unit-testable)
  recipe/      RecipeIndex (per reload), StationAdapter (interface), adapters per station type
  network/     SelectRecipePayload (C2S), ConflictOptionsPayload (S2C)
  data/        PlayerChoices (player attachment/saved data)
  config/      PolyCraftConfig (YaclConfig)
  mixin/       lookup mixins per station
  client/      ConflictButton, OptionPopup, screen injection
fabric/ neoforge/   loader glue only; client screen hooks, attachment registration
```

## Resolved questions

| # | Question | Answer |
|---|---|---|
| 1 | Campfire / stonecutter | Stonecutter untouched. Campfire uses configured default group, else vanilla |
| 2 | No-player crafting | Hoppers/dispensers don't craft. Crafter gets a button plus a pin toggle (see row 4) |
| 3 | Same item, different count | Conflict (primary use case) |
| 4 | Locking | Per-block pin toggle on furnaces/Crafter (pin > player > pack default > vanilla). Pack-level disable/lock **deferred to the ideas Inbox** |
| 5 | `/polycraft conflicts` permission | Anyone |
| 6 | Recipe viewers | Recipe book is a nice-to-have for v1 (drop if awkward); JEI/REI/EMI later |
| 7 | Perf budget | <= 250 ms starting point |
| 8 | Public API | v1.1+. Detection is keyed on recipe type, so modded stations should mostly work; modders only add the button to their GUI |
| 9 | Acting player for unpinned furnaces | GUI viewer; several viewers: whoever last changed the input slot |
| 10 | Mid-cook switch | Applies to the cook in progress; keep elapsed ticks, new total time; no wasted fuel or input |
| 11 | Unpinned Crafter | Keep the separate lock toggle for consistency |

## Open questions

None. Re-resolution timing under "Switching mid-cook" is my interpretation of your answer (a closed GUI doesn't
flip the output); flag it if you meant otherwise.
