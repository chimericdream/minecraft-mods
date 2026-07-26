# Potential Features — Hopper X-Treme

Brainstormed, thematically appropriate feature ideas. Nothing here is committed or implemented; these are
starting points for future planning.

The mod's identity: everything stays **recognizably "hopper"** — speed tiers, directions, and filtering.
New ideas should slot into that grid rather than invent a new machine.

## Completing the grid

* **Sideways "Slopper"?** — a hopper that pushes horizontally with the same tier family. (Vanilla hoppers
  can already point sideways, so this only earns its place if it adds something — e.g. pushing *and*
  pulling horizontally in a straight line, making compact item lines.)
* **Hopper Minecarts by tier** — golden/diamond/netherite hopper minecarts matching block-form speeds,
  plus a filtered hopper minecart. Rail-based farms deserve the same upgrades.
* **Trickle tier for multi-hoppers** — if the honeyed tier exists for hoppers and huppers, complete the
  matrix with honeyed multi-hoppers/multi-huppers (rate-limited aggregation is a real sorting trick).

## Filtering, deepened

* **Tag-based filter entries** — filter by item tag (`#logs`, `#ores`) in addition to specific items, so
  one filter slot can cover whole families.
* **Component-aware matching** — optional strict mode matching enchantments, custom names, or damage
  ranges ("only broken tools," "only Mending books").
* **Filter copy/paste with the Wrench** — sneak-click to copy a hopper's filter config, click to apply it
  to the next hopper. Building a 40-hopper sorting hall currently means 40 manual setups.
* **Filter presets item** — write a filter configuration onto a blank "filter card" that can be
  duplicated in a crafting grid and slotted into hoppers, making sorting-hall blueprints shareable.
* **Overflow routing** — a per-hopper toggle: items rejected by the filter pass through to the hopper
  below instead of clogging, turning a vertical hopper stack into a natural sorting cascade.

## Wrench upgrades

* **Mode cycling** — wrench modes for rotate, side enable/disable, filter copy/paste, and a **stats
  mode** that shows items/minute through the clicked hopper.
* **Side I/O control** — wrench-toggle individual faces of a hopper (accept from top only, ignore left,
  etc.), shown with subtle connector textures like the beam connections in Minekea.

## Tier & material flavor

* **Copper oxidation (cosmetic)** — copper hoppers weather through the vanilla oxidation stages (waxable,
  scrapeable) with zero behavior change. Pure texture flavor that matches vanilla copper.
* **Upgrade smithing path** — apply an upgrade template to promote a hopper in place (golden → diamond)
  keeping contents, name, and filter, instead of crafting a new block. Pairs with the existing
  deprecated-block conversion machinery.

## Redstone & integration

* **Per-hopper redstone modes** — vanilla lock behavior, inverted (only runs while powered), or pulse
  (moves one item per pulse) — the last one makes tiered hoppers usable in precise item-counting circuits.
* **Comparator fidelity** — ensure comparator output accounts for filter slots sensibly (filters
  shouldn't read as "contents").
* **Honeyed hopper + note block?** — silly flavor: honeyed hoppers make a soft "blip" when they move an
  item. Config-gated, default off.

## Documentation hooks

* The README's four teachable concepts (tiers, multi, huppers, filtering) would become five with
  splitters — worth keeping the concept count low and the grid complete rather than adding one-off blocks.
