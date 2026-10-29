# Test Plan — Rules for Thee

Rules for Thee gives players their own value for a game rule. The moving parts: `EffectiveRules` (the
lookup: own override, then profile, then the world), `RulesData` (saved data), `RulesForTheeCommand`,
`RuleNotices`/`RuleNotifier` (telling players), `ClientSync` (the rules the client caches), and one mixin
per vanilla class that reads a player's rules: `Player`, `ServerPlayer`, `PlayerAdvancements`,
`TamableAnimal`, `PhantomSpawner`, `NetherPortalBlock`, `PlayerSpawnFinder`, `MinecraftServer`, and
`ServerPlayer`'s anonymous command source.

## Automated

* `./gradlew :rules-for-thee:fabric:test` — JUnit: precedence, typed values, the one-profile cap, saved-data
  round trips, unknown/invalid stored values, and the notification decision.
* `./gradlew :rules-for-thee:fabric:runGameTest` — GameTests (`fabric/src/gametest`) drive the real vanilla
  methods and the real command tree:
  * `RulesCommandGameTest` — profile and player commands, permissions, failures, and offline targeting.
  * `PlayerRuleHooksGameTest` — damage rules, inventory drop, XP drop, respawn inventory copy; every mixin
    target class loads.
  * `PhantomRulesGameTest` — the level-wide gate and the per-player decision.
  * `ClientSyncGameTest` — the packets sent for the client-cached rules.
  * `EasyRulesGameTest` — portal delays (survival and creative), command feedback, the scoped respawn radius,
    integer command arguments.
* Run `bun run verify:gametests` after adding a GameTest class.

NeoForge has no GameTest runner in this repo. The NeoForge jar's targets were checked with `javap` against the
patched Minecraft (all hooked methods and the `lambda$award$0` / `ServerPlayer$3` names exist), and
`./gradlew :rules-for-thee:neoforge:runServer` starts cleanly with the mixins applied.

## Not covered by automation

A mock player has no network connection and isn't in the player list, so these are manual. Setup: a
dedicated server (hard difficulty, `keepInventory` off) with two accounts, `Kid` and `Adult`, both able to log
in, and `Kid` set up with:

```
/rulesforthee profile create kids
/rulesforthee profile edit kids set keep_inventory true
/rulesforthee player Kid profile set kids
```

Fabric full pass, NeoForge smoke pass (items 1, 2 and 9).

1. **Vanilla client joins.** Join the server from an unmodded vanilla client (no Fabric/NeoForge, no mods).
   This is the highest-impact check: the mod must not require anything on the client.
2. **Keep inventory.** Die as `Kid` with items and XP: the items and XP are kept and `Kid` respawns with
   them. Die as `Adult`: everything drops. Repeat after a server restart.
3. **Notification.** With `Kid` online, run `/rulesforthee player Kid set fall_damage false`: `Kid` gets a chat
   message listing the changed rules immediately. Log `Kid` out, change a rule, log in: the message appears
   once at login and not again on the next login. Remove every rule: `Kid` is told they were removed.
4. **Death and advancement messages.** `/rulesforthee player Kid set show_death_messages false`, then kill
   `Kid`: `Adult` sees no death message in chat. Do the same for an advancement
   (`show_advancement_messages`). Kill `Kid`'s tamed wolf: no message either. `Adult`'s own deaths are still
   announced.
5. **Immediate respawn / reduced debug info.** Set `immediate_respawn true` for `Kid` only: `Kid` skips the
   death screen and `Adult` still sees it. Change it while `Kid` is online (no relog needed). Then run
   `/gamerule immediate_respawn false` and `true`: `Kid`'s setting must not be reset. Repeat with
   `reduced_debug_info` and the F3 screen.
6. **Phantoms.** On a night with `Kid` and `Adult` both outside under open sky and long without sleeping,
   set `spawn_phantoms false` for `Kid` while the world allows them: phantoms spawn only for `Adult`. Then
   `/gamerule spawn_phantoms false` in the world and `spawn_phantoms true` for `Kid`: phantoms spawn only
   for `Kid`.
7. **Portal delay and respawn radius.** Set `players_nether_portal_default_delay 5` (ticks) for `Kid`: `Kid`
   travels almost at once; `Adult` waits the normal time. Set `respawn_radius 0` for `Kid` with a large world
   value: `Kid` respawns exactly at the spawn point.
8. **Command feedback.** Set `send_command_feedback false` for `Kid`: `Kid` gets no "set the time" style
   feedback for commands they run; `Adult` does.
9. **Persistence.** Restart the server: profiles, memberships, overrides, and the "already told" state all
   survive, and no one is told their rules again.
10. **Permissions.** As a non-operator, everything but `/rulesforthee mine` is refused.
11. **Death-handling mods.** With *Your Items Are Safe* installed, die as `Kid`: today it still builds a grave
    chest, because it reads the world's `keepInventory` itself. This is the known gap tracked in
    `ideas/core/grave-mod-compatibility.md`. Record what actually happens.
