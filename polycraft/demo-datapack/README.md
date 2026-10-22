# PolyCraft kitchen sink datapack

A test and screenshot pack: 48 recipes that conflict with each other (and with a few vanilla recipes) at every station
PolyCraft handles. Not for real worlds. Every recipe id is in the `polycraft_demo` namespace.

## Install

Copy this folder (`demo-datapack`, the one with `pack.mcmeta`) into a world's `datapacks` folder and run `/reload`. Then:

- `/function polycraft_demo:kit` gives you everything below.
- `/function polycraft_demo:stations` places one of each station two blocks in front of you.

The pack's `rules/demo.json` sets a default for two conflicts and hides one recipe (marked below).

## What to try

| Station | Put in | You get a choice of |
|---|---|---|
| Crafting table, 2x2 grid, Crafter | 1 cobblestone | stick, diamond, emerald, stone, coal, lapis, gold ingot, copper ingot, nether star. Nine options, so the list scrolls. A tenth (dragon egg) is hidden by the rules file, so it never shows up. |
| Crafting table, 2x2 grid, Crafter | 1 sand | 1, 4, or 16 glass. The same item in different amounts counts as a conflict. The rules file defaults it to 4. |
| 2x2 grid or crafting table | a 2x2 square of dirt | grass block, podzol, mycelium, moss block |
| 2x2 grid or crafting table | a 2x2 square of honey bottles | honey block (vanilla), stone, blue concrete, diamond, sponge. Each leaves four empty glass bottles in the grid. |
| Crafting table or Crafter | 1 water bucket + 1 lava bucket | obsidian, cobblestone, basalt, magma block. Each leaves two empty buckets in the grid. |
| Crafting table or Crafter | 3 feathers in a row | white wool, 8 arrows, phantom membrane |
| Crafting table | 1 white wool | nothing by default. Turn on **Detect overlapping inputs** and you get paper or 4 string (one recipe wants white wool, the other any wool). |
| Furnace | 1 oak log (any burnable log) | charcoal (vanilla, 200 ticks), 2 sticks (100), coal (200), diamond (400), netherite ingot (1000). Switch recipes mid-cook to watch the arrow change. |
| Furnace | 1 sand | 1 glass (vanilla) or 2 glass (300 ticks) |
| Blast furnace | 1 raw iron | iron ingot (vanilla), gold ingot, netherite scrap, 9 iron nuggets |
| Smoker | 1 beef | cooked beef (vanilla), cooked mutton, leather, bone |
| Campfire | 1 potato | no screen: it uses the pack default, golden carrot. Delete that line from `rules/demo.json` to get vanilla's baked potato. |
| Smithing table | netherite upgrade template + diamond pickaxe + netherite ingot | netherite pickaxe (vanilla), netherite axe, netherite shovel |
| Smithing table | book + lapis lazuli (no template) | name tag, map, compass |
| Stonecutter | anything | untouched, PolyCraft leaves it alone |

Also worth showing:

- **Lock:** pin a recipe on the furnace, blast furnace, smoker or Crafter, then open it as another player or feed it with a hopper.
- **Disabled option:** with something already in a furnace's output slot, recipes that make a different item show greyed out.
- **Per-player memory:** pick an option, leave, come back, and it is still picked.
- `/polycraft conflicts polycraft_demo` lists every group, and `/polycraft export` writes them with their keys.

## Checked

A temporary GameTest (not kept) loaded the pack on a Fabric server and confirmed all 48 recipes load and that the option
counts in the table are right for the crafting table, 2x2 grid, furnace, blast furnace, smoker and smithing table. The
Crafter and campfire use the same lookups as the tests in the main suite, and the overlap row is a config setting that
was not exercised there.
