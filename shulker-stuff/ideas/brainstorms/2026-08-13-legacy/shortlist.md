# Shulker Stuff — Feature Shortlist

Scored and ranked from the legacy backlog in this folder ([`ideas-md.md`](ideas-md.md), your older notes,
and [`potential-features.md`](potential-features.md), the agent brainstorm). There is no
`combined-ideas.md` for a legacy backlog, so this list was built straight from the source notes and
checked against what the mod ships today. Everything that isn't ranked is in
[What was cut and why](#what-was-cut-and-why).

The generic "Add thematic/fun advancements" bullet was dropped on purpose: it was too vague to act on. The
specific advancements from the agent note are ranked as #5.

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

Ranking is by value relative to effort. The identity is "shulker boxes, made more powerful and more
pleasant to use", and it is **vanilla-friendly and additive**: existing boxes keep working, and every change
is an opt-in extra. So anything that changes how boxes behave in play (open-in-hand, soulbound,
quick-deposit) needs a config toggle (A2), and enchantment-based ideas are naturally opt-in because the player
has to enchant the box. Ideas that deepen what the mod already has (the enchantments, the dyeing station,
plating) rank above new systems (A1).

## Already shipped

Your older notes have most of the checkboxes ticked, and the code agrees:
- **Bundle-like handling:** insert by clicking with the box, extract by clicking a slot, and throw items.
  (Open-in-hand is *not* part of this; see #7.)
- **Arbitrary dyeing,** including separate top and bottom colors, correct inventory and world rendering,
  particle colors and the Shulker Dyeing Station. This covers the agent note's "two-tone dyeing".
- **Enchantments:** Refill (I), Vacuum (II) and Void (I).
- **Netherite Plating,** a single smithing upgrade that replaced both old Hardened and Plated ideas.
- **The config screen** no longer shows Miniblock Merchants chances, so the agent note's "known issue" is
  fixed; it now exposes only the plating template chance.

## Ranked list

Linked rows have their own idea files.

| # | Feature | Effort | Value | Votes | My Vote |
|---|---|---|---|---|---|
| | **Tier 1 — Quick wins** | | | | |
| 1 | Enchantment tuning in the config | S | ★★ | — | No |
| 2 | Comparator fill reading | S | ★★ | — | GameTest only; I'm confident this already works |
| 3 | [Undye slot](../../dyeing/undye-slot.md) | S | ★★ | — | Yes |
| 4 | [Netherite Plating, concretized](../../plating/plating-concretized.md) | S | ★★ | — | Yes |
| 5 | [Shulker advancements](../../progression/shulker-advancements.md) | S | ★★ | — | Yes |
| | **Tier 2 — Solid mid-size features** | | | | |
| 6 | [Deep Storage enchantment](../../enchantments/deep-storage.md) | M | ★★★ | — | Maybe |
| 7 | [Open-in-hand](../../handling/open-in-hand.md) | M | ★★★ | — | Yes |
| 8 | [Soulbound enchantment](../../enchantments/soulbound.md) | M | ★★ | — | Yes |
| 9 | [Quick-deposit](../../handling/quick-deposit.md) | M | ★★ | — | Maybe |
| 10 | Sorting enchantment | M | ★★ | — | No |
| 11 | Vacuum filter slots | M | ★★ | — | No |
| 12 | [Scrollable tooltip preview](../../display/scrollable-tooltip-preview.md) | M | ★★ | — | Maybe |
| 13 | [Shulker Pearl](../../drops/shulker-pearl.md) | M | ★★ | — | Maybe |
| 14 | [Nameplates above named boxes](../../display/nameplates.md) | M | ★ | — | Maybe |
| | **Tier 3 — Big bets** | | | | |
| 15 | [Banner patterns on shulker boxes](../../dyeing/banner-patterns.md) | L | ★★ | — | Maybe |

## Tier 1 — Quick wins

### 1. Enchantment tuning in the config — S · ★★

Replace the config's single option with real tuning for the enchantments: Vacuum range per level, how often
Refill checks, and a toggle for each enchantment's behavior. It's the "real config options" item from the
agent note, minus the bug it was about. **Recommendation:** defaults equal to today's behavior (N1), and
let servers disable an enchantment's effect without removing it from existing items. The existing
`ShulkerStuffConfig` has one option, so this is the first real use of the config screen.

### 2. Comparator fill reading — S · ★★

Placed shulker boxes emit a comparator signal proportional to how full they are. Vanilla already does
this, so the first step is verifying that it still works for dyed and plated boxes (the mod swaps the
rendering and block entity data). **Recommendation:** this is a verification task first; add a gametest
that reads the signal from a dyed, plated, and enchanted box, and only change code if it fails. No config.

### 3. Undye slot — S · ★★

A water-bottle slot at the Dyeing Station that returns any box to default purple, matching the station's
"no wasted dye" idea. **Recommendation:** reuse the station's existing recolor logic, with a bottle as the
trigger and no extra cost. Keep the vanilla crafting-grid way of un-dyeing (cauldron) working as it does.

### 4. Netherite Plating, concretized — S · ★★

Define exactly what "durable" means and document it. Today the README says plated boxes are protected from
explosions and fire both as an item and when placed. The agent note goes further: the item floats in lava,
doesn't burn, and doesn't despawn, the full netherite-item treatment. **Recommendation:** check what the
code does now, document it in the README and tooltip, and add only the pieces that are missing. If the
despawn protection isn't there, decide whether to add it; it affects the world, so make it a config toggle.

### 5. Shulker advancements — S · ★★

The three specific advancements from the agent note: "Pack Rat" (fill a shulker box completely),
"Interior Decorator" (dye boxes in all 16 colors and two-tone), and "Turtle Power" (die and keep a
Soulbound box). **Recommendation:** ship "Pack Rat" and "Interior Decorator" now. "Turtle Power" depends on
#8, so it comes with that enchantment.

## Tier 2 — Solid mid-size features

### 6. Deep Storage enchantment — M · ★★★

It's the one unchecked enchantment in your notes, and it already exists in the assets but isn't
registered. Each level adds a row of capacity (up to three levels, one row each). The agent note calls it
the most requested category of shulker feature. **Recommendation:** capacity changes both the screen and
the saved contents, so decide up front how it works with the vanilla 27-slot shulker screen, and what
happens to contents above the new size if the enchantment is removed (it must not delete items). Level
count and extra rows should be config options (A2).

### 7. Open-in-hand — M · ★★★

Right-click air while holding a shulker box to open it without placing. The agent note calls it the single
biggest shulker quality-of-life feature in the ecosystem. **Recommendation:** config-gated, for any box or
only enchanted ones, default to enchanted only. The mod already has a `use` hook for the "throw a stack"
action, so decide which action gets the plain right-click and which uses sneak. Make sure a box that is
open can't be moved out of the slot while the screen is up, which is the classic duplication risk.

### 8. Soulbound enchantment — M · ★★

A treasure enchantment for plated boxes only: the shulker box stays in your inventory on death.
**Recommendation:** an endgame item that makes plating worth it. Make it respect the keep-inventory
gamerule, and make it opt-in in the config (A2) since it changes death penalties. Brings the "Turtle Power"
advancement with it (#5).

### 9. Quick-deposit — M · ★★

Sneak-click a placed container with a shulker box to dump all items matching the container's existing
contents. **Recommendation:** only move items that match what the container already holds, and skip
containers that refuse items. Config-gated and off by default for servers that don't want new automation
(A2).

### 10. Sorting enchantment — M · ★★

Sneak-use a placed enchanted box with an empty hand to sort and merge its contents, alphabetically or by
registry order. **Recommendation:** one sort order (by item name) to begin with, and a config for the
order. It's a small, pleasant feature that pairs with Deep Storage.

### 11. Vacuum filter slots — M · ★★

At Vacuum II, a small include or exclude filter, reusing the filter concept from Hopper X-Treme.
**Recommendation:** ideally via a shared filter API in Chimeric Lib, so both mods use the same item and
UI. That makes it bigger than it looks (M, or L if the shared API doesn't exist yet), so check what Hopper
X-Treme's filter code would need to be extracted first.

### 12. Scrollable tooltip preview — M · ★★

A richer item tooltip: a full grid preview of the contents, shift-scroll pages and a fill-percentage bar.
**Recommendation:** vanilla already shows a contents preview, so check what it does for large (Deep Storage)
boxes first. The fill bar alone is a cheap first step. Client-side only and optional (A4).

### 13. Shulker Pearl — M · ★★

A rare shulker drop used in recipes (the Soulbound book, plating additions), giving End raids a
mod-specific prize. Shulkers are in scope per the identity ("including the mob and its drops").
**Recommendation:** only worth doing if two or more recipes need it, so decide #8 and #6 first. Make the
drop rate configurable, and keep existing recipes working without it (N1).

### 14. Nameplates above named boxes — M · ★

From your notes: display a name above named shulker boxes. **Recommendation:** a client-side block entity
renderer that draws the custom name, with a render distance and an on/off setting. It's a pure flourish, so
keep it small, and consider showing it only when looking at the box.

## Tier 3 — Big bets

### 15. Banner patterns on shulker boxes — L · ★★

From your notes ("apply banners to a shulker") and the agent note's "pattern stamping": apply a banner at
the Dyeing Station to emboss a pattern on the lid. **Recommendation:** the cost is rendering (a layered
texture on the lid, in both item and block form, with the custom dye colors). Start with a handful of
stencil patterns and see if the render path holds up before supporting arbitrary banner layers.

## Honorable mentions

None beyond the cuts below.

## What was cut and why

**Too weird or too heavy for the payoff.**
- **Sharing enchantment** (a placed box remembers per-player view state). The agent note itself flags it
  as probably too weird. Cut.
- **Ender Chest bridge** (view a placed box remotely through a spyglass). The agent note calls it
  parking-lot material; it is scope-heavy and doesn't fit "pleasant to use".

**Already shipped.**
- Bundle-like behavior, arbitrary and two-tone dyeing, the Hardened and Plated upgrades (now one Plated
  upgrade) and the Vacuum, Void and Refill enchantments (see above).

**Too vague to act on.**
- **Thematic advancements** (dropped; see the intro).

## Suggested first arc

Do #1 (config tuning), #2 (comparator check) and #4 (document plating) as a quick tidy-up, plus #3
(undye slot) and #5's two safe advancements. Then #6 (Deep Storage) and #7 (open-in-hand) as the headline
features, with #8 (Soulbound) and its advancement following. #9–#11 are the smaller handling tweaks.
#12–#14 are polish, and #15 is a later rendering project.
