# Beacon & Conduit Tweaks — Ideas

Feature ideas for this mod, from first thought to ready to build. The process is described in
[`docs/FEATURE-WORK.md`](../../docs/FEATURE-WORK.md).

## Identity

> **Beacons and conduits, your way.** Beacon & Conduit Tweaks starts from making their reach and
> behavior configurable, and extends to anything beacons and conduits do: effects, payments, beams, and
> new mechanics such as inverted beacons or redirectable beams. Everything should be tunable, and new
> behavior should be toggleable so a server can run a near-vanilla setup.

## Active ideas

The legacy backlog was shortlisted in
[`brainstorms/2026-08-13-legacy/shortlist.md`](brainstorms/2026-08-13-legacy/shortlist.md).

**Direction:** the mod is allowed to add new blocks. Several ideas below may become their own blocks,
for example an "advanced" beacon with more powerful effects, or a "tainted" beacon for negative effects.

### Range

- [Range shape](range/range-shape.md) — Choose sphere, cylinder (full height) or box reach.
- [Separate vertical and horizontal range](range/vertical-and-horizontal-range.md) — Independent up/down
  and sideways reach.
- [Uniform-pyramid bonus](range/uniform-pyramid-bonus.md) — A small bonus for a single-material pyramid.
- [Per-dimension overrides](range/per-dimension-overrides.md) — Different range settings per dimension.

### Conduits

- [Conduit attack range](conduits/conduit-attack-range.md) — The attack range becomes its own setting,
  separate from the buff range.
- [Conduit frame material weights](conduits/conduit-frame-weights.md) — Each frame block type contributes
  a configurable amount of range.

### Effects

- [Effect duration and reapply window](effects/effect-duration.md) — How long an effect lingers after you
  leave range.
- [Amplifier control per pyramid level](effects/amplifier-control.md) — Configure the effect level each
  pyramid tier grants; may live on an advanced beacon.
- [Effect pool via datapack](effects/effect-pool-datapack.md) — A data-defined list of selectable effects
  per pyramid level, including modded ones.
- [Variable bonuses depending on payment](effects/payment-bonuses.md) — Different payment items give
  different results.
- ["Inverted" beacons](effects/inverted-beacons.md) — Beacons with negative effects; may be a separate
  tainted beacon block.

### Beams

- [Range visualization](beams/range-visualization.md) — Particles show the beacon's configured reach.
- [Redirect beacon beams](beams/redirect-beams.md) — Bend a beam using amethyst crystals, visuals only to
  start.

### Automation

- [Comparator output](automation/comparator-output.md) — Beacons and conduits emit a signal for pyramid
  level and frame completeness.

### Configuration

- [Config presets](configuration/config-presets.md) — Shippable presets such as "vanilla+" and "mega-base".

## Inbox

_Empty._

## Archive

_Empty._
