# Better Portal Linking — Feature Shortlist

Ranked, scored version of the legacy backlog in [`potential-features.md`](potential-features.md). This
folder has no `combined-ideas.md`, so the shortlist was written straight from that file. Nothing was cut
outright; see [What was cut and why](#what-was-cut-and-why).

## How items were scored

- **Effort**, for a two-loader (Fabric + NeoForge) Architectury mod, counting art, datagen and
  rendering, not just code:
  - **S**: a day or two, mostly assets and data, reuses existing code.
  - **M**: a new block or block entity, a new interaction, or a moderate rendering change.
  - **L**: a new entity, custom rendering, worldgen, or a new UI.
  - **XL**: a system that touches many other features.
- **Value**, ★ to ★★★: fit with the mod's identity statement ("a simple, in-world way to control where
  portals link", optional, and out of the way), and how much players would notice it.
- **Votes**: how many independent brainstorm answers suggested it. Legacy items have none, so it shows `—`.

Ranked by value relative to effort. The mod is small (about 600 lines, one mixin pair and one linker), so
the best ideas reuse the linker's existing candidate scoring and debug logging rather than adding new
systems. Every idea is checked against [`docs/BRAINSTORMING-RULES.md`](../../../../docs/BRAINSTORMING-RULES.md),
and the config and accessibility notes below come from A3 and A4.

## Ranked list

Rows linked to a file have their own idea file under `ideas/`.

| # | Feature | Effort | Value | Votes | My Vote |
|---|---|---|---|---|---|
| | **Tier 1 — Quick wins** | | | | |
| 1 | [`/portallink debug` command](../../troubleshooting/portallink-debug-command.md) | S | ★★ | — | Maybe |
| 2 | [First-link advancement](../../progression/first-link-advancement.md) | S | ★ | — | Yes |
| 3 | [Arrival cue when an address matched](../../troubleshooting/arrival-cue.md) | S | ★★ | — | Yes |
| | **Tier 2 — Solid mid-size features** | | | | |
| 4 | [Address preview](../../troubleshooting/address-preview.md) | M | ★★★ | — | Maybe |
| 5 | [Auto-labeled new portals](../../addressing/auto-labeled-new-portals.md) | M | ★★★ | — | Yes |

## Tier 1 — Quick wins

### 1. `/portallink debug` command — S · ★★

Prints the entry portal's address and every scored candidate for the player's last transit. The linker
already builds exactly that list for its debug log (`logDecision` in `PortalAddressLinker`), so this mostly
means remembering the last decision per player and formatting it as chat output. It is the lowest-effort
way to answer "why did my portal go there?" without turning on log files.
**Recommendation:** op-only, a single `last` subcommand, no history. Skip persisting anything across
restarts. It doesn't change gameplay, so there is no balance config (A2); the existing debug toggle can stay
the only knob (A4).

### 2. First-link advancement — S · ★

A one-time advancement for the first time a transit is routed by address instead of vanilla. Cheap, since
the linker's `select` already returns whether an address won, and datagen for one advancement is small.
Value is low but real: it also tells new players the feature exists, which the README is the only place
that does today.
**Recommendation:** a single, specifically-worded advancement, not a tab. If you want a quirky title,
propose one at promote time. Unlike the vague "thematic advancements" bullets dropped elsewhere, this one
has a defined trigger.

### 3. Arrival cue when an address matched — S · ★★

A subtle particle burst and/or sound at the destination when address matching, not vanilla, picked the
exit, so the feature is noticeable without reading logs. Server-side `sendParticles` plus one sound, fired
from the same place the log decision is made.
**Recommendation:** ship with a config toggle (A4) and a way to turn off both the particle and the sound
independently (A3, since neither should be the only signal). Use an existing vanilla sound and particle
(N3 isn't a problem, since this is a new cue, not a duplicate). Defer anything fancier. Pairs with #1 for
troubleshooting and with #4 for discovery.

## Tier 2 — Solid mid-size features

### 4. Address preview — M · ★★★

Shows which address blocks a portal is currently reading, so a link that isn't behaving can be diagnosed
in-world. The backlog offers two forms: sneak-looking at the corners, or a held-item tooltip.
**Recommendation:** pick a **use-interaction** instead of either: sneak and right-click an empty hand on a
portal frame block (or a corner block) to get an action-bar message listing the four corner blocks and
whether the portal is addressed. It needs no per-tick raycast and no client code, and the mod's client side
stays optional. The sneak-look form needs client rendering or per-tick server raycasts for a small gain, so
skip it. Show block names as text, not only colors (A3). Related: #1, which gives the same information for
the last transit instead of a block you're looking at.

### 5. Auto-labeled new portals — M · ★★★

When the game builds a brand-new exit portal because no match exists, stamp its diagonal corners with the
entry portal's address blocks, so a freshly-dug pair links itself. This is the biggest usability gain in the
backlog: today a player has to travel through, then place matching blocks by hand on the far side.
**Recommendation:** ship **off by default** with a config toggle, since it writes blocks into the world
(N1 requires an opt-out; defaulting off makes it opt-in, which is stronger). Only stamp corner positions
that the portal generation left empty or replaceable, never overwrite existing player blocks, and skip
silently if a corner is unavailable. Needs a hook where `PortalForcer` creates the portal, so it is the one
item here that touches vanilla generation. Re-estimate effort once the hook point is checked: it could
move to L if the creation path is hard to intercept on one loader.

## Honorable mentions

- None beyond the list above. The legacy backlog only had six ideas, and all of them made the list.

## What was cut and why

Nothing was cut. Two reshapes are worth knowing about:

- **#4 sneak-look / held-item tooltip** was reshaped into a use-interaction (see #4); the original forms
  need client code or per-tick raycasts.
- **#5 auto-labeling** needed a rule change: it breaks N1 as written (it alters the world without an
  opt-out), so the shortlisted version is opt-in via config.

## Suggested first arc

Do #1, #3 and #2 first: they are all small, share the linker's decision point, and give players and
testers visibility into what the mod is doing. Then #4 for in-world diagnosis, and #5 last, since it is
the only item that touches vanilla portal generation and deserves its own testing pass.
