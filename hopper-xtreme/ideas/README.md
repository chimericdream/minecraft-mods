# Hopper X-Treme — Ideas

Feature ideas for this mod, from first thought to ready to build. The process is described in
[`docs/FEATURE-WORK.md`](../../docs/FEATURE-WORK.md).

## Identity

> Everything stays **recognizably "hopper"** — speed tiers, directions, and filtering. New ideas should slot into that grid rather than invent a new machine.

## Active ideas

### Filtering

- [Filter copy and paste with the wrench](filtering/filter-copy-and-paste.md) — Copy one hopper's filter and apply it to others.
- [Tag-based filter entries](filtering/tag-filter-entries.md) — Filter by item tag as well as by item.
- [Overflow routing](filtering/overflow-routing.md) — Rejected items pass through to the hopper below.
- [Component-aware filter matching](filtering/component-aware-matching.md) — Match enchantments, names or damage.
- [Filter cards](filtering/filter-cards.md) — Shareable, craftable filter configurations.

### Wrench

- [Wrench modes and stats mode](wrench/wrench-modes-and-stats.md) — A multi-mode wrench with items-per-minute stats.

### Redstone

- [Comparator fidelity](redstone/comparator-fidelity.md) — Filter slots shouldn't read as contents.
- [Per-hopper redstone modes](redstone/redstone-modes.md) — Inverted and pulse behaviors.

### Completing the grid

- [Honeyed and copper multi-hoppers](grid/honeyed-and-copper-multi-hoppers.md) — Fill the missing multi-hopper tiers.
- [Upgrade smithing path](grid/upgrade-smithing-path.md) — Promote a hopper in place, keeping its data.
- [Hopper minecarts by tier](grid/hopper-minecarts.md) — Tiered and filtered hopper minecarts.

### Internals

- [Block and screen collapse](internals/block-and-screen-collapse.md) — Finish the hopper de-duplication: shared block-entity plumbing for the block classes, and shared filtered screen handlers/screens.

The legacy backlog is shortlisted in [`brainstorms/2026-08-13-legacy/shortlist.md`](brainstorms/2026-08-13-legacy/shortlist.md) and
has been voted on; side I/O control (#11) was not promoted.

## Inbox

_Empty._

## Archive

_Empty._
