# All Hallows Steve — Feature Shortlist

A curated cut of [`combined-ideas.md`](combined-ideas.md). The weakest ideas are dropped (see
[What was cut](#what-was-cut-and-why) at the bottom). The rest are ranked by **value relative to
effort**, so cheap, high-value ideas come first and expensive ones come last even when they are exciting.

## How items were scored

- **Effort** — rough estimate for a two-loader (Fabric + NeoForge) Architectury mod, counting art,
  datagen, and rendering, not just code.
  - **S** — a day or two, mostly assets and data, reuses existing code.
  - **M** — a new block or block entity, a new interaction, or a moderate rendering change.
  - **L** — a new entity, custom rendering, worldgen, or a new UI.
  - **XL** — a system that touches many other features.
- **Value** — how well it fits the mod's identity (pumpkins + a grounded autumn tone) and how much players
  would notice it. ★ to ★★★.
- **Votes** — how many of the 13 brainstorms independently suggested it. This is a signal, not the
  deciding factor.

The ideas that build on the carving station rank highest. They reuse existing code, and they make
the one thing the mod already does well go deeper instead of spreading into a general autumn mod.

## Ranked list

Features voted **Yes** or **Maybe** link to their own file, where each idea is iterated on before
it's built. Those files live at the top of `ideas/`, grouped by theme: `carving/`, `crops/`,
`harvest-crafts/`, `farm-creatures/`, and `folklore/`.

| #  | Feature                                     | Effort | Value | Votes | My Vote |
|----|---------------------------------------------|:------:|:-----:|:-----:|:---:|
|    | **Tier 1 — Quick wins**                     |        |       |       |   |
| 1  | [Candle-lit pumpkins](../../carving/candle-lit-pumpkins.md) |   S    |  ★★★  |   4   | Yes |
| 2  | [More stencils (autumn/folklore motifs)](../../carving/more-stencils.md) |   S    |  ★★★  |   5   | Yes |
| 3  | [Stencils as exploration loot and trades](../../carving/stencil-loot-and-trades.md) |   S    |  ★★   |   3   | Yes |
| 4  | Stencil copying                             |   S    |  ★★   |   2   | No |
| 5  | [Carving yields seeds, roasted pumpkin seeds](../../carving/carving-seeds.md) |   S    |  ★★   |   5   | Yes |
| 6  | Stencil-dependent light level               |   S    |  ★★   |   3   | No |
| 7  | Moth particles around lit pumpkins          |   S    |  ★★   |   2   | No |
| 8  | [Harvest foods (soul cakes, soup, cider)](../../harvest-crafts/harvest-foods.md) |   S    |  ★★   |   7   | Maybe |
| 9  | [Harvest bundles and wreaths](../../harvest-crafts/harvest-bundles-and-wreaths.md) |   S    |  ★★   |   9   | Maybe |
| 10 | Autumn-colored leaf litter + rake           |  S–M   |  ★★   |  10   | No |
|    | **Tier 2 — Solid mid-size features**        |        |       |       |   |
| 11 | [Pumpkin aging + honeycomb waxing](../../carving/pumpkin-aging-and-waxing.md) |   M    |  ★★★  |   5   | Yes |
| 12 | [Wearable decorated pumpkins](../../carving/wearable-decorated-pumpkins.md) |   M    |  ★★★  |   3   | Yes |
| 13 | [White pumpkins and gourd varieties](../../crops/white-pumpkins-and-gourds.md) |   M    |  ★★★  |  10   | Maybe |
| 14 | Hanging / wall-mounted pumpkins             |   M    |  ★★   |   4   | No |
| 15 | [Turnip lanterns](../../harvest-crafts/turnip-lanterns.md) |   M    |  ★★   |   1   | Maybe |
| 16 | [Scarecrow](../../farm-creatures/scarecrow.md)    |  M–L   |  ★★★  |   7   | Yes |
| 17 | [Cider press](../../harvest-crafts/cider-press.md) |   M    |  ★★   |   9   | Yes |
| 18 | [Drying rack](../../harvest-crafts/drying-rack.md) |   M    |  ★★   |   4   | Maybe |
| 19 | [Weathering gravestones + iron fencing](../../folklore/gravestones-and-iron-fencing.md) |   M    |  ★★   |   1   | Yes |
| 20 | Wind chimes                                 |  S–M   |   ★   |   3   | No |
|    | **Tier 3 — Big bets**                       |        |       |       |   |
| 21 | Custom stencil editor                       |   XL   |  ★★★  |   2   | No |
| 22 | [Will-o'-the-wisps + buried caches](../../folklore/will-o-the-wisps.md) |   L    |  ★★★  |   1   | Yes |
| 23 | [Crows](../../farm-creatures/crows.md)            |   L    |  ★★   |   6   | Yes |
| 24 | Harvest moon / Thin Night event             |   L    |  ★★   |   5   | No |
| 25 | [Tall corn](../../crops/tall-corn.md)             |  M–L   |  ★★   |   5   | Maybe |
| 26 | Ground fog                                  |   L    |  ★★   |   8   | No |

---

## Tier 1 — Quick wins

### 1. Candle-lit pumpkins — S · ★★★
Light a decorated pumpkin with 1–4 candles instead of a torch. Light level scales with candle count, and
the glow texture takes the candle's color. Snuff it with an empty hand and relight it with flint and
steel. This is an almost direct extension of the existing lit-pumpkin variants and the
`DispenserBehaviors` helper, and vanilla players already understand candle stacking.
*Note: Minecraft light has no color, so "tinted" means the glow texture or overlay, the same as the
current soul/copper/redstone variants.*

### 2. More stencils with autumn and folklore motifs — S · ★★★
Oak leaf, wheat sheaf, moth, owl, raven, crescent moon, moon phases, bare tree, a Samhain-style knot,
plus eerie vanilla designs (warden, sculk shrieker, pale oak). This is mostly pixel art plus
recipes and datagen, and more designs make every other carving feature better.

### 3. Stencils as exploration loot and trades — S · ★★
Put the rarer new designs in trial chambers, ancient cities, and woodland mansions, and have some sold by
specific villager professions (cartographer, librarian). This reuses the loot-table pattern already in
place for the Heart/Jigsaw/Spawner/Structure Block stencils.

### 4. Stencil copying — S · ★★
Blank stencil + any stencil → two copies, like copying a banner pattern or map. This matters most for
loot-only stencils, and it's a prerequisite for sharing designs if the custom editor (#21) happens.

### 5. Carving yields seeds, roasted pumpkin seeds — S · ★★
Carving a pumpkin at the station returns a few pumpkin seeds. Roast them on a campfire for a small snack.
It's a small detail, but it makes carving feel physical.

### 6. Stencil-dependent light level — S · ★★
Each stencil gets an "openness" value. Mostly open designs let out full light, and fine designs cap it
lower. This gives players a functional reason to choose a design, and it's a small change to how
lit-pumpkin light is calculated. Keep the range modest (e.g. −0 to −4) so torch-type choice still matters
most.

### 7. Moth particles around lit pumpkins — S · ★★
A few small moth particles that flutter around lit decorated pumpkins at night. It's client-side only,
there's no mob AI, and it makes displays feel alive. (This is the cheap version of the "harvest moth" mob
idea.)

### 8. Harvest foods — S · ★★
Two or three well-chosen foods, not a cooking system: **soul cakes** (from the real "souling" tradition),
**pumpkin soup** (bowl food), and **mulled cider** (see #17 for the full cider version). Standard vanilla-
tier effects only.

### 9. Harvest bundles and wreaths — S · ★★
Decorative blocks: corn shocks, wheat sheaves, hanging dried-flower and herb bundles, and a door wreath
with a few material variants. These are simple models and recipes with no machinery. They're the
cheapest way to give builders an autumn palette around their pumpkins.

### 10. Autumn-colored leaf litter + rake — S–M · ★★
Vanilla already has a leaf litter block, so the new part is gold, rust, and red variants, maybe
obtained by composting leaves or at a crafting table. A rake could collect leaf litter and pile it
in bulk. Skip natural build-up over time. It adds block-update load and nobody asked for chores.

---

## Tier 2 — Solid mid-size features

### 11. Pumpkin aging + honeycomb waxing — M · ★★★
Placed decorated pumpkins slowly go through about three stages (fresh → soft → slumped), with muted dye
and softened carving edges rather than visible rot. Honeycomb waxes a pumpkin at its current stage, the
same way it works on copper. Because the mod already recolors and re-carves pumpkins in the station, you
could let the station "restore" a pumpkin, or not, as a deliberate choice. **Make aging opt-in via
config or very slow by default** so decorative builds don't punish players.

### 12. Wearable decorated pumpkins — M · ★★★
Equip a dyed or carved pumpkin on your head with vanilla carved-pumpkin behavior (enderman safety, the
vision overlay). The main work is rendering the tinted and stenciled pumpkin on the player and armor
stand heads. A per-stencil vision overlay is a nice stretch goal; skip the stealth and fuel mechanics.

### 13. White pumpkins and gourd varieties — M · ★★★
The most-requested idea overall. Start with two or three varieties rather than ten:
- **White pumpkin**: a clean base that makes dyed colors look truer. It plugs straight into the station.
- **Warty/heirloom pumpkin**: a different texture, and still carvable.
- **Ornamental gourds**: small non-full blocks for piles and tabletop displays.

Each one needs a stem/crop, a block, seeds, and a way to find them (rare seeds from grass or loot). Add
more varieties only once the pipeline exists.

### 14. Hanging / wall-mounted pumpkins — M · ★★
A bracket block or hanging variant so carved pumpkins can go on walls, fences, and under beams. It makes
lit pumpkins usable as real architectural lighting. Skip the wind-swinging animation, at least at first.

### 15. Turnip lanterns — M · ★★
A small hanging carved-root lantern, nodding to the Irish and Scottish tradition that came before
pumpkins. It has a strong folklore angle, it's cheap-ish (one block, lit/unlit, hanging like a lantern),
and it gives the mod a small lighting option that isn't a full block.

### 16. Scarecrow — M–L · ★★★
The second most-suggested idea. It's an armor-stand-like entity with a (decorated) pumpkin head that can
be dressed. Give it one clear job so it's more than decoration: **no crop trampling and no rabbits eating
crops within a radius**, and later it scares off crows (#23). Keep it stationary and without personality,
as the brainstorms warned. If entity work is too heavy, a static multiblock is a cheaper fallback.

### 17. Cider press — M · ★★
Apples in, cider bottles out. Suggested by 9 of 13 brainstorms. Use vanilla apples rather than adding
orchard trees. Mulled cider (cider + spice or sweet berries) can be the upgraded version. Skip barrel
aging and multiblocks.

### 18. Drying rack — M · ★★
A rack that visibly turns fresh items into dried ones over time: herbs and flowers into bundles (#9), and
apples into dried apple rings. It works like a slow campfire and pairs well with the harvest decorations.

### 19. Weathering gravestones + iron fencing — M · ★★
Headstones you can write on like signs, which slowly gather moss the way copper oxidizes (and can be
waxed, matching #11). Add wrought-iron fencing and gates. It fits the Hallowmas/All Hallows theme
directly. Keep it decorative only (no graves, no loot) to stay on-tone and avoid clashing with
death-chest mods.

### 20. Wind chimes — S–M · ★
A hanging block with soft, occasional chime sounds. It's pleasant but doesn't connect to anything else;
build it only if the sound assets come easily.

---

## Tier 3 — Big bets

### 21. Custom stencil editor — XL · ★★★
Paint your own design on a pixel grid in the Carving Station and save it as a stencil item. This is the
single most distinctive feature on the list, and it would make the mod much deeper. It needs a new UI,
pixel data stored on the item, networking, and dynamic textures on placed, held, and worn pumpkins. Do it
after the carving system is otherwise settled, since it touches everything carving-related.

### 22. Will-o'-the-wisps + buried caches — L · ★★★
Faint lights that drift through swamps and Pale Gardens at night and retreat as you approach. Following
one sometimes leads to a buried cache, a good home for the rarest stencils (#3). It's pure folklore and
eerie without being horror, and it would be the mod's signature step beyond pumpkins. The cost is a light
entity with simple AI, particles, and cache generation.

### 23. Crows — L · ★★
Ambient birds that perch on fences, peck at crops, and scatter as a flock when you approach. On their
own they're just atmosphere, but together with the scarecrow (#16) they form a small gameplay loop. A
full new mob (model, animations, AI, spawning, sounds) is the most expensive part.

### 24. Harvest moon / Thin Night event — L · ★★
A periodic special night, tied to a moon phase so it isn't calendar-locked and can be turned off in
config. It shows a bigger, warmer-colored moon and brings more moths and wisps; carved pumpkins stay lit
longer or candles flicker harder. It's a good way to showcase several features at once, but it's worth
little until #7, #22, and #23 exist.

### 25. Tall corn — M–L · ★★
A 2–3 block tall crop for field mazes, with husks feeding into corn shocks (#9). It's popular and
on-theme, but multi-block crops are fiddly, and it pulls the mod toward general farming.

### 26. Ground fog — L · ★★
Low dawn fog in valleys and forests. Eight brainstorms suggested it, and it has high atmospheric payoff,
but custom fog rendering is fragile across loaders and conflicts with shader packs and other fog mods.
Consider it only as an optional client-side setting.

---

## Honorable mentions

These nearly made the list and would be fine additions if a related feature is already being built:

- **Redstone-switchable lit pumpkins** (S) — lit pumpkins that turn on and off with redstone.
- **Finishes** (M) — whitewash, dark stain, and a matte wax finish in the station alongside dyes.
- **Giant pumpkins** (L) — a 2×2 or 3×3 showpiece crop. Fun, but a lot of work for one block.
- **Autumn flowers** (M) — mums and marigolds for builders. Only worth it if the station gets custom
  autumn dye colors.
- **Harvest merchant** (M) — a seasonal wandering trader selling stencils and gourd seeds, if #3 and #13
  exist.

## What was cut and why

**Scope creep: these belong in a general seasons or farming mod, not a pumpkin mod.**
Season progression, shorter days, first frost, autumn biomes and foliage tinting, warmth/cold systems,
food spoilage and root cellars, preserves, spices, maple sap, orchard trees and grafting, mushroom
foraging, flour mills, cranberries, acorns, candlemaking systems, bonfires and hearths, new wood types,
thatch, knitted clothing, cloaks and bracelets, owls, squirrels, deer, foxes, butterflies, migrating
geese, village festivals, abandoned orchard structures, hay wagons, and hayride carts. Several are good
ideas, but together they'd turn All Hallows Steve into a different, much larger mod, and several overlap
with established seasons, farming, and cooking mods.

**Already in vanilla.** Falling leaf particles, base leaf litter, natural pumpkin patches, and placeable
colored candles.

**Hard to build for the payoff.** Voxel-depth carving, shadow projection, carving skill tracks, pumpkin
genetics and seed-saving, and curing. These are interesting ideas, but each needs a lot of rendering or
system work, and players would barely notice the result.

**Off-tone or mechanically odd.** The beacon/conduit-boosting "Resonant Jack o'Lantern", the lantern
alignment ritual, the hayride "joy" XP buff, spawn-reducing spice candles, brightness scaling with dye
color, glowing harvest-moon mobs, villagers walking luminaria paths, and remembrance lanterns.

**Too vague to act on.** "Compatibility with popular mods", "themed music", and "harvest cycle".

## Suggested first arc

If you want a single direction, **items 1, 2, 6, 11, and 12** together make "your pumpkin" a much richer
object: how it's lit, what's carved into it, how it ages, and wearing it. They mostly reuse existing
code. Then **13 → 16 → 23** (gourds → scarecrow → crows) grows the mod outward into the farm around the
pumpkin, and **22** (wisps) is the signature big feature once the core is solid.
