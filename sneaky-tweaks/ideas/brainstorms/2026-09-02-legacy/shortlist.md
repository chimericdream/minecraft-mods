# Sneaky Tweaks — Feature Shortlist

Scored and ranked from the legacy backlog in this folder ([`potential-features.md`](potential-features.md)
and [`ideas-md.md`](ideas-md.md) where present). There is no `combined-ideas.md` for a legacy backlog, so
this list was built straight from the source notes. Everything that didn't make the ranked list is in
[What was cut and why](#what-was-cut-and-why).

The generic "Add thematic/fun advancements" bullet was dropped on purpose: it was too vague to act on.
Specific advancement ideas are still welcome and are scored like any other item (see #14). The mod
already has an advancement tab, so each feature below should also ship with its own advancement.

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

Ranking is by value relative to effort. The mod's identity is "find every place vanilla could have checked
`isCrouching` and didn't", so the best ideas are the ones where sneaking already *means* something in
vanilla (careful, polite, unnoticed) and the mod just extends it. Those are small mixins on an existing
check, and they sit beside the shipped berry-bush, campfire and crouch-bridge features. The joke tier is
ranked low but not cut. It's part of the mod's character, as long as it ships off by default. Every
balance-affecting item needs a config toggle (A2), and every item should say what else is tunable (A4).

## Already shipped

These came out of the same notes and are in the mod today, so they aren't re-ranked: sweet berry bush
immunity, timed campfire immunity (with HUD meter), and crouch bridging. Each has its own on/off toggle
in the config screen, which is the pattern every item below should follow.

## Ranked list

Linked rows have their own idea files.

| # | Feature | Effort | Value | Votes | My Vote |
|---|---|---|---|---|---|
| | **Tier 1 — Quick wins** | | | | |
| 1 | [No accidental pickups](../../stealth-utility/no-accidental-pickups.md) | S | ★★★ | — | Yes |
| 2 | [Tip-toe through cacti](../../careful-footing/tip-toe-through-cacti.md) | S | ★★★ | — | Maybe |
| 3 | [No slipping on ice](../../careful-footing/no-slipping-on-ice.md) | S | ★★ | — | Yes |
| 4 | Pressure plates and tripwire ignore you | S | ★★ | — | No |
| 5 | Bees stay calm without smoke | S | ★★ | — | No |
| 6 | Piglins don't clock you without gold | S | ★★ | — | No |
| 7 | A gentler landing | S | ★★ | — | No |
| | **Tier 2 — Solid mid-size features** | | | | |
| 8 | [Cats and foxes don't bolt](../../mob-courtesy/cats-and-foxes-dont-bolt.md) | M | ★★ | — | Yes |
| 9 | Parrots hold their tongue | S | ★ | — | No |
| 10 | ["Bit tolerance" presets](../../configuration/bit-tolerance-presets.md) | M | ★★ | — | Maybe |
| | **Tier 3 — The joke tier (off by default)** | | | | |
| 11 | "Highly Illegal": lava doesn't ignite you for one tick | S | ★ | — | No |
| 12 | [Golems bow](../../cosmetic/golems-bow.md) | L | ★ | — | Maybe |
| 13 | [The enderman solidarity clause](../../cosmetic/enderman-solidarity.md) | M | ★ | — | Maybe |
| 14 | ["Uncomfortable Silence" advancement](../../progression/uncomfortable-silence-advancement.md) | S | ★★ | — | Yes |

## Tier 1 — Quick wins

### 1. No accidental pickups — S · ★★★

Sneaking stops the automatic item and XP-orb vacuum, so you can stand in a farm's drop pile or beside a
sorting build without filling your inventory. It's the most useful idea in the backlog and fits the
identity exactly: sneaking already means "be careful", and this is the careful thing you actually want
when you're standing in a pile of items. **Recommendation:** ship it with a config toggle (A2) and decide
whether XP orbs and items are one switch or two. Two is nicer for XP grinders who still want the drops
left alone. Items you walk over while sneaking should still be pickable by other means (right-click
isn't a thing for dropped items, so make sure dropping and re-picking while standing still isn't broken).

### 2. Tip-toe through cacti — S · ★★★

Moving into a cactus while sneaking skips the damage tick. This is the berry-bush feature applied to the
next obvious spiky block, so it reuses the existing mixin pattern and the same config style.
**Recommendation:** do it as written (sneaking only, no free walking through a spike field), and add it
to the same config category as the berry bush. The name "tip-toe" gives the advancement a good home.

### 3. No slipping on ice — S · ★★

Crouching on ice, packed ice, blue ice or frosted ice removes the sliding acceleration, trading speed for
control. Frost Walker makes ice but you can't comfortably stand on it, and this closes that gap.
**Recommendation:** make it a single friction override while sneaking, and keep it to the ice family
rather than slime or other low-friction blocks, which are different problems. The existing
`LivingEntityMixin` is the likely home.

### 4. Pressure plates and tripwire ignore you — S · ★★

A sneaking player doesn't trigger wooden pressure plates or tripwire, for actual stealth through actual
traps. **Recommendation:** start with pressure plates and tripwire only. Pressure-plate variants are
worth a quick check first: vanilla treats some plates differently (weighted vs. entity-counted), and the
"wooden only" framing in the original note is probably the safest scope. It affects redstone contraptions
and adventure maps, so it needs a toggle (A2), on by default only if you're happy with that. Worth asking
before it ships.

### 5. Bees stay calm without smoke — S · ★★

Harvesting a hive or nest while sneaking counts as gentle for aggro purposes, with the same protection as
campfire smoke and no campfire needed. It fits "be polite" exactly. **Recommendation:** only the
hive-harvest check, not general bee aggro, and a toggle because it changes mob behavior (A2). Pairs
well with the existing campfire feature, since both are about getting hurt by things that should be
avoidable.

### 6. Piglins don't clock you without gold — S · ★★

Sneaking near piglins suppresses their aggro check the same way gold armor does. It's a small mixin on an
existing check. **Recommendation:** make this a toggle (A2), and suppress only the "you aren't wearing
gold" aggro, not aggro from attacking or opening chests. Piglins reacting to you opening a chest should
still happen.

### 7. A gentler landing — S · ★★

Sneak in the tick before you hit the ground and shave a couple points off fall damage, as a nod to
tucking and rolling. **Recommendation:** make the reduction an amount, not a percentage, and configurable
(A2, A4), default small (about 2 points), and don't let it cancel lethal heights. It's a survival
advantage, so it needs a clear toggle, and the timing window should be forgiving (A3), for example any
sneak held during the landing tick rather than a precise press.

## Tier 2 — Solid mid-size features

### 8. Cats and foxes don't bolt — M · ★★

Sneaking within their flee radius is treated like holding a trust-building item, so tamed-adjacent
animals don't scatter from your existence. They still flee from sudden movement or combat.
**Recommendation:** start with cats (ocelots) and foxes as written. It's M because the flee behavior
lives in goal classes, and each animal needs its own check. Skip other animals for now.

### 9. Parrots hold their tongue — S · ★

A shoulder parrot won't mimic a nearby hostile mob's sound while you're sneaking, so a creeper hiss
doesn't fake you out mid-stealth. It's small and charming but only a few players will see it.
**Recommendation:** do it only if you're already adding the animal-behavior hooks from #8.

### 10. "Bit tolerance" presets — M · ★★

A top-level config dropdown (Vanilla+ / Full Bit / Chaos) that bulk-enables features by how straight-faced
they are, instead of making players hunt through categories. It earns its place once there are more than
about 8 features, which this list would get the mod to. **Recommendation:** build it after most of Tier 1,
and use the three tiers in this shortlist as the preset boundaries: Vanilla+ for Tier 1, Full Bit adds
Tier 2, Chaos adds Tier 3. Each preset only sets defaults, so the individual toggles still win (A4).

## Tier 3 — The joke tier (off by default)

Each of these should ship disabled by default and say so plainly in its config description, as the original
notes asked.

### 11. "Highly Illegal": lava doesn't ignite you for one tick — S · ★

Sneaking into lava for exactly one tick doesn't set you on fire. It's a chaos-server novelty, not a
survival feature. **Recommendation:** keep the "Highly Illegal" config name, default off, and make the
config description say it's a joke in as many words. It breaks nothing but needs the A2 toggle.

### 12. Golems bow — L · ★

Iron and snow golems within a few blocks dip their model toward the ground as you sneak past. It's the
cutest idea in the list and the most expensive: it needs a client-side render-state change for two
entity types. **Recommendation:** only if there's appetite for custom entity rendering; it doesn't
affect gameplay, so it needs no balance toggle, but a client-side off switch is polite (A3).

### 13. The enderman solidarity clause — M · ★

A nearby enderman crouches when you crouch. It does nothing and fixes nothing. Endermen have no crouch
pose in vanilla, so this needs a small model change. **Recommendation:** pair it with #12 if golems get
built, since both are "mobs react cosmetically to sneaking", and skip it otherwise.

### 14. "Uncomfortable Silence" advancement — S · ★★

A joke advancement for cumulative time spent sneaking, measured in absurd units ("you have now spent one
full Minecraft day of your life crouching"). It fits the mod's tone and the advancement tab already
exists. **Recommendation:** use the vanilla crouch-time stat if one exists rather than adding tracking
of our own, and offer a couple of tiers (an hour, a full day) so it keeps rewarding. It has no gameplay
effect, so it needs no toggle.

## Honorable mentions

- **Sneaking narrows your FOV by one degree.** Genuinely does nothing perceptible; it exists so the
  changelog can say "1% more immersive". Cheap (S) if you want a pure bit. It would need an A3 setting
  to disable, which makes the joke slightly less funny.
- **Per-feature keybind override.** For anything gated on "sneaking plus something else". Only the lava
  tick needs it, so it's not worth building on its own.

## What was cut and why

**Probably already in vanilla (N3).**
- **Sculk shriekers stay quiet.** Vanilla sneaking already stops the step vibrations that sculk sensors
  and shriekers listen for. It's worth verifying in-game, but the effect is likely already there, so it
  breaks N3 until proven otherwise.

**Too vague to act on.**
- **Sleeping villagers stay asleep.** Vanilla villagers don't wake from "ambient noise checks"; there's
  no such mechanic to gate on sneaking. The note would need a concrete trigger before it can be scored.

**Not a feature.**
- **Per-feature toggles.** Already the convention (A4): every feature gets its own switch, as the shipped
  ones do.

## Suggested first arc

Start with #1 (no accidental pickups) and #2 (cacti), the two ★★★ items, plus #3 (ice) for a first
release that is mostly "vanilla's careful-sneaking, extended". Follow with the behavior group #4–#7,
checking each toggle default with you first because they change mob and redstone behavior. Build #10
(presets) once about eight features exist, and treat the joke tier as an end-of-release garnish.
