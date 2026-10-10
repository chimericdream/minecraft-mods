# Sponj — Feature Shortlist

Scored and ranked from the legacy backlog in this folder ([`potential-features.md`](potential-features.md);
`ideas-md.md` holds only an empty bullet). There is no `combined-ideas.md` for a legacy backlog, so this
list was built straight from the source notes and checked against what the mod ships today. Everything
that isn't ranked is in [What was cut and why](#what-was-cut-and-why).

The generic "Add thematic/fun advancements" bullet was dropped on purpose: it was too vague to act on.
The specific advancement ideas in the notes have already shipped (see below).

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

Ranking is by value relative to effort. The identity is "simple, slightly silly, extremely useful fluid
handling", with one block or item doing one job. So fixes and small additions to the existing sponj loop
(absorb, dry, reuse) rank first, and anything that needs a new entity or a new system ranks last (A1).
The mod has no config today: the radius (`6 + 3 × (connected − 1)`) and the 64-blocks-per-sponj budget
are hard-coded in `AbstractSponjBlock` and `ModBlocks`. Balance-affecting items need a config toggle
(A2), and each item says what else should be tunable (A4). Fluid-handling work stays on Sponj's side of
the boundary with Log All the Things! (see the README of this folder's parent).

## Already shipped

These came out of the same notes and are in the mod today, so they aren't re-ranked:

- **Advancements.** "Spill Response Team", "Dry Heat" and "Big Gulp" all exist, plus a "Space Heater"
  advancement.
- **Fluid-tag-driven absorption.** Each sponj absorbs whatever its fluid tag names, so other mods' fluids
  can opt in through the tag. That covers the notes' "modded fluid tags" idea, apart from documenting it.
- **Waterlogged blocks (probably).** Absorption goes through vanilla's bucket-pickup path, which takes the
  water out of waterlogged stairs, slabs and fences without breaking them. Worth a quick GameTest to
  confirm, not a feature.

## Ranked list

Linked rows have their own idea files.

| # | Feature | Effort | Value | Votes | My Vote |
|---|---|---|---|---|---|
| | **Tier 1 — Quick wins** | | | | |
| 1 | [Make wet lava sponjes safe to smelt in stacks](../../drying-loop/stackable-wet-lava-sponj.md) | S | ★★ | — | Maybe |
| 2 | [Configurable radius and budget](../../configuration/radius-and-budget-config.md) | S | ★★ | — | Yes |
| 3 | [Dispensers place sponjes](../../automation/dispensers-place-sponjes.md) | S | ★★ | — | Maybe |
| 4 | [Squishy sounds](../../flavor/squishy-sounds.md) | S | ★ | — | Yes |
| | **Tier 2 — Solid mid-size features** | | | | |
| 5 | [Squeezing a wet sponj](../../drying-loop/squeezing-wet-sponj.md) | M | ★★ | — | Maybe |
| 6 | [Drying rack](../../drying-loop/drying-rack.md) | M | ★★ | — | Yes |
| 7 | [Snow sponj](../../sponj-types/snow-sponj.md) | M | ★★ | — | Maybe |
| 8 | Universal sponj | M | ★ | — | No |
| 9 | Sponj slab and carpet | M | ★ | — | No |
| | **Tier 3 — Flavor and big bets** | | | | |
| 10 | Rain shield (off by default) | M | ★ | — | No |
| 11 | [Sponj golem](../../flavor/sponj-golem.md) | L | ★ | — | Maybe |

## Tier 1 — Quick wins

### 1. Make wet lava sponjes safe to smelt in stacks — S · ★★

The README warns "only insert a single wet lava sponj at a time, or you won't get dry ones back". That
is a trap, and the notes suggest turning it into a feature. **Recommendation:** the simplest fix is to
make the wet lava sponj item unstackable (max stack size 1), so every furnace slot holds one and the
dry sponj always comes back. It removes the warning from the README. Check how this interacts with the
StackItUp mod's configurable stack sizes first. If an in-GUI warning is wanted instead, it is a bigger job
for less payoff.

### 2. Configurable radius and budget — S · ★★

Expose the base radius, the per-connected-sponj bonus, the per-sponj block budget and the connected-sponj
cap in a config, so packs can tune from "kiddie pool" to "drain the ocean monument". This is a textbook
A4 case: the numbers are a matter of play style, not what the block is. **Recommendation:** defaults
equal today's values (so nothing changes for existing worlds), server-side only, and keep the formula
shape fixed. Only the four numbers become options. It is the mod's first config, so budget a little for
setting that up on both loaders.

### 3. Dispensers place sponjes — S · ★★

Dispensers can place sponjes, which soak up liquid immediately, for fully automated spill response.
Vanilla dispensers don't place arbitrary blocks, so this is a registered dispense behavior.
**Recommendation:** placing only. The note's "collect the wet sponj back" half is vague (it starts with
"with shears? no —"), and hoppers and pistons already move the wet block. Make it follow the same rules
as hand placement.

### 4. Squishy sounds — S · ★

Custom squish sounds for placing, breaking and walking on sponj blocks. The note's slow-sink step
"like a firm mattress" is a movement change and not worth it. **Recommendation:** sounds only. It's
cosmetic, so no config is needed, and nothing depends on hearing it (A3).

## Tier 2 — Solid mid-size features

### 5. Squeezing a wet sponj — M · ★★

Right-click a wet sponj with a glass bottle to get a water bottle, and leave a dry sponj. A piston pushing
into a wet sponj could eject a bottle and leave a dry one, for automation. It gives the "wet" half of the
loop a use besides drying. **Recommendation:** start with the right-click, one bottle per wet sponj and a
dry sponj left behind. The note's "after a few squeezes" needs extra block states, so skip it. Add the
piston version only if the hand version lands well. Consider whether a squeezed sponj competes with
drying in the nether (#6), since both turn a wet sponj into a dry one.

### 6. Drying rack — M · ★★

A block that slowly dries wet sponjes placed on it, faster over a campfire and instant over soul fire.
It gives the drying loop a home base instead of nether round-trips. **Recommendation:** make the speeds
configurable (A4) and keep the nether and furnace methods working. It is a block entity with a ticker,
so keep it cheap, and decide whether it also handles wet lava sponjes (they dry in the End).

### 7. Snow sponj — M · ★★

Absorbs powder snow and snow layers in a radius, and comes back as a "cold wet sponj" that dries in the
nether instantly (with a satisfying *fssss*). It is the most natural new sponj type, since snow cleanup
is a real chore. It is M, not S, because snow is made of blocks and not fluids, so the fluid-tag path
doesn't apply, and it needs a wet variant, art and recipes. **Recommendation:** snow layers and powder
snow only, and a toggle for the nether drying shortcut if it feels too fast (A4).

### 8. Universal sponj — M · ★

Absorbs *any* fluid, including modded ones, for an expensive recipe (sponj + lava sponj + something
rare). One block that ends all fluid cleanup. Because absorption is already tag-driven, the absorb half
is cheap. The hard part is the wet variant, since it isn't clear how it dries or what it contains.
**Recommendation:** only if you have a drying rule in mind, and a config toggle (A2), since it
makes the two basic sponjes less necessary.

### 9. Sponj slab and carpet — M · ★

Thin sponj variants for shallow cleanup jobs and decorative bathroom builds. Each variant needs wet
versions, and the connected-sponj bonus needs a rule for partial blocks. **Recommendation:** carpet
only, as a cheap way to dry a floor after a flood, and only if players ask. It is the least on-theme of
the Tier 2 items.

## Tier 3 — Flavor and big bets

### 10. Rain shield (off by default) — M · ★

Placed dry sponjes very slowly become damp in thunderstorms if exposed to rain. It's pure flavor for
players who like consequences, and the notes already say default off. It changes what players have
built, so it breaks N1 unless there is an opt-out, and it adds upkeep, which N2 warns against.
**Recommendation:** shortlisted in the changed form from the notes: a config option, off by default,
that makes the damp sponj dry again by itself when the rain stops, so it never becomes a chore.

### 11. Sponj golem — L · ★

Build it like a snow golem (sponj + carved pumpkin). It waddles around your base soaking up puddles and
rain-placed water, and wrings itself out over farmland. It is extremely silly and extremely on-brand, and
also the one idea that needs a new entity with AI, a model and animations. **Recommendation:** treat it
as a stretch goal, behind a config, and only after the drying loop (#5, #6) is solid, since it needs a
way to wring out.

## Honorable mentions

- **Waterlogging check.** Add a GameTest that a sponj next to a waterlogged stair removes the water
  and leaves the stair. If it fails, it becomes a Tier 1 fix.
- **Document the fluid tag.** Add a note to the README that other mods can opt in to what a sponj
  absorbs through its fluid tag.

## What was cut and why

**Already shipped.**
- The "Spill Response Team", "Dry Heat" and "Big Gulp" advancements, and the modded-fluid-tags idea (see
  above).

**Too vague to act on.**
- **Dispenser "collect the wet sponj back".** Folded into #3 as placement only.
- **Slow-sink walking.** The movement half of #4.

## Suggested first arc

Start with #1 (stackable wet lava sponj fix) and #2 (config), because they fix a real trap and give
every later feature somewhere to put its numbers. Add #3 (dispensers) and #4 (sounds) in the same
release. Then build the drying loop together: #5 (squeezing) and #6 (drying rack). #7 (snow sponj) is
the best new block after that. The golem and rain shield are garnish for later.
