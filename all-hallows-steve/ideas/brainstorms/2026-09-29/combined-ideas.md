# All Hallows Steve — Combined Feature Ideas

Every idea from the 13 brainstorm files in this folder (`agent1.md`–`agent13.md`), merged, de-duplicated,
and grouped by category. The numbers in brackets show which files suggested the idea, e.g. `[1, 7, 13]`.
More numbers means more of the brainstorms landed on it independently. Where the brainstorms offered
different takes on the same idea, the variants are listed under it.

Existing features for context: the Pumpkin Carving Station (dyeing up to 3 dyes, carving up to 4
stencils), 15 stencils (some loot-only), lit decorated pumpkins (torch / soul / copper / redstone),
shears to unlight, dispenser light/unlight, and torch/shears on vanilla pumpkins.

For the curated, prioritized version, see [`shortlist.md`](shortlist.md).

---

## Contents

1. [Carving station & decorated pumpkins](#1-carving-station--decorated-pumpkins)
2. [Pumpkin lighting](#2-pumpkin-lighting)
3. [Pumpkin display & wearing](#3-pumpkin-display--wearing)
4. [Gourds, crops & farming](#4-gourds-crops--farming)
5. [Orchards, food & drink](#5-orchards-food--drink)
6. [Candles, lanterns & general lighting](#6-candles-lanterns--general-lighting)
7. [Decoration & building blocks](#7-decoration--building-blocks)
8. [Wearables & textiles](#8-wearables--textiles)
9. [Creatures](#9-creatures)
10. [World, weather & atmosphere](#10-world-weather--atmosphere)
11. [Seasons & events](#11-seasons--events)
12. [Villagers, structures & exploration](#12-villagers-structures--exploration)
13. [Survival mechanics](#13-survival-mechanics)
14. [Transport](#14-transport)
15. [Miscellaneous](#15-miscellaneous)
16. [Tone guidance from the brainstorms](#16-tone-guidance-from-the-brainstorms)

---

## 1. Carving station & decorated pumpkins

- **Pumpkin aging/decay and waxing** [1, 2, 7, 8, 13] — Placed carved pumpkins slowly age over in-game
  days. Honeycomb (or another sealant) waxes one to lock its current stage, like copper.
  - Three-ish stages: fresh → soft → collapsing/slumped [13]; fresh → faded → softly weathered [2].
  - Aging shows as dried edges and muted dye rather than looking rotten [8].
  - Keep aging slow or opt-in so decorative builds stay low-maintenance [2].
  - Alternative sealants: maple syrup glaze (also makes the dye look richer) [8], pine resin (matte finish) [8].
  - Composting old pumpkins gives them a useful end [2].
- **Curing** [7] — Fresh pumpkins are damp and fragile. Curing them somewhere dry (or on hay) over a few
  days makes them harder, better to carve, and slower to rot.
- **Carving byproducts** [1, 5, 7, 12, 13] — Carving yields pumpkin seeds and pulp (or "shavings").
  - Seeds roast on a campfire as a snack [1, 7, 12, 13].
  - Pulp goes in a composter or is used in baking [1].
  - Shavings are a candle ingredient [5].
  - Pressed seed oil is lamp fuel [7].
- **More stencils with autumn and folklore motifs** [2, 4, 6, 9, 13] — Oak leaves, wheat sheaves, moths,
  owls, ravens, moon phases, crescent moons, bare trees/branches, a Samhain-style knot, geometric and
  heraldic patterns, and vanilla-flavored eerie ones (warden, sculk shrieker).
- **Stencils as exploration rewards** [2, 9, 13] — Rare stencils found in structures (trial chambers,
  ancient cities, Pale Garden structures), sold by different villager professions, or found in stencil
  books at abandoned orchards. This extends the existing loot-only stencils.
- **Custom stencil editor** [1, 13] — Paint your own design on a pixel grid at the Carving Station and save
  it as a stencil item. Both brainstorms called it the feature players would talk about most, and also
  the biggest single piece of work.
- **Stencil copying and sharing** [1, 8] — Copy a stencil to trade or share it. One version uses copper
  printing plates and stencil books [8].
- **Finishes as well as colors** [2, 4, 8] — Matte wax, whitewash, dark stain, a metallic accent [2]; a
  resin matte finish [8]; "spectral dye" that makes the carved areas glow faintly when lit [4].
- **Expanded autumn dye palette** [9, 10] — Burnt sienna, ochre, russet, and deep amber, made from autumn
  flowers, spices, walnut hulls, pokeberry, goldenrod, or cooked squash. A related idea is a matching
  fabric-dyeing station that reuses the carving station's logic [10].
- **Carving other gourds** [9, 13] — Small gourds carve into tiny hanging lights or gourd "bells". Turnip
  lanterns are a nod to the Irish/Scottish tradition of carving turnips before pumpkins [13].
- **Voxel-depth carving** [7] — Chip away rind thickness rather than applying flat stencils. Thinner walls
  glow brighter but weather faster.
- **Carving as a skill** [7] — A proficiency track (cleaner cuts, finer stencils, less waste) that unlocks
  patterns.
- **Resonant Jack o'Lantern** [3] — Glowstone + lit pumpkin makes a pumpkin that pulses warmly and slightly
  extends a nearby beacon's or conduit's range.

## 2. Pumpkin lighting

- **Candle-lit pumpkins** [1, 2, 7, 13] — Light a decorated pumpkin with candles instead of a torch.
  - 1–4 candles. Light level scales with candle count, candle color tints the glow, and it flickers more
    softly than a torch. Snuff it with an empty hand. Reuses the lit-pumpkin and dispenser code [13].
  - Candle color follows the dye of the candle used [1].
  - Different candle positions or liners change how the eyes and openings look [2].
- **Insert-dependent glow color** [7] — The light source inside tints the result: amber from tallow, cooler
  from glowstone, sickly green from something fungal. This partly exists already with the torch variants.
- **Stencil-dependent light level** [1, 5, 7] — More open designs let out more light, and fine designs
  glow dimmer. This gives stencil choice a functional side.
- **Redstone-controllable carved lanterns** [5] — Carved lanterns that switch on and off with redstone.
- **Light from flint and steel, brightness by dye** [11] — Light a carved pumpkin with flint and steel,
  with brightness scaling by dye color.
- **Shadow projection** [7] — A candle-lit carved pumpkin projects its cut pattern onto nearby walls and
  floors like a real jack-o'-lantern.
- **Lantern alignment ritual** [5] — Four carved lanterns in a circle, activated with flint and steel,
  grant a temporary area buff (night vision, less fall damage).

## 3. Pumpkin display & wearing

- **Wearable decorated pumpkins** [2, 7, 13] — Wear a dyed and carved pumpkin like a vanilla carved
  pumpkin, with the same enderman protection. Your carving becomes your mask [13]. Variations:
  - The vision overlay matches the stencil's openings, with an accessibility setting for how much it
    blocks [2, 7].
  - Pumpkin heads mask you from mob detection at range, a real stealth option [7].
  - A lit pumpkin on your head is a mobile light source that burns fuel and makes you more visible [7].
  - Rind weight and cut fineness affect the movement and vision penalty [7].
- **Hanging and wall-mounted pumpkins** [2, 9] — Brackets and hangers for fences and slabs. A hanging
  pumpkin could swing gently in the wind [9].
- **Display forms** [8, 11] — A stand, a windowsill block, a fence-post mount [8], or armor-stand display
  [11], so carved pumpkins read as household objects and not just helmets.
- **Pumpkin in a wreath** [8] — A carved pumpkin can sit in the center of a wreath.

## 4. Gourds, crops & farming

- **Squash and gourd varieties** [1, 2, 4, 5, 7, 8, 9, 11, 12, 13] — The single most-suggested idea.
  White "ghost" pumpkins, warty heirlooms, striped gourds, butternut, acorn, delicata, blue Hubbard,
  Cushaw, Jarrahdale, turban squash, and small ornamental gourds.
  - White pumpkins make great bases for dyeing [13].
  - Each variety serves a purpose (baking, soup, seeds, decoration) so they aren't inventory clutter [2].
  - Some carvable, some decorative, some edible [1, 5, 9].
  - Different rind hardness, carving detail, and light transmission, so some make better lanterns and some
    are better to eat [7]. Different shapes get different stencil sets [8].
  - Found as rare seeds [13]. Can be stacked into piles or cornucopias [10].
- **Giant pumpkins** [1] — A slow, carefully tended crop that grows into a 2×2 or 3×3 centerpiece.
- **Pumpkin breeding / seed saving** [7, 8] — Adjacent vines cross-pollinate for simple traits such as
  size, color, and rind thickness [7]. Seeds from a well-colored pumpkin favor that color in the next crop
  [8].
- **Tall corn** [1, 4, 10, 11, 12] — A 2–3 block tall crop for field mazes. Yields corn, husks, and
  stalks, which bundle into shocks and husk decorations. Multicolored heirloom corn [10].
- **Scarecrows** [1, 2, 5, 7, 10, 12, 13] — Built from sticks, hay, and a carved (decorated) pumpkin head.
  They can be dressed like an armor stand. Suggested functions vary:
  - Scares off crows [1, 5, 13] or rabbits eating crops [10, 13].
  - Reduces crop trampling within a radius [5, 7].
  - Deters passive mobs from fields [7], or hostile ones such as zombies and phantoms [12].
  - Purely decorative and stationary [2].
  - Weathers over time and needs repair [7].
- **Pumpkin patches** [9, 11] — Natural clusters of pumpkins and gourds in plains biomes.
- **Cranberries** [12] — A vine crop grown in shallow water.
- **Acorns** [11, 12] — Dropped by oaks. They plant as saplings (possibly faster ones) or press into oil.
- **Dried berry bushes** [12] — A withered sweet berry bush variant with a different harvest.
- **Trellising and hauling** [7] — Pumpkins grown on fence posts, and wheelbarrows or handcarts for moving
  harvests.
- **Harvest cycle** [6] — Crops and resources that become available or change over time.
- **Mill and flour** [12] — Mill block → flour → bread and pies.

## 5. Orchards, food & drink

- **Cider press** [1, 2, 5, 7, 8, 9, 10, 11, 12] — Apples (or pumpkins + sugar [5]) pressed into cider.
  - Mild warmth effect, cold resistance, or saturation [5, 7, 9, 11].
  - Ages in barrels: to a stronger variant [1], or sweet → dry → vinegar [10]. A cider barrel that slowly
    turns juice into cider [12]. Fermentation-bucket version [9].
  - Mulled or spiced cider variant [7, 11, 13].
  - Pomace becomes compost or animal feed [2].
  - Pressing as a multiblock structure [11].
- **Apple and orchard trees** [1, 2, 5, 11] — Apple (and possibly pear) trees that drop fruit seasonally.
  Grafting onto oaks [5].
- **Maple sap and syrup** [1, 5, 8, 12] — Tap trees with a spout and bucket, then boil the sap into syrup
  at a campfire, cauldron, or smoker. Used as a sweetener or pumpkin glaze [8].
- **Seasonal dishes** [1, 4, 6, 9, 11, 12, 13] — Pumpkin pie, apple pie, squash or pumpkin soup, vegetable
  stew, apple crumble, apple fritters, cinnamon rolls, candied apples, roasted chestnuts, baked apples,
  and roasted pumpkin seeds.
  - **Soul cakes** [13] — Based on the real "souling" tradition of Hallowmas.
  - **Oven or hearth baking** [12] with multi-step recipes. Lattice pie patterns for craft value [7].
  - Mild effects: comfort, faster hunger regen, saturation, brief speed, less fall damage, removes mining
    fatigue [1, 4, 9, 11].
- **Preserves** [7, 8, 12] — Jams, pickled vegetables and gourds, dried apple rings, mushroom conserve, and
  cranberry sauce, in glass jars with wax seals.
- **Spices** [5, 8, 9, 11] — Cinnamon, nutmeg, cloves, star anise, and saffron from new bushes or rare
  trees. Used in food, candles, and dyed wax, and as dye sources.
- **Drying rack** [1, 2, 3, 10] — Slowly dries herbs, flowers, apples, berries, corn husks, and mushrooms,
  visibly changing from fresh to dried. Makes decorations and long-lasting food.
- **Mushroom foraging** [7, 9, 10, 11] — Chanterelles, porcini, oyster, enoki, chicken/hen of the woods,
  lobster mushrooms. They grow on specific surfaces (logs, leaf litter) or only in autumn. A cold, damp
  "mushroom flush" and fairy rings [7].
- **Root cellars** [7] — Cool storage that keeps food fresh longer than a chest.

## 6. Candles, lanterns & general lighting

- **Candlemaking** [3, 7, 8, 10, 12] — Beeswax and tallow candles.
  - A candle-dipping station that reuses the carving station's logic [3].
  - Tapers, pillars, and a hearth candle that melts down and can be recast [8].
  - Different burn times and light levels, and dimming before going out [7].
  - Rushlights and tallow as dim, smoky early-game light that goes out in rain [10].
  - Beeswax from autumn-active beehives [12].
- **Spice candles** [5] — Give a "comfort" effect that slightly reduces monster spawning nearby.
- **Autumn lanterns** [4, 6, 9, 11, 12] — Black iron lanterns with amber, red, or orange glass [9, 11];
  wrought-iron or carved styles [12]; warm flickering "emberlight" [4].
- **Candle stands and candelabras** [9, 12] — Stackable holders with a subtle flicker.
- **Luminaria paths** [7] — Placed light trails that villagers walk along at night.
- **Light as an economy** [7] — Candles, oils, and lanterns with different colors, brightness, and burn
  times, so managing the night becomes an activity.
- **Remembrance lanterns** [7] — Lanterns placed as memorials to a lost pet, item, or place. Atmospheric
  only.

## 7. Decoration & building blocks

- **Leaf litter** [1, 2, 3, 5, 6, 7, 8, 9, 10, 12] — The second most-suggested idea. Thin, stackable
  fallen-leaf layers (like snow layers) in gold, rust, and brown.
  - Rake into piles or baskets [1, 2, 7, 8, 12].
  - Composts into bone meal, rich soil, or leaf mold [1, 7, 8, 9, 12].
  - Crunches underfoot or muffles footsteps [1, 7, 9, 10, 12].
  - Kicks up particles and slightly slows sprinting [5].
  - Builds up naturally under trees over time [1, 8, 9].
- **Autumn leaf blocks** [1, 6] — Placeable red, orange, and gold leaves for builders.
- **Wreaths and garlands** [1, 2, 5, 6, 8, 11, 12] — Leaves, wheat, twigs, berries, pinecones, and small
  gourds combined into door wreaths, swags, and fence garlands. Several variants from different material
  combinations, with no extra crafting UI [2].
- **Harvest bundles** [1, 4, 10, 11, 12, 13] — Corn shocks, corn husk bundles, wheat sheaves, hanging herb
  and dried-flower bundles, and dried corn hanging from rafters.
- **Hay and straw variants** [3, 4, 6, 10, 11, 12] — Fresh-cut and weathered hay textures, grain stacks,
  packed hay, and thatch roofing blocks with overhanging eaves [10]. Hay seating [1]. Hay that softens
  falls, serves as animal bedding, or burns as fuel [12].
- **Harvest baskets** [2] — Containers that visibly show the apples, pumpkins, or produce inside.
- **Offering bowls** [10] — Placeable bowls filled with grain, fruit, or seeds. Decorative only.
- **Wind chimes** [2, 11, 12] — Hanging blocks with soft, occasional sounds. Wooden chimes, seed-pod
  rattles, or suspended leaves [2]. Can output a redstone signal [12].
- **Autumn flowers** [9, 12] — Chrysanthemums (mums), marigolds, asters, and black-eyed susans. They fill a
  gap in vanilla's warm-tone palette and double as dye sources [9].
- **Pressed flowers** [12] — For books or frames.
- **Gravestones** [13] — Headstones you can write on like a sign, which slowly gather moss like copper
  oxidizing. Decorative only.
- **Wrought-iron cemetery fencing and gates** [13], plus a memorial candle or small shrine.
- **Textiles** [9] — Autumn-colored banners, quilts, and tapestries.
- **New wood types** [4, 9, 11, 12] — Dark, gnarled "Duskwood" [4]; gnarled branches and driftwood [11];
  weathered, reddish, and dark-stained barn wood [12]; rowan (mountain ash) with red berries [9].
- **Bonfire** [10, 12] — A large, long-burning fire as a gathering point. A multiblock of logs, kindling,
  and tinder that burns for days, with woods that add colored or aromatic smoke [10]. Gives nearby players
  regen or warmth [12].
- **Hearth / fireplace block** [8] — Burns applewood, dried leaves, or peat. Nearby players' hunger drains
  slower.

## 8. Wearables & textiles

- **Knitted clothing** [1, 11] — Dyeable scarves, sweaters, and caps in autumn colors.
- **Cloaks and capes** [8, 10, 11] — Dyeable wool cloaks with raisable hoods [10], quilted patchwork capes
  [11], lined coats [8]. Mostly cosmetic, with small cold or rain resistance.
- **Restrained fall palette for wool and linen** [8] — Rust, ochre, ink, cream, pine.
- **Woven bracelets** [10] — From flax or nettle (new plants). Cosmetic.
- **Walnut ink** [8] — For stencil notes and darkening wool.

## 9. Creatures

- **Crows or ravens** [1, 5, 10, 11, 12, 13] — Passive birds that perch on fences and scarecrows, peck at
  crops, and scatter as a flock when approached. They tie into the scarecrow.
- **Will-o'-the-wisps** [13] — Faint lights in swamps and Pale Gardens at night that drift away as you
  approach. Following one sometimes leads to a buried cache with rare stencils. The brainstorm's favorite
  idea.
- **Moths** [12, 13] — Ambient particles or tiny mobs drawn to lit pumpkins and lanterns at night.
- **Owls** [8, 12] — Nocturnal ambient birds that perch and hoot at dusk.
- **Migrating birds** [7, 8, 10] — Geese or crows flying overhead in V formations. Ambient only.
- **Squirrels** [11] — Dart around oaks and drop acorns. Can be fed until they follow you to nuts.
- **Foxes and deer** [3, 12] — An autumn-colored fox more active at dusk [12]. A red fox that drops acorns,
  or deer [3].
- **Migrating butterflies** [11] — In autumn colors.

## 10. World, weather & atmosphere

- **Fog and mist** [1, 4, 6, 8, 9, 10, 11, 12] — Low ground fog at dawn (or dusk) in valleys, forests, and
  orchards, clearing by midday. Denser near water [11]. Light sources create visible beams in it [10].
  Muffles footsteps [8]. Reduces visibility slightly [4, 12].
- **Morning frost** [1, 7, 9, 12] — Frost on grass that melts in sunlight [1, 12] and could slightly slow
  movement [12]. A frosty autumn-to-winter transition zone [9].
- **Falling leaf particles** [5, 6, 9, 11] — Leaves drift from trees, sometimes during "wind" states.
  Client-side and toggleable [5].
- **Ambient sound** [6, 9, 10, 12] — Rustling leaves, owl calls, wind through dry corn, creaking branches,
  acorns thudding to the ground.
- **Light and sky tinting** [9] — Cooler grey-blue skies and warmer orange light near lanterns during
  overcast weather or autumn.
- **Autumn biome** [3, 4, 10, 11] — A rare forest biome with permanent red, orange, and gold trees
  (maple, aspen, beech), leaf-litter ground cover, falling leaves, ambient sounds, and its own spawns.
- **Autumn tinting of existing biomes** [9, 12] — Shift oak and birch leaf colors in some biomes or during
  a season.
- **Pumpkin patches** — see section 4.

## 11. Seasons & events

- **Season progression** [6, 7, 9, 11, 12] — The world moves through autumn: leaves turn, then fall, then
  branches go bare. Fog increases, geese migrate, and crops thrive or stall. One brainstorm proposed it as
  the backbone that other features hang off [7]. Another described it as a calendar that triggers autumn
  worldgen, spawns, and colors [11].
- **Shorter days** [7, 12] — Dusk comes earlier as the season goes on, so lighting infrastructure matters.
- **First frost** [7] — A world event that kills unharvested tender crops and ends the season, giving
  players a deadline.
- **Harvest moon** [1, 7, 8, 11] — A rare larger, warmer-colored moon. Crops grow faster overnight [1, 8];
  carved pumpkins stay lit longer [8]; mobs spawn with a subtle orange glow [11].
- **The Thin Night** [13] — Tied to a moon phase (e.g. the new moon) rather than the calendar, and can be
  turned off in config. Brings low mist, more crows, more wisps, and harder-flickering candles.
- **Real-calendar triggers** [5, 11] — Features that appear only in October.

## 12. Villagers, structures & exploration

- **Harvest merchant** [5] — A seasonal wandering trader who sells dyes, rare stencils, cider, gourds,
  and banner patterns.
- **Village harvest festival** [7] — Villagers set out stalls and lanterns, and trades shift toward
  preserves, textiles, and seeds.
- **Abandoned orchard** [8, 11] — A rare structure with overgrown apple rows, a weathered barn or cider
  shed, a press, empty jars, a stencil book, and a hidden cellar with loot.
- **Villager professions trade different stencils** [2] — see section 1.

## 13. Survival mechanics

- **Warmth and cold** [1, 5, 7, 8, 9, 11, 12] — Warm foods, cider, clothing, and hearths give cold
  resistance or "comfort" effects. A heat radius around fires during cold snaps [7].
- **Food spoilage and preservation** [7] — Implied by root cellars and preserves.

## 14. Transport

- **Hay wagon** [3] — A land "boat" entity for two, made from packed hay, a cart chassis, and a lead.
- **Hayride minecart** [5] — Hay-bale seats. Plays harvest music on powered rails and gives riders a
  "joy" effect (faster XP gain).
- **Wheelbarrows and handcarts** [7] — For hauling harvests.

## 15. Miscellaneous

- **Compatibility with popular similar mods** [6].
- **Themed music** [6].
- **Harvest-themed banner patterns** [5, 6].

## 16. Tone guidance from the brainstorms

The prompt asked for ideas that weren't campy or horror, and several brainstorms added tone notes. The
common ground:

- **Target feel**: quiet, seasonal, slightly melancholy [7]; "a working farmstead preparing for winter,
  not a theme park" [12]; real harvest and Samhain folklore with Minecraft's own eerie side (Pale Garden,
  soul fire, sculk) for quiet unease [13].
- **Avoid**: jump scares, haunted mobs, "spoopy" names, candy as a resource, cartoon skeletons [7];
  animated scarecrows with personalities, trick-or-treat drops, costume sets, screaming blocks, graveyard
  loot [8]; novelty item names [12].
- **Prefer**: real harvest terminology [12], subtle particles over heavy-handed ones [12], lots of ambient
  sound [12], and features that loop back into the carving station [1, 8, 9, 13].
- **Rule of thumb** [8]: "If a feature needs a punchline or a fright to justify itself, it is outside the
  tone you want."
