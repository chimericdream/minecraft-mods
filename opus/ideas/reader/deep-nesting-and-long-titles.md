# Deep nesting and long titles in the sidebar

> Source: collapsible chapters work, 2026-10-06 · Effort **S** · Value ★★
> Status: **Exploring**

## Description

Chapters can nest to any depth, but the sidebar has a fixed width. Each level indents by 6 pixels (up to 8 levels, then it stops indenting) and a title that does not fit is clipped, with the full title shown in a tooltip on hover. That is fine for two or three levels with short names, and it gets worse with long names, such as "Changes to vanilla mechanics" three levels down, where only the first few words are visible.

Decide what the sidebar should do when a book is deeper or wordier than that. Candidate approaches, which can be combined:

- **Stop indenting sooner.** Cap indentation at four or five levels and rely on the arrows and the breadcrumbs to show depth.
- **Wrap long titles** onto a second line instead of clipping them, at the cost of variable row height (the sidebar currently assumes every row is 18 pixels).
- **Let the sidebar scroll sideways**, or let the player drag its edge wider.
- **Drill-down navigation** for very large books: the sidebar shows only the current chapter's contents with a "up one level" row, instead of a tree. This is a different style from the tree, so it would be a per-book or player setting.
- **A validator warning** when a book nests deeper than a configurable depth, so authors find out while writing. This is the "nesting-depth limit" as an authoring check rather than a hard limit, and nothing is ever refused.

**Out of scope:** changing how the book is stored; folders keep nesting to any depth.

**Dependencies:** `BookScreen`'s sidebar drawing and hit-testing (rows are currently a fixed height), and for the validator option, `ValidateBook`.
**Verification:** headless screenshots of a deeply nested sample book at a couple of window sizes; a unit test for the validator warning.

## Decisions

_None yet._

## Brainstorm variants

_None yet._

## Related

- [Expand and collapse all chapters](expand-collapse-all.md) — both keep a large sidebar manageable.
- [Reader accessibility](accessibility.md) — a text-scale setting makes clipping worse, so the two should be designed together.

## Open questions

- How deep and how wordy do real books get? The first real test is the user's own modpack; look at it before choosing. Recommend waiting for that, then starting with the cheapest option (stop indenting sooner, plus the validator warning).
- Is a draggable or wider sidebar acceptable, given the text column is already capped for readability and there is spare room on wide screens?
- Should drill-down navigation exist? Recommend only if the tree proves unmanageable at modpack scale.
