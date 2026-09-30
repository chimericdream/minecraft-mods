# Potential Features — Better Portal Linking

Brainstormed, thematically appropriate feature ideas. Nothing here is committed or implemented; these are starting points for future planning.

The mod's identity: give players a **simple, in-world way to control where portals link**, without
turning it into a whole redstone-adjacent system. Every idea below should stay optional and stay out
of the way for anyone who doesn't decorate their portals.

## Addressing niceties

* **Auto-labeled new portals** — when the game creates a brand-new portal on arrival (no existing exit
  found), optionally stamp its corners to match the entry portal's address automatically, so a
  freshly-dug pair links itself without the player placing any blocks by hand.
* **Address preview** — sneak-look at a portal's corners (or a held item's tooltip) to show which
  address blocks it's currently reading, useful for troubleshooting a link that isn't behaving as
  expected.

## Troubleshooting

* **`/portallink debug` command** — print the entry portal's address and the scored candidates for the
  last transit, for players who don't want to leave debug logging on all the time.
* **In-world feedback** — a subtle particle or sound cue on arrival when address matching picked the
  destination, so the feature's effect is noticeable without checking logs.

## Misc.

* **Advancement** — for successfully linking two portals by address for the first time.
