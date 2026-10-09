# Block-entity interval and area helpers

> Source: user note while refining [JD Crafte's feeding trough breeding](../../../jdcrafte/ideas/animals/feeding-trough-breeding.md), 2026-10-09 · Status: **Exploring**

## Description

Small helpers that "block entities that do something to nearby entities" would otherwise each rewrite.
The feeding trough is the first consumer. The candidates:

- **Interval ticker.** Run an action every N ticks (or seconds/minutes) from a block entity's server
  tick, with a stagger offset so many identical blocks don't all fire on the same tick. Should survive
  chunk reloads sensibly (e.g. phase derived from game time, not a saved counter).
- **Area entity scan.** Find entities of a given class or predicate in a box defined as horizontal radius
  plus vertical radius around a block position, with the box type built once rather than per call.
- **Feed an entity.** Put an animal into love mode as if fed, with or without a player, and consume the
  item. Includes the "is this animal eligible" check (adult, not in love, zero breeding cooldown, accepts
  this food).
- **Effect spawners.** Heart and bonemeal-style particles over an entity or block, server-triggered so
  clients see them. Overlaps with the existing [sound and particle helpers](sound-particle-helpers.md);
  fold that in rather than duplicating.

## Decisions

_None yet._

## Related

- [Sound and particle helpers](sound-particle-helpers.md) — the particle piece belongs there.
- [GameTest harness helpers](../testing/gametest-harness-helpers.md) — the same helpers should be easy to
  test, e.g. spawn two cows, advance 200 ticks, assert both are in love mode.
- [JD Crafte feeding trough breeding](../../../jdcrafte/ideas/animals/feeding-trough-breeding.md) — first
  consumer.

## Open questions

1. **Extract now or after?** Build the trough with the logic local, then lift the proven pieces into the
   lib, or design the lib API first? Extracting afterwards avoids guessing the shape of the API.
2. **Fake player.** Should there be a "fake player" for interactions that need one (e.g. a trough
   triggering `mobInteract`)? Calling the love-mode method directly avoids it, and a fake player has
   loader-specific and Architectury wrinkles. Probably not worth it until a second consumer needs it.
3. **Anything else worth sharing** from the other mods (e.g. the hopper block entities' tick logic)?
