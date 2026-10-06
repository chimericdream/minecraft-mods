# Expand and collapse all chapters

> Source: collapsible chapters work, 2026-10-06 · Effort **S** · Value ★★
> Status: **Exploring**

## Description

Chapters in the sidebar now start closed and the player opens them one at a time. In a book with a lot of chapters, such as a large modpack's guide, that gets tedious in both directions: there is no quick way to see everything at once, and no quick way to tidy up after exploring. Add a control in the sidebar header to open every chapter and one to close every chapter, and optionally an "accordion" mode where opening a chapter closes its siblings so only one branch is open at a time.

The control acts on the same per-book open/closed state the arrows already use, so the page being read stays visible (its ancestors are never closed by "collapse all").

**Out of scope:** remembering open chapters between game sessions (today they are remembered only until the game closes), and changing how chapters look when closed.

**Dependencies:** `BookScreen`'s sidebar and its open-chapter set only; no change to the book model. The open/closed logic could move into a small core class so it can be unit tested, since today it lives in the screen and is checked only by looking at it.
**Verification:** a unit test for "collapse all keeps the current page's ancestors open" if the logic moves into core; otherwise a headless screenshot check and a line in `TEST_PLAN.md`.

## Decisions

_None yet._

## Brainstorm variants

_None yet._

## Related

- [Deep nesting and long titles](deep-nesting-and-long-titles.md) — both are about keeping a large sidebar manageable.
- [Tag pages and bookmarks](tag-pages-and-bookmarks.md) — another way to reach pages without walking the tree.

## Open questions

- One toggle button (which flips between "open all" and "close all") or two separate buttons? The sidebar header is narrow, and the search box and its clear button already use most of it. Recommend two small icon-style buttons on a second header row, or a single toggle if that row is too costly.
- Should accordion mode exist at all, and if so is it a per-book setting in `book.yml` or a player setting? Recommend leaving it out until someone asks, because the manual arrows already cover it.
- Should the controls appear only when the book has more than a handful of chapters? Recommend yes, so small books stay uncluttered.
