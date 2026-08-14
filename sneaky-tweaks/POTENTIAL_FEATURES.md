# Potential Features — Sneaky Tweaks

Brainstormed, thematically appropriate feature ideas. Nothing here is committed or implemented; these are starting points for future planning.

The mod's identity: any vanilla behavior that could plausibly be gated on `Player#isCrouching` is fair game. Vanilla already treats sneaking as a "be careful / be polite / be unnoticed" flag in a handful of places; this mod's job is finding everywhere else that logic could have applied and didn't. Not every idea below needs to be sensible — a few are here purely because "unified sneaking theory" was too funny not to chase to its logical, slightly ridiculous conclusion.

## Careful footing

The sweet berry bush precedent, applied everywhere else something spiky, hot, or slippery lives.

* **Tip-toe through cacti** — moving into a cactus while sneaking (and only while sneaking — no free walking through spike fields) skips the damage tick, same cancellation as the berry bush.
* **No slipping on ice** — crouching on blue/packed/frost ice removes the sliding acceleration, trading speed for control (frost walker already lets you make ice; this lets you actually stand on it).
* **A gentler landing** — sneak in the tick before you hit the ground and shave a couple points off fall damage, as a nod to "tucking and rolling" rather than full physics.

## Stealth utility

Sneaking already hides you from a few things. Let it hide you from a few more, and stop things from happening to you by accident while you're trying to be careful.

* **No accidental pickups** — sneaking suppresses the automatic item/XP-orb vacuum, so standing in a farm's drop pile while sneaking doesn't fill your inventory. Immensely useful for XP grinding and sorting builds; arguably should have been vanilla.
* **Pressure plates and tripwire ignore you** — sneaking across a wooden pressure plate or tripwire no longer triggers it, for actual stealth through actual traps.
* **Sculk shriekers stay quiet** — extend the existing sculk-sensor sneak dampening to shriekers, so a fully sneaking, fully stationary player doesn't ratchet up the warden warning level.
* **Piglins don't clock you without gold** — sneaking near piglins suppresses their aggro check the same way gold armor does, on the theory that piglins startle easily and a crouching, unhurried player just reads as"not worth it."
* **Bees stay calm without smoke** — harvesting a hive/nest while sneaking counts as "gentle" for aggro purposes, same effective protection as campfire smoke, minus the campfire.

## Mob courtesy

Vanilla mobs already react to sneaking in a couple of specific spots (enderman eye contact, villager nameplates). This section is "what if more of them did," played straight — right up until it isn't.

* **Sleeping villagers stay asleep** — ambient noise checks near sleeping villagers are skipped while you're sneaking nearby, so 3 a.m. base runs don't wake the neighborhood.
* **Parrots hold their tongue** — a shoulder parrot won't mimic a nearby hostile mob's sound (the infamous creeper hiss) while you're sneaking, since apparently they know better than to startle you mid-stealth.
* **Cats and foxes don't bolt** — sneaking within their flee radius is treated like holding a trust-building item; they'll still scatter from sudden movement or combat, just not from your existence.
* **The enderman solidarity clause** *(bit)* — a nearby enderman crouches when you crouch. Does nothing. Fixes nothing. Pure, uncut respect between two beings who understand the value of keeping a low profile.

## Purely cosmetic absurdity

The joke tier. Each of these should ship off by default and be clearly labeled as a joke in its config description — the mod can be a little silly without being silly *by default*.

* **The floor is, briefly, not lava** *(config name: "Highly Illegal")* — sneaking into lava for exactly one tick doesn't ignite you. Framed entirely as a chaos-server novelty, not a survival feature, and disabled by default with a config description that says so in as many words.
* **Golems bow** — iron golems and snow golems within a few blocks dip their whole model toward the ground while you sneak past, like tiny, blocky butlers.
* **"Uncomfortable Silence" advancement** — a joke advancement for cumulative time spent sneaking (measured in absurd units — "you have now spent one full Minecraft day of your life crouching").
* **Sneaking narrows your FOV by one imperceptible degree** — does genuinely nothing perceptible. Exists purely so the changelog can say "sneaking is now 1% more immersive" with a straight face.

## Configuration ergonomics

Because this mod is a grab bag by design, the config needs to pull its weight more than most.

* **Per-feature toggles** — every tweak above gets its own on/off switch in the YACL screen; nobody should have to accept the whole bit to get the one fix they wanted.
* **"Bit tolerance" preset levels** — a top-level dropdown (Vanilla+ / Full Bit / Chaos) that bulk-enables features by how straight-faced they are, rather than making players hunt through categories.
* **Per-feature keybind override** — for anything gated on "sneaking + something else" (e.g. the lava tick), let it optionally bind to a dedicated key instead of overloading crouch further.

## From the idea backlog (2026-08-13)

* Add thematic/fun advancements.
* **"Wile E. Coyote" style sneaking** — when sneaking, if you don't look down, you can cross a 2-3 block air gap as though you're on solid ground.
