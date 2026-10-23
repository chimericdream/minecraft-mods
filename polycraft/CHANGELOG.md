### Unreleased changes

#### New Features

- Added `priorityNamespaces` to the rules file: list a mod or datapack namespace (like `"othermod"`) to prefer all of its recipes.

### 26.2 - 1.0.0

#### New Features

- Added PolyCraft: when the items in a station could make more than one thing, a small button appears next to the result slot. Click it to choose which result you want.
- Works at the crafting table, your 2x2 inventory grid, furnaces, blast furnaces, smokers, smithing tables, and the Crafter. Campfires use the pack's default, and the stonecutter is left alone.
- Furnaces, blast furnaces, smokers, and Crafters have a lock next to the button that pins a recipe to the block, so automation keeps using it no matter who opens the screen.
- Switching recipes while a furnace is cooking changes the current cook without wasting fuel or ingredients.
- Choosing a recipe from the recipe book counts as choosing it.
- Pack makers can set defaults and hide recipes from the choices with `config/polycraft/rules.json` or a datapack, list conflicts with `/polycraft conflicts`, and export them with `/polycraft export`.
- Not compatible with Polymorph or Polymorph+; the game will not start with both installed.
