# Effective Gear — Feature Shortlist

Scored and ranked from the legacy backlog in this folder ([`potential-features.md`](potential-features.md)).
There is no `combined-ideas.md` for a legacy backlog, so this list was built straight from the source
notes and checked against what the mod ships today. Everything that isn't ranked is in
[What was cut and why](#what-was-cut-and-why).

A lot of this backlog has already shipped since it was written. All 18 vanilla trim *patterns* now have a
bonus (including Silence, Ward and Snout, which were the examples in the notes), and the mod ships nine
custom trim materials. What's left is a smaller set of genuinely new ideas.

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

Ranking is by value relative to effort. The identity statement says bonuses "start small and situational"
and "never outclass vanilla's own progression", so the cheap wins are new trim materials that follow the
existing pattern (a full matching set grants one small bonus, usually an immunity or a "mob X ignores
you"), because the mod already has the plumbing for exactly that. The big bet is wielded-gear trims,
which the identity statement explicitly promises ("wielded-gear trims, enchantments, and other gear
mechanics as the mod grows"). Balance-affecting items need a config toggle (A2), and each item says
what else should be tunable (A4).

## Already shipped

These came out of the same notes and are in the mod today, so they aren't re-ranked:

- **Trim-template bonuses.** All 18 patterns have a bonus, including Silence, Ward and Snout.
- **Materials:** blaze powder, echo shard, enchanted golden apple, ender pearl, honeycomb, nether star,
  prismarine shard, slimeball and turtle scute. Note that some shipped with different bonuses than the
  notes suggested (prismarine gives guardian-laser resistance, not underwater breathing).
- **Preserving** (shears enchantment, fixed default leaf color).
- **Covered by a pattern bonus:** wither immunity (Rib) and levitation immunity (Spire).

## Ranked list

Linked rows have their own idea files.

| # | Feature | Effort | Value | Votes | My Vote |
|---|---|---|---|---|---|
| | **Tier 1 — Quick wins** | | | | |
| 1 | [Glowstone trim material](../../trim-materials/glowstone-trim-material.md) | S | ★★ | — | Yes |
| 2 | [Ghast tear trim material](../../trim-materials/ghast-tear-trim-material.md) | S | ★★ | — | Yes |
| 3 | [Phantom membrane trim material](../../trim-materials/phantom-membrane-trim-material.md) | S | ★★ | — | Yes |
| 4 | [Mixed-material bonus](../../trim-bonuses/mixed-material-bonus.md) | S | ★★ | — | Maybe |
| 5 | [Snow trim material](../../trim-materials/snow-trim-material.md) (blue ice: [own idea](../../trim-materials/blue-ice-trim-material.md)) | S | ★ | — | Yes, but blue ice instead |
| | **Tier 2 — Solid mid-size features** | | | | |
| 6 | [Totem of undying trim material](../../trim-materials/totem-trim-material.md) | M | ★★ | — | Maybe |
| 7 | [Preserving: capture the exact biome tint](../../enchantments/preserving-biome-tint.md) | L | ★★ | — | Maybe |
| | **Tier 3 — Big bets** | | | | |
| 8 | [Wielded-gear trims (weapons and tools)](../../wielded-gear/wielded-gear-trims.md) | XL | ★★★ | — | Yes |

## Tier 1 — Quick wins

### 1. Glowstone trim material — S · ★★

A full set trimmed with glowstone makes you immune to the blindness and darkness effects. The notes also
floated "full brightness for the wearer", but that's a client rendering change and would be hard to make
accessible. **Recommendation:** go with the immunity version only (the mod already has several
immunity-style bonuses, so it's mostly an effect-removal check plus the material's assets and data). It
also helps players who find those effects hard to play through (A3), which is a pleasant side effect.
Glowstone dust is cheap, so the bonus should stay small and nothing else.

### 2. Ghast tear trim material — S · ★★

Ghasts stop targeting you while you wear the full set. The notes also offered "regeneration after taking
fire damage", but a "mob X ignores you" bonus is a pattern the mod already has (iron, resin, ender pearl,
gold, snout) and is far cheaper. **Recommendation:** ghasts ignore you, with a toggle (A2). A ghast tear
is rare enough to feel earned without being overpowered.

### 3. Phantom membrane trim material — S · ★★

Two options in the notes: no phantom spawns from insomnia, or a slow-fall / feather-falling boost.
**Recommendation:** go with "phantoms ignore you" (or no insomnia-spawned phantoms) rather than the fall
bonus, because it fits the mod's existing ignore-bonus pattern and doesn't overlap with idea #4 or with
the Flow and Bolt abilities. Membrane drops only from phantoms, so it's a nicely thematic ingredient.
Needs a toggle (A2).

### 4. Mixed-material bonus — S · ★★

A small universal bonus (the note suggests reduced fall damage) for wearing four *different* trim
materials at once. This adds a second axis next to the matched-set bonus and rewards variety, which
suits a mod whose bonuses are all about trim choices. It reuses `TrimSetUtils`, so it's mostly a new
check on the same data. **Recommendation:** configurable amount and on/off toggle (A2, A4). Keep it
smaller than any matched-set bonus so matching is still the better deal. Say clearly in the README
that matched sets and mixed sets don't stack.

### 5. Snow trim material — S · ★

Freezing immunity, and the ability to walk on powder snow. Vanilla's leather armor already grants both
(only the boots do the walking part), so this only repeats it for a different material (N3, but as a
variant: it lets any armor type get it). **Recommendation:** keep it as a cheap, low-value filler for
cold-biome players, and cut it if the vote is lukewarm.

## Tier 2 — Solid mid-size features

### 6. Totem of undying trim material — M · ★★

A high-rarity material. On lethal damage while wearing the full set, you survive once without consuming
anything, then it recharges on a long cooldown. It mirrors the totem item itself, which makes it the
most exciting material in the notes and the riskiest: it can outclass vanilla's own progression.
**Recommendation:** only with a long, configurable cooldown (A2, A4), a per-player cooldown that
survives relogging, and clear feedback (particles and a sound, plus a cooldown indicator) so players
aren't surprised. The totem is dropped by evokers in raids, so the material is hard to get in bulk, which
helps the balance.

### 7. Preserving: capture the exact biome tint — L · ★★

Instead of locking mined leaves to vanilla's default color, remember the exact tint they had when broken,
so builders can match a specific biome's foliage anywhere. This needs a non-ticking block entity to hold
the captured color, which the design doc argues is cheap in practice. **Recommendation:** treat it as a
builder-focused upgrade and follow [`docs/PRESERVING-PER-BIOME-TINT.md`](../../../docs/PRESERVING-PER-BIOME-TINT.md).
The unresolved questions in that doc (the tooltip mechanism and the naming scheme) need your decision
before building. It deepens a feature that already exists (A1), but it is a bigger change than it looks
because it adds a block entity to a previously blockstate-only feature.

## Tier 3 — Big bets

### 8. Wielded-gear trims (weapons and tools) — XL · ★★★

A parallel trim system for swords and tools, with bonuses gated on wielding rather than wearing. The
identity statement already promises it, and only armor is implemented, so it's the biggest gap between
what the mod says it is and what it does. It is also the most expensive idea in the list: trimmed
items need smithing recipes, a rendering story for trims on held items (vanilla has none), and its own
bonus rules. **Recommendation:** treat it as a design project before any code. Settle first whether
tools can be trimmed at all in vanilla terms (visuals, or just a tooltip and data), then reuse the armor
trim materials and keep bonuses tiny. If it proceeds, the notes' examples are good first bonuses: a
blaze powder pickaxe that gives lava immunity while mining, and an amethyst tool that always drops max
yield from geodes.

## Honorable mentions

- **Wither skull / soul soil material.** The notes wanted wither immunity or "withers never target you".
  Immunity ships as the Rib pattern bonus, so only "withers ignore you" is new. It's a reasonable
  extra material if you want a rare one after #6.
- **Echo shard pickaxe (silent mining).** Mostly covered by the Silence pattern (sneak-breaking doesn't
  alert sensors). It only makes sense as part of #8.

## What was cut and why

**Already shipped.**
- Silence, Ward and Snout trim templates, and the whole "trim-template-based bonuses" item, which now
  covers all 18 patterns.
- Blaze powder, echo shard, ender pearl and prismarine materials (listed under "New trim materials" in the
  notes).
- Shulker shell for levitation immunity, which the Spire pattern already gives.

**Overlaps something shipped.**
- Prismarine / sea lantern underwater breathing. Turtle scute already gives water breathing, and
  prismarine's own bonus is guardian resistance.

**Too vague to act on.**
- The "etc." entries in the original notes.

## Suggested first arc

Start with #1–#3 (glowstone, ghast tear and phantom membrane) as one release of new materials. They share
the same plumbing, so they're nearly free as a set, then add #4 (mixed-material) since it needs no new art.
Decide on #6 (totem) once those exist, because it is the one that can unbalance the mod. #7 and #8 are
separate projects: #7 needs two decisions from you (tooltip and naming), and #8 needs a design pass
first.
