# Mob equipment

> Shortlist Bonus · Tier 2 — Solid mid-size features · Effort **M** · Value ★★★ · Votes — · My vote **Yes**
> Status: **Exploring**

## Description

*Added by the user during voting; not from the legacy backlog.* Lets a bound mob wear armor and hold a
weapon or shield, so a test can answer "how does this hit land against a *fully armored* zombie?" and not
only against a bare one. Armor, toughness, and protection enchantments are the biggest damage modifiers
in the game, and the identity statement names armor explicitly.
**Recommendation:** equip by right-clicking the dummy with an armor piece (to the matching slot) or a
held item (main hand). Store the equipment on the block entity, because the mob is discarded whenever
power is lost and respawned fresh, so equipment held only on the mob would vanish on every power cycle.
Persist it the same way the binding is remembered. Leave a GUI out; a six-slot screen would make this L.
No config needed (A4), since it is what the feature is. A removal gesture is still undecided.

## Decisions

_None yet._

## Brainstorm variants

None. This idea was added by the user during voting, so it is not in the legacy backlog.

## Related

- [Counter-clockwise rotation](counter-clockwise-rotation.md) — also needs a new interaction, so the gestures should be chosen together

## Open questions

- How is equipment removed? Empty-hand click rotates and sneak + empty-hand clears the binding.
- Is the equipment held in the block entity so it survives power cycles? (Recommended; confirm.)
