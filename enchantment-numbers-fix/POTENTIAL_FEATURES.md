# Potential Features — Enchantment Numbers Fix

Brainstormed, thematically appropriate feature ideas. Nothing here is committed or implemented; these are
starting points for future planning.

This mod's charm is being tiny and purely cosmetic, so every idea below preserves that: client-side only,
display-layer only, safe to add or remove at any time.

## Numeral style options

* **Style picker** — a config choice between:
  * **Roman (extended)** — the current behavior (XV, XLII, MMXXVI…).
  * **Roman with vinculum** — overline notation for 4,000+ (V̄ = 5,000), so even command-block-level
    absurdities like level 32767 render as "proper" Roman numerals.
  * **Decimal everywhere** — the inverse fix: "Sharpness 5" instead of "Sharpness V," for players who
    never liked Roman numerals in the first place. Same consistency goal, opposite direction.
* **Fallback threshold** — configurable level above which the mod gives up on Roman numerals and shows
  decimal (e.g. above 3,999), since MMMCMXCIX stops being readable long before it stops being correct.
* **Compact hybrid** — optional "X (10)" or "XV [15]" tooltip style showing both notations at once.

## Wider display coverage

* **Potion & status effect amplifiers** — apply the same fix to effect names ("Strength 15" → "Strength
  XV") in tooltips, the HUD effect list, and the inventory effect panel.
* **Beacon UI** — effect levels shown in the beacon screen follow the same numbering.
* **Anywhere-else audit** — mob effect command feedback, Jade/WTHIT-style tooltip mods, and enchanted
  book lore lines; a small API or hook so other client mods can reuse the formatter.

## Localization awareness

* **Locale-respecting numerals** — Roman numerals are a Western convention; offer per-language overrides
  so, e.g., CJK localizations can choose native numerals (十五) or decimal instead. Driven by lang files
  so translators decide.

## Cosmetic flourishes (strictly optional)

* **Over-vanilla highlighting** — levels above an enchantment's natural maximum render in a configurable
  color (subtle gold?), quietly flagging "this came from commands/mods" without judging it.
* **Tooltip alignment fix-ups** — ensure very long numerals don't wrap awkwardly in narrow tooltips.

## Non-goals worth writing down

* No gameplay changes, no server requirement, no data components — the moment an idea needs the server,
  it belongs in a different mod.
