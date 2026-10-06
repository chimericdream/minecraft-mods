# Reader accessibility

> Source: initial design discussion, 2026-10-06 · Effort **M** · Value ★★★
> Status: **Exploring**

## Description

Text scale setting, a high-contrast theme, full keyboard navigation (tab through links, Enter to follow), and narrator support for headings and links. Colour is never the only link signal (links are already underlined). Follows rule A3 in `docs/BRAINSTORMING-RULES.md`.

**Out of scope:** translating books; that is each book's job.

**Dependencies:** A YACL config (via chimeric-lib's `YaclConfig`), the layout engine (already scale-aware through `TextMetrics`), and `BookScreen` focus handling.
**Verification:** JUnit for scaled layout; manual narrator and keyboard pass.

## Decisions

_None yet._

## Brainstorm variants

_None yet._

## Related

_None yet._

## Open questions

- Is text scale a per-player setting only, or can a book hint a minimum size? Recommend per-player only.
