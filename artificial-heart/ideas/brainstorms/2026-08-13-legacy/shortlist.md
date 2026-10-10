# Artificial Heart — Feature Shortlist

Scored and ranked from the legacy backlog in this folder ([`ideas-md.md`](ideas-md.md), your older notes,
and [`potential-features.md`](potential-features.md), the agent brainstorm). There is no
`combined-ideas.md` for a legacy backlog, so this list was built straight from the source notes and
checked against what the mod ships today. Everything that isn't ranked is in
[What was cut and why](#what-was-cut-and-why).

The generic "Add thematic/fun advancements" bullet was dropped on purpose: it was too vague to act on.
The two specific advancements from the notes are kept and ranked as one item (#1).

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

Ranking is by value relative to effort. The identity is "the pale garden, built with": ideas can be any
size but **must belong to the pale garden itself, not to a general tamed-blocks theme**. The agent
brainstorm was written against an older, broader pitch, so several of its ideas (tamed sculk shriekers,
bee nests, candles) fall outside the current statement and are cut below. Your own notes, the locked
heart and the "creaking eye", are in scope. Ideas that change gameplay need a config toggle (A2), and each
item says what else should be tunable (A4).

## Already shipped

Several notes in both files have shipped since they were written:

- **Locked creaking heart.** The Detached Creaking Heart is decorative, never spawns creakings and
  keeps its look (made with an axe).
- **Locked eyeblossoms.** The Clipped Eyeblossom and Clipped Open Eyeblossom (made with shears), plus
  potted versions.
- **Inert creaking statue.** A golem shape built around a Detached Creaking Heart makes a statue that
  never moves. A *poseable* statue is still open (see #9).
- **Consistent tool verbs.** Shears clip plants and an axe detaches the heart.

## Ranked list

Linked rows have their own idea files.

| # | Feature | Effort | Value | Votes | My Vote |
|---|---|---|---|---|---|
| | **Tier 1 — Quick wins** | | | | |
| 1 | ["Change of Heart" and "Garden Variety" advancements](../../progression/garden-variety-advancement.md) | S | ★★ | — | Yes (Change of Heart already exists) |
| 2 | Dormant Detached Creaking Heart | S | ★★ | — | No |
| 3 | [Un-taming](../../conversion/un-taming.md) | S | ★★ | — | Yes |
| 4 | Clipped Pale Hanging Moss | S | ★ | — | No (pale hanging moss doesn't grow) |
| 5 | [Light-level dial on the Detached Creaking Heart](../../heart-controls/light-level-dial.md) | S | ★ | — | Maybe |
| 6 | [Comparator support on the Detached Creaking Heart](../../heart-controls/comparator-support.md) | S | ★ | — | Maybe |
| | **Tier 2 — Solid mid-size features** | | | | |
| 7 | [Creaking eye](../../mechanics/creaking-eye.md) | M | ★★★ | — | Yes |
| 8 | [Heartbeat toggle](../../heart-controls/heartbeat-toggle.md) | M | ★ | — | Maybe |
| | **Tier 3 — Big bets** | | | | |
| 9 | [Poseable creaking statue](../../mechanics/poseable-creaking-statue.md) | L | ★★ | — | Yes |

## Tier 1 — Quick wins

### 1. "Change of Heart" and "Garden Variety" advancements — S · ★★

"Change of Heart" for detaching your first creaking heart, and "Garden Variety" for collecting every
decorative variant the mod adds. Both are specific and fit the mod's tone. **Recommendation:** ship both
together as the mod's first advancement tab. "Garden Variety" has to be kept in sync as variants are added,
so drive it from a tag or list rather than a hard-coded set. No gameplay effect, so no config.

### 2. Dormant Detached Creaking Heart — S · ★★

A third visual state (the dim, dormant look) to go with the glowing active-face look, so builders can
pick exactly which stage of the heart they want. The vanilla heart already has these looks, and the
decorative one only covers some of them. **Recommendation:** reuse the placement-orientation mechanic
the active version already has, and add the state as a blockstate property with its own model. Mostly
assets and data.

### 3. Un-taming — S · ★★

Right-click a decorative variant with a different tool to convert it back to the vanilla block, so no
build decision is permanent. This folds in the agent notes' "consistent verbs" and "waxing metaphor"
ideas, which were variations on the same workflow question. **Recommendation:** one reverse tool per
block family (for example, an axe on a clipped eyeblossom, or shears on a detached heart), picked so
that it can't happen by accident, and the same rule everywhere. Skip the honeycomb-waxing alternative,
since the shears/axe verbs are already in the mod.

### 4. Clipped Pale Hanging Moss — S · ★

Pale hanging moss frozen at a chosen length, which never grows and never changes. Pale hanging moss is
vanilla pale-garden content, so this stays on-theme. **Recommendation:** only if players ask, since
vanilla pale hanging moss doesn't visibly sway and bone meal growth is the only change you'd be
preventing. Clip it with shears like the eyeblossoms.

### 5. Light-level dial on the Detached Creaking Heart — S · ★

Cycle the emitted light from 0 to 15. The notes say "wrench-cycle", but there is no wrench in the mod.
**Recommendation:** use an existing interaction instead (for example, sneak-use with an empty hand, as the
Houdini Block cycles its modes), show the new level as an actionbar message, and make the whole feature
a config toggle (A4).

### 6. Comparator support on the Detached Creaking Heart — S · ★

The detached heart outputs a fixed, configurable comparator signal, making it a stylish redstone constant
for pale-garden-themed contraptions. **Recommendation:** a signal strength that is set with the same
interaction as the light dial (#5), config-gated and default off (A2).

## Tier 2 — Solid mid-size features

### 7. Creaking eye — M · ★★★

A new placeable block that emits a redstone signal when you look at it, with strength possibly depending
on your distance. It is your own idea and the most on-theme mechanic in the backlog, since creakings
already freeze when watched. **Recommendation:** a block entity that checks nearby players' line of sight
on a slow tick (a few times a second, not every tick, to keep it cheap), with the range and the
distance-to-strength curve configurable (A4). Decide whether it needs a clear line of sight or just a
direction, and whether any player or only the nearest triggers it. It could pair with a creaking golem
for gameplay that reacts to being watched.

### 8. Heartbeat toggle — M · ★

A purely cosmetic option that makes the Detached Creaking Heart softly pulse its glow, with none of the
gameplay behavior. **Recommendation:** only if you want animated block rendering in this mod, since a
blockstate-based glow can't pulse and needs a custom renderer. Make the pulse optional and slow (A3).

## Tier 3 — Big bets

### 9. Poseable creaking statue — L · ★★

A creaking-shaped decorative entity, like an armor stand, that stands where you pose it. This is the
open half of "Inert Creaking Statue": today's statue is built from a fixed golem shape and can't be
posed. **Recommendation:** treat it as a new entity with its own model, pose controls and
inventory-less behavior. It is the most ambitious idea in the list and the most fun for builders.
Reuse the armor-stand pose code as far as possible.

## Honorable mentions

None beyond the cuts below.

## What was cut and why

**Not the pale garden (identity).** The identity statement says ideas "must belong to the pale garden
itself, not to a general tamed-blocks theme", so these are cut. If you want a general tamed-blocks mod, it
belongs elsewhere:
- Muted Sculk Shrieker and Still Sculk Sensor.
- Silent Spore Blossom.
- Empty Bee Nest.
- Unlit / ever-lit candle variants.

**Duplicates vanilla (N3).**
- **Artificial Resin Veins.** Vanilla's resin clump already places on surfaces as a multi-face block, so
  placeable resin seams are a duplicate. Worth reviving only as a clearly different look.

**Not possible today.**
- **Potted clipped eyeblossoms in decorated pots.** The notes say "if vanilla ever allows".

**Merged into another item.**
- "Consistent taming verbs" and "waxing metaphor" are part of #3.

## Suggested first arc

Do #1–#3 together: the advancements, the dormant heart state and un-taming round out the workflow the
mod already has. #5 and #6 are tiny follow-ups once you decide how the heart is configured. The
creaking eye (#7) is the headline feature and deserves its own design pass on line of sight and cost.
The poseable statue (#9) is a later project.
