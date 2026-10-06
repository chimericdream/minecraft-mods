# Pages unlocked by advancements

> Source: initial design discussion, 2026-10-06 · Effort **S** · Value ★★
> Status: **Exploring**

## Description

`requires: [advancement ids]` in frontmatter already loads and filters the table of contents; `BookScreen#advancementDone` is a stub that returns true. Wire it to the client's advancement progress, and refresh the table of contents when progress changes. Locked pages stay linkable but show a short "not unlocked yet" notice.

**Out of scope:** server-driven unlocks that are not advancements.

**Dependencies:** A way to read the player's advancement progress on the client (see the first open question), and `BookNode#isUnlocked` (done and tested).
**Verification:** JUnit for the filter (exists); manual check with an advancement-gated page.

**Finding (2026-10-06, 26.2):** `ClientAdvancements` does not expose the player's progress. The progress map is a private field (`private final Map<AdvancementHolder, AdvancementProgress> progress`), and the only public hook is `setListener`, which the vanilla advancements screen also sets and clears, so a listener of ours would break it. `AdvancementProgress#isDone` is public once you have the progress object.

## Decisions

_None yet._

## Brainstorm variants

_None yet._

## Related

_None yet._

## Open questions

- How should the client learn which advancements are done? Options: (a) widen access to the private `progress` field with an access widener, using this repo's `copy:accesswideners` flow (`docs/agents/gotchas/access-wideners.md`); (b) a mixin accessor for the same field; (c) the server sends the player's completed ids to the client in a small packet, which would also work for non-advancement unlocks and could share the packet plumbing that [recipe lookup](../authoring/server-recipe-lookup.md) needs. Recommend (c) if recipe lookup is going ahead anyway, otherwise (a).
- Show locked pages greyed out in the table of contents, or hide them entirely? Recommend hide, per the existing `hidden` behaviour.
