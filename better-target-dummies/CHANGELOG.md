### Unreleased changes


### 26.2 - 1.0.0

#### New Features

* Added the Target Dummy block:
  * Craft it with a target block and 4 mob drop items.
  * Right-click it with a spawn egg to bind that mob to the dummy, so you can test how attacks
    perform against a specific mob or mob category.
  * Hits on a bound dummy show the exact damage dealt in the action bar.
  * The bound mob starts out facing the cardinal direction (north/east/south/west) the player was
    standing on when it was bound. Right-click empty-handed to rotate it 90° clockwise (and check
    which mob is bound); sneak + right-click empty-handed to clear it.
  * Bound mobs are immune to environmental hazards — fire, rain, drowning, suffocation, and the like
    never hurt them, and won't even make them flinch or cry out. Your hits still land normally. They
    also stay quiet otherwise (no idle ambient noise).
  * The dummy needs a redstone signal to spawn its mob — turn it off with redstone at any time to
    remove it (the binding is remembered, so power restores the same mob), or break the block or
    `/kill` it to remove it outright.
  * Each of the 4 sides has its own texture, and the block faces you when placed.
* Added the Dummy Spawn Egg: a survival-friendly, reusable alternative to a real spawn egg. Craft it
  from an egg and paper, right-click in the air to pick a mob from a searchable list, then right-click
  a Target Dummy with it to bind that mob. Its tooltip always shows which mob is currently picked. It
  isn't consumed, so it can be re-picked and reused for a different mob at any time. An anvil rename
  still works too, as a shortcut for anyone who'd rather type the name. Real spawn eggs still work as
  well, and are consumed on use as usual.
