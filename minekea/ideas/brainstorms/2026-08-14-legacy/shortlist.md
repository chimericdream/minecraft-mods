# Minekea — Feature Shortlist

Scored and ranked from the legacy backlog in this folder ([`ideas-md.md`](ideas-md.md), your older notes,
and [`potential-features.md`](potential-features.md), the agent brainstorm). There is no
`combined-ideas.md` for a legacy backlog, so this list was built straight from the source notes and
checked against what the mod ships today. Everything that isn't ranked is in
[What was cut and why](#what-was-cut-and-why).

The generic "Add thematic/fun advancements" bullet was dropped on purpose: it was too vague to act on.

## How items were scored

- **Effort**, for a two-loader (Fabric + NeoForge) Architectury mod, counting art, datagen and rendering,
  not just code:
  - **S**: a day or two, mostly assets and data, reuses existing code.
  - **M**: a new block or block entity, a new interaction, or a moderate rendering change.
  - **L**: a new entity, custom rendering, worldgen, or a new UI.
  - **XL**: a system that touches many other features.
- **Value**, ★ to ★★★: fit with the mod's identity statement, and how much players would notice it.
- **Votes**: how many independent brainstorm answers suggested it. Legacy items have none, so this column
  shows `—`.

Ranking is by value relative to effort. The identity is "functional, decorative furniture, some assembly
required: storage that displays, furniture you can use, and finishing touches for every room". Ideas that
deepen existing furniture families (shelves, tables, seats, bookshelves, the wrench and beam connections)
rank above brand-new families (A1). Minekea's **boundary** with But What About...? matters here: blocks that
finish a vanilla family are that mod's job, and Minekea keeps furniture and its own original blocks while
being pared down. So everything ranked is furniture, finishing touches or its own tools, and the
vanilla-family items are called out under [What was cut and why](#what-was-cut-and-why). New blocks add
to creative tabs and to datagen, so every item here includes the models, recipes and tags, not just the code.

## Already shipped

Several items in the agent note exist already, which changes the list:
- **Wardrobes**: the `armoires` family.
- **Cushions**: the `pillows` family, and chairs, foot stools and tables.
- **Display cases, shelves, shutters, secret doors and trapdoors** in bookshelf types, which also cover part
  of the "disguised storage" idea.
- **Beams with wrench connections, covers, and the block painter.**
- **The "hammer"** is today a five-material random-block placer (it places blocks from a number of slots,
  and also mines like a pickaxe). It is the thing your notes call the trowel functionality.

## Ranked list

Linked rows have their own idea files.

| # | Feature | Effort | Value | Votes | My Vote |
|---|---|---|---|---|---|
| | **Tier 1 — Quick wins** | | | | |
| 1 | [Iron shelves](../../shelves/iron-shelves.md) | S | ★★★ | — | Yes |
| 2 | [Copper and gold shelves](../../shelves/copper-and-gold-shelves.md) | S | ★★ | — | Yes |
| 3 | [Vertical stairs and slabs for new blocks](../../block-families/vertical-stairs-and-slabs.md) | S | ★★ | — | Yes |
| 4 | [Port missing legacy block sets](../../block-families/port-legacy-block-sets.md) | S | ★★ | — | Yes/Maybe: some of this might also go in But What About... or Cobblicious |
| 5 | [Bar stools](../../kitchen/bar-stools.md) | S | ★ | — | Maybe |
| 6 | [Coffee and end tables](../../living-room/coffee-and-end-tables.md) | S | ★★ | — | Maybe |
| | **Tier 2 — Solid mid-size features** | | | | |
| 7 | [Hammer rework](../../tools/hammer-rework.md) | M | ★★ | — | Yes |
| 8 | [Trowel replacement item](../../tools/trowel-replacement-item.md) | M | ★★ | — | Yes |
| 9 | [Couches and benches](../../living-room/couches-and-benches.md) | M | ★★★ | — | Yes |
| 10 | [Counters and cabinets](../../kitchen/counters-and-cabinets.md) | M | ★★★ | — | Yes |
| 11 | [Curtains](../../textiles/curtains.md) | M | ★★ | — | Maybe |
| 12 | [Rugs](../../textiles/rugs.md) | M | ★★ | — | Maybe |
| 13 | Wallpaper | M | ★★ | — | "Wallpaper" may be a misnomer, but it's the "Compressed Paper" block. |
| 14 | Crown molding and baseboards | M | ★★ | — | No |
| 15 | [Mirrors](../../decor/mirrors.md) | M | ★★ | — | Maybe |
| 16 | Planter boxes | M | ★★ | — | No. Belongs in JD Crafte |
| 17 | Trellis | M | ★★ | — | No; added to JD Crafte |
| 18 | [Grandfather clock](../../living-room/grandfather-clock.md) | M | ★★ | — | Maybe; toss-up with JD Crafte |
| 19 | [False floor hatch](../../hidden-storage/false-floor-hatch.md) | M | ★★ | — | Maybe |
| 20 | [Disguised crates](../../hidden-storage/disguised-crates.md) | M | ★★ | — | Maybe |
| 21 | Hollow books | M | ★★ | — | No. Belongs in Athenaeum++ |
| 22 | [Block painter extensions](../../tools/block-painter-extensions.md) | M | ★★ | — | Maybe |
| 23 | [Furniture catalog](../../documentation/furniture-catalog.md) | M | ★ | — | Yes, but Opus |
| | **Tier 3 — Big bets** | | | | |
| 24 | [Rolling library ladder](../../living-room/rolling-library-ladder.md) | L | ★★ | — | Maybe |

## Tier 1 — Quick wins

### 1. Iron shelves — S · ★★★

From your notes and the README's "planned" list: wall-mounted shelves that display their contents, in iron.
The shelf family already exists, so this is a new material for it, not a new system. **Recommendation:**
reuse the existing shelf block, model and recipe code, and add the iron texture and tag entries. Keep it
the same size and capacity as wooden shelves unless you want iron to be sturdier or hold more.

### 2. Copper and gold shelves — S · ★★

The agent note's extension of #1: copper shelves that oxidize (and can be waxed, like vanilla copper), and
gold for the treasure room. **Recommendation:** do it with #1, since the code is shared. Oxidation stages
mean four textures and the usual waxing and scraping interactions, so the copper part is the bigger half; gold
is a straight re-skin.

### 3. Vertical stairs and slabs for new blocks — S · ★★

From your notes: vertical stairs and slabs for the blocks added in 1.21.x and 26.x. These are Minekea's own
original blocks (vanilla has no vertical stairs), so they stay here under the boundary rule. **Recommendation:**
generate them with the existing datagen, and first check that the vertical family for the new blocks isn't
already planned in But What About... Keep the names and shapes consistent with the existing ones.

### 4. Port missing legacy block sets — S · ★★

From your notes: blocks and sets the mod had in older versions that haven't been ported to 26.x, such as
stairs made from logs. **Recommendation:** start with an audit that diffs the old version's block list
against the current one (the `LogWoodFamilies` class already exists, so some are in), and then port only
what is Minekea's own and not a vanilla family. Anything that finishes a vanilla family belongs in But What
About...?.

### 5. Bar stools — S · ★

Taller foot stools for counter seating. The stool and chair families exist. **Recommendation:** a taller
model of the existing stool in each wood type, with the same sit behavior. Worth doing mostly alongside #10,
since the counters are what they go with.

### 6. Coffee and end tables — S · ★★

Lower-profile tables that visually connect with the existing table types. **Recommendation:** add low and
tall variants of the existing table block, sharing its connection logic, so they join up with the
tables you already have. Cheap because the family is built.

## Tier 2 — Solid mid-size features

### 7. Hammer rework — M · ★★

From your notes: remove the trowel functionality from the hammer, make right-click convert a block to its
cracked or cobbled variant when one exists, and break a 3x3 area instead of one block. **Recommendation:**
the changes affect an item players already own, so follow N1: add the new behavior behind a config toggle
for a version, and only remove the placer behavior in a major release with a changelog note (#8 gives it a
new home first). The 3x3 break should respect tool tier and hardness, and "convert" should be a data-driven
map (block to variant) so packs can extend it. Decide whether 3x3 is always on or a sneak toggle.

### 8. Trowel replacement item — M · ★★

A new item that takes over the random-block placing the hammer does today, per your notes. **Recommendation:**
do this before #7. Port the slot handling and the placement logic into a new "trowel" item with its own
recipes, keep the hammer's placer behavior working for now, and convert existing hammers' stored slots on
use if possible. It is mostly a move, not new code.

### 9. Couches and benches — M · ★★★

Multi-seat furniture using the existing seat entity, with corner pieces that connect like beams (wrench
toggles). **Recommendation:** the biggest living-room piece. Reuse the wrench's connect and disconnect
language so couches, counters and trim behave like beams, which the agent note calls "assembly language".
Start with straight and corner pieces in the existing wood types.

### 10. Counters and cabinets — M · ★★★

Kitchen-height storage in every wood type with drawer and door fronts, where the cabinet interiors work like
small chests. **Recommendation:** use the existing crate and barrel container code for the inventory, and make
counters connect like beams so a run of them looks continuous. This is the most useful storage idea in the
list. A sink is left for later (see the honorable mentions).

### 11. Curtains — M · ★★

Dyeable, two-block-tall, open and close on right-click or redstone, matching bed colors. **Recommendation:**
use the block painter for dyeing (see #22) rather than 16 separate blocks, and keep the open/close state a
blockstate so redstone works naturally.

### 12. Rugs — M · ★★

Patterned carpets, either woven patterns per fiber type or driven by banner patterns. **Recommendation:**
the banner-pattern version is the more interesting one, but it needs a block entity and a renderer. The
simpler start is a small set of fixed patterns in dye colors, reusing the covers and the painter.

### 13. Wallpaper — M · ★★

Your README credits a wallpaper texture but doesn't list it as a feature. Dyeable wallpaper panels with
matching trim would finish the walls the covers started. **Recommendation:** treat it as a cover-family
member (so it reuses the cover shapes) with the painter for color. Pairs with #14.

### 14. Crown molding and baseboards — M · ★★

Trim blocks in the beam and cover family for finishing interior walls. **Recommendation:** thin trim
shapes that connect around corners, built with the beam connection code, in the same wood types. Do this with
#13 so a room can be finished in one pass.

### 15. Mirrors — M · ★★

A polished block with a subtle fake reflection. **Recommendation:** a shimmer or brightened texture is
enough; real reflections are not worth it. Use a texture with a slight animation or a shifting model, and
make the animation optional (A3).

### 16. Planter boxes — M · ★★

Placeable soil containers that accept any vanilla plant, with a window-box variant that snaps under windows.
**Recommendation:** start with a box that holds one plant and renders it, and treat the window box as a
variant of the same block. Skip growth mechanics: it should be decorative, not a farm.

### 17. Trellis — M · ★★

A climbable lattice in all wood types that vines and glow berries can grow along. **Recommendation:** make
it climbable first, and make vine growth a stretch. Climbable-only already makes it a useful building piece.

### 18. Grandfather clock — M · ★★

A two-block-tall clock whose face shows the in-game time, with an optional soft tick. **Recommendation:**
render the hands from the world time on the client (a block entity renderer), and make the tick sound a
config option, default off (A3).

### 19. False floor hatch — M · ★★

A trapdoor that perfectly mimics the covering block. It is one of the "other types of hidden or disguised
storage" the README promises. **Recommendation:** build on the existing secret trapdoors and the cover
family. A hatch you can still find with the wrench is friendlier than a perfect one (see #20).

### 20. Disguised crates — M · ★★

Crates skinned as ordinary building blocks and revealed with the wrench. **Recommendation:** store the
disguise as a block entity setting, reuse the crate inventory, and give the wrench a way to reveal or
reset. Make sure it works with hoppers like the plain crates do.

### 21. Hollow books — M · ★★

A bookshelf-slot item with a small hidden inventory, where the bookshelf looks identical. **Recommendation:**
only on the mod's own variant bookshelves, which already display books. Decide how the hidden inventory is
opened (a sneak-click on the specific book) so it is hard to trigger by accident.

### 22. Block painter extensions — M · ★★

Let the painter recolor the new textiles (curtains, rugs, cushions, wallpaper). **Recommendation:** not a
separate project; build it into #11–#13 as they land. Listed here so it isn't forgotten.

### 23. Furniture catalog — M · ★

A Patchouli "showroom catalog" that lists every Minekea block with its recipe. **Recommendation:** only if
you want an optional Patchouli dependency, and generate it with datagen from the block list so it stays
accurate. Low value until the block count is stable, since the mod is being pared down.

## Tier 3 — Big bets

### 24. Rolling library ladder — L · ★★

A ladder that attaches to bookshelf walls and slides along them, pairing with the variant bookshelves.
**Recommendation:** needs a custom movement mechanic or an entity and a way to attach to the shelf row. The
nicest piece in the library set, and the hardest to get to feel right. Do it last.

## Honorable mentions

- **Sink** (a decorative water block, with an optional infinite-water config). Do it with #10 if you want a
  complete kitchen.
- **Fireplace mantel, room divider screens and interior window frames.** Reasonable finishing pieces once
  #13 and #14 exist.
- **Armchairs.** The pillows and chairs mostly cover this; revisit if #9 doesn't.
- **Garden arch, weathervane and mailbox.** Small garden pieces that could follow #16 and #17.

## What was cut and why

**Belongs in But What About...? (boundary).**
- Anything that finishes a vanilla block family (stairs, slabs, walls, cracked, chiseled or mossy variants
  for vanilla blocks). This is also the reason for the audit step in #4.

**Hard to build for the payoff.**
- **Painting safe.** An item-frame-style painting that swings open over a cavity. It needs a custom hanging
  entity; #21 and #20 cover the hidden-storage need more cheaply.
- **Bunk beds and loft frames.** Beds are special blocks in 26.x, so a decorative frame is a lot of work for
  a small result.
- **Plate and mug items.** A new item and display system for a small decorative gain.

**Merged into another item.**
- **Wrench connection modes for new families**, into #9, #10 and #14. It is a principle, not a feature.
- **Other hidden storage**, into #19–#21.

**Too vague to act on.**
- **Thematic advancements** (dropped; see the intro).

## Suggested first arc

Do #1 and #2 (iron and other metal shelves), since the README already promises them, plus #3 and #4 for the
loose ends from your notes, and #6 (coffee and end tables) as a cheap table win. Then the hammer pair:
#8 first (move the placer to its own item), then #7. The living-room and kitchen set (#9, #10) is the next
headline, with #11–#14 finishing the room. The hidden-storage items (#19–#21) fulfill the README's other
promise. #24 is a later project.
