# Pages unlocked by advancements

> Source: initial design discussion, 2026-10-06 · Effort **S** · Value ★★
> Status: **Exploring**

## Description

`requires: [advancement ids]` in frontmatter already loads and filters the table of contents; `BookScreen#advancementDone` is a stub that returns true. Wire it to the client's advancement progress, and refresh the table of contents when progress changes. Locked pages stay linkable but show a short "not unlocked yet" notice.

**Out of scope:** server-driven unlocks that are not advancements.

**Dependencies:** Client advancement tracker (`ClientAdvancements`), `BookNode#isUnlocked` (done and tested).
**Verification:** JUnit for the filter (exists); manual check with an advancement-gated page.

## Decisions

_None yet._

## Brainstorm variants

_None yet._

## Related

_None yet._

## Open questions

- Show locked pages greyed out in the table of contents, or hide them entirely? Recommend hide, per the existing `hidden` behaviour.
