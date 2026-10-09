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
- 2026-10-09 — The existing "clear stored mob" gesture (sneak + empty-hand click) changes to a
  right-click on the block with shears or an axe. This is a behavior change to a shipped feature, so it
  needs a changelog note and a README update.
- 2026-10-09 — Clearing the bound mob with shears or an axe requires sneaking, to avoid accidental clears.
- 2026-10-09 — Slot targeting is a progressive enhancement. If the player is targeting a specific slot,
  the item goes there, following normal equipment rules. Otherwise the slots are tried in sequence: head,
  body, legs, feet, main hand, off hand.

## Brainstorm variants

None. This idea was added by the user during voting, so it is not in the legacy backlog.

## Related

- [Counter-clockwise rotation](counter-clockwise-rotation.md) — its gestures were chosen alongside this one

## Open questions

- How is a slot "targeted"? Candidate: armor-stand style, by the height of the click on the mob. It needs
  right-clicks on the bound mob itself to be intercepted. Can be settled during implementation, and since
  targeting is an enhancement, the sequential fallback works without it.
- In the sequential fallback, does an item go to the first slot it can legally occupy, whether or not that
  slot is full (swapping the old piece back to the player), or the first *empty* legal slot, so a second
  sword lands in the off hand?
- When sneak + empty-hand clicking without targeting a slot, does removal also follow a sequence, or do
  nothing?
- Is the equipment held in the block entity so it survives power cycles? (Recommended; confirm.)
