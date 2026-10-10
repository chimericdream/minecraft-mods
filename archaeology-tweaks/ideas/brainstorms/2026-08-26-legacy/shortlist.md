# Archaeology Tweaks — Feature Shortlist

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

Ranking is by value relative to effort. The identity is "the whole archaeology system, kept
vanilla-friendly": items, enchantments, loot, pottery, trades, advancements and worldgen, nothing Mojang
couldn't have shipped, and datapack-friendly. **The addon boundary matters most here.** This mod owns no
new blocks until the companion addon, "Archaeology Tweaks++", exists, so every idea that needs a new block
is pulled out of the ranking into [Addon ideas](#addon-ideas-need-new-blocks) below. The mod has no config
today, and the brush speed lives in `BrushItemMixin`. Balance-affecting items need a config toggle (A2),
and each item says what else should be tunable (A4).

## Already shipped

- **Advancements.** "First Dig" (first brush use), "Museum Quality" (collect every pottery sherd) and a
  Nether brushing advancement all exist, plus one for the Gentle Touch enchantment's second drop.
- **Dig sites, phase 1.** Scattered single-block deposits of the mod's suspicious blocks, in fitting
  biomes. The clustered, structure-style version is still open (see #10).
- **Pottery sherd trades.** Wandering traders sell a random sherd for 8 emeralds and a brick.

## Ranked list

Linked rows have their own idea files.

| # | Feature | Effort | Value | Votes | My Vote |
|---|---|---|---|---|---|
| | **Tier 1 — Quick wins** | | | | |
| 1 | [Craftable pottery sherds](../../pottery/craftable-pottery-sherds.md) | S | ★★★ | — | Yes, some |
| 2 | [Brushing speed and loot multipliers (config)](../../configuration/brushing-multipliers.md) | S | ★★ | — | Yes |
| 3 | [Depth-tiered loot](../../loot-and-trades/depth-tiered-loot.md) | S | ★★ | — | Maybe |
| 4 | [Wandering traders sell brushes and suspicious blocks](../../loot-and-trades/trader-brushes-and-blocks.md) | S | ★★ | — | Yes |
| | **Tier 2 — Solid mid-size features** | | | | |
| 5 | [Pottery sherds for the special banner patterns](../../pottery/banner-pattern-sherds.md) | M | ★★ | — | Yes |
| 6 | [Brush tiers (copper, gold, diamond, netherite)](../../brushes/brush-tiers.md) | M | ★★ | — | Yes |
| 7 | ["Keen Eye" enchantment](../../brushes/keen-eye-enchantment.md) | M | ★★ | — | Yes |
| 8 | [New sherd designs for the mod's materials](../../pottery/material-sherd-designs.md) | M | ★ | — | Maybe |
| 9 | Brush handle customization | M | ★ | — | No |
| | **Tier 3 — Big bets** | | | | |
| 10 | [Buried dig sites around vanilla structures](../../dig-sites/buried-dig-sites.md) | L | ★★ | — | Yes |
| 11 | [Torn map fragments](../../dig-sites/torn-map-fragments.md) | M | ★★ | — | Yes |
| 12 | [Field Journal](../../progression/field-journal.md) | L | ★★ | — | Yes |

## Tier 1 — Quick wins

### 1. Craftable pottery sherds — S · ★★★

Copy a pottery sherd the way smithing templates are copied: the sherd, a quantity of bricks and a base
block make two. Right now sherds only come from brushing, so a player who wants a set of one design for a
pot has to find it again and again. **Recommendation:** a crafting-table recipe only. Your note asks
whether a special workstation is needed. That would be a new block, which belongs to the addon, so the
recipe approach also keeps within the boundary. The cost (bricks per copy) should be configurable (A4).
Mirror the template cost ratio so it feels vanilla.

### 2. Brushing speed and loot multipliers (config) — S · ★★

Global multipliers so pack makers can make archaeology faster or slower, or more or less generous. This is
the mod's first config. **Recommendation:** two numbers, default 1.0, server-side. Brush speed already
goes through `BrushItemMixin`, so the speed half is small. Loot rolls need a look at how the loot table
is applied (A2, A4).

### 3. Depth-tiered loot — S · ★★

An optional rule where suspicious blocks below a configurable Y level roll a rarer loot table, so deeper
digs mean better finds. **Recommendation:** do it as a loot table condition (data), not code, so
datapacks can change it. The Y level is a config or datapack value, and the rule is off by default (A2).
It rewards the deepslate-level and Nether digs the mod already spawns.

### 4. Wandering traders sell brushes and suspicious blocks — S · ★★

Wandering traders occasionally sell brushes and single suspicious blocks, giving survival players a
renewable (if pricey) way to seed their own dig sites. Sherd trades already exist, so the trade-set
code is in place. **Recommendation:** brushes first, then one trade for a random suspicious block
from this mod. Keep the prices configurable (A4). It uses existing blocks only, so no addon is needed.

## Tier 2 — Solid mid-size features

### 5. Pottery sherds for the special banner patterns — M · ★★

A sherd for each special banner pattern (the ones with their own loom items, such as creeper, skull,
flower, globe and piglin). It's your own note and ties together two of the mod's loves: archaeology and
banners. **Recommendation:** pair it with #1 so players can craft copies, and decide how the original
sherd is found (loot from existing suspicious blocks is simplest). The cost is art: six or so sherd
textures plus the matching pot faces.

### 6. Brush tiers (copper, gold, diamond, netherite) — M · ★★

A ladder mirroring tools: higher tiers brush faster and/or last longer, and gold could trade durability
for a luck bonus. **Recommendation:** start with copper and a netherite brush, since they bracket the
vanilla one, and make the speed, durability and luck values configurable (A4). It is balance-affecting,
so it needs a toggle (A2). The recipes and speed changes build on `BrushItemMixin`.

### 7. "Keen Eye" enchantment — M · ★★

Suspicious blocks within a few chunks shimmer faintly while a brush with this enchantment is held. It
fixes the discoverability problem of invisible blocks in terrain. **Recommendation:** make the range
configurable and the shimmer an outline or particle (not color alone) so it works for color-blind
players (A3). The client rendering is the main cost. A fallback is a distance-based sound.

### 8. New sherd designs for the mod's materials — M · ★

Sherd designs matching the mod's materials, such as a soul-flame sherd from soul sand, usable on vanilla
decorated pots. **Recommendation:** only designs tied to blocks this mod already has (soul sand, soul
soil, clay, mud). The mushroom sherd from the original note needs suspicious mycelium, which is an addon
block, so it moves to the addon list.

### 9. Brush handle customization — M · ★

Combine a brush with dye or a banner pattern purely for looks. **Recommendation:** only if #6 happens,
and then do it as a dye option. It is cosmetic with no balance impact, so no toggle is needed.

## Tier 3 — Big bets

### 10. Buried dig sites around vanilla structures — L · ★★

Small worldgen features that cluster 5–15 suspicious blocks around a skeleton, fossil or ruined foundation,
so players find a "site" instead of lone blocks. Phase 1 shipped the scattered version.
**Recommendation:** build it from suspicious sand and gravel plus this mod's existing blocks only. The
fossil-excavation variant needs new blocks and is an addon idea. Keep the density configurable (A4),
since it changes worldgen (A2).

### 11. Torn map fragments — M · ★★

A loot item that, when several are combined, points to a nearby dig site, treasure-map style.
**Recommendation:** only after #10, because it needs a site to point to. Make the number of fragments
needed configurable.

### 12. Field Journal — L · ★★

An item that records every unique artifact you've brushed up. It doubles as a collection checklist
with advancement hooks. **Recommendation:** a book-like item with a screen listing found and missing
items, built on the same data as the "Museum Quality" advancement. The screen is what makes it L. Keep
it to sherds and unique loot, not every item.

## Addon ideas (need new blocks)

These were pulled out of the ranking because they need a registered block, and this mod owns none until
"Archaeology Tweaks++" exists (see the identity statement). They are listed here so they aren't lost.
There is no addon mod yet, and `docs/FEATURE-WORK.md` says ideas for mods that don't exist are filed as
GitHub Issues labeled `ideas`.

- Suspicious Snow / Powder Snow (frozen digs; the note has them melt if the biome warms, which would
  break N2 as written, so make it opt-in).
- Suspicious Moss, Suspicious Mycelium / Podzol, Suspicious Netherrack / End Stone, and Suspicious Ash /
  Basalt Sand.
- Fossil excavations (vanilla fossils encased in suspicious blocks).
- Datapack-defined suspicious variants (any full block as brushable).
- A special pottery workstation, if the recipe in #1 turns out not to be enough.
- The mushroom sherd, which needs suspicious mycelium (see #8).

## What was cut and why

**Already shipped.**
- The "First Dig", "Museum Quality" and Nether advancements, and phase 1 of the dig sites.

**Moved to the addon list.**
- Every idea that needs a new block (above).

## Suggested first arc

Do #1 (craftable sherds) with #2 (config) and #3 (depth-tiered loot) as a small "pottery and loot"
release, then #4 (trader stock). #5 (banner-pattern sherds) follows naturally, since #1 makes them
obtainable. #6 (brush tiers) and #7 (Keen Eye) are the headline brush features. The dig-site cluster
(#10) leads to #11 (torn maps), so decide on them together.
