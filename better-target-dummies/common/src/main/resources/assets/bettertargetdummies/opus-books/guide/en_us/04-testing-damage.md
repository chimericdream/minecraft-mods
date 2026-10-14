---
title: Testing damage
icon: item:minecraft:iron_sword
tags: [usage, combat]
summary: The damage readout and what the mob ignores.
---
The point of a dummy is to see how your attacks do against a specific mob.

## The damage readout

Each time you hit a bound mob, the action bar shows the damage it took, for example "7.0 damage" followed by the
mob's name. The number is the final amount, after armor and enchantments such as Smite. The mob's health does not
actually go down, so you can keep swinging.

A zombie takes extra damage from Smite, and a spider takes extra from Bane of Arthropods. Bind each one and
compare.

## Real mobs, tame behaviour

* **The mob is real.** Mob-specific damage rules apply, so your results match what you would see in a fight.
* **No AI.** The mob never moves or attacks.
* **Quiet.** It makes no idle noises, such as clucking or groaning.
* **No sunburn.** Zombies and skeletons do not catch fire in daylight.
* **Hazards do nothing.** Fire, rain, drowning, suffocation, and similar damage do not hurt it, flinch it, or make it
  cry out.
* **Your hits count.** When you hit it, it still plays its usual hurt sound and flash.

## Getting rid of it

`/kill` and falling into the void still remove the mob. Turning off the redstone signal removes it too, and
turning it back on brings it back.

> [!TIP] A comparison trick
> Bind one dummy per mob, power them all from one lever, and walk down the row with the same weapon.
