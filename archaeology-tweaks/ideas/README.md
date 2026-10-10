# Archaeology Tweaks — Ideas

Feature ideas for this mod, from first thought to ready to build. The process is described in
[`docs/FEATURE-WORK.md`](../../docs/FEATURE-WORK.md).

## Identity

> **The whole archaeology system, kept vanilla-friendly.** Archaeology Tweaks extends vanilla's
> brush-and-reveal loop through items, enchantments, loot, pottery, trades, advancements, and worldgen,
> so archaeology feels like a natural part of exploring. Ideas should feel like something Mojang could
> have shipped, build on existing mechanics, and stay datapack-friendly.

### Addon boundary

The long-term plan is a companion addon, "Archaeology Tweaks++", that owns every **block**. That
includes the eight suspicious blocks this mod ships today, so moving them will be a breaking change
when it happens. Until then the existing blocks stay put and no new ones are added here.

- **This mod owns** items, enchantments, loot tables, pottery sherds, trades, advancements, config,
  and worldgen that uses vanilla blocks (for example, dig sites built from suspicious sand and gravel).
- **The addon owns** any registered block and anything that needs one: new suspicious variants,
  fossil excavations built from them, and datapack-defined brushable blocks.
- **Progressive enhancement:** the addon unlocks and expands what this mod does, and this mod must stay
  complete and useful without it. Where the addon's code lives (in this mod's jar, gated on the addon
  being present, or in the addon's own jar) is still undecided.

When a brainstorm turns up an idea that needs a new block, file it as an addon idea rather than a
core one.

## Active ideas

The legacy backlog was shortlisted in
[`brainstorms/2026-08-26-legacy/shortlist.md`](brainstorms/2026-08-26-legacy/shortlist.md). Ideas that
need new blocks are listed there under "Addon ideas" and are not active here.

### Pottery

- [Craftable pottery sherds](pottery/craftable-pottery-sherds.md) — Copy a sherd like a smithing
  template, with a crafting recipe and no workstation.
- [Pottery sherds for the special banner patterns](pottery/banner-pattern-sherds.md) — A sherd for each
  special banner pattern.
- [New sherd designs for the mod's materials](pottery/material-sherd-designs.md) — Sherds tied to blocks
  the mod already has, such as soul sand.

### Brushes

- [Brush tiers](brushes/brush-tiers.md) — Copper, gold, diamond and netherite brushes with different
  speed and durability.
- ["Keen Eye" enchantment](brushes/keen-eye-enchantment.md) — Nearby suspicious blocks shimmer while a
  brush with it is held.

### Loot and trades

- [Depth-tiered loot](loot-and-trades/depth-tiered-loot.md) — Deeper suspicious blocks roll a rarer loot
  table.
- [Wandering traders sell brushes and suspicious blocks](loot-and-trades/trader-brushes-and-blocks.md) — A
  renewable way to seed your own dig sites.

### Dig sites

- [Buried dig sites around vanilla structures](dig-sites/buried-dig-sites.md) — Clusters of suspicious
  blocks around a skeleton, fossil or ruin.
- [Torn map fragments](dig-sites/torn-map-fragments.md) — Combine fragments to find a nearby dig site.

### Progression

- [Field Journal](progression/field-journal.md) — Records every unique artifact you've brushed up.

### Configuration

- [Brushing speed and loot multipliers](configuration/brushing-multipliers.md) — Global multipliers for
  brush speed and loot generosity.

## Inbox

_Empty._

## Archive

_Empty._
