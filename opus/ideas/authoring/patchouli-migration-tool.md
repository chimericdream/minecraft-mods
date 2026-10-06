# Patchouli to Opus converter

> Source: initial design discussion, 2026-10-06 · Effort **M** · Value ★★★
> Status: **Exploring**

## Description

A script that turns a Patchouli book (`book.json`, categories, entries, page types) into an Opus folder of Markdown files, so hopper-xtreme and minekea (both on Patchouli, which lags MC releases) can move over without rewriting their docs by hand. Categories become chapter folders, entries become pages, `text`/`crafting`/`spotlight`/`image` pages become Markdown, recipe blocks and item icons. Unsupported page types are kept as a comment plus a warning so nothing is silently lost.

**Out of scope:** keeping Patchouli compatibility at runtime, and converting multiblock pages (until Opus has a multiblock widget).

**Dependencies:** The existing `scripts/update-patchouli-books.ts` shows how this repo already handles Patchouli books; the validator (`ValidateBook`) checks the output.
**Verification:** Run on hopper-xtreme's and minekea's real books and validate the result with `ValidateBook`.

## Decisions

_None yet._

## Brainstorm variants

_None yet._

## Related

_None yet._

## Open questions

- Written in TypeScript (Bun, like the other scripts) or Java next to the validator? TypeScript matches `scripts/`.
