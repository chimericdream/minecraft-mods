# Potential Features — Shulker Stuff

Brainstormed, thematically appropriate feature ideas. Nothing here is committed or implemented; these are
starting points for future planning.

The mod's identity: shulker boxes, made **more powerful and more pleasant** — enchantments, upgrades, and
QoL, all recognizably shulker-flavored.

## Finish what's started

* **Deep Storage enchantment** — it already exists in the mod's assets but isn't registered. Ship it:
  each level adds a row of capacity (or increases max stack size for stored items). This is the most
  requested category of shulker feature across the ecosystem.
* **Real config options** — the config screen currently exposes Miniblock Merchants drop chances by
  mistake (per the README's known issue). Replace them with enchantment tuning: Vacuum radius per level,
  Refill tick rate, Void behavior toggles.
* **Netherite Plating, concretized** — define exactly what "durable" means and document it: the placed
  box resists explosions, the item form floats in lava, doesn't burn, and doesn't despawn — the full
  netherite item treatment.

## New enchantments

* **Soulbound** *(treasure, max I)* — a plated-box-only enchantment: the shulker box stays in your
  inventory on death. Expensive, endgame, and exactly what people carry shulkers for.
* **Sorting** *(max I)* — right-click a placed enchanted box with an empty hand + sneak to
  auto-sort/merge its contents alphabetically or by registry order.
* **Sharing** *(max I)* — a placed box remembers per-player view state… (probably too weird — cut
  candidate; noted for completeness.)
* **Vacuum filter slots** — not a new enchantment but a deepening: Vacuum II unlocks a small
  include/exclude filter, reusing the item-filter concept from Hopper X-Treme (ideally via a shared
  ChimericLib filter API).

## Quality of life

* **Open-in-hand** — right-click air while holding an enchanted (or any, config) shulker box to open it
  without placing. The single biggest shulker QoL in the ecosystem; config-gated for balance-minded
  servers.
* **Quick-deposit** — sneak-click a placed container with a shulker box to dump all items that match the
  container's existing contents.
* **Scrollable tooltip preview** — richer item-form tooltip: full grid preview, shift-scroll pages,
  and a fill-percentage bar.
* **Comparator fill reading** — placed boxes emit comparator signal proportional to fullness (verify
  vanilla parity, extend for plated/enchanted variants if missing).

## Dyeing Station upgrades

* **Two-tone dyeing** — separate lid and base colors; the station's GUI already owns "recoloring," so
  gradients/duotones are its natural second act.
* **Pattern stamping** — apply a banner to a shulker box at the station to emboss a simple pattern on
  the lid (rendering cost is real, but even a handful of stencil patterns would be distinctive).
* **Undye slot** — a water-bottle slot to return any box to default purple (matching the "no wasted
  dye" ethos of the station).

## Shulker-adjacent flavor

* **Shulker Pearl** — rare shulker drop used in recipes above (Soulbound book, plating smithing
  additions), giving End raids a mod-specific prize.
* **Boxed advancements** — "Pack Rat" (fill a shulker box completely), "Turtle Power" (die and keep a
  Soulbound box), "Interior Decorator" (dye boxes in all 16 colors + two-tone).
* **Ender Chest bridge (stretch)** — an enchantment or upgrade letting a specific placed box be remotely
  viewed (not accessed) through a spyglass… flavor-rich but scope-heavy; parking-lot material.
