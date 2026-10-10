# Miniblock Merchants — Feature Shortlist

Scored and ranked from the legacy backlog in this folder ([`ideas-md.md`](ideas-md.md), your older notes,
and [`potential-features.md`](potential-features.md), the agent brainstorm). There is no
`combined-ideas.md` for a legacy backlog, so this list was built straight from the source notes and
checked against what the mod ships today. Everything that isn't ranked is in
[What was cut and why](#what-was-cut-and-why).

The generic "Add thematic/fun advancements" bullet was dropped on purpose: it was too vague to act on. Your
"Custom advancements" bullet is partly shipped already; the specific ones that aren't are ranked as #4.

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

Ranking is by value relative to effort. The identity is a triangle: **profession theme, discovery method,
miniblock catalog**. New ideas must keep all three, so a new profession needs a themed conversion item
found through a specific activity, and a catalog of miniblocks. Content that fits the triangle (more
miniblocks, more professions) is cheap because the pipeline exists: 26 professions are already built from
the same parts. Ideas that change balance or existing worlds are config-gated (N1, A2, A4).

A note on cost: every new **profession** is really a bundle (a villager skin, a conversion item with art,
a loot hook, a chance in the config, and a catalog of roughly 40 miniblocks with skull textures). Treat "M"
for a profession as "reuses the pipeline and still takes a good while".

## Already shipped

- **26 professions** and nearly 1000 miniblocks, each with a conversion item and a configurable drop chance.
- **Conversion of Miniblock Traders datapack villagers** to the mod's professions.
- **An advancement tab** with a root, "trade with any merchant", "trade with all merchants", two item
  advancements, and trade-count milestones (100 to 10,000 trades). This covers the "Full Employment"
  advancement from the agent note.
- Professions deliberately use **no workstation** (the villager's job site is `none`), since conversion
  items are the only way to get one.

## Ranked list

Linked rows have their own idea files.

| # | Feature | Effort | Value | Votes | My Vote |
|---|---|---|---|---|---|
| | **Tier 1 — Quick wins** | | | | |
| 1 | [More miniblocks](../../catalog/more-miniblocks.md) | S | ★★★ | — | Yes |
| 2 | [Head-drop datapack sample](../../integrations/head-drop-datapack-sample.md) | S | ★★ | — | Yes |
| 3 | [Per-profession enable toggles](../../configuration/profession-toggles.md) | S | ★★ | — | Maybe |
| 4 | Conversion advancements | S | ★★ | — | Mostly implemented already |
| 5 | Market Day | S | ★ | — | No |
| | **Tier 2 — Solid mid-size features** | | | | |
| 6 | Uninstall and convert-back path | M | ★★ | — | No. The mod has grown beyond the datapack. |
| 7 | [Data-driven trade tables](../../configuration/data-driven-trade-tables.md) | M | ★★ | — | Maybe |
| 8 | [Endologist profession](../../professions/endologist.md) | M | ★★★ | — | Yes |
| 9 | [Spelunker profession](../../professions/spelunker.md) | M | ★★ | — | Maybe |
| 10 | [Trailblazer profession](../../professions/trailblazer.md) | M | ★★ | — | Yes |
| 11 | [Paleontologist profession](../../professions/paleontologist.md) | M | ★★ | — | Yes |
| 12 | [Musician profession](../../professions/musician.md) | M | ★★ | — | Yes |
| 13 | [Meteorologist profession](../../professions/meteorologist.md) | M | ★ | — | Maybe |
| 14 | Confectioner profession | M | ★ | — | No |
| 15 | [Wandering Peddler](../../discovery/wandering-peddler.md) | M | ★★ | — | Maybe |
| 16 | [Decorative job blocks](../../decor/decorative-job-blocks.md) | M | ★ | — | Maybe |
| 17 | Restock requests | M | ★ | — | No |
| | **Tier 3 — Big bets** | | | | |
| 18 | [Collector's Ledger](../../catalog/collectors-ledger.md) | L | ★★★ | — | Yes |
| 19 | [Completion rewards](../../catalog/completion-rewards.md) | L | ★★ | — | Maybe |
| 20 | [Profession shops](../../structures/profession-shops.md) | L | ★★ | — | Yes |
| 21 | [Modded-structure support](../../structures/modded-structure-support.md) | L | ★ | — | Maybe |

## Tier 1 — Quick wins

### 1. More miniblocks — S · ★★★

From your notes. The catalog is the heart of the mod and more minis is the most direct way to add to it.
The cost is per batch: a texture or skull and a trade entry per block. **Recommendation:** add them in
themed batches to existing professions, each batch a small release, and prioritize blocks added in recent
Minecraft versions that no profession sells yet. Check against the profession's theme; a block that
doesn't belong in any profession is a sign of a missing profession (see #8–#14).

### 2. Head-drop datapack sample — S · ★★

The README promises a sample datapack with loot tables that wire the mod's heads to head-drop mods and
datapacks (More Mob Heads from Vanilla Tweaks, for example). **Recommendation:** ship it in `docs/` as a
ready-to-copy datapack, with a short README section on how it works. It's the kind of promise that is
cheap to keep and annoying to leave open.

### 3. Per-profession enable toggles — S · ★★

Let packs run a curated subset of professions. **Recommendation:** one config toggle per profession,
default on (N1: a world that already has these villagers must keep working). Turning one off should stop
its conversion item from dropping and hide its trades, and it should not delete villagers already
converted.

### 4. Conversion advancements — S · ★★

Specific advancements for the conversion path: "First Convert" (convert your first villager), and
tiered ones for collecting every conversion item or converting every profession. They sit beside the trade
advancements that already ship. **Recommendation:** start with "First Convert" and one "every profession"
advancement. The "Completionist tier per catalog quarter" advancements from the agent note need
catalog tracking and belong with #18.

### 5. Market Day — S · ★

A config-scheduled in-game day when miniblock merchants discount their trades. Nice for server communities
that build market squares. **Recommendation:** off by default, with a configurable interval and discount
(A2). It is a small system, so only worth it if you plan servers events around it.

## Tier 2 — Solid mid-size features

### 6. Uninstall and convert-back path — M · ★★

The README promises a way to convert the mod's villagers back to the Miniblock Traders datapack versions.
**Recommendation:** an admin command (for example `/miniblockmerchants export`) that converts mod villagers
to their datapack-tag equivalents, with a dry-run mode that only reports counts. It reverses the
conversion the mod already does in the other direction.

### 7. Data-driven trade tables — M · ★★

Let servers adjust emerald costs and trade lists without code. **Recommendation:** move trades into
datapack files with the current values as defaults. This is a lot of files for 26 professions, so do it
with datagen from the existing trade classes. It also lets #1 add miniblocks without a code change.

### 8. Endologist profession — M · ★★★

From your notes ("Enderologist" and the End version of the Netherographer). End miniblocks: chorus
plants, end rods, shulker boxes and a dragon egg replica. The agent note's conversion item is *Petrified
Chorus Fruit*, from breaking chorus plants or End City chests. **Recommendation:** the strongest new
profession, since the End has nothing in the roster. It also pairs with the End-structure idea in #20.

### 9. Spelunker profession — M · ★★

Deep-dark and cave miniblocks: sculk, amethyst clusters, dripstone, glow lichen lamps. Conversion item:
an *Echoing Compass*, a rare find in ancient city chests. **Recommendation:** check overlap with the
Mineralogist and Petrologist before committing; if the catalog is mostly blocks they already sell, fold the
unique ones into those professions instead.

### 10. Trailblazer profession — M · ★★

Trial chamber miniblocks: copper bulbs, vaults, chiseled tuff. Conversion item: an *Ominous Trial Key*, a
rare drop from ominous vaults. **Recommendation:** good discovery path, since it rewards beating the
trials. Make sure the drop is rare enough that it is a trophy (and configurable).

### 11. Paleontologist profession — M · ★★

Fossil and bone miniblocks and museum pieces. Conversion item: an *Immaculate Fossil*, a rare drop from
brushing suspicious blocks, which is a natural crossover with Archaeology Tweaks. **Recommendation:** gate
the crossover on the other mod being installed, and give it another path (a chest loot source, say) when it
isn't.

### 12. Musician profession — M · ★★

Jukebox, note block and instrument minis, maybe album crates per music disc. Conversion item: a *Signed
Music Disc*, a rare drop from creepers killed by skeletons. **Recommendation:** the disc drop is a fun nod
to the vanilla mechanic. Check that the instrument minis are enough to fill a catalog.

### 13. Meteorologist profession — M · ★

Weather minis: lightning rod, snow golem, storm cloud. Conversion item: *Fulgurite*, from lightning
strikes. **Recommendation:** the thinnest catalog of the new professions. Only do this if #1 turns up
enough weather-themed blocks.

### 14. Confectioner profession — M · ★

Cakes, candles and sweet-themed minis. Conversion item: a *Sugar-Dusted Egg*, dropped when baking a
cake. **Recommendation:** overlaps with the Baker and Chef. Only worth it if the candle and cake minis
don't already sit in those catalogs.

### 15. Wandering Peddler — M · ★★

A rare wandering-trader variant that carries a random sample of minis from all professions and, very
rarely, a conversion item. It gives players who miss loot chests a second discovery path. **Recommendation:**
keep the conversion item very rare and configurable (A2), and make the peddler optional (N1).

### 16. Decorative job blocks — M · ★

Unique placeable blocks per profession, so a trading hall can look themed. The Bartender's conversion
item is already a *Mixology Station*, which could simply be placeable. This is also your "Workstations?"
note. **Recommendation:** purely decorative blocks that don't claim professions (the mod's professions
deliberately have no job site, so real workstations would change how conversion works). Check overlap with
Minekea's furniture before building them.

### 17. Restock requests — M · ★

Merchants sometimes "want" a themed item (the Chef wants wagyu, the Astronomer an amethyst block), and
delivering it gives a temporary discount. **Recommendation:** small, characterful, and a separate system
to maintain, so low priority. Make it config-gated.

## Tier 3 — Big bets

### 18. Collector's Ledger — L · ★★★

An in-game book or screen tracking which miniblocks you own per profession, with completion percentages.
With nearly 1000 items it turns the catalog into a visible collection, and the agent note calls it the
single biggest engagement multiplier available. **Recommendation:** start as a read-only list of what you
have collected, driven by a stat or an advancement criterion, with no new blocks. It enables #19 and the
"Completionist" tiers.

### 19. Completion rewards — L · ★★

Completing a profession's catalog unlocks a unique "master trade" (a one-off showpiece mini, such as a
golden version of the profession's job block). **Recommendation:** depends on #18; do it only after the
ledger exists. Keep the reward cosmetic.

### 20. Profession shops — L · ★★

Your notes ask for profession-specific structures, custom houses in biomes, and custom structures for
merchants who don't normally live in villages, such as the Endologist and Netherographer. The agent note
suggests 3–4 shared "market stall" templates re-skinned per profession, not 25 bespoke structures.
**Recommendation:** use the shared-template approach, and decide first how villagers get there (they only
become merchants through conversion today, so a structure needs to hold a pre-converted villager). Start
with the out-of-village ones (End, Nether, ocean), where nothing exists. Needs worldgen on both loaders.

### 21. Modded-structure support — L · ★

Built-in support for mods that add structures (the "YUNG's Better..." family from your notes).
**Recommendation:** do it as datapack tags and loot-table additions so each integration is a data file,
not code, and only after #20 so the shared templates exist to place.

## Honorable mentions

None beyond the cuts below.

## What was cut and why

**Belongs in another mod.**
- **Display furniture** (Curio Shelf, Display Pedestal). The agent note itself says to defer to Minekea's
  shelves. Furniture for displaying minis belongs there.

**Too vague to act on.**
- **Thematic advancements** (dropped; see the intro). "Custom advancements" is covered by #4 and #18.

## Suggested first arc

Do #1 (more miniblocks) and the easy promises: #2 (head-drop sample) and #6 (convert-back path), plus #3
(per-profession toggles) and #4 (conversion advancements). Then #7 (data-driven trades) so later content is
cheap, and #8 (the Endologist) as the first new profession. The ledger (#18) is the biggest bet and the
reason to keep adding to the catalog; the shops (#20) come after.
