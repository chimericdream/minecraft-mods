# Grave-mod compatibility

> Source: user's request, 2026-10-28 · Status: **Building** (YIAS done, other mods open)

## Description

Make per-player `keepInventory` overrides work with "grave" style mods, which replace or extend vanilla
death handling. The required first target is Serilum's
[*Your Items Are Safe*](https://github.com/Serilum/Your-Items-Are-Safe/tree/26.2.0) (YIAS), which the
user's own server runs. Integrations must be optional and active only when the other mod is present.

Out of scope: any hard dependency on a grave mod, and grave mods other than YIAS until it works.

## Findings: Your Items Are Safe (branch `26.2.0`)

Read from the public source, 2026-10-28:

- **It reads the rule from the level, not the player.** `DeathEvent.onPlayerDeath` starts with
  `player.level().getGameRules().get(GameRules.KEEP_INVENTORY)` and returns early if true. A lookup of
  that shape has no idea which player it's for, so a plain "override the rule value" hook can't help.
- **It runs on the death event.** NeoForge: `LivingDeathEvent` at default priority, with
  `LivingDropsEvent` at `LOWEST` to collect drops. Fabric: the Natamus Collective events
  `PLAYER_DEATH` and `ON_LIVING_ENTITY_DROPS`. So YIAS depends on *Natamus Collective*.
- **No API or mixins.** Its mixin list is empty and it exposes no hooks. The only per-player gating is its
  own config (`Util.isIgnoredPlayer`, `isIgnoredDimension`), which we shouldn't touch.
- **Behaviour it must keep.** With world `keepInventory` off, an unmodified YIAS builds a chest and
  armor stand for every death. A kid with `keepInventory = true` must skip that and simply keep their
  items; an adult with an override of `false` while the world is `true` must get YIAS-style handling or
  vanilla drops, whichever applies.

## Candidate approaches

1. **Subject-scoped rule lookup (preferred).** A mixin on `GameRules#get` that, while a "current subject
   player" is set, returns that player's override for `KEEP_INVENTORY`. Our mod sets the subject before
   the death handlers run and clears it after. On NeoForge that is `HIGHEST` and `LOWEST` listeners
   around YIAS. Fabric ordering depends on Collective's event order and must be tested. This helps any
   mod that reads the rule from a level during a death event, not only YIAS.
2. **Event-level shim.** Cancel or filter YIAS's work ourselves. Not viable without touching its internals.
3. **Compatibility via `/gamerule` swap.** Flip the world rule for the duration of the death event.
   Rejected: it's global state and breaks the "only the player in question" decision.

## Decisions

- **YIAS: wrap its one `keepInventory` read (2026-10-30).** It reads the rule once, at the top of
  `DeathEvent.onPlayerDeath`, and not again in its delayed task. A `@Pseudo` mixin wraps that call and resolves
  it for the dying player (`RFT$YourItemsAreSafeMixin`). This replaces the subject-scoped `GameRules#get`
  approach above: no event ordering, no thread-local, same code on both loaders.
- **Optional by plugin.** `CompatMixinPlugin` guards `rulesforthee.compat.mixins.json` and applies a mixin only
  when its target class file exists (checked without loading it). A new mod is one mixin class plus one row in
  its `TARGETS` map.
- **Fail soft.** The compat injectors use `require = 0`, so a changed YIAS logs a warning instead of crashing.
- **Testing is manual** (`TEST_PLAN.md` item 11).

## Related

- [Per-player game rule overrides](per-player-rule-overrides.md) — the mechanism this builds on.

## Open questions

1. **Vanilla verification.** Confirm where vanilla reads `KEEP_INVENTORY` for a death
   (item drops, and the inventory copy on respawn) by decompiling 26.2. The cached sources don't cover
   those classes yet, so this hasn't been checked.
2. **Fabric event ordering.** Can we reliably run before and after Collective's `PLAYER_DEATH` and
   `ON_LIVING_ENTITY_DROPS`, or do we need our own mixin on the death path?
3. **Is a thread-local / scoped subject safe?** Death handling runs on the server thread, but YIAS
   enqueues a delayed task (`enqueueCollectiveServerTask`). Anything it reads later, outside our scope,
   will see the world value. Does that matter for what YIAS does in the task?
4. **Other grave mods.** Which are worth testing after YIAS? Candidates to survey: Gravestones-style mods,
   Corail Grave, Sophisticated-style backpack interactions. A brainstorm or survey could cover this.
5. **Testing.** YIAS needs Natamus Collective installed. Can a GameTest run with both mods loaded, or is
   this manual steps in `TEST_PLAN.md`?
