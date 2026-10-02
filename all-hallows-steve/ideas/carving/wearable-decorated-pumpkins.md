# Wearable decorated pumpkins

> Shortlist #12 · Tier 2 — Solid mid-size features · Effort **L** (was M, see Decisions) · Value ★★★ · Votes 3 · My vote **Yes**
> Status: **Ready**

## Description

Equip an unlit decorated pumpkin on your head with vanilla carved-pumpkin behavior. Any unlit decorated
pumpkin can be worn, whatever its dye color and whether or not it is carved.

**What the player sees.**
- **On other players and armor stands:** the dyed, carved pumpkin renders on the head.
- **In first person:** a full-screen vision overlay that matches the carving, shown in first person
  only. The carved openings are the only place you can see through, and the rest of the screen is the
  pumpkin, tinted with its dye color. A pumpkin with nothing carved on the face you look through blocks
  your vision completely. That is intentional.
- **Choosing the face:** a keybind cycles which of the four faces is the front, in the order
  north → east → south → west. The overlay and the head rendering (what other players see) both follow it.
- **Reduced overlay:** a client option lowers how much the overlay blocks. Full opacity is the default.
- **Endermen:** you can look at them safely, as with a vanilla carved pumpkin. The same item tag also
  stops a creaking from being frozen by your gaze, as vanilla's carved pumpkin does.

**In scope:** unlit decorated pumpkins only; head rendering on players and armor stands; the per-stencil
first-person vision overlay, built at runtime from the existing stencil overlay textures (no new art);
the face-cycling keybind; the reduced-overlay client option.
**Out of scope:** lit pumpkins (torch or candle) as headwear, stealth and mob-detection effects, fuel
or a mobile light source, rind weight or cut fineness penalties, and the display forms (stand,
windowsill, fence-post mount) from the brainstorm.

**Dependencies:** the stencil overlay textures already in
`textures/block/decorated_pumpkin/overlays/`; the pumpkin-on-a-head rendering is shared with
[Scarecrow](../farm-creatures/scarecrow.md), so build it in a reusable way. The item needs an
`Equippable` (head) component, and endermen need the item in the `minecraft:gaze_disguise_equipment`
tag. The keybind needs a client-to-server packet, and the chosen face is stored on the player and synced
to other players. Existing precedents to copy: camel-nostrils' per-loader player/entity attachment
(`CN$CamelSnoutState` with Fabric and NeoForge implementations) and the keybind-driven payloads in
minekea (`CyclePainterColorPayload`) and effective-gear (`UseAbilityPayload`). Both loaders are
affected by the render layer, overlay, keybind, attachment, and packet hooks, so check Fabric and
NeoForge.
**Verification:** a visual smoke test of the head rendering (player and armor stand) and of the vision
overlay for a carved and an uncarved pumpkin, a JUnit test for the face-cycling order, a GameTest that
turning the face never changes the pumpkin item (it still stacks with an identical one), plus manual
steps in `TEST_PLAN.md` for the enderman behavior, the equip / unequip flow, the keybind (including how
other players see it), and the reduced-overlay option.

## Decisions

- 2026-10-02 — Only unlit decorated pumpkins can be worn, like vanilla (a carved pumpkin is wearable, a
  jack o'lantern isn't). Torch-lit and candle-lit pumpkins cannot be worn.
- 2026-10-02 — Any unlit decorated pumpkin is wearable, including one that is only dyed with no carving.
- 2026-10-02 — The first-person vision overlay is per-stencil and in scope now, not a later stretch goal.
- 2026-10-02 — A dyed-only (uncarved) pumpkin blocks the wearer's vision completely. Intentional.
- 2026-10-02 — Wearing one keeps endermen calm, as a vanilla carved pumpkin does.
- 2026-10-02 — The player chooses which face is the front: a keybind cycles north → east → south →
  west. This replaces the proposal that `north` is always the front.
- 2026-10-02 — The vision overlay is built at runtime from each stencil's existing overlay texture, so
  no new art is needed.
- 2026-10-02 — A client option lowers how much the overlay blocks, with full opacity as the default.
  Accessibility is now brainstorming rule A3 in `docs/BRAINSTORMING-RULES.md`.
- 2026-10-02 — Same-as-vanilla creaking behavior is accepted: while worn, the pumpkin stops a
  creaking from being frozen by your gaze.
- 2026-10-02 — The overlay shows in first person only, as vanilla's does.
- 2026-10-02 — The face-cycling key is unbound by default and only works while a decorated pumpkin is
  worn. The cycle visits all four faces, including uncarved ones.
- 2026-10-02 — The chosen face is stored on the player, not on the item, so identical pumpkins always
  stack and nothing has to be cleared when one leaves the head slot. This replaces the earlier ideas
  that it lives on the worn item (and survives taking it off, or is removed on unequip). The player's
  choice is synced to other players, applies to whichever decorated pumpkin they wear, and follows the
  default (north) until they change it. Armor stands always show north.
- 2026-10-02 — The reduced-overlay option is an opacity slider, from fully opaque (the default) down to a
  faint tint, so even an uncarved pumpkin can be made see-through.
- 2026-10-02 — Effort re-estimated from M to **L**: head rendering on players and armor stands plus a new
  per-stencil vision overlay renderer.

## Brainstorm variants

From [combined-ideas § 3](../brainstorms/2026-09-29/combined-ideas.md#3-pumpkin-display--wearing). Bracketed
numbers are the `brainstorms/2026-09-29/agentN.md` files that suggested each variant.

- Wear a dyed and carved pumpkin like a vanilla carved pumpkin, with the same enderman protection. Your
  carving becomes your mask [2, 7, 13].
  - The vision overlay matches the stencil's openings, with an accessibility setting for how much it
    blocks [2, 7].
  - Pumpkin heads mask you from mob detection at range, a real stealth option [7].
  - A lit pumpkin on your head is a mobile light source that burns fuel and makes you more visible [7].
  - Rind weight and cut fineness affect the movement and vision penalty [7].
- **Display forms** [8, 11] — A stand, a windowsill block, a fence-post mount [8], or armor-stand display
  [11], so carved pumpkins read as household objects and not just helmets.

## Related

- [Scarecrow](../farm-creatures/scarecrow.md) — needs the same pumpkin-on-a-head rendering.
- [Candle-lit pumpkins](candle-lit-pumpkins.md) — lit pumpkins are not wearable, so this doesn't affect
  that idea.

## Open questions

None that change what gets built. Both can be decided during implementation:

- Whether to limit the opacity slider's lowest value.
- Whether the player's chosen face persists across logins (an attachment normally does) and across
  death, or resets to north. Proposal: persist it.
- How a player who starts tracking another player gets that player's current face (the equipment
  packets that sync the item won't carry it).
