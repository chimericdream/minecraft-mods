# Hopper X-Treme — Feature Shortlist

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

Ranking is by value relative to effort. The identity is "everything stays recognizably hopper: speed
tiers, directions, and filtering; new ideas should slot into that grid rather than invent a new machine".
So ideas that deepen filtering, the wrench and the existing grid rank above anything that adds a new kind
of block. Ideas that change behavior are per-hopper opt-ins or config-gated so existing builds don't
change (N1, A2), and each item says what else should be tunable (A4).

## Already shipped

- **Filtered hoppers.** The first line of your notes. Filtering is built into the upgraded hoppers
  (Include/Exclude, five or ten entries, with the Hopper Item Filter and Diamond Hopper Item Filter).
- **The wrench** exists, but it only rotates and adjusts. The ideas below extend it.

## Already active

- [Block and screen collapse](../../internals/block-and-screen-collapse.md): the internal cleanup of the
  hopper block and screen classes. Not re-ranked here. Do it before the filter and wrench items below,
  since they all touch the same screens and block classes.

## Ranked list

Linked rows have their own idea files.

| # | Feature | Effort | Value | Votes | My Vote |
|---|---|---|---|---|---|
| | **Tier 1 — Quick wins** | | | | |
| 1 | [Comparator fidelity](../../redstone/comparator-fidelity.md) | S | ★★ | — | Yes |
| 2 | [Honeyed and copper multi-hoppers](../../grid/honeyed-and-copper-multi-hoppers.md) | S | ★★ | — | Yes |
| 3 | Honeyed hopper blip | S | ★ | — | No |
| | **Tier 2 — Solid mid-size features** | | | | |
| 4 | [Filter copy and paste with the wrench](../../filtering/filter-copy-and-paste.md) | M | ★★★ | — | Yes |
| 5 | [Tag-based filter entries](../../filtering/tag-filter-entries.md) | M | ★★★ | — | Yes |
| 6 | [Overflow routing](../../filtering/overflow-routing.md) | M | ★★ | — | Maybe |
| 7 | [Per-hopper redstone modes](../../redstone/redstone-modes.md) | M | ★★ | — | Maybe |
| 8 | [Upgrade smithing path](../../grid/upgrade-smithing-path.md) | M | ★★ | — | Maybe |
| 9 | [Wrench modes and stats mode](../../wrench/wrench-modes-and-stats.md) | M | ★★ | — | Yes |
| 10 | Copper oxidation | M | ★ | — | No |
| | **Tier 3 — Big bets** | | | | |
| 11 | Side I/O control | L | ★★ | — | This is how multi-hoppers work today |
| 12 | [Hopper minecarts by tier](../../grid/hopper-minecarts.md) | L | ★★ | — | Maybe |
| 13 | [Component-aware filter matching](../../filtering/component-aware-matching.md) | L | ★★ | — | Yes |
| 14 | [Filter cards](../../filtering/filter-cards.md) | L | ★ | — | Maybe |

## Tier 1 — Quick wins

### 1. Comparator fidelity — S · ★★

Make the comparator output count only real contents, so a hopper's filter slots don't read as items.
This is the quiet kind of bug that bites sorting halls: a comparator on a filtered hopper reports
"full" when the filter is. **Recommendation:** first check what the filtered hoppers do today (the filter
slots share the container, so there is a good chance they leak into the signal). If they do, fix it as a
bug, not a feature. No config, since it corrects an unintended reading.

### 2. Honeyed and copper multi-hoppers — S · ★★

Complete the grid: multi-hoppers exist in Multi, Golden, Diamond, Netherite and Nether Star, but not in
the honeyed or copper tiers that regular hoppers and huppers have. Honeyed multi-hoppers are a real
rate-limited sorting trick, and a copper multi-hopper gives redstone-proof splitting. **Recommendation:**
add both. They reuse the existing multi-hopper classes and only need a cooldown, a texture and recipes.
Consider honeyed and copper multi-huppers in the same pass, since the grid should stay complete.

### 3. Honeyed hopper blip — S · ★

A soft note-block-style blip each time a honeyed hopper moves an item. Pure flavor, and it fits the slow,
rhythmic honeyed tier. **Recommendation:** make it a config toggle, default off (it would be annoying in a
clock build), and keep the volume low (A3). Only worth doing if it takes an afternoon.

## Tier 2 — Solid mid-size features

### 4. Filter copy and paste with the wrench — M · ★★★

Sneak-click a hopper with the wrench to copy its filter, then click another hopper to apply it. Building a
40-hopper sorting hall currently means 40 manual setups. It is the highest-value item in the backlog.
**Recommendation:** copy the whole filter (items and Include/Exclude mode) and show the result as an
actionbar message. Put the "copied" state on the wrench itself as an item component so it survives
relogs. Skip a paste-to-many mode until you see how this is used. Pairs with #9.

### 5. Tag-based filter entries — M · ★★★

Filter by item tag (`#logs`, `#ores`) as well as by specific item, so one slot can cover a whole family.
It is the biggest upgrade to filtering and a good match for the identity. **Recommendation:** let a filter
slot hold a tag entry chosen from a picker in the filter screen, and show a clear tag icon so it isn't
confused with an item. Keep the old item entries as they are. Resolve tags live so datapacks and other
mods' tags work. Needs a UI pass on both filter screens, which is why it isn't S.

### 6. Overflow routing — M · ★★

A per-hopper toggle: items rejected by the filter pass through to the hopper below instead of clogging
the inventory, turning a vertical stack of filtered hoppers into a natural sorting cascade.
**Recommendation:** a per-hopper setting, default off, so existing builds are untouched (N1). Store it
with the filter settings so copy and paste (#4) carries it, and decide whether it applies to Include mode
only or both.

### 7. Per-hopper redstone modes — M · ★★

Three behaviors beyond vanilla's lock: inverted (runs only while powered), and pulse (moves one item per
pulse, which makes tiered hoppers usable in precise item-counting circuits). Copper hoppers already ignore
redstone, so this makes redstone handling a real part of the grid. **Recommendation:** a mode setting
chosen in the hopper's screen, default vanilla. Start with inverted and pulse, and skip anything more.

### 8. Upgrade smithing path — M · ★★

Promote a hopper in place with a smithing upgrade (golden to diamond to netherite), keeping its contents,
name and filter, instead of crafting a new block. Pairs with the existing deprecated-block conversion
machinery, which already does the "swap the block, keep the data" part. **Recommendation:** add this as
an extra path, not a replacement for the crafting recipes. Needs one template item, or reuse of an
existing one.

### 9. Wrench modes and stats mode — M · ★★

Turn the wrench into a multi-mode tool: rotate (today), copy and paste (#4), and a stats mode that shows
items per minute through the hopper you click. The stats mode is useful for tuning farms and shows off
the speed tiers. **Recommendation:** cycle modes with sneak-use in the air, show the current mode as an
actionbar message, and build this around #4 (copy and paste is the first new mode). Keep stats to a
simple rolling average that isn't saved.

### 10. Copper oxidation — M · ★

Copper hoppers weather through the vanilla oxidation stages (waxable, scrapeable), with no change in
behavior. It matches vanilla copper, but it **would change existing builds** (N1): every placed copper
hopper would start to age. **Recommendation:** only with a config toggle, default off, so existing worlds
don't change. It is four stages of art for each copper block (hopper, hupper, and the multi forms from
#2), which is why it is M and why the value is just ★.

## Tier 3 — Big bets

### 11. Side I/O control — L · ★★

Toggle individual faces of a hopper with the wrench (accept from the top only, ignore the left side, and so
on), shown with subtle connector textures like the beam connections in Minekea. It is the most powerful
control item here and it is on-theme ("directions"), but it needs per-face state, rendering for the
connectors, and a way to show it. **Recommendation:** do it after #9 so it can be a wrench mode, and
keep the first version to "accept from this face: yes or no". Default is all faces on, as today.

### 12. Hopper minecarts by tier — L · ★★

Golden, diamond and netherite hopper minecarts matching the block speeds, plus a filtered hopper minecart,
so rail-based farms get the same upgrades. **Recommendation:** treat it as a new entity family. It fits the
"tiers and filtering" grid, but only if you want to carry the minecart code. Start with a single filtered
minecart to see how much of the filter UI carries over.

### 13. Component-aware filter matching — L · ★★

An optional strict mode that matches enchantments, custom names or damage ranges ("only broken tools",
"only Mending books"). It deepens filtering for advanced players. **Recommendation:** only after #5,
since it needs a way to describe a match in the filter UI. Start with two matchers (enchanted or not, and
damaged or not). A full "match this stack's components" mode is a stretch.

### 14. Filter cards — L · ★

Write a filter configuration onto a blank "filter card" that can be duplicated in a crafting grid and
slotted into hoppers, making sorting-hall blueprints shareable. **Recommendation:** skip it unless #4
turns out to be too limiting. Copy and paste with the wrench gets most of the benefit with no new item.

## Honorable mentions

- **Sideways "Slopper".** Vanilla hoppers already point sideways, so a new block only earns its place if
  it pushes and pulls in a straight line. Revisit if #11 doesn't cover that need.

## What was cut and why

**Breaks the identity.**
- **Pipes and ducts** (from your notes). The identity says new ideas "slot into that grid rather than
  invent a new machine", and pipes are a different machine with their own routing rules. If you want them,
  they belong in a separate logistics mod, and a hopper-themed mod should stay a hopper mod.

**Already shipped.**
- **Filtered hoppers** (see above).

**Too vague to act on.**
- **Thematic advancements** (dropped; see the intro).
- **Documentation hooks.** The README note about "splitters" refers to a block that doesn't exist, so
  there is nothing to build. It is a reminder to keep the number of concepts low, which is already the
  plan.

## Suggested first arc

Do the active block and screen collapse first, then #1 (comparator fidelity) and #2 (completing the grid),
which are small and finish what the mod already says it does. Then the wrench and filter core: #4, #9 and
#5, which together make big sorting halls pleasant to build. #6 and #7 are the next behavior options
(both opt-in), then #8. The big bets (#11, #12, #13) come later, with #11 building on the wrench modes.
