# Numeral style option

> Shortlist #2 · Tier 1 — Quick wins · Effort **S** · Value ★★ · Votes — · My vote **Yes**
> Status: **Exploring**

## Description

A config choice of style: extended Roman (today's behavior) or decimal everywhere, the inverse fix for
players who never liked Roman numerals. The agent note also proposed a "vinculum" style (an overline for
numbers over 4,000). **Recommendation:** ship the two simple styles. The overline relies on a combining
character that Minecraft's default font may not draw correctly, so leave it out unless you test it
first. Keep the default as today's behavior (A4).

## Decisions

_None yet._

## Related

- [Fallback threshold](fallback-threshold.md) — introduces the config this uses.
- [Compact hybrid style](compact-hybrid-style.md) — a third value of this option.
- [Locale-respecting numerals](locale-numerals.md) — per-language overrides of the style.

## Open questions

_None yet._
