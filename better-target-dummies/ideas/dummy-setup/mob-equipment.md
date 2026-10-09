# Mob equipment

> Shortlist Bonus · Tier 2 — Solid mid-size features · Effort **M** · Value ★★★ · Votes — · My vote **Yes**
> Status: **Exploring**

## Description

*Added by the user during voting; not from the legacy backlog.* Lets a bound mob wear armor and hold a
weapon or shield, so a test can answer "how does this hit land against a *fully armored* zombie?" and not
only against a bare one. Armor, toughness, and protection enchantments are the biggest damage modifiers
in the game, and the identity statement names armor explicitly.
**Recommendation:** store the equipment on the block entity, because the mob is discarded whenever power
is lost and respawned fresh, so equipment held only on the mob would vanish on every power cycle. Persist
it the same way the binding is remembered. Leave a GUI out; a six-slot screen would make this L. No
config needed (A4), since it is what the feature is. Gestures are decided; see Decisions.

## Decisions

- 2026-10-09 — Equip by right-clicking the dummy with an item: armor goes to its matching slot, weapons
  and tools to the main hand, shields to the off-hand. Block items still place normally.
- 2026-10-09 — Remove gear with sneak + empty-hand right-click on the targeted armor or weapon slot.
- 2026-10-09 — The existing "clear stored mob" gesture (sneak + empty-hand click) changes to a plain
  right-click on the block with shears or an axe. This is a behavior change to a shipped feature, so it
  needs a changelog note and a README update.

## Brainstorm variants

None. This idea was added by the user during voting, so it is not in the legacy backlog.

## Related

- [Counter-clockwise rotation](counter-clockwise-rotation.md) — its gestures were chosen alongside this one

## Open questions

- How is the targeted slot determined? Candidate: armor-stand style, by the height of the click on the
  mob (head, chest, legs, feet), with the hands chosen by a rule still to be picked. This means
  intercepting right-clicks on the bound mob itself, not only on the block. Can be settled during
  implementation, but it decides whether the hands are reachable.
- Does the shears or axe clear need a sneak, or is a plain right-click enough? A plain click risks an
  accidental clear for players carrying an axe.
- Is the equipment held in the block entity so it survives power cycles? (Recommended; confirm.)
