# Potential Features — Villager Tweaks

Brainstormed, thematically appropriate feature ideas. Nothing here is committed or implemented; these are
starting points for future planning.

The mod's identity: **small, configurable fixes for real villager headaches** — trading halls, breeders,
curing, and moving villagers around. Every idea below should stay a toggle, not a system.

## Bagged Villager, deepened

* **Tooltip manifest** — the Bagged Villager item shows profession, level, and a trade summary on hover,
  so a chest of bagged villagers isn't a mystery-box lottery.
* **Bag more mob types** — config toggles to allow bagging zombie villagers (carefully — mid-cure?),
  wandering traders, and maybe allays. Each is its own headache the bundle trick could solve.
* **Villager Transit Coupon (flavor)** — bagged villagers slowly become "cranky" if left bagged too many
  in-game days, temporarily raising prices after release. Default **off**; for servers that want the
  convenience to have a cost.

## Trading hall QoL

* **Job Posting Board** — a block that shows which nearby villager claims which workstation (the #1
  "why won't you take the job" debugging pain). Sneak-click a villager, then the board, to see its
  claimed bed/station highlighted briefly.
* **Profession Contract item** — lock a villager's profession and trades permanently (no more accidental
  workstation-break rerolls). Craftable with paper + emerald; applied like a name tag.
* **Trade preview on locked trades** — show what a trade *will* restock to; or a config to reveal all
  trade tiers grayed-out so hall-builders can plan without leveling every villager first.
* **Restock rules config** — restock frequency, whether sleeping is required for restock, and restock
  count per day, as plain toggles/sliders.

## Reputation & curing (extending existing sections)

* **Reputation viewer** — an inspect mode (sneak + empty hand?) showing your numeric reputation with a
  villager, so the existing global/negative-rep toggles have visible feedback.
* **Discount cap config** — set a maximum total cure-discount so stacked curing can be bounded on
  servers that consider 1-emerald trades broken.
* **Cure keepsake** — config so a cured villager remembers *who* cured it and greets that player with
  particles… okay, the real feature is per-player cure-discount config; the particles are free flavor.

## Breeder & population QoL

* **Growth time config** — adjustable baby villager grow-up time (the breeder-adjacent sibling of the
  existing cure-time override).
* **Breeding requirements config** — toggle the bed requirement, adjust food thresholds — the two
  fiddly halves of every breeder design.
* **Panic toggle** — option so villagers don't panic-sprint from zombies *when safely behind glass*, or
  a blanket "no panic" for decorative/trading-hall villagers. (Pairs naturally with the existing
  zombies-always-convert option.)

## Lure & movement polish

* **Configurable lure items** — drive the lure feature from an item tag instead of the fixed
  emerald-block list, so packs can choose what villagers follow.
* **Lure priority & speed** — config for follow speed and whether luring overrides work schedules.
* **Follow leash (stretch)** — allow leads on villagers as a config toggle. It solves the same problem
  as bagging for players who want the journey, not the teleport.

## Misc.

* **Nitwit dignity option** — nitwits can be converted by Miniblock Merchants conversion items but not
  vanilla professions… actually that's Miniblock Merchants' call; noted here as a cross-mod config
  suggestion only.
* **Advancements** — "Bag and Tag" (bag a max-level villager), "Fresh Start" (cure and re-employ the
  same villager), "Pied Piper" (lure 10 villagers at once).
