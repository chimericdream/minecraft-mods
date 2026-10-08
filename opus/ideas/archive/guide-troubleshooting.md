# Guide troubleshooting chapter

> Source: inbox note, 2026-10-07, raised while refining [the give command](../archive/player-give-command.md) · Effort **M** · Value ★★
> Status: **Shipped** in 1.1.0

## Description

A new top-level "Troubleshooting" chapter in the built-in Opus Guide for pack authors and server admins, covering the problems they are most likely to hit. It sits between "For pack authors" and the appendices (`04-troubleshooting`, ahead of `99-appendices`), with one page per symptom group, so a symptom can be linked to directly.

First version covers two symptom groups:

- **A book shows "Book '<id>' was not found".** Causes to list: the resource pack isn't enabled on the client, the book id doesn't match the `<namespace>:<book>` folder names, the namespace is wrong, or the id was mistyped in `/give` or `/opus give`. This replaces the short note now in "For pack authors", which then links to the chapter.
- **`opus-guides.json` problems.** A malformed default guide id (logged as a warning, falls back to `opus:guide`), a missing or invalid file (falls back to the defaults with a warning), the ops-only option refusing non-op players, and the fact that edits apply on the next command with no restart. Says where the file lives (`config/opus-guides.json`, server-side only) and what the log line looks like.

Each entry is written as symptom, likely cause, fix, in the guide's plain tone. Non-technical wording where the reader isn't a developer, with the validator named as the first tool to reach for.

- Opening a missing book shows a small screen (replacing the old action-bar message, which cannot hold a link) with the "Book '<id>' was not found" message, an **Open troubleshooting** button that opens the chapter's "not found" page in the default guide, and a **Close** button that returns to the previous screen. If the default guide itself is the missing book, or is not installed, there is nothing to open, so the troubleshooting button is left out.

**Out of scope for the first version:** player-facing troubleshooting, books that load but look wrong (missing item texture or model, broken links, widgets), and reload and cache problems. Any of these can become a later page in the same chapter.

**Dependencies:** [Player-usable give command](../archive/player-give-command.md), shipped in 1.1.0, whose `opus-guides.json` behaviour one page documents. No chimeric-lib or loader-specific work: the screen is common client code.
**Verification:** Run the book validator on the guide (`0 errors, 0 warnings`), and open the chapter in game to check it renders and the links work. Check the screen by opening a missing book (`/give @s opus:book[opus:book_id={book_id:"opus:nope"}]`): the button opens the chapter, and Close returns to the previous screen. Add a line to `TEST_PLAN.md` if the guide's pages are listed there.

## Decisions

- 2026-10-08 — Troubleshooting is a new top-level chapter in the Opus Guide, not a section of "For pack authors" or an appendix.
- 2026-10-08 — The audience is pack authors and server admins, not players.
- 2026-10-08 — The first version covers "not found" books and `opus-guides.json` problems only.
- 2026-10-08 — The "not found" message links to the troubleshooting chapter. The old message was action-bar text, which cannot be clicked, so it is replaced by a small screen with an **Open troubleshooting** button (chosen over a clickable chat message, which would need a client command on both loaders).
- 2026-10-08 — No pointer from the guide's front page; being a top-level item in the main nav is enough.
- 2026-10-08 — Effort re-estimated from S to M: the new "not found" screen opens a page in another book, on top of the guide pages.

## Brainstorm variants

_None yet._

## Related

- [Player-usable give command](../archive/player-give-command.md) — defines the `opus-guides.json` behaviour this chapter documents.
- [Validate books in the build](../authoring/build-time-validation.md) — the validator is the first fix to suggest for broken books.

## Open questions

_None._
