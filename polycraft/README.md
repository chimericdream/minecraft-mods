# PolyCraft (Fabric/NeoForge)

![Version: 1.0.0](https://img.shields.io/badge/version-1.0.0-blueviolet?style=flat-square) ![Modloader: Fabric](https://img.shields.io/badge/modloader-Fabric-1976d2?style=flat-square) ![Modloader: NeoForge](https://img.shields.io/badge/modloader-NeoForge-1976d2?style=flat-square) ![Client: required](https://img.shields.io/badge/client-required-4caf50?style=flat-square) ![Server: required](https://img.shields.io/badge/server-required-4caf50?style=flat-square)

_Resolve recipe conflicts in large modpacks by letting players choose which output to craft_

## Introduction

In a big modpack, several mods add recipes that use the same ingredients. Normally one recipe wins and the others can
never be crafted. PolyCraft keeps every recipe and lets the player pick which result they want, at any station.

### Minecraft Versions

* 26.2: supported

### Current Features

* A small button above the result slot appears whenever the items you have in a station could make more than one thing. Click it to open a row of the possible results, and click one to choose it. The button shows how many choices there are.
* Works at the crafting table, your 2x2 inventory grid, furnaces, blast furnaces, smokers, smithing tables, and the Crafter. Campfires use the pack's default. The stonecutter already lets you choose, so PolyCraft leaves it alone.
* Your choices are remembered per player, per recipe conflict.
* A lock beside the button on furnaces, blast furnaces, smokers, and Crafters pins the recipe to that block. A pinned block always uses its pinned recipe, even when another player with a different choice is using it, so hopper-fed furnaces and redstone-fired Crafters can be set up once and left alone.
* Switching recipes while a furnace is cooking changes what comes out of the current cook. Time already spent is kept, and no fuel or ingredients are lost.
* Placing a recipe from the recipe book counts as choosing it.
* Recipes that give the same item in a different amount count as a choice, such as a pack that adds a higher-yield stair recipe. Identical copies of the same result do not.
* Nothing is ever removed or rewritten, so recipe viewers still show every recipe.

### For modpack makers

Everything works with no setup. These are the knobs when you want them.

**Choose the default.** PolyCraft's order is: the block's pin, then the choice of the player using it, then your defaults, then whatever the game would normally pick. Set defaults in `config/polycraft/rules.json` (created the first time the game runs), or ship the same file in a datapack at `data/<namespace>/polycraft/rules/<name>.json`:

```json
{
  "priority": ["minecraft:oak_planks", "othermod:planks_x4"],
  "exclude": ["badmod:duplicate_recipe"],
  "groups": {
    "<group key or recipe id>": { "default": "mod:recipe_id" }
  }
}
```

* `priority`: recipes to prefer everywhere, best first.
* `exclude`: recipes never offered as a choice. If the game would have picked one, another matching recipe is used instead.
* `groups`: a default for one conflict. Use a group key from the export below, or a recipe id to apply to any conflict that includes it.
* Edit the config file's rules, then run `/polycraft reload`. Datapack rules reload with `/reload`.

**Find your conflicts.** `/polycraft conflicts [mod]` lists the conflicts PolyCraft found, and `/polycraft export` writes them to `config/polycraft/conflicts.json` with the group keys to copy into your rules.

**Commands.** `/polycraft conflicts` and `/polycraft reset` (forget your own choices) work for everyone. `export`, `reload`, and `reset <player>` need operator permission.

**Settings** (in the config screen, or `config/polycraft.json5`):

| Setting | Default | What it does |
|---|---|---|
| Detect overlapping inputs | off | Also treat recipes that the same items can satisfy as a conflict, even when their ingredient lists differ (for example a tag and one specific item) |
| Enabled / disabled recipe types | empty | Limit PolyCraft to some recipe types, or leave some out. Entries are a type id or `modid:*` |
| Visible options | 6 | How many results the list shows before it gets scroll arrows |
| Show choice count | on | The number on the button |
| Remember choices | on | Save each player's choices with the world |
| Debug logging | off | Extra detail in the game log |

PolyCraft is **not compatible with Polymorph or Polymorph+** (including Polymorph's unofficial ports). They do the same job in the same places, and the game will refuse to start if both are installed.

### Known limitations

* Recipe viewers (JEI, REI, EMI) are not integrated yet; picking a recipe from them is not treated as a choice.
* Only the recipe types the game ships with are looked at when listing conflicts with `/polycraft conflicts` and `export`. Conflicts at a station, including for other mods' recipes that use the standard lookup, are found from the items actually in it.
* Other mods' custom crafting stations need their own button; there is no add-on API yet.
* The picker's button and lock icons are placeholders until the final art is added.

## Notes for Documentation

* How it is designed: [DESIGN.md](./DESIGN.md). How it is tested: [TEST_PLAN.md](./TEST_PLAN.md).
* Pack-maker settings live in `config/polycraft.json5` and `config/polycraft/rules.json`.

## Issues & Suggestions

Please use the [GitHub issue tracker](https://github.com/chimericdream/minecraft-mods/issues) to report any bugs you find or suggest new features.

## Credits

Obviously this mod would not be possible if not for the people at Mojang making an awesome game. Thanks also go to the developers of the Fabric and NeoForge mod loaders and the Architectury API.

Thanks also to the creators of Polymorph, Polymorph+, and similar mods, whose earlier work on choosing between conflicting recipes inspired this one. (PolyCraft uses no code or assets from any other mod.)

## License

This mod is released under the MIT license. [The full text of the license can be found here.](./LICENSE)
