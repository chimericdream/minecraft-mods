# Test Plan — PolyCraft

Status: draft. Companion to [DESIGN.md](DESIGN.md). Core logic (signatures, grouping, resolution, rules
parsing) is pure Java and unit tested; station integration is GameTested; UI needs in-game checks.

## Automated: unit (JUnit, `fabric/src/test/java/com/chimericdream/polycraft/test/`)

Run: `./gradlew :polycraft:fabric:test`. Bootstrap MC via chimeric-lib `BootstrapMinecraft` where registries are touched.

| Class | Covers |
|---|---|
| `SignatureBuilderTest` | shaped normalisation (mirror, trim empty rows/cols, 2x2 inside 3x3), shapeless ordering, tag vs item, uncanonicalisable ingredient -> unique group |
| `ConflictDetectorTest` | identical inputs/different outputs grouped; same item with different count IS a conflict; same item and count collapsed; different recipe types never grouped; overlap mode off vs on; deterministic group keys across runs |
| `ChoiceResolverTest` | resolution order (block pin > acting player > pack default > vanilla); stale pin/remembered id falls through; pin beats a differing player preference; no acting player skips step 2 |
| `RulesParserTest` | valid/invalid JSON, unknown ids warn not crash, wildcard type lists, datapack + config merge order |
| `PlayerChoicesTest` | serialise/deserialise, unknown ids preserved or dropped (decide in impl), cap on stored entries |
| `ConfigTest` | defaults match DESIGN.md table; out-of-range `visibleOptions` clamped |
| `PerfTest` (tagged slow) | 10k synthetic recipes grouped under the budget in DESIGN.md |

## Automated: GameTest (`gametest` source set)

Fixture datapack registers synthetic conflicting recipes (e.g. `polycraft_test:a`/`b`, both 2 planks -> different
outputs) in every station type.

| Test | Asserts |
|---|---|
| `crafting_default_resolution` | no choice, no rule -> vanilla winner |
| `crafting_player_choice` | select B -> result slot is B; relog -> still B |
| `crafting_shift_click_bulk` | whole stack crafted as selected recipe |
| `furnace_choice` / `smoker_` / `blast_` | selection changes cooked output |
| `furnace_mid_cook_switch` | switching mid-cook changes the output of the current cook; input and fuel not wasted; elapsed ticks kept; elapsed >= new total completes next tick; XP follows the completing recipe |
| `furnace_close_gui_keeps_recipe` | player selects B, closes GUI: output stays B; a new input item re-resolves via pin/default/vanilla |
| `furnace_unmergeable_output` | selected recipe whose result can't merge into the output slot behaves like vanilla can't-smelt |
| `smithing_choice` | template+base+addition conflicts resolve |
| `hopper_fed_furnace_uses_pack_default` | unpinned, no viewer: no player memory leaks to automation |
| `furnace_pin_beats_player` | pinned furnace uses the pin although the viewing player prefers another; unpin -> player preference used |
| `furnace_pin_persists` | pin survives chunk unload/reload and server restart |
| `crafter_pin` | pinned Crafter uses the pin; unpinned -> pack default, else vanilla crafter; survives chunk reload |
| `campfire_uses_pack_default` | configured default used, else vanilla |
| `stonecutter_untouched` | no button, no behavior change |
| `forged_pin_not_in_group` | forged pin payload for a recipe outside the group, or for a non-BE station, is ignored |
| `forged_select_not_in_group` | payload with recipe not in group/inputs is ignored |
| `reload_removes_chosen_recipe` | falls through, no exception, no stale button |
| `no_conflict_no_button_payload` | single-recipe case sends nothing |

## Manual (in game), Fabric and NeoForge

Setup: dev world with the fixture datapack; `/give` planks.

1. **Button appears only on conflict** in each station; absent otherwise.
2. **Popup**: horizontal list; with > `visibleOptions` candidates arrows appear and the wheel scrolls; selection highlights; Esc closes without change.
3. **GUI scales 1-4 + small window**: popup never clipped or overlapping the result slot.
4. **Keyboard** navigation of the popup.
5. **Tooltips** show output, source mod, recipe id (F3+H).
6. **Persistence**: choose, relog, restart server -> remembered.
7. **Two players** at one table pick differently.
8. **Recipe book click** (if it shipped in v1) sets the choice (JEI/REI/EMI transfer is post-v1).
8a. **Pin toggle**: lock icon appears only on furnace/smoker/blast furnace/Crafter, not on the crafting table or smithing table; tooltip names the pinned output; toggling off restores player preference.
8b. **Crafter**: pin persists across relog; behaviour when the block is broken and replaced is documented.
9. **Config**: toggle `detectOverlappingInputs` live-reload behaviour (reload needed? record result).
10. **Rules**: priority, default, exclude via `rules.json` and via datapack; `/polycraft reload`.
11. **Commands**: `conflicts`, `export`, `reset`.
12. **Dedicated server** with vanilla client (no mod): plays as vanilla, no errors/kicks.
12a. **Incompatibility**: with Polymorph (or a 26.2 fork) installed, each loader refuses to launch with a clear incompatibility message.
13. **Big pack smoke**: ATM-style 300+ mod pack, record reload time and group count.

## Definition of done (v1)

All unit + GameTests green on `:polycraft:fabric`; manual list passed on both loaders; README, CHANGELOG
and a rules.json example documented; no vanilla-client kick; reload cost within budget.
