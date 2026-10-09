# `/portallink debug` command

> Shortlist #1 · Tier 1 — Quick wins · Effort **S** · Value ★★ · Votes — · My vote **Maybe**
> Status: **Exploring**

## Description

Prints the entry portal's address and every scored candidate for the player's last transit. The linker
already builds exactly that list for its debug log (`logDecision` in `PortalAddressLinker`), so this mostly
means remembering the last decision per player and formatting it as chat output. It is the lowest-effort
way to answer "why did my portal go there?" without turning on log files.
**Recommendation:** op-only, a single `last` subcommand, no history. Skip persisting anything across
restarts. It doesn't change gameplay, so there is no balance config (A2); the existing debug toggle can stay
the only knob (A4).

## Decisions

_None yet._

## Brainstorm variants

From [`brainstorms/2026-08-18-legacy/potential-features.md`](../brainstorms/2026-08-18-legacy/potential-features.md)
(Troubleshooting):

- **`/portallink debug` command** — print the entry portal's address and the scored candidates for the
  last transit, for players who don't want to leave debug logging on all the time.

## Related

- [Address preview](address-preview.md) — same information for a block you're looking at, instead of the last transit
- [Arrival cue](arrival-cue.md) — the in-world half of the same troubleshooting story

## Open questions

_None yet._
