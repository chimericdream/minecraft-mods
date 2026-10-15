# Filter copy and paste

> Shortlist #4 · Tier 2 — Solid mid-size features · Effort **M** · Value ★★★ · Votes — · My vote **Yes**
> Status: **Ready**

## Description

Building a 40-hopper sorting hall currently means 40 manual filter setups. Players have asked for ways to
copy a filter, both in the crafting grid and from a hopper in the world. This idea covers two routes:

1. **In the crafting grid.** A custom shapeless recipe: a configured filter + a blank filter of the same tier
   gives back the original plus an identical copy (like book cloning), so a configured filter can be
   duplicated without opening any hopper.
2. **In the world, with the wrench.** Copy the filter from one placed hopper, then paste it onto others. The
   "copied" state is an item component on the wrench so it survives relogs, and results show as actionbar
   messages.

**Background:** a hopper's filter is a physical item (`hopper_item_filter`, 5 slots, or
`diamond_hopper_item_filter`, 10 slots) sitting in the hopper's filter slot. Its contents are the
`CONTAINER` component and its Include/Exclude mode is the `filter_mode` component. Copying a filter means
cloning one item's data into another filter item. Copies carry the whole filter: items and mode (and
overflow routing, #6, if that ships). Every hopper family has exactly one filter slot, so paste works the
same on all of them.

**Recipe rules:**

- Matches exactly one configured filter and one blank filter of the same tier, and nothing else. Two
  configured filters, two blanks, or mixed tiers give no result.
- The result is a copy of the source's contents and mode. The source is returned to the player, not
  consumed. How to keep the original is an implementation detail; vanilla's book cloning is the reference.
- A vanilla shapeless recipe can't tell a blank from a configured filter, so this is a `CustomRecipe` type.
  Opus' `TomeCombineRecipe` is the reference, and it is all common code: Architectury's `DeferredRegister`
  covers both loaders, plus a one-line recipe JSON. The shared plumbing is also tracked as a
  [ChimericLib idea](../../../chimeric-lib/ideas/suite-patterns/custom-recipe-helpers.md).

**Wrench rules:**

- The wrench has modes, as in #9: sneak-use in the air cycles them. In Copy-Paste mode, a click with nothing
  copied copies the hopper's filter, and a click with something copied pastes it. Rotate mode behaves as it
  does today.
- Pasting onto a hopper with no filter item consumes a blank filter from the player's inventory (creative
  mode skips the cost), fills it, and installs it.
- Pasting onto a hopper that already has a filter refuses. Sneak-click overwrites it.
- The wrench copies from placed hoppers only, not from loose filters (the recipe covers those).

**A "blank" filter** is any same-tier filter with no items in its container, whatever its mode. Copying
overwrites the blank's mode.

**Out of scope for now:** a paste-to-many mode, cross-tier copies, and the separate filter-card item (#14).

**Dependencies:** the wrench's mode system from [Wrench modes](../wrench/wrench-modes-and-stats.md) (Copy-Paste
is its first mode, so build the mode cycling alongside); always registering the wrench as "Hopper Wrench"
(a new texture from the user); optionally the
[ChimericLib recipe helper](../../../chimeric-lib/ideas/suite-patterns/custom-recipe-helpers.md). No
loader-specific work is expected.

**Verification:** GameTests for the recipe (matches exactly one configured and one blank filter of the same
tier, copies contents and mode, returns the source) and for wrench copy/paste (blank consumed, refuse vs.
sneak-overwrite, creative skips the cost); manual steps for the wrench mode switching and actionbar messages
in `TEST_PLAN.md`.

**Effort re-estimate:** **M**. The recipe is common code, following Opus' `TomeCombineRecipe`.

## Decisions

- 2026-10-14 — Players want both copy routes: a crafting-grid recipe and an in-world interaction with a
  filtered hopper.
- 2026-10-14 — Pasting onto a hopper with no filter item consumes a blank filter from the player's
  inventory (creative skips the cost).
- 2026-10-14 — Pasting onto a hopper that already has a filter refuses unless the player is sneaking;
  sneak-paste overwrites.
- 2026-10-14 — The grid recipe keeps the original and needs a blank filter per copy (configured filter +
  blank filter → two identical filters).
- 2026-10-14 — The wrench is mode-based, as in #9: sneak-use in the air cycles modes, and Copy-Paste mode
  uses click = copy (nothing held) or paste (something held), sneak-click = overwrite. Rotate mode keeps
  today's behavior.
- 2026-10-14 — The wrench copies from placed hoppers only, not from loose filter items.
- 2026-10-14 — Copies are same-tier only (standard to standard, diamond to diamond). No truncation or
  cross-tier cases.
- 2026-10-14 — A "blank" filter is any same-tier filter with no items in its container, whatever its
  Include/Exclude mode. Copying overwrites the blank's mode.
- 2026-10-14 — The diamond upgrade recipe's suspected loss of filter contents is tracked separately (#139), not
  fixed as part of this feature.
- 2026-10-14 — The grid recipe was briefly replaced by an off-hand + main-hand sneak-use gesture, then
  restored. Opus' `TomeCombineRecipe` showed that a custom recipe is common-code only, so the recipe's main
  cost is gone. The gesture is dropped (it also collided with sneak-use toggling Include/Exclude).
- 2026-10-14 — Hopper X-Treme's wrench is always registered, dropping the "skip if Minekea is loaded"
  check, because it is gaining filter copy and paste. To avoid confusion with Minekea's wrench it is
  renamed "Hopper Wrench" (display name only; the item ID `hopperxtreme:tools/wrench` is unchanged). The
  user will supply a new texture. The `minekea:tools/wrench` rotation workaround in the multi-hopper
  blocks can stay.
- 2026-10-14 — The custom-recipe registration plumbing is captured as a ChimericLib idea so other mods can
  reuse it. Hopper X-Treme is its second consumer after Opus.

## Related

- [Wrench modes and stats mode](../wrench/wrench-modes-and-stats.md) — copy and paste is its first mode.
- [Overflow routing](overflow-routing.md) — the setting should be copied too.
- [Filter cards](filter-cards.md) — the item-based alternative.
- [Custom recipe helpers (ChimericLib)](../../../chimeric-lib/ideas/suite-patterns/custom-recipe-helpers.md) —
  shared plumbing for the copy recipe. Either build the recipe first and extract the helper afterwards, or
  the reverse.

## Open questions

- **Sequencing with the lib helper.** Build the recipe directly like Opus did and extract the helper later,
  or build the helper first? Can be decided during implementation.
- **Keeping the original.** Confirm the 26.2 mechanism for returning the source stack from a `CustomRecipe`
  (compare vanilla `BookCloningRecipe`). Can be decided during implementation.
- **Adjacent bug (tracked separately).** The diamond upgrade recipe (`filter` + `diamond`) copies no
  components, so upgrading a configured standard filter likely wipes its contents. Tracked in
  [#139](https://github.com/chimericdream/minecraft-mods/issues/139); verify in-game there.
