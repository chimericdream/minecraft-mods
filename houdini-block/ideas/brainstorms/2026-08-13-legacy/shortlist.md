# Houdini Block — Feature Shortlist

Scored and ranked from the legacy backlog in this folder ([`potential-features.md`](potential-features.md);
`ideas-md.md` holds only an empty bullet). There is no `combined-ideas.md` for a legacy backlog, so this
list was built straight from the source notes and checked against what the mod ships today. Everything
that isn't ranked is in [What was cut and why](#what-was-cut-and-why).

The generic "Add thematic/fun advancements" bullet was dropped on purpose: it was too vague to act on.
The three specific advancement ideas from the notes are kept and ranked as one item (#2).

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

Ranking is by value relative to effort. The identity is "update suppression as a tool — for redstone
engineers, technical players, and clean builders", with the stage-magic naming leaned into. So precision
and debugging features for technical players rank highest, and anything that drifts into general
decoration ranks lowest (A1). The mod today is a single block with four modes, a small mixin that decides
whether to suppress, and no config, so ideas that reuse that mixin are cheapest. Balance-affecting items
need a config toggle (A2), and each item says what else should be tunable (A4).

## Already shipped

The notes' "mode switching ergonomics" idea is partly done already: sneaking and right-clicking the item
in the air cycles the four modes and shows an actionbar message. What's left of it is the visual
distinction (see #1).

## Ranked list

Linked rows have their own idea files.

| # | Feature | Effort | Value | Votes | My Vote |
|---|---|---|---|---|---|
| | **Tier 1 — Quick wins** | | | | |
| 1 | [Per-mode look for block and item](../../visuals/per-mode-look.md) | S | ★★ | — | Yes |
| 2 | [Stage-flair advancements](../../progression/stage-flair-advancements.md) | S | ★★ | — | Yes |
| | **Tier 2 — Solid mid-size features** | | | | |
| 3 | [Directional masks](../../precision/directional-masks.md) | M | ★★★ | — | Maybe |
| 4 | [Update visualization](../../precision/update-visualization.md) | M | ★★ | — | Maybe |
| 5 | Choose which update types are suppressed | M | ★★ | — | No |
| 6 | Silent retraction | M | ★★ | — | No |
| 7 | [The Houdini Wand](../../tools/houdini-wand.md) | M | ★★ | — | Maybe |
| | **Tier 3 — Big bets** | | | | |
| 8 | [Update capture and replay](../../tricks/update-capture-and-replay.md) | L | ★★★ | — | Maybe |
| 9 | Camouflage mode | L | ★★ | — | No |
| 10 | Shape variants (slab, stair, wall) | M | ★ | — | No |
| 11 | The Assistant ("Ghost Block") | M | ★ | — | No |

## Tier 1 — Quick wins

### 1. Per-mode look for block and item — S · ★★

Give each of the four modes a visibly different block and item, so you can tell a "prevent all" from a
"replace block" at a glance (the original note suggested per-mode tinting). The block already stores its
mode as blockstate properties, so this is mostly model and texture work, plus an item model that reads
the mode from the item's data. **Recommendation:** use a different pattern or marking per mode, not a
color shift alone, so it works for color-blind players (A3). Tinting can be an addition on top.

### 2. Stage-flair advancements — S · ★★

Three advancements with the stage-magic theme: "Now You See Me" (first placement), "The Prestige" (use
replace mode to swap a block inside a powered contraption without it noticing) and "Escape Artist"
(silent retraction, see #6). **Recommendation:** ship "Now You See Me" now. "The Prestige" is hard to
detect as written, so simplify it to "use replace mode for the first time" or "replace a block next to
a powered redstone component". Hold "Escape Artist" until #6 exists. These have no gameplay effect, so
they need no config.

## Tier 2 — Solid mid-size features

### 3. Directional masks — M · ★★★

Configure *which* neighbors get notified: suppress upward updates only, skip the north face, and so on.
It turns the block from an on/off trick into a precision instrument, which is the heart of the
"tool for technical players" identity. **Recommendation:** store six per-face flags on the item and
block (blockstate or a small block entity, to be decided), with a simple way to set them in-world. The
design question is the control scheme for six faces plus four modes without a screen. A small GUI
may be worth it. It builds directly on the existing mixin (A1).

### 4. Update visualization — M · ★★

While holding the block, briefly show particles on blocks that *would* receive updates from a place or
break at the targeted position. Debugging suppression setups by eye is guesswork today. It pairs well
with #3, because masks make the preview more useful. **Recommendation:** client-side only, particles
rather than overlays, and a client config toggle plus a non-color cue (A3, A4). Keep it to the
first-order neighbor set at first.

### 5. Choose which update types are suppressed — M · ★★

Separate toggles for neighbor updates, shape updates and comparator updates, for players who know exactly
which mechanism they're exploiting. This absorbs the notes' "suppression radius / extended silence"
idea, which was too vague on its own, since "second-order updates" are mostly shape updates.
**Recommendation:** make it per-mode or per-block rather than a global config, since this is the sort
of thing technical players want to vary by contraption. A global default can sit in a config (A4).

### 6. Silent retraction — M · ★★

Powering a placed Houdini Block makes it remove itself without updates, enabling remote,
wireless-feeling suppression in circuits. It is a new interaction on the existing block, and it is
the trick that makes the advancement "Escape Artist" (#2) possible. **Recommendation:** add it as a
fifth mode rather than changing the existing four, since it changes how the block behaves when powered
and could surprise existing builds.

### 7. The Houdini Wand — M · ★★

An item that applies Houdini behavior to *other* blocks: break or place any block without triggering
neighbor updates. The block is the prop and the wand is the magician. It is powerful, so the note
suggests creative-only or expensive in survival. **Recommendation:** make it creative-only by default
with a config to allow it in survival (A2), and treat it as a variant of the Replace block mode rather
than a new system. Like the block, it must respect unbreakable blocks.

## Tier 3 — Big bets

### 8. Update capture and replay — L · ★★★

The showstopper: a mode where the block *records* the updates it suppressed and fires them all when
triggered by a redstone pulse or interaction. Delayed-consequence contraptions follow: prime a sand
column, then release it on cue. It is the most on-brand idea in the list ("the trick, then the
reveal"), and also the riskiest, since it needs a block entity that stores positions and a safe way to
replay them. **Recommendation:** prototype with a cap on the number of stored updates, and decide the
chunk-unload behavior before building. A server-side config for the cap (A2, A4).

### 9. Camouflage mode — L · ★★

The block copies the appearance of the block it replaced (or an adjacent block), so replace-mode swaps
are visually seamless and secret doors write themselves. **Recommendation:** needs a block entity and
custom rendering, so the cost is high for a mostly builder-facing payoff. Do it only if #1 shows there
is appetite for visual work in this mod.

### 10. Shape variants (slab, stair, wall) — M · ★

Houdini slab, stair and wall for suppression tricks in tight builds. Each is another block that needs
all four modes, so the cost grows quickly. **Recommendation:** one shape at a time, starting with the
slab, and only if players ask for it.

### 11. The Assistant ("Ghost Block") — M · ★

A companion block that is visible but has no collision and never emits or receives updates:
walk-through scenery for adventure maps and secret entrances. It is more about decoration than update
suppression, so it stretches the identity (A1). **Recommendation:** only if the mod's identity grows
toward map-making, and keep it separate from the main block.

## Honorable mentions

- **Structure-void behavior.** An option for the block to be ignored by structure blocks and jigsaw
  saves, useful for map makers embedding suppression setups in prefabs. A niche audience, and hard to
  verify without testing the structure-block code.

## What was cut and why

**Too vague to act on.**
- **Suppression radius ("extended silence").** "Second-order updates" were never defined. Folded into #5.
- **Generic "thematic/fun advancements".** Replaced by the three specific ones in #2.

## Suggested first arc

Do #1 (per-mode look) and the first advancement in #2 as a small release that makes the existing modes
easier to read. Then build #3 (directional masks) and #4 (visualization) together, because the preview
makes the masks usable. Add #6 (silent retraction) and the "Escape Artist" advancement after. #8 (capture
and replay) is the long-term showpiece and deserves its own design pass before any code.
