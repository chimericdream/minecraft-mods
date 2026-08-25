# Potential Features — Effective Gear

Brainstormed, thematically appropriate feature ideas. Nothing here is committed or implemented; these are starting points for future planning.

The mod's identity: small quality-of-life tweaks and bonuses for player armor, weapons, and tools.

## From the idea backlog (2026-08-13)

* **Specific trim templates give situational bonuses**:
  * A "Silence" armor trim could give a chance to perform "noisy" tasks without setting off skulk
    sensors.
  * etc.
* etc.

## From the idea backlog (2026-08-22)

* **New trim materials** (same pattern as existing materials — full matching set grants a bonus):
  * Prismarine / sea lantern — indefinite underwater breathing, or reduced hunger drain while
    swimming.
  * Glowstone — full brightness for the wearer, or immunity to blindness/darkness.
  * Wither skeleton skull / soul soil — wither effect immunity, or withers never target you.
  * Snow / powder snow bucket — never take freezing damage; walk on powder snow like leather boots
    do for rabbits.
  * Phantom membrane — no phantom spawns from insomnia, or a slow-fall/feather-falling boost.
  * Totem of undying — high-rarity material; on lethal damage while wearing the full set, survive
    once without consuming anything, on a long cooldown (mirrors the totem item itself).
  * Ghast tear — regen after taking fire damage, or ghasts stop targeting you.
  * Shulker shell — immunity to the levitation effect.
* **Weapon/tool trims** — the README already frames the mod's scope as "armor, weapons, and tools,"
  but only armor is implemented. A parallel trim system for swords/tools, with bonuses gated on
  wielding rather than wearing, would fill that gap:
  * Blaze powder pickaxe — lava immunity while mining near lava.
  * Echo shard pickaxe — mines sculk without triggering nearby sensors (same idea as the "Silence"
    template above, but material-driven instead of template-driven).
  * Amethyst tool — geodes/amethyst clusters always drop max yield.
* **Trim-template-based bonuses** — give specific vanilla trim *templates* (not materials) their own
  minor situational bonus that stacks with the material bonus:
  * Silence — see above.
  * Ward — extra resistance to the warden's sonic boom specifically.
  * Snout — piglins/hoglins never provoked by nearby block-breaking.
* **Mixed-material bonus** — a smaller universal bonus (e.g. reduced fall damage) for wearing four
  *different* trim materials at once, rewarding variety as a second axis alongside the existing
  matched-full-set bonus.

## From the idea backlog (2026-08-25)

* **Preserving: capture the specific biome tint, not just the default color.** The shipped Preserving
  enchantment always locks a mined leaf to vanilla's fixed default color. A more nuanced version would
  instead remember the *exact* tint the leaves had when broken (e.g. "Oak" vs. "Oak with taiga tint"),
  so a builder could match a specific biome's leaf color anywhere. This needs a (non-ticking, cheap)
  block entity to hold the captured color/biome, since blockstate can't hold an open-ended biome
  identifier. Full design write-up, including why it needs a block entity, why that's not a performance
  concern here, and what's still unresolved (tooltip mechanism, naming scheme): see
  `docs/PRESERVING-PER-BIOME-TINT.md`.
