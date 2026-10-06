# Open-in-guide from items

> Source: initial design discussion, 2026-10-06 · Effort **M** · Value ★★★
> Status: **Exploring**

## Description

An item tooltip line and a key (like recipe viewers' "R") that opens the book page documenting the hovered item, using a registry of item id to page that books declare in frontmatter (`documents: [minecraft:hopper]`). Lets a mod ship docs that players find from the item itself.

**Out of scope:** auto-generating pages from registries.

**Dependencies:** A new frontmatter key and index in `Book`, a tooltip hook per loader (Fabric `ItemTooltipCallback`, NeoForge tooltip event, as hopper-xtreme does), and a keybind.
**Verification:** JUnit for the index; manual pass for the hook.

## Decisions

_None yet._

## Brainstorm variants

_None yet._

## Related

_None yet._

## Open questions

- Does every book get to claim items, or only ones the player has "installed" (held)? Recommend any loaded book.
