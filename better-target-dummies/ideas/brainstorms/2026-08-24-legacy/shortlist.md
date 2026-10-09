# Better Target Dummies — Feature Shortlist

Ranked, scored version of the legacy backlog in [`potential-features.md`](potential-features.md). This
folder has no `combined-ideas.md`, so the shortlist was written straight from that file. The three ideas
that didn't make the ranked list are explained in [What was cut and why](#what-was-cut-and-why).

## How items were scored

- **Effort**, for a two-loader (Fabric + NeoForge) Architectury mod, counting art, datagen and
  rendering, not just code:
  - **S**: a day or two, mostly assets and data, reuses existing code.
  - **M**: a new block or block entity, a new interaction, or a moderate rendering change.
  - **L**: a new entity, custom rendering, worldgen, or a new UI.
  - **XL**: a system that touches many other features.
- **Value**, ★ to ★★★: fit with the mod's identity statement (an accurate, convenient way to test how an
  attack performs against a specific mob or category, using the real vanilla mob so combat math stays
  correct), and how much players would notice it.
- **Votes**: how many independent brainstorm answers suggested it. Legacy items have none, so it shows `—`.

Ranked by value relative to effort, favoring ideas that deepen testing accuracy and readability over ideas
that spread the mod sideways (A1). The mod already reports each hit's exact damage in the action bar, so the
strongest ideas build on that readout. Every idea is checked against
[`docs/BRAINSTORMING-RULES.md`](../../../../docs/BRAINSTORMING-RULES.md); A3 (accessibility) and A4 (what
should be configurable) come up repeatedly below.

## Ranked list

Rows linked to a file have their own idea files under `ideas/`.

| # | Feature | Effort | Value | Votes | My Vote |
|---|---|---|---|---|---|
| | **Tier 1 — Quick wins** | | | | |
| 1 | [DPS / average damage readout](../../damage-feedback/dps-average-damage-readout.md) | S | ★★★ | — | Yes |
| 2 | Visual powered indicator | S | ★★ | — | Already present |
| 3 | [Counter-clockwise rotation](../../dummy-setup/counter-clockwise-rotation.md) | S | ★★ | — | Maybe |
| 4 | [Status effect handling for bound mobs](../../dummy-setup/status-effect-handling.md) | S | ★★ | — | Maybe |
| | **Tier 2 — Solid mid-size features** | | | | |
| 5 | [Mob category presets](../../dummy-setup/mob-category-presets.md) | M | ★★ | — | Maybe |
| 6 | [Floating combat text](../../damage-feedback/floating-combat-text.md) | M | ★★ | — | Yes |
| 7 | [Running damage log](../../damage-feedback/running-damage-log.md) | M | ★★ | — | Maybe |
| Bonus | [Mob equipment](../../dummy-setup/mob-equipment.md) | M | ★★★ | — | Yes |
| | **Tier 3 — Big bets** | | | | |
| 8 | Client-side expected-damage preview | L | ★★ | — | No |

## Tier 1 — Quick wins

### 1. DPS / average damage readout — S · ★★★

Aggregates hits over a short window into an average damage per hit and damage per second, so weapons and
enchantments can be compared directly instead of by eyeballing individual action-bar numbers. This is the
payoff of the mod's whole purpose, and the hit-damage readout it extends already exists.
**Recommendation:** append the average and DPS to the existing action-bar message, using a rolling window
that resets after a few seconds without a hit. Make the window length a config option (A4) and keep the
state in memory only. Skip any GUI. Related: #7, which would keep the individual hits this summarizes.

### 2. Visual powered indicator — S · ★★

A lit and unlit texture for the dummy block tied to its `powered` blockstate, so on/off is readable at a
glance and from a distance. Today the only sign is whether the mob is standing there.
**Recommendation:** a simple lit/unlit variant of the existing side textures. Good for accessibility
(A3) because the state no longer depends on noticing a missing mob; make the unlit look distinct in
shape or brightness, not only hue. No config needed, since this is what the block is (A4).

### 3. Counter-clockwise rotation — S · ★★

Empty-hand right-click only rotates 90° clockwise, and sneak + right-click is taken by clear-binding, so
reaching the other side takes three clicks. A counter-clockwise option needs its own trigger.
**Recommendation:** choose the trigger before building. Candidates: a held item such as a stick, the
off-hand, or splitting the dummy face into left and right halves. The held-item option is the least
surprising and needs no new input. Unconditional, so no config. This is the one item with a real design
question; see the idea file's open questions after promotion.

### 4. Status effect handling for bound mobs — S · ★★

Bound mobs can currently receive potion effects. Absorption in particular skews the reported number, since
only armor and magic absorption are accounted for and not the Absorption effect.
**Recommendation:** a config option with three behaviors: allow all effects (today), block all effects, or
allow all but make the readout account for Absorption. Default to allowing effects, since testing against
Resistance or Weakness is a legitimate use. It changes testing behavior rather than balance, but A2/A4
both still point to a config.

## Tier 2 — Solid mid-size features

### 5. Mob category presets — M · ★★

Quick-bind presets for common test groups (undead, arthropod, aquatic) that cycle through every mob in a
category. This helps test category-specific enchantments like Smite and Bane of Arthropods quickly.
**Recommendation:** drive the categories from vanilla entity type tags rather than a hard-coded list, so
datapacks can extend them. Reuse the existing mob picker for the UI: add category filters, and let a
click on the dummy cycle to the next mob in the chosen category. Skip a command interface for now.

### 6. Floating combat text — M · ★★

Shows the damage number above the dummy so everyone watching sees it, not only the attacker reading their
action bar.
**Recommendation:** spawn a short-lived vanilla text display entity rather than writing custom
rendering, which keeps it close to M. Make it a config toggle and keep the text readable without
relying on color (A3). Related: #1 and #7, which also expose damage data.

### 7. Running damage log — M · ★★

Keeps the last N hits per dummy (weapon used, damage dealt) instead of only the most recent action-bar
message.
**Recommendation:** store the hits in memory only, and surface them through a chat command, not a GUI
(a GUI would push this to L). If #1 is built, consider whether the log is still needed; it is mainly
valuable for seeing the individual hits behind an average. Make N a config option (A4).

### Bonus. Mob equipment — M · ★★★

*Added by the user during voting; not from the legacy backlog.* Lets a bound mob wear armor and hold a
weapon or shield, so a test can answer "how does this hit land against a *fully armored* zombie?" and not
only against a bare one. Armor, toughness, and protection enchantments are the biggest damage modifiers
in the game, and the identity statement names armor explicitly.
**Recommendation:** equip by right-clicking the dummy with an armor piece (to the matching slot) or a
held item (main hand). Store the equipment on the block entity, because the mob is discarded whenever
power is lost and respawned fresh, so equipment held only on the mob would vanish on every power cycle.
Persist it the same way the binding is remembered. Leave a GUI out; a six-slot screen would make this L.
No config needed (A4), since it is what the feature is. A removal gesture is still undecided.

## Tier 3 — Big bets

### 8. Client-side expected-damage preview — L · ★★

Hovering a bound dummy previews the expected damage for the held weapon before swinging.
**Recommendation:** defer. The mod's whole promise is accurate combat math by using the real mob. A client
preview would need to duplicate vanilla damage calculation, armor, enchantment bonuses and resistances,
and any drift from the real result would undercut the identity. Only revisit if there is a trustworthy way
to run the real calculation without a hit.

## Honorable mentions

- **Surface custom mob data in the tooltip** — cheap if a request comes in, but see the cut list.

## What was cut and why

- **Remember custom mob data** (surface it in the empty-hand tooltip). Too small a payoff: the dummy
  already inherits custom data from the spawn egg, and the Dummy Spawn Egg only carries a name, so there
  is little to surface. Easy to revisit if you want the empty-hand click to list equipment too.
- **Multi-dummy comparison** (rig or stand accessory). Dummies are blocks and can already be placed side
  by side, so a rig adds a new block family for little gain, which spreads the mod sideways (A1).
- **Suppress idle animation.** Hard to build for the payoff: it needs a mixin per animated mob family,
  since there is no single hook like the one that silences ambient sound. Revisit only if the animations
  turn out to bother players in practice.

## Suggested first arc

Start with #1, which makes the mod's core readout clearer (#2 turned out to exist already), then #3 and #4
to clear the small usability gaps. The Bonus mob equipment idea is the highest-value mid-size item and fits
well alongside them. #5 and #6 are the next most noticeable features. Leave #7 until #1 is in and you've
seen whether it's still wanted, and leave #8 for last or never.
