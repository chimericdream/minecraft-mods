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
- 2026-10-09 — In the sequential fallback, an item goes to the first *empty* slot it can legally occupy,
  so a second sword lands in the off hand.
- 2026-10-09 — Sneak + empty-hand click with no slot targeted removes gear in reverse sequence, one piece
  per click, taking the first occupied slot in the order off hand, main hand, feet, legs, body, head.
- 2026-10-09 — A slot is targeted the way armor stands do it, by the height of the click on the mob.
- 2026-10-09 — Equipment is stored on the block entity, so it survives power cycles.

## Brainstorm variants

None. This idea was added by the user during voting, so it is not in the legacy backlog.

## Related

- [Counter-clockwise rotation](counter-clockwise-rotation.md) — its gestures were chosen alongside this one

## Open questions

- Armor stands have a fixed humanoid shape, but bound mobs vary in height and build. How are the click-height
  bands scaled to each mob's bounding box, and how are the hands reached? Settle during implementation; the
  fallback works for any mob in the meantime. This needs right-clicks on the bound mob itself to be
  intercepted.
