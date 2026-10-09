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

No active ideas yet. The legacy backlog is in [`brainstorms/2026-08-26-legacy/`](brainstorms/2026-08-26-legacy/),
waiting to be shortlisted and voted on (`ideas-shortlist`) when work on this mod starts.

## Inbox

_Empty._

## Archive

_Empty._
