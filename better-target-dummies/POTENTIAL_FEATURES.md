# Potential Features — Better Target Dummies

Brainstormed, thematically appropriate feature ideas. Nothing here is committed or implemented; these
are starting points for future planning.

The mod's identity: an accurate, convenient way to **test how an attack performs against a specific
mob or mob category**. The dummy binds the real vanilla mob (immobilized) rather than faking its
model, so combat math (armor, enchantment category bonuses, resistances) is correct for free —
everything below should preserve that.

## Mob selection

* **Mob category presets** — quick-bind buttons/commands for common test groups (undead, arthropod,
  aquatic) that cycle through every mob in the category.
* **Remember custom mob data** — if a spawn egg carries custom NBT/components (named, equipped, etc.),
  the dummy already inherits it on spawn; surface that state back in the empty-hand tooltip. The
  Dummy Spawn Egg only carries a mob name, not full custom data, since it's a plain renamed item.

## Damage feedback

* **Running damage log** — track the last N hits (weapon used, damage dealt) per dummy instead of only
  the most recent action-bar message, viewable via GUI or command.
* **DPS / average damage** — aggregate a short combat window into an average-damage-per-hit or
  damage-per-second readout, useful for weapon/enchantment comparisons.
* **Floating combat text** — render the damage number in-world above the dummy (like many test-dummy
  mods do) instead of only the action bar, so damage is visible to everyone watching, not just the
  attacker.
* **Client-side damage tooltip** — hovering a bound dummy could preview expected damage for the
  currently held weapon before swinging, factoring in enchantments.

## Dummy behavior

* **Status effect immunity toggle** — decide whether a bound dummy should be able to receive potion
  effects (currently it can; effects like Absorption would slightly skew the reported damage number
  since only armor/magic absorption are accounted for, not the Absorption effect).
* **Multi-dummy comparison** — a small rig/stand accessory to line up several dummies bound to
  different mobs for side-by-side testing of one weapon.
* **Visual powered indicator** — the block currently looks identical powered or not (only the mob's
  presence shows it); a lit/unlit texture variant tied to the `powered` blockstate would make the
  on/off state readable at a glance, even from a distance.
* **Counter-clockwise rotation** — empty-hand right-click only rotates 90° clockwise; sneak +
  right-click is already used to clear the binding, so a counter-clockwise option would need its own
  trigger (e.g. off-hand, a different key, or a held item).
* **Suppress idle animation** — ambient *sound* is silenced mod-wide via one generic hook
  (`Mob.playAmbientSound`), but idle *animations* (a chicken's wing flap, a spider's leg wiggle, ...)
  are driven by per-species fields that update every tick independent of AI, so there's no equivalent
  single hook. Doing this properly would mean a mixin per animated mob family rather than one general
  fix.
