# Beacon & Conduit Tweaks — Feature Shortlist

Scored and ranked from the legacy backlog in this folder ([`ideas-md.md`](ideas-md.md), your older notes,
and [`potential-features.md`](potential-features.md), the agent brainstorm). There is no
`combined-ideas.md` for a legacy backlog, so this list was built straight from the source notes and
checked against what the mod ships today. Everything that isn't ranked is in
[What was cut and why](#what-was-cut-and-why).

The generic "Add thematic/fun advancements" bullet was dropped on purpose: it was too vague to act on.

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

Ranking is by value relative to effort. The identity is "beacons and conduits, your way": everything
tunable, and new behavior toggleable so a server can run a near-vanilla setup. So almost every item here
is a config or a mixin on code the mod already patches (range, rendering, conduit), and each one must be
off or vanilla-equivalent by default (A2). The mod already has a YACL config with five range options, so
config-style ideas are cheap. Fixed values are called out per item (A4).

## Already shipped

- **Hide beacon beams.** A carpet on top of a beacon stops the beam rendering, and tinted glass shows and
  hides it. That covers your "Hide beacon beams" note.
- **Configurable range** for beacons (base, per level, per block) and conduits (add to or replace vanilla,
  per frame block).

## Ranked list

Linked rows have their own idea files.

| # | Feature | Effort | Value | Votes | My Vote |
|---|---|---|---|---|---|
| | **Tier 1 — Quick wins** | | | | |
| 1 | [Separate vertical and horizontal range](../../range/vertical-and-horizontal-range.md) | S | ★★ | — | Maybe |
| 2 | [Conduit attack range as its own setting](../../conduits/conduit-attack-range.md) | S | ★★ | — | Yes |
| 3 | [Effect duration and reapply window](../../effects/effect-duration.md) | S | ★★ | — | Maybe |
| 4 | [Comparator output](../../automation/comparator-output.md) | S | ★★ | — | Maybe |
| 5 | [Uniform-pyramid bonus](../../range/uniform-pyramid-bonus.md) | S | ★ | — | Maybe |
| 6 | [Config presets](../../configuration/config-presets.md) | S | ★ | — | Maybe |
| | **Tier 2 — Solid mid-size features** | | | | |
| 7 | [Range shape (sphere, cylinder, box)](../../range/range-shape.md) | M | ★★★ | — | Yes |
| 8 | [Amplifier control per pyramid level](../../effects/amplifier-control.md) | M | ★★ | — | Maybe |
| 9 | [Effect pool via datapack (additional effects)](../../effects/effect-pool-datapack.md) | M | ★★ | — | Yes |
| 10 | [Variable bonuses depending on payment](../../effects/payment-bonuses.md) | M | ★★ | — | Yes |
| 11 | ["Inverted" beacons (negative effects)](../../effects/inverted-beacons.md) | M | ★★ | — | Maybe |
| 12 | [Per-dimension overrides](../../range/per-dimension-overrides.md) | M | ★★ | — | Yes |
| 13 | [Range visualization](../../beams/range-visualization.md) | M | ★★ | — | Yes |
| 14 | [Conduit frame material weights](../../conduits/conduit-frame-weights.md) | M | ★ | — | Yes |
| | **Tier 3 — Big bets** | | | | |
| 15 | [Redirect beacon beams](../../beams/redirect-beams.md) | L | ★ | — | Yes |
| 16 | Beam customization | L | ★ | — | No |
| 17 | HUD hint for active effects | M | ★ | — | No |

## Tier 1 — Quick wins

### 1. Separate vertical and horizontal range — S · ★★

Separate up and down reach settings, since bases sprawl horizontally far more than vertically. The mod
already computes range, so this splits one number into two. **Recommendation:** two options, each
defaulting to today's behavior. It also paves the way for #7 (shapes), where "cylinder" is just "infinite
vertical range".

### 2. Conduit attack range as its own setting — S · ★★

Configure the Conduit Power buff range and the range at which a conduit attacks hostile mobs
independently. Today one number drives both. **Recommendation:** add the second option, defaulting to
"same as the buff range" so nothing changes (A2, A4).

### 3. Effect duration and reapply window — S · ★★

How long an effect lingers after you leave range, which helps large bases where players skirt the
boundary. **Recommendation:** one number for duration, default vanilla. Keep the reapply interval
fixed, since it is part of how beacons work rather than a play-style choice (A4).

### 4. Comparator output — S · ★★

Beacons emit a signal proportional to pyramid level, and conduits proportional to frame completeness.
They are cheap, vanilla-flavored automation hooks, and the signal is fully predictable.
**Recommendation:** toggle (A2), default off so existing contraptions are unaffected. Beacons
and conduits have no comparator output in vanilla, so this adds an interaction (not N3).

### 5. Uniform-pyramid bonus — S · ★

An optional rule where a pyramid made of a single material earns a small range or amplifier bonus over a
mixed one. The mod already counts pyramid blocks, so it is one more check. The agent note called this
"mixed-material", but the idea is the reverse. **Recommendation:** off by default (A2), range bonus
only for now.

### 6. Config presets — S · ★

Shippable presets ("vanilla+", "mega-base", "lite") selectable in the YACL screen. With five options
today the payoff is small, but it grows with every item above. **Recommendation:** do this last in a
release, once there are enough options to make presets worth maintaining. Presets only set defaults.

## Tier 2 — Solid mid-size features

### 7. Range shape (sphere, cylinder, box) — M · ★★★

Choose sphere, cylinder (full world height) or vanilla-style box per config. The cylinder is the classic
"my whole base, every Y level" request and is the most obviously useful item in the backlog.
**Recommendation:** start with cylinder and box (box is vanilla), and add sphere only if asked. It needs
care in the effect-application and visualization (#13) code. Default stays vanilla (A2).

### 8. Amplifier control per pyramid level — M · ★★

Configure the effect level each pyramid tier grants (for example Haste II at level 2, Haste III at level
4), instead of vanilla's fixed primary/secondary split. **Recommendation:** a small table of
level → amplifier in the config, default vanilla. It's balance-affecting, so the toggle is mandatory (A2).

### 9. Effect pool via datapack (additional effects) — M · ★★

Drive the beacon's selectable effects from a data-defined list per pyramid level, so packs can offer
Speed at level 1 but hold Resistance for level 4, or add modded effects entirely. This is also how
"additional beacon effects" from your notes would land, as data and not as new code. **Recommendation:**
default list equals vanilla's. It is M because the beacon screen and packets also need to know the list.

### 10. Variable bonuses depending on payment — M · ★★

Different payment items give different results, for example a netherite ingot for a stronger or longer
effect than iron. **Recommendation:** make the table data-driven so it can share the datapack work
in #9, and keep vanilla's payment behavior as the default (A2). The payment items and their bonuses are
a matter of taste, so everything here is configurable (A4).

### 11. "Inverted" beacons (negative effects) — M · ★★

Beacons that apply negative effects (slowness, weakness) to players in range, for traps, PvP servers and
adventure maps. **Recommendation:** a way to switch a beacon to inverted (for example a particular block
on top, like the carpet that hides the beam), a server toggle (A2), and a sensible team/owner exemption
or at least an opt-out, since otherwise a griefer can place one at spawn. It deserves a design pass
on who is affected.

### 12. Per-dimension overrides — M · ★★

Different range math per dimension. The agent note's reason (the Nether's 8:1 distance compression) is
weaker than it sounds, since beacon range isn't tied to portal math, but servers do want different
numbers in different dimensions. **Recommendation:** a per-dimension override table where each value
defaults to the global one (A4). Do after #1 and #7 so the overrides cover all the range settings.

### 13. Range visualization — M · ★★

A brief particle shell or boundary shimmer when the beacon UI is closed, or when sneaking near the
beacon, so players can see the configured reach. Once reach is configurable, nobody knows what it is.
**Recommendation:** client-side and optional, on a keybind or toggle (A3), and use shape-aware
particles once #7 exists. A client config for density and a non-color-only cue (A3).

### 14. Conduit frame material weights — M · ★

Sea lanterns, prismarine bricks and dark prismarine each contribute a configurable amount of range.
**Recommendation:** drive it from a block tag with a weight table in the config. It's a niche
extension of the existing per-block option, so build it only if someone asks.

## Tier 3 — Big bets

### 15. Redirect beacon beams — L · ★

Redirect a beacon beam using amethyst crystals placed on the side of a block. Your note asks whether it
is "visuals only?". **Recommendation:** yes, visuals only, at least first. Redirecting effects as
well would turn range, shape and ownership into a puzzle. Even visuals-only needs custom beam rendering
with bends, which is why it's L.

### 16. Beam customization — L · ★

Config for beam width, visibility through blocks, and a configurable blend distance for gradient colors
from stained glass. Hiding beams is already shipped, so what remains is width and blending, which is
rendering work with a modest payoff. **Recommendation:** pick at most one (beam width) if it ever
matters. Make it a client-side option (A3).

### 17. HUD hint for active effects — M · ★

A small icon showing which beacon and conduit effects currently reach you and from roughly how far.
The vanilla effect icons already show active effects, so the extra value is the "from where". 
**Recommendation:** skip unless #13 proves players want more feedback.

## Honorable mentions

None beyond the cuts below.

## What was cut and why

**Already shipped.**
- "Hide beacon beams" from your notes.

**Too vague to act on.**
- "Additional beacon effects" as a standalone idea. Covered by #9.

## Suggested first arc

Do #1–#3 together as a "finer range and duration" release, since they all extend the numbers the mod
already exposes. Add #4 (comparator) as the cheap automation hook. Then #7 (shapes) is the headline
feature; build #13 (visualization) with it, since a shape you can't see is hard to configure. #8–#10 are
a natural second release around a datapack-driven effect system. #11 (inverted beacons) needs a design
pass on who gets affected before any code.
