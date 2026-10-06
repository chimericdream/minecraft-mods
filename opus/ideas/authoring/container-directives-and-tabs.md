# Tabs, accordions and other containers

> Source: initial design discussion, 2026-10-06 · Effort **M** · Value ★★
> Status: **Exploring**

## Description

`:::tabs` / `:::details` style containers for long pages (alternative recipes as tabs, collapsible spoilers). Needs a custom block parser in commonmark and layout support for hit-testable headers. Callouts already cover the common case, so this is for pages that outgrow them.

**Out of scope:** arbitrary nesting depth and any scripting.

**Dependencies:** A commonmark block-parser extension in the Markdown parser, new `Block` types, and layout/renderer support.
**Verification:** JUnit for parsing and layout; a visual smoke test for the tab header.

## Decisions

_None yet._

## Brainstorm variants

_None yet._

## Related

_None yet._

## Open questions

- Is this worth it before real books show a need? Recommend waiting for the first migrated book.
