# Enchantment Numbers Fix — Feature Shortlist

Scored and ranked from the legacy backlog in this folder ([`ideas-md.md`](ideas-md.md), your older notes,
and [`potential-features.md`](potential-features.md), the agent brainstorm). There is no
`combined-ideas.md` for a legacy backlog, so this list was built straight from the source notes and
checked against what the mod ships today. Everything that isn't ranked is in
[What was cut and why](#what-was-cut-and-why).

The generic "Add thematic/fun advancements" bullet was dropped on purpose: this mod has no gameplay for an
advancement to attach to.

## How items were scored

- **Effort**, for a two-loader (Fabric + NeoForge) Architectury mod, counting art, datagen and rendering,
  not just code:
  - **S**: a day or two, mostly assets and data, reuses existing code.
  - **M**: a new block or block entity, a new interaction, or a moderate rendering change.
  - **L**: a new entity, custom rendering, worldgen, or a new UI.
  - **XL**: a system that touches many other features.
- **Value**, ★ to ★★★: fit with the mod's identity statement, and how much players would notice it.
- **Votes**: how many independent brainstorm answers suggested it. Legacy items have none, so this column
  shows `—`.

Ranking is by value relative to effort. The identity is one sentence: "converts enchantment levels above
10 to Roman numerals instead of their decimal version". The mod is client-side and display-only, and it
has no config today (one mixin, `ENFEnchantmentMixin`, and a Roman-numeral helper). So the ranked list
covers **display-layer** ideas only, which are cheap and safe to add or remove at any time. Your own notes
also contain a different kind of idea: gameplay changes that need the server (new enchantments,
compatibility overrides, enchanting-table changes). Those don't fit this identity and are in
[Beyond the current identity](#beyond-the-current-identity-moved-to-a-separate-mod) below, with a question.
Ideas that change behavior need a config toggle (A2), and each item says what else should be tunable (A4).
Most of the list needs a config, so budget for the mod's first config screen.

## Already shipped

Levels above 10 render as Roman numerals everywhere the enchantment level name is shown (tooltips,
enchanted book names). Nothing else from the notes is in yet.

## Ranked list

Linked rows have their own idea files.

| # | Feature | Effort | Value | Votes | My Vote |
|---|---|---|---|---|---|
| | **Tier 1 — Quick wins** | | | | |
| 1 | [Fallback threshold](../../display/fallback-threshold.md) | S | ★★ | — | Yes |
| 2 | [Numeral style option](../../display/numeral-style.md) | S | ★★ | — | Yes |
| 3 | [Over-vanilla highlighting](../../display/over-vanilla-highlighting.md) | S | ★★ | — | Yes |
| 4 | [Compact hybrid style](../../display/compact-hybrid-style.md) | S | ★ | — | Yes |
| | **Tier 2 — Solid mid-size features** | | | | |
| 5 | [Potion and status effect amplifiers](../../effects/effect-amplifiers.md) | M | ★★★ | — | Yes |
| 6 | [Configurable tooltip sorting](../../tooltips/tooltip-sorting.md) | M | ★★ | — | Maybe |
| 7 | [Configurable tooltip colors and icons](../../tooltips/tooltip-colors-and-icons.md) | M | ★★ | — | Maybe |
| 8 | [Locale-respecting numerals](../../display/locale-numerals.md) | M | ★ | — | Maybe |

## Tier 1 — Quick wins

### 1. Fallback threshold — S · ★★

A configurable level above which the mod gives up on Roman numerals and shows decimal, since MMMCMXCIX
stops being readable long before it stops being correct. This also fixes the one real weakness of the mod:
a command-block enchantment at level 32767 turns into an unreadable string. **Recommendation:** default
to 3,999 (the last "proper" Roman number), and make it a config number. This is the mod's first config, so
set that up here and reuse it for everything below.

### 2. Numeral style option — S · ★★

A config choice of style: extended Roman (today's behavior) or decimal everywhere, the inverse fix for
players who never liked Roman numerals. The agent note also proposed a "vinculum" style (an overline for
numbers over 4,000). **Recommendation:** ship the two simple styles. The overline relies on a combining
character that Minecraft's default font may not draw correctly, so leave it out unless you test it
first. Keep the default as today's behavior (A4).

### 3. Over-vanilla highlighting — S · ★★

Levels above an enchantment's natural maximum render in a configurable color (a subtle gold, say),
quietly flagging that the level came from commands or other mods. **Recommendation:** use a text
decoration as well as color, for example a trailing mark, so it isn't color-only (A3), and make it
default off. It is the cheap half of #7.

### 4. Compact hybrid style — S · ★

An optional "X (10)" or "XV [15]" tooltip style showing both notations at once. **Recommendation:**
add it as a third value of the style option in #2, not as a separate feature. Useful mostly for people
who can't read big Roman numerals at a glance, which is the accessibility argument for it (A3).

## Tier 2 — Solid mid-size features

### 5. Potion and status effect amplifiers — M · ★★★

Apply the same fix to effect amplifiers: "Strength 15" becomes "Strength XV" in tooltips, the HUD effect
list and the inventory effect panel. Vanilla has Roman names for only the first few amplifier levels
and falls back to decimal, which is the same inconsistency this mod already fixes for enchantments, so
this is the most natural extension. The agent note adds the beacon screen; fold that in as part of the
same pass. **Recommendation:** do it, but note it stretches the identity sentence slightly, which is only
about enchantment levels. If you agree, the identity statement should say "enchantment and effect
levels". Share the threshold and style options from #1 and #2.

### 6. Configurable tooltip sorting — M · ★★

Choose how enchantments are ordered in an item's tooltip: alphabetical, vanilla order, a custom order, or
grouped by type. It is one of your own notes and it is client-side and display-only, so it fits the "no
server required" pillar. **Recommendation:** start with alphabetical and vanilla, then add grouped
by type, and treat the custom order as a stretch. Default stays vanilla (A4).

### 7. Configurable tooltip colors and icons — M · ★★

Configurable colors and icons for enchantment lines in tooltips, from your notes. It overlaps with #3,
which is the same idea for one case. **Recommendation:** decide whether #3 stands alone or becomes a
setting inside this one. Colors should always be paired with another cue (A3), and icons need art.
If you build this, build #3 as a part of it.

### 8. Locale-respecting numerals — M · ★

Roman numerals are a Western convention. Offer per-language overrides so, for example, CJK localizations
can choose native numerals or decimal. It's driven by the language files, so translators decide.
**Recommendation:** only if there is a request from translators. It would be a small addition once
the style option exists.

## Beyond the current identity (moved to a separate mod)

These are from your notes. They are real gameplay features that need the server, so they conflict with
this mod's identity sentence and with the agent note's non-goals ("no gameplay changes, no server
requirement... the moment an idea needs the server, it belongs in a different mod"). They aren't
ranked. Decision: they move to a separate mod, tracked in [#137](https://github.com/chimericdream/minecraft-mods/issues/137), and this mod stays a client-side display fix with its identity widened to "enchantment and effect levels".

- Rename the mod (it may become more of a "Tooltip Tweaks" mod; see your note below).
- Add some new enchantments.
- Override which items can be enchanted with which enchantments.
- Allow previously incompatible enchantments to be combined, with some still disabled by default (Silk
  Touch and Fortune, Riptide and Loyalty, Riptide and Channeling).
- Allow non-solid blocks between bookshelves and the enchanting table (inspired by the Enchanter Fix mod).

**Your note:** Not right now. At most, I might rename it and make it more of a "Tooltip Tweaks" mod.

## What was cut and why

**Too vague to act on.**
- **Anywhere-else audit** (command feedback, Jade-style tooltip mods, a reusable formatter API). There are
  no concrete targets. Revisit after #5.
- **Tooltip alignment fix-ups.** No known problem to fix.

**Merged into another item.**
- Beacon UI levels, into #5. The compact hybrid style, into the style option (#2/#4).

## Suggested first arc

Do #1 (fallback threshold) and #2 (style option) together, since #1 introduces the mod's first config and
#2 uses it. #3 and #4 are small follow-ups. Then #5 (effects) is the headline addition, with an identity
tweak. #6 and #7 are the display-layer ideas from your own notes. The bigger question is the rename and
the gameplay ideas, which are a separate decision.
