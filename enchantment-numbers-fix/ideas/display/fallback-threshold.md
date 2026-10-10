# Fallback threshold

> Shortlist #1 · Tier 1 — Quick wins · Effort **S** · Value ★★ · Votes — · My vote **Yes**
> Status: **Exploring**

## Description

A configurable level above which the mod gives up on Roman numerals and shows decimal, since MMMCMXCIX
stops being readable long before it stops being correct. This also fixes the one real weakness of the mod:
a command-block enchantment at level 32767 turns into an unreadable string. **Recommendation:** default
to 3,999 (the last "proper" Roman number), and make it a config number. This is the mod's first config, so
set that up here and reuse it for everything below.

## Decisions

_None yet._

## Related

- [Numeral style option](numeral-style.md) — shares the mod's first config.
- [Potion and status effect amplifiers](../effects/effect-amplifiers.md) — reuses the threshold.

## Open questions

_None yet._
