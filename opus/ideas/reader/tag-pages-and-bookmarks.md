# Tag pages and bookmarks

> Source: initial design discussion, 2026-10-06 · Effort **M** · Value ★★
> Status: **Exploring**

## Description

The tag index is already built at load (`Book#tags`). Add a generated Tags page in the reader that lists every tag with its pages, clickable tag chips under a page title, and bookmarks the player can add and remove, remembered per book between sessions.

**Out of scope:** syncing bookmarks between devices or servers.

**Dependencies:** `Book#tags` (done), a client-side save file in the game config folder, and `BookScreen` UI.
**Verification:** JUnit for the index (exists); manual pass for the UI.

## Decisions

_None yet._

## Brainstorm variants

_None yet._

## Related

_None yet._

## Open questions

- Where do bookmarks persist: a JSON file in the config folder? Recommend yes.
