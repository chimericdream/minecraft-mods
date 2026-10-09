# Counter-clockwise rotation

> Shortlist #3 · Tier 1 — Quick wins · Effort **S** · Value ★★ · Votes — · My vote **Maybe**
> Status: **Exploring**

## Description

Empty-hand right-click only rotates 90° clockwise, and sneak + right-click is taken by clear-binding, so
reaching the other side takes three clicks. A counter-clockwise option needs its own trigger.
**Recommendation:** choose the trigger before building. Candidates: a held item such as a stick, the
off-hand, or splitting the dummy face into left and right halves. The held-item option is the least
surprising and needs no new input. Unconditional, so no config. This is the one item with a real design
question; see the idea file's open questions after promotion.

## Decisions

- 2026-10-09 — Trigger is the click position: an empty-hand right-click on the left half of the dummy's
  face rotates counter-clockwise, and the right half keeps today's clockwise rotation.

## Brainstorm variants

From [`brainstorms/2026-08-24-legacy/potential-features.md`](../brainstorms/2026-08-24-legacy/potential-features.md)
(Dummy behavior):

- **Counter-clockwise rotation** — empty-hand right-click only rotates 90° clockwise; sneak + right-click is already used to clear the binding, so a counter-clockwise option would need its own trigger (e.g. off-hand, a different key, or a held item).

## Related

- [Mob equipment](mob-equipment.md) — its gestures were chosen alongside this one; the clear-binding change frees sneak + empty-hand

## Open questions

- Left and right are as the player sees the face. Does the middle of the face need a dead zone, or is a
  clean 50/50 split fine? (Can be decided during implementation.)
- The README and the empty-hand message should teach this, since the gesture is not self-evident.
