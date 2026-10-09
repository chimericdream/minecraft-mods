# Feeding trough breeding

> Source: [legacy ideas.md](../brainstorms/2026-09-02-legacy/ideas-md.md) ("Feeding trough: feeds animals in a given radius"), reshaped by the user on 2026-10-09 · Effort **M** · Status: **Ready**

## Description

Today the feeding trough is a decorative container: it holds up to three stacks of one food type (wheat,
beetroot, carrots, potatoes, or wheat seeds) and changes its model to show how full it is. This idea gives
it a job. **When the trough holds food that nearby animals like, it puts a pair of them in love mode, so
pens breed without the player hand-feeding each animal.**

Behavior:

- **Pace.** Every 10 seconds (200 ticks) the trough runs a check. Nothing happens while it is empty.
- **Area.** Animals within 5 blocks on X and Z and ±2 blocks on Y of the trough.
- **Eligible animal.** An adult animal of a supported species that accepts the stored food as breeding
  food (the animal's own food check), is not already in love mode, and has a breeding cooldown of zero.
- **Supported species.** Cows, sheep, pigs, chickens, rabbits, and horses only. Everything else is ignored,
  including modded animals.
- **The food picks the animals.** The player chooses which species breed by choosing what to stock. A
  wheat trough serves cows and sheep, a carrot trough serves pigs and rabbits, a wheat-seeds trough serves
  chickens, and a golden-carrot trough serves horses and rabbits.
- **Pairing.** The trough acts only when at least two eligible animals of the same species are in range.
  Each check feeds at most **one pair**: two animals, two food items. A lone animal is never fed.
- **Cost.** One food item per animal, the same as hand-feeding.
- **Crowding limit.** If 24 or more animals are in range, the trough does nothing. Every supported-species
  animal in range counts, babies and adults alike, whatever the trough is stocked with: a wheat-seeds
  trough still counts the pigs and sheep nearby. Babies are counted but never fed.
- **New foods.** Golden carrots and golden apples are added as trough foods so horses (and, with golden
  carrots, rabbits) can be bred. The user supplies the textures for both. A golden-apple trough serves
  horses only.
- **No credit.** Feeding gives no "bred animals" advancement or stat.
- **Feedback.** The normal heart particles on each fed animal, and green bonemeal ("happy villager")
  particles above the trough.
- **Redstone.** No redstone interaction for now.
- **Configurable.** The numbers above are defaults, and players and server operators can change them in
  the mod's config, within these limits:
  - Enabled: an on/off kill switch for the whole feature (default on). When off, troughs still store
    food but never feed animals.
  - Horizontal radius: 1–16 blocks (default 5).
  - Vertical radius: 0–8 blocks (default 2).
  - Check interval: at least 1 second (default 10).
  - Animal cap: at least 2 (default 24).

  JD Crafte has no config yet, so this adds its first one, using chimeric-lib's
  `YaclConfig` as the other mods do. The trough logic runs on the server, so the server's values apply
  and nothing needs syncing to clients.

Vanilla pairing and baby spawning take over once the animals are in love mode. The trough's `level` and
`food` blockstate already track its contents, so it empties visibly as it works.

**Out of scope (for now):** redstone control, feeding babies to speed up growth, healing animals,
attracting animals toward the trough, wolves, cats and other species, enchanted golden apples, and
any other new foods beyond golden carrots and golden apples.

## Decisions

- 2026-10-09 — Radius is 5 blocks on X and Z and ±2 on Y; the check runs every 10 seconds.
- 2026-10-09 — The trough only feeds when two eligible animals of the same species are in range.
  "Eligible" excludes animals with a non-zero breeding cooldown.
- 2026-10-09 — Costs one food item per animal, same as hand-feeding.
- 2026-10-09 — Supported species are cows, sheep, pigs, chickens, rabbits, and horses only.
- 2026-10-09 — Breeding stops when 24 or more animals are in range.
- 2026-10-09 — No redstone interaction for now.
- 2026-10-09 — Feeding shows the normal heart particles on the animals and green bonemeal particles above
  the trough.
- 2026-10-09 — Horses are supported by adding golden carrots and golden apples as trough foods. The user
  provides the textures for both.
- 2026-10-09 — The 24-animal cap counts every supported-species animal in range, babies and adults alike,
  not just species the stored food could breed.
- 2026-10-09 — The radius, vertical range, check interval, and animal cap are configurable by players
  and server operators, with the values above as defaults.
- 2026-10-09 — The config adds an on/off kill switch for the whole feature, and limits the values:
  horizontal radius 1–16, vertical radius 0–8, interval at least 1 second, animal cap at least 2.
- 2026-10-09 — Enchanted golden apples are not accepted.
- 2026-10-09 — Models, lang strings, and datagen for the new foods are handled during implementation.
- 2026-10-09 — One pair is fed per check.
- 2026-10-09 — The stored food determines which animals breed.
- 2026-10-09 — No advancements or stats are awarded.

## Brainstorm variants

_None yet._

## Related

- [Reusable block-entity helpers](../../../chimeric-lib/ideas/conveniences/block-entity-interval-and-area-helpers.md)
  in ChimericLib — the interval ticker, area scan, and feed-an-entity helpers this idea would be the first
  consumer of.
- [Sound and particle helpers](../../../chimeric-lib/ideas/conveniences/sound-particle-helpers.md) in
  ChimericLib — would cover the particle effects here.
- The legacy "Irrigation minecart" idea is the same kind of "automate a farm chore" feature.

## Dependencies and verification

- **Dependencies:** the user's textures for golden carrot and golden apple. No other idea blocks this.
  The ChimericLib [interval and area helpers](../../../chimeric-lib/ideas/conveniences/block-entity-interval-and-area-helpers.md)
  are optional and come afterwards, by extracting from this work. No loader-specific work.
- **Verification:** GameTests for pairing, the cooldown exclusion, the 24-animal cap, babies counted but
  not fed, and one pair per check; config tests that changed values take effect, out-of-range values are
  clamped, and the kill switch stops feeding; a manual check of the heart and bonemeal particles for the
  mod's `TEST_PLAN.md`.

## Open questions

None that change what gets built. To settle while building: each food has its own
`trough_level<N>_<food>` models, so golden carrots and golden apples each need three level models, a
`FoodType` entry, lang strings, and datagen, using the textures the user provides.
