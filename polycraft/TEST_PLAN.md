# Test Plan — PolyCraft

Companion to [DESIGN.md](DESIGN.md). Core logic (signatures, grouping, resolution, rules, pins, cook arithmetic) is plain Java
and unit tested. Station behaviour is tested by GameTests that drive the real menus and block entities on a real server. What
a person has to judge (feel, layout at different GUI scales) is the manual list at the end.

Status as of the first overnight session: **65 unit tests and 38 GameTests, all passing** on Fabric. NeoForge compiles and its
dev server loads every mixin target with all mixins applied; NeoForge has no GameTest runner in this repo, so its behaviour is
covered by the shared common code plus that load check.

## Automated: unit (`fabric/src/test/java/com/chimericdream/polycraft/test/core/`)

Run: `./gradlew :polycraft:fabric:test`. No Minecraft bootstrap is needed.

| Class | Tests | Covers |
|---|---|---|
| `SignatureBuilderTest` | 13 | shaped normalisation (mirror, empty rows and columns, 2x2 inside 3x3), shapeless order and counts, ordered slots, tag vs item list, unknown ingredient gives its own group, malformed shapes, stable keys |
| `ConflictDetectorTest` | 11 | identical inputs with different outputs conflict; same item in a different amount conflicts; same result collapses (keeping the earlier recipe); lone recipe is no conflict; overlapping mode on and off; unique signatures never group; `detectAll` ordering and exclusions |
| `ChoiceResolverTest` | 10 | pin > player > pack default > vanilla; stale pin, player choice, and pack entries fall through; no acting player; empty options rejected |
| `RulesParserTest` | 12 | full file, empty, invalid JSON, wrong types, unknown keys, bad ids, groups without a default, merge precedence, pack order, exclusions, `IdFilter` allow/deny/wildcards |
| `PlayerChoicesTest` | 15 | per-player and per-group storage, least-recently-used cap, remove, clear, prune, unknown ids survive a save, restore trims, `PinMap` replace/cap/round trip, `CookMath` switching and output-merge rules |
| `ConfigTest` | 3 | defaults match DESIGN.md; `visibleOptions` clamping |
| `PerfTest` (tagged `slow`) | 1 | 10,000 recipes grouped well inside the 250 ms budget (about 55 ms) |

## Automated: GameTest (`fabric/src/gametest`, run with `./gradlew :polycraft:fabric:runGameTest`)

The fixture datapack (`data/polycraft_test`) adds conflicting recipes: cactus (crafting; a: 3 green dye, b: 5, c: lime dye
excluded by rules, plus an identical copy of a), sweet berries (no rule), eight amethyst-shard recipes (scroll arrows),
gunpowder (smelting; coal in 100 ticks or charcoal in 200), stick + flint (smithing), and an egg (campfire; paper preferred by
the rules). Furnaces are ticked by calling the real `serverTick`, so a cook takes no game time.

| Class | Tests | Covers |
|---|---|---|
| `CommandGameTest` | 10 | datapack rules load; `conflicts` lists the fixture groups; `export` contents (collapse, exclude, both options); `export` and `reload` need op; `reset` self and other; `rememberChoices` off; save and load of choices |
| `CraftingGameTest` | 9 | vanilla default; pack default beats vanilla; player choice beats pack default and persists into a new menu; per-player isolation; forged selections ignored; excluded and duplicate recipes not offered; lone recipe makes no offer; taking the result uses up inputs; shift-click crafts everything with the chosen recipe |
| `FurnaceGameTest` | 11 | unwatched furnace uses vanilla; pin used from the start; viewer choice applies and a pin beats it; mid-cook switch keeps time and wastes nothing; switch past the new total finishes at once; closing the screen keeps the recipe; options the output slot cannot take are disabled and cannot be chosen; pin and unpin validation; pins and the recipe in use survive save and load; blast furnace and smoker types; recipe-book placement counts as a choice |
| `StationsGameTest` | 8 | smithing table offers and applies a choice; the 2x2 grid; stonecutter left alone; unwatched Crafter uses the pack default; a pin makes a Crafter choice stick; Crafter pin saves and loads; campfire uses the pack default; recipe-book placement at the crafting table counts as a choice |

Mixin coverage: the smithing, Crafter, and campfire tests would each fail without their mixin, because vanilla would pick a
different result. Run `bun run verify:gametests` after adding or renaming a test class.

## Visual checks done (temporary client GameTests, deleted afterwards)

Crafting table (button, count badge, popup, scroll arrows, scrolling, hover, choosing), furnace (button, lock off and on, disabled
option), smithing table, and Crafter (button above the preview slot, lock, popup). Screenshots are linked from
`OVERNIGHT-LOG.md`. These prove it renders and the click path works; they do not prove it feels right.

## Manual (in game), Fabric and NeoForge

Setup: a dev world with the fixture datapack (or any pack with conflicts); `/give` yourself the ingredients.

1. **Button appears only on a conflict** at the crafting table, 2x2 grid, furnace, blast furnace, smoker, smithing table, and Crafter; absent otherwise.
2. **Popup**: opens and closes; Esc closes it without changing anything; clicking outside closes it; with more than `visibleOptions` options the arrows appear and the mouse wheel scrolls.
3. **GUI scales 1 to 4 and a small window**: the button, lock, and popup are never clipped or hidden behind something; the popup flips below the screen when there is no room above.
4. **Keyboard**: with the popup open, Left and Right move, Enter or Space chooses.
5. **Tooltips** on options show the result, the mod it came from, and (with F3+H) the recipe id; a disabled option says why.
6. **Persistence**: choose, relog, restart the server: the choice is remembered. Two players at one table choose differently.
7. **Pin**: pin a furnace, then have a second player with a different choice open it; the pinned recipe still cooks. Feed a pinned furnace with a hopper. Pin a Crafter and fire it with redstone.
8. **Mid-cook switch**: change the recipe while the arrow is part-way; the arrow may jump but fuel and items are untouched.
9. **Recipe book**: click a conflicting recipe in the book at the table and at a furnace; it becomes your choice.
10. **Config**: toggling `detectOverlappingInputs` (does it need a world reload? record the answer), `showConflictBadge`, `visibleOptions`; the recipe-type allow and deny lists.
11. **Rules**: `priority`, `exclude`, and a group `default` from `config/polycraft/rules.json` and from a datapack; `/polycraft reload`.
12. **Commands**: `conflicts`, `export`, `reset` for yourself and another player, permissions for a non-op.
13. **Dedicated server with a vanilla client** (no mod): plays as vanilla, no errors or kicks. (Not yet tried; whether NeoForge lets a client without the mod join is also worth checking.)
14. **Incompatibility**: with Polymorph or Polymorph+ installed, each loader refuses to start with a clear message. (Checked on both loaders with a stand-in mod; try the real jars once.)
15. **Big pack smoke**: a 300+ mod pack; note the reload time and the number of conflicts `/polycraft conflicts` reports.

## Definition of done (v1)

All unit tests and GameTests green on `:polycraft:fabric`; both loaders compile and load; the manual list passed on both loaders;
README, CHANGELOG, and a `rules.json` example documented; no vanilla-client kick; reload cost within budget; final art in place
of the lock placeholders.
