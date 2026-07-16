# Potential Features — Miniblock Merchants

Brainstormed, thematically appropriate feature ideas. Nothing here is committed or implemented; these are
starting points for future planning.

The mod's identity: **professions with personality**, each with a themed conversion item found through a
specific gameplay activity, trading decorative miniblocks. New ideas should keep that triangle intact:
profession theme → discovery method → miniblock catalog.

## New professions (with conversion-item hooks)

* **Spelunker** — deep-dark/cave miniblocks (sculk set, amethyst clusters, dripstone, glow lichen lamps).
  Conversion item: *Echoing Compass*, rare find in Ancient City chests.
* **Trailblazer** — trial chamber miniblocks (copper bulbs, vaults, chiseled tuff). Conversion item:
  *Ominous Trial Key*, rare drop from ominous vaults.
* **Endologist** — End miniblocks (chorus plants, end rods, shulker boxes, dragon egg replica).
  Conversion item: *Petrified Chorus Fruit*, 1-in-N from breaking chorus plants or End City chests.
* **Paleontologist** — fossil and bone miniblocks, museum display pieces. Conversion item: *Immaculate
  Fossil*, rare drop while brushing suspicious blocks (a natural crossover with Archaeology Tweaks).
* **Confectioner** — cakes, candles, sweet-themed minis. Conversion item: *Sugar-Dusted Egg*, rare drop
  when baking a cake (crafting trigger).
* **Musician** — jukebox, note block, and instrument minis; maybe tiny "album crates" per music disc.
  Conversion item: *Signed Music Disc*, rare drop from creepers killed by skeletons (a wink at the
  vanilla disc mechanic).
* **Meteorologist** — weather-themed minis (lightning rod, snow golem, storm cloud). Conversion item:
  *Fulgurite*, created when lightning strikes sand near a villager… or more simply, rare from lightning
  strikes.

## Collection gameplay (nearly 1000 minis deserve a metagame)

* **Collector's Ledger** — an in-game book/UI tracking which miniblocks you own per profession, with
  per-profession completion percentages. Turning ~1000 items into a visible collection is the single
  biggest engagement multiplier available to this mod.
* **Completion rewards** — completing a profession's catalog unlocks a unique "master trade" (a one-off
  showpiece mini, e.g. a golden version of the profession's job block).
* **Display furniture** — a **Curio Shelf** that holds 4–8 miniblocks on one block face, and a rotating
  **Display Pedestal** for showpieces. (Or defer to Minekea's shelves with explicit compat.)
* **Advancements** — "First Convert," "Full Employment" (one of every profession), "Completionist" tiers
  per catalog quarter.

## Merchant life & flavor

* **Restock requests** — merchants occasionally "want" a themed item (the Chef wants wagyu, the
  Astronomer wants an amethyst block); delivering it grants a temporary discount. Gossip-adjacent, small,
  characterful.
* **Market Day** — a config-scheduled in-game day when all miniblock merchants discount trades; pairs
  well with server communities that build market squares.
* **Merchant job blocks** — unique job-site blocks per profession (Mixology Station is already an item —
  make it placeable!), so trading halls can be visually themed per merchant.
* **Wandering Peddler** — a rare wandering-trader variant carrying a random sampling of minis from all
  professions plus, very rarely, a conversion item — a discovery path for players who miss loot chests.

## Already-planned items, made concrete

* **Structures per profession** — small themed shops/stalls; suggest starting with 3–4 shared "market
  stall" templates re-skinned per profession rather than 25+ bespoke structures.
* **Uninstall/convert-back path** — the README promises it; a `/miniblockmerchants export` command
  converting mod villagers back to datapack-tag equivalents would close the loop.
* **Head-drop datapack sample** — ship it in `docs/` as promised, with a README section on wiring it to
  More Mob Heads.

## Config & compatibility

* **Per-profession enable toggles** — let packs run a curated subset of professions.
* **Trade rebalancing hooks** — data-driven trade tables so servers can adjust emerald costs without
  code.
