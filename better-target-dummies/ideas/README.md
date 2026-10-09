# Better Target Dummies — Ideas

Feature ideas for this mod, from first thought to ready to build. The process is described in
[`docs/FEATURE-WORK.md`](../../docs/FEATURE-WORK.md).

## Identity

> An accurate, convenient way to **test how an attack performs against a specific mob or mob category**. The dummy binds the real vanilla mob (immobilized) rather than faking its model, so combat math (armor, enchantment category bonuses, resistances) is correct for free — everything below should preserve that.

## Active ideas

### Damage feedback

- [DPS / average damage readout](damage-feedback/dps-average-damage-readout.md) — Add average damage per
  hit and damage per second to the action-bar readout, over a short rolling window.
- [Floating combat text](damage-feedback/floating-combat-text.md) — Show each hit's damage above the
  dummy so everyone watching sees it.
- [Running damage log](damage-feedback/running-damage-log.md) — Keep the last N hits per dummy,
  viewable by command.

### Dummy setup

- [Mob equipment](dummy-setup/mob-equipment.md) — Let a bound mob wear armor and hold a weapon, kept
  across power cycles.
- [Mob category presets](dummy-setup/mob-category-presets.md) — Quick-bind and cycle through the mobs of
  a category such as undead, arthropod, or aquatic.
- [Counter-clockwise rotation](dummy-setup/counter-clockwise-rotation.md) — A second trigger to rotate
  the bound mob the other way.
- [Status effect handling](dummy-setup/status-effect-handling.md) — Config for whether bound mobs
  accept potion effects, with Absorption counted in the readout.

## Inbox

_Empty._

## Archive

_Empty._
