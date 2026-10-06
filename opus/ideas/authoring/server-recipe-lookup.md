# Look recipes up by id

> Source: initial design discussion, 2026-10-06 · Effort **L** · Value ★★★
> Status: **Exploring**

## Description

`recipe: minecraft:hopper` in a recipe block resolves to the real recipe, so pages stay correct when a modpack changes recipes. Needs the server to send recipe data to the client, because vanilla no longer syncs full recipes (check the 26.2 sources for exactly what the client still receives). Inline definitions keep working and stay the fallback when no server data is available (client-only play, or an id the server did not send).

**Out of scope:** editing recipes, and recipe types the mod does not know how to draw (they fall back to a text line).

**Dependencies:** A small server-to-client sync packet (Architectury `NetworkManager`, as in all-hallows-steve's `PumpkinFaceNetworking`), `RecipeSpec.BY_ID` (already parsed), and the widget renderer's by-id branch (a TODO today).
**Verification:** JUnit for the by-id resolution rules; a GameTest or manual check with a datapack that changes a recipe.

## Decisions

_None yet._

## Brainstorm variants

_None yet._

## Related

_None yet._

## Open questions

- Send only the recipes a book actually references (collected at load), or all crafting recipes? Sending only referenced ones keeps the packet small but needs a request round-trip.
- Should a server without Opus installed degrade silently to inline-only? (Assumed yes.)
