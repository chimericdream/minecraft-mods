# Banner Tweaks — Feature Shortlist

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

Ranking is by value relative to effort. The identity is "banners, end to end": making, copying,
displaying and placing them, staying vanilla-friendly, and generated banners only if they reliably look
good. Note that the agent brainstorm in `potential-features.md` was written against an older, narrower
identity ("fewer walls, no new content"). Your own notes (new shapes, new ways to hang) are in scope under
the current statement, so they are ranked on equal terms here. The mod already has a YACL config (a single
max-layers option), several mixins on the loom and renderer, and a tooltip, so config-style ideas are
cheap. Balance-affecting items need a config toggle (A2), and each item says what else should be tunable
(A4).

## Already shipped

- **Layer count in the tooltip.** Banners show "9/12 layers" today.
- **Map marker handling** has a mixin (`MapStateMixin`). You noted that banners do not render on maps the way idea #2 assumed, so that idea was not promoted.
- **A configurable layer cap** (1–32, default 12), applied to the loom, banner rendering and tooltip.

## Ranked list

Linked rows have their own idea files.

| # | Feature | Effort | Value | Votes | My Vote |
|---|---|---|---|---|---|
| | **Tier 1 — Quick wins** | | | | |
| 1 | [Copy banners regardless of layer count](../../copying-and-sharing/copy-any-layer-count.md) | S | ★★ | — | Yes |
| 2 | Map marker layer limit option | S | ★ | — | n/a (banners don't render on maps in this way) |
| 3 | Dye cost scaling for extra layers | S | ★ | — | No |
| 4 | [Separate limits for crafting and commands](../../configuration/separate-crafting-and-command-limits.md) | S | ★ | — | Maybe |
| | **Tier 2 — Solid mid-size features** | | | | |
| 5 | Undo last layer at the loom | M | ★★ | — | No |
| 6 | [Banner stencil](../../copying-and-sharing/banner-stencil.md) | M | ★★ | — | Yes (see note on 12) |
| 7 | [Shield parity](../../copying-and-sharing/shield-parity.md) | M | ★★ | — | Yes |
| 8 | [Hang banners from the underside of blocks](../../placement/underside-banners.md) | M | ★★ | — | Yes |
| 9 | [Curated banners for structures](../../structures/curated-structure-banners.md) | M | ★★ | — | Yes |
| 10 | [Bigger loom preview](../../loom/bigger-loom-preview.md) | M | ★★ | — | Yes |
| 11 | [Copy a placed banner in-world](../../copying-and-sharing/copy-placed-banner.md) | M | ★ | — | Maybe |
| | **Tier 3 — Big bets** | | | | |
| 12 | [Layer list editor (delete, reorder, recolor)](../../loom/advanced-loom.md) | L | ★★★ | — | Yes: this should be a new block ("advanced loom") |
| 13 | [Banner shape variants (jagged, sharp, pennant)](../../shapes/banner-shape-variants.md) | L | ★★★ | — | Yes |
| 14 | [Horizontal banners](../../placement/horizontal-banners.md) | L | ★★ | — | Yes |
| 15 | Distant banner level of detail | M | ★ | — | No |

## Tier 1 — Quick wins

### 1. Copy banners regardless of layer count — S · ★★

Patch banner duplication so a banner with more than six layers can still be copied onto a blank banner.
It is a natural part of raising the cap: if tall banners can be made but not copied, the cap is half
useful. **Recommendation:** first check what vanilla does with a 7+ layer banner in the duplicate recipe
(the notes say it "may refuse or truncate"), then fix whichever is true. If it already works, turn it
into a GameTest and drop it from the list. The copy should respect the configured cap.

### 2. Map marker layer limit option — S · ★

An option for how many layers render on banner map markers, for servers where many complex banners are
marked on one map. The map rendering is already patched, so this adds a number to the config.
**Recommendation:** default to the full layer cap, so nothing changes unless a server opts in (A4).

### 3. Dye cost scaling for extra layers — S · ★

An optional rule where layers past vanilla's six cost extra dye (or XP), letting servers balance the
raised cap. **Recommendation:** off by default (A2). It only matters to servers that raise the cap
and want it to cost something. Keep it to dye and skip the XP variant.

### 4. Separate limits for crafting and commands — S · ★

Separate caps for loom crafting versus commands and datapacks, so pack makers can hand out banners above
the crafting limit as loot while keeping survival crafting bounded. **Recommendation:** two config
numbers, the second defaulting to the 32-layer maximum. It is cheap, but check that the render and
tooltip code can handle a banner over the crafting cap without the "9/12" display misleading.

## Tier 2 — Solid mid-size features

### 5. Undo last layer at the loom — M · ★★

A one-click revert of the most recent layer applied at the loom. It's much cheaper than cauldron-washing,
which removes the top layer destructively and wastes the dye. **Recommendation:** a button that removes
the top layer from the banner in the input slot, and refunds nothing (the dye is already spent). It
builds on the existing loom patches (A1), and is the smallest piece of the layer editor in #12.

### 6. Banner stencil — M · ★★

"Save" a banner's full pattern list onto a paper-based stencil, then apply it to any blank banner of any
base color. It lets players share designs without shipping the banner itself, which fits the
"shareable designs" part of the identity. **Recommendation:** a single new item with a data component
holding the pattern list, applied in the loom, with the cap respected. Decide whether the stencil is
consumed on use (probably not, so it works like a template) and whether it records the dye colors.

### 7. Shield parity — M · ★★

Apply the raised layer limit to shields too, and let a stencil (#6) apply a design directly to a shield.
Shields carry a banner's patterns, so a 12-layer banner that becomes a 6-layer shield is a visible
inconsistency. **Recommendation:** raise the shield limit with the banner limit, and do the stencil
part only if #6 is built.

### 8. Hang banners from the underside of blocks — M · ★★

Allow placing banners on the underside of blocks, hanging down from a ceiling. It is one of your own
notes and fits "new ways to hang them" directly. **Recommendation:** a new block state with its own
model and collision, and a placement rule when you click the bottom face of a block. Decide how it
renders relative to wall banners. It also unlocks flag-style builds under bridges and roofs.

### 9. Curated banners for structures — M · ★★

Banners generated for different structures. Your note says it can't be totally random, or it will look
like garbage, and the identity statement requires any such idea to explain how it avoids ugly output.
**Recommendation:** use hand-authored sets, not generation. Write 8–12 designs per structure type as
data (loot tables or structure templates), each checked by eye, and pick among them at random. It stays
vanilla-friendly and datapack-extensible. The art direction is the real cost, so scope it to one or two
structure types first (for example villages and pillager outposts).

### 10. Bigger loom preview — M · ★★

The loom's tiny preview gets cramped at 12+ layers, so add a hover-to-zoom or a side panel preview so
dense designs are legible while editing. **Recommendation:** hover-to-zoom is the cheaper of the two
and stays in the existing screen. It is purely client-side. Keep it usable without a mouse (A3), for
example a keybind to toggle it.

### 11. Copy a placed banner in-world — M · ★

Sneak-use a blank banner on a placed banner to copy it, with no loom or crafting grid needed.
**Recommendation:** config-gated and off by default (A2), because it makes copying trivial. It is a fun
shortcut but the least in keeping with "vanilla-friendly" in this tier.

## Tier 3 — Big bets

### 12. Layer list editor (delete, reorder, recolor) — L · ★★★

A scrollable layer panel in the loom showing every pattern on the working banner, with the ability to
delete a middle layer, reorder layers, or swap just one layer's dye. This merges three notes from the
agent brainstorm, since they all need the same panel. Today a 12-layer banner with one wrong color means
starting over, which is the best argument for this mod's whole premise. **Recommendation:** build #5
(undo) and #10 (preview) first, since they teach you the loom UI, then add the panel. It's L because it is
a new UI inside a container screen on two loaders. Reordering is the hardest part, so ship delete and
recolor first.

### 13. Banner shape variants (jagged, sharp, pennant) — L · ★★★

Alternative banner shapes: jagged bottoms, sharp (pointed), and pennant. It is the most visible new
content in the backlog and a headline feature for the CurseForge page. It is also the most expensive: it
needs new models and rendering for each shape on all banner types, a way to choose a shape (crafting or
loom), and a decision about how shape interacts with patterns and with shields. **Recommendation:** treat
it as a design project. Start with one shape (pennant) to prove the pipeline, then decide on the rest.

### 14. Horizontal banners — L · ★★

Allow placing banners horizontally. The note doesn't say what that means. It could be a banner
lying flat on the ground or a table, or one sticking out sideways from a wall like a pennant on a pole.
**Recommendation:** clarify before scoring further. Either reading is a new block state and model, and
it overlaps with #8 (underside hanging) and #13 (pennant shapes), so decide those first.

### 15. Distant banner level of detail — M · ★

Optionally reduce the rendered layer count beyond N blocks, for performance in banner-heavy builds such
as flag plazas and team lobbies. **Recommendation:** only after someone measures a real problem. It is
a performance idea without a known bottleneck, and it makes distant banners look different from close
ones. If built, client-side config with a default off (A4, A3).

## Honorable mentions

- **Framed banner rendering.** The notes want banners in item frames to render their full stack
  faithfully. The mod's renderer patch probably already covers it. A quick in-game check is enough, and
  it's a bug fix if it fails.

## What was cut and why

**Already shipped.**
- Layer count in the tooltip.

**Merged into another item.**
- "Recolor a layer in place" and the "delete or reorder" layers are all part of #12.

## Suggested first arc

Do #1 (copy any count) and #2–#4 (the cheap config options) as one small release that rounds out the
layer cap. Then build the loom improvements in order: #5 (undo), #10 (bigger preview), and #6 (stencil)
with #7 (shields). #8 (underside hanging) is the best of your own ideas to start with because it is
self-contained. #12 (layer editor) and #13 (shapes) are the two headline projects; each deserves its
own design pass. #9 (structure banners) is mostly art direction and can run in parallel.
