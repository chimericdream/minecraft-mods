# Potential Features — Banner Tweaks

Brainstormed, thematically appropriate feature ideas. Nothing here is committed or implemented; these are
starting points for future planning.

The mod's identity: vanilla-friendly fixes for the rough edges around **making, copying, and displaying
elaborate banners**. Everything below stays inside that lane — no new banner "content," just fewer walls.

## Loom quality of life

* **Layer list editor** — a scrollable layer panel in the loom showing every pattern on the working
  banner, with the ability to **delete a single middle layer** or **reorder layers** instead of vanilla's
  all-or-nothing cauldron wash.
* **Recolor a layer in place** — select an existing layer and swap just its dye, preserving everything
  above and below. Currently a 12-layer banner with one wrong color means starting over.
* **Live full-size preview** — the loom's tiny preview gets cramped at 12+ layers; a hover-to-zoom or
  side panel preview would make dense designs legible while editing.
* **Undo last layer** — one-click revert of the most recent application at the loom (cheaper than
  cauldron-washing, which removes the top layer *destructively* and wastes the dye).

## Copying & sharing designs

* **Copy regardless of layer count** — patch banner duplication so a banner with more than six layers can
  still be copied onto a blank banner (vanilla logic may refuse or truncate past its own cap).
* **Banner Stencil item** — "save" a banner's full pattern list onto a paper-based stencil, then apply it
  to any blank banner of any base color. Lets players share designs without shipping the banner itself.
* **Shield parity** — apply the raised layer limit to shields too, and let a stencil apply a design
  directly to a shield.
* **Copy from placed banners** — sneak-use a blank banner on a placed banner to copy it in-world, no loom
  or crafting grid needed (config-gated).

## Rendering & display

* **Map marker fidelity config** — options for how many layers render on banner map markers, for servers
  where many complex banners are marked on one map.
* **Distant banner LOD** — optionally reduce rendered layer count beyond N blocks for performance in
  banner-heavy builds (flag plazas, team lobbies), restoring full detail up close.
* **Framed banner rendering** — make banners in item frames render their full pattern stack faithfully.

## Configuration

* **Per-station limits** — separate configurable caps for loom crafting vs. commands/datapacks, so pack
  makers can hand out >limit banners as loot while keeping survival crafting bounded.
* **Dye cost scaling** — optional rule where layers past vanilla's six cost extra dye (or XP), letting
  servers balance the raised cap.
* **Layer count in tooltip** — show "Patterns: 9/12" on banner tooltips so players know how much room a
  design has left.
