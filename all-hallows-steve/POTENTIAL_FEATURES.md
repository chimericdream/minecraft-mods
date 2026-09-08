# Potential Features — All Hallows Steve

Brainstormed, thematically appropriate feature ideas. Nothing here is committed or implemented; these are starting points for future planning.

The mod's identity: a Halloween-themed content pack — new blocks, items, and decoration options built
around jack-o'-lanterns, pumpkins, and other harvest/spooky-season flavor.

## Pumpkin Dyeing

The headline feature: let players dye carved/uncarved pumpkins (and jack-o'-lanterns) an arbitrary
color, the same way [[Shulker Stuff]]'s Dye Station lets players recolor shulker boxes without
wasting dye on a crafting-grid re-dye.

* **Dye Station analog** — a dedicated block (`DyeStationBlock` equivalent) with its own GUI/screen
  handler; right-click opens a menu where a pumpkin/jack-o'-lantern item plus a dye item combine into
  a recolored output, mirroring `ShulkerStuffDyedColorComponent`'s `lidColor`/`baseColor` pattern
  (a pumpkin only needs a single stored color, so likely a simpler one-field data component).
* **Rendering** — placed pumpkins/jack-o'-lanterns need a render-state mixin or custom
  `BlockEntityRenderer` to tint the model at render time from the stored color component, matching
  how Shulker Stuff patches `ShulkerBoxRenderState`/the shulker box item renderer. Check for
  z-fighting if any tint overlay is drawn as a second coplanar layer (see CLAUDE.md's
  custom-block-model-rendering note) — likely avoidable here if the tint is a straight color multiply
  on the existing model instead of a decal.
* **Data component + codec** — a `DyedColorComponent(int color)` record with a `Codec`, registered the
  same way `ShulkerStuffComponentTypes` registers its dyed-color component.
* **Undye** — a water-bottle slot (or similar) to return a pumpkin to its natural orange, matching the
  "no wasted dye" ethos of the Shulker Stuff station.
* **Scope question to resolve before implementation**: does dyeing apply to the block form, the item
  form, or both? Shulker Stuff dyes the item (color travels with it into block form when placed);
  the same model probably fits pumpkins.

## Other Halloween content (unstarted brainstorming)

* **New carved pumpkin variants** — alternate jack-o'-lantern face styles (in addition to color),
  selectable at carving time.
* **Candy/treat items** — harvest-season food items with minor effects (short buffs), themed around
  trick-or-treating.
* **Halloween mob decorations** — cosmetic blocks (fake tombstones, cobwebs, hanging lanterns) for
  seasonal builds.
* **Datagen coverage** — every new block/item needs recipes, loot tables, and (per CLAUDE.md's MC
  26.2 datagen gotcha) component binding at the top of any datagen path that reads item components —
  relevant here since the dye recolor path is exactly that kind of component-reading code.
