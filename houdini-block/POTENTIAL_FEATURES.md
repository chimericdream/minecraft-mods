# Potential Features — Houdini Block

Brainstormed, thematically appropriate feature ideas. Nothing here is committed or implemented; these are
starting points for future planning.

The mod's identity: **update suppression as a tool** — for redstone engineers, technical players, and
clean builders. The stage-magic naming (Houdini) is a gift; lean into it.

## Finer control over suppression

* **Directional masks** — configure *which* neighbors get notified: suppress updates upward only, skip
  the north face, etc. Turns the block from an on/off trick into a precision instrument.
* **Suppression radius option** — an "extended silence" mode that also swallows the second-order updates
  (diagonal/observer-visible state changes), for the gnarlier suppression tricks.
* **Mode switching ergonomics** — sneak-scroll or wrench-click to cycle the four modes in-world, with a
  clear actionbar message and per-mode block tinting so you can tell a "prevent all" from a "replace
  block" at a glance.

## New tricks in the act

* **The Houdini Wand** — an item that applies Houdini behavior to *other* blocks: break or place any
  block without triggering neighbor updates. The block is the prop; the wand is the magician. (Likely
  creative-only or expensive in survival, since silent-breaking is powerful.)
* **The Assistant ("Ghost Block")** — a companion block that is visible but has no collision and never
  emits *or receives* updates — walk-through scenery for adventure maps and secret entrances.
* **Update Capture & Replay** — the showstopper: a mode where the block *records* the updates it
  suppressed and fires them all when triggered (redstone pulse or interaction). Delayed-consequence
  contraptions: prime a sand column, release it on cue.
* **Silent Retraction** — powering a placed Houdini Block makes it remove *itself* without updates,
  enabling remote, wireless-feeling suppression in circuits.

## Builder-facing features

* **Camouflage mode** — the block copies the appearance of the block it replaced (or an adjacent block),
  so "Replace block" swaps are visually seamless. Secret doors write themselves.
* **Shape variants** — Houdini slab/stair/wall for suppression tricks in tight builds where a full cube
  doesn't fit the geometry.
* **Structure-void behavior** — an option for the block to be ignored by structure blocks/jigsaw saves,
  useful for map makers embedding suppression setups in prefabs.

## Technical-player polish

* **Update visualization** — while holding the block (or the wand), briefly render small particles on
  blocks that *would* receive updates from a place/break at the targeted position. Debugging suppression
  setups by eye is currently guesswork.
* **Advancements with stage flair** — "Now You See Me" (first placement), "The Prestige" (use replace
  mode to swap a block inside a powered contraption without it noticing), "Escape Artist" (silent
  retraction).
* **Config: which update types** — separate toggles for neighbor updates, shape updates, and comparator
  updates, for players who know exactly which mechanism they're exploiting.
