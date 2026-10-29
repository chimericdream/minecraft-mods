# Supported rules

> Source: user's rule lists, 2026-10-28 · Status: **Exploring**

## Description

Which game rules the mod can override per player, in what order they're built, and how each is hooked.
The mechanism itself is in [per-player rule overrides](per-player-rule-overrides.md). This file is the
catalog on top of it.

Rule names are the real constants in 26.2's `net.minecraft.world.level.gamerules.GameRules`. The hook
notes come from reading the decompiled 26.2 call sites (cached in `.sources/minecraft/26.2`), not from
memory. **"Subject"** means who the rule is read about, which decides whether it fits the "only the
player in question" decision.

Rule values are typed. The data model, storage, and commands support **boolean and integer** values from
v1.

### Built (2026-10-29)

Every rule in the two v1 tables below is implemented, with GameTests for the hooks and manual steps in
`rules-for-thee/TEST_PLAN.md` for what a mock player can't exercise. How the build differed from the notes in
the tables:

- **Client-synced rules** (`IMMEDIATE_RESPAWN`, `REDUCED_DEBUG_INFO`) are not wrapped in the login packet.
  The player is sent their own value as packets right after they join, when their effective rules change, and
  after `MinecraftServer.onGameRuleChanged` has broadcast a changed world rule (read: it sends the world's
  value to every online player). The client keeps both flags across a respawn, so nothing is needed on death.
  `LIMITED_CRAFTING` uses the same mechanism and is a small addition if wanted.
- **`SPAWN_PHANTOMS`** needed two hooks: the level-wide rule read opens if the world or any online player's
  override allows phantoms, and the per-player `isSpectator` check also skips players the rule doesn't allow.
  The filter inside the vanilla loop (night, sky, random roll) isn't covered by a GameTest.
- **`SEND_COMMAND_FEEDBACK`** is hooked through `ServerPlayer$3`, the anonymous command source, via its
  synthetic `this$0` field. That class name is the same in the NeoForge jar.
- **`RESPAWN_RADIUS`** uses a thread-local `SubjectScope` opened in `ServerPlayer.adjustSpawnLocation`.
- Messages are literal text, not translation keys, because the mod is server-side only.
- **Not yet done:** the "maybe" tier, `UNIVERSAL_ANGER`, and grave-mod compatibility.

### v1 — definitely

| Rule | Type | Subject and hook |
|---|---|---|
| `KEEP_INVENTORY` | bool | Three reads, all about the dying player: `Player` (drop inventory), `Player` (XP dropped on death), and `ServerPlayer.restoreFrom` (copy inventory to the respawned player). See [grave-mod compatibility](grave-mod-compatibility.md). |
| `IMMEDIATE_RESPAWN` | bool | Read in `PlayerList` when the player logs in, to build their login packet. A mid-session change needs a per-player resend of the matching game-event packet. See "Client-synced rules" below. |
| `DROWNING_DAMAGE`, `FALL_DAMAGE`, `FIRE_DAMAGE`, `FREEZE_DAMAGE` | bool | All four are in `Player.isInvulnerableTo(level, source)`. `this` is the victim, so this is the cleanest hook in the mod: one wrapper, four rules. |
| `SPAWN_PHANTOMS` | bool | `PhantomSpawner.tick` checks the rule **once, before** looping over the level's players. Per-player needs the check moved inside the loop, and the top check changed to "world rule is on, or anyone has an override". The most invasive v1 hook. |
| `SHOW_ADVANCEMENT_MESSAGES` | bool | `PlayerAdvancements`, read about the player earning it. Easy. |
| `SHOW_DEATH_MESSAGES` | bool | `ServerPlayer`, read about the dying player. Easy. `TamableAnimal` reads it too, for pet deaths; the owner's override applies there. |

**What "show messages" means.** These rules control whether *this player's* advancement and death
messages are announced. They don't control what the player *sees* about others. A kid with
`SHOW_DEATH_MESSAGES = false` has their own deaths kept out of chat; they still see everyone else's.
Controlling what they receive would be a different (chat filtering) feature.

### v1 — only if easy

| Rule | Type | Subject and hook |
|---|---|---|
| `SEND_COMMAND_FEEDBACK` | bool | Read inside `ServerPlayer`'s own command-source object, so the subject is clear. Easy. |
| `RESPAWN_RADIUS` | int | `PlayerSpawnFinder.findSpawn(level, suggestion)` is static and doesn't receive the player. The caller in `ServerPlayer` has one, so it needs a scoped "current subject" like the death path. First integer rule, so it also exercises the integer support. |
| `PLAYERS_NETHER_PORTAL_DEFAULT_DELAY`, `PLAYERS_NETHER_PORTAL_CREATIVE_DELAY` | int | `NetherPortalBlock.getPortalTransitionTime` already has the `Player` in hand. Easy. |
| `REDUCED_DEBUG_INFO` | bool | Login-packet rule like `IMMEDIATE_RESPAWN`, so it comes nearly free with that mechanism. |

### Maybe

| Rule | Type | Subject and hook |
|---|---|---|
| `LOCATOR_BAR` | bool | `ServerWaypointManager.isLocatorBarEnabledFor(ServerPlayer)` is already per player and static, so it's one redirect. It's only consulted when connections are created or updated, so turning it off for a player who already has connections needs their connections rebuilt. |
| `ENDER_PEARLS_VANISH_ON_DEATH` | bool | `ThrownEnderpearl`, read about the owning player. Clean. |
| `FORGIVE_DEAD_PLAYERS` | bool | **Player-specific.** Read in `ServerPlayer` when it dies (the dying player is the subject), and in `StopBeingAngryIfTargetDead` / `StartCelebratingIfTargetDead` when the mob's target is dead (the target player is the subject). A mob forgiving *you* is about you. |
| `ALLOW_ENTERING_NETHER_USING_PORTALS` | bool | Read in `ServerLevel` when an entity tries to use a portal. Needs the entity in scope. Probably clean, not yet read. |
| `ELYTRA_MOVEMENT_CHECK`, `PLAYER_MOVEMENT_CHECK` | bool | **Player-specific.** Both are read in `ServerGamePacketListenerImpl.shouldCheckPlayerMovement`, which has `this.player`. |
| `NATURAL_HEALTH_REGENERATION` | bool | **Player-specific.** `FoodData.tick(player)` and a peaceful-difficulty branch in `ServerPlayer`. Two sites. |
| `LIMITED_CRAFTING` | bool | Login-packet rule (like `IMMEDIATE_RESPAWN`), plus a game-event packet. Same client-sync mechanism. |
| `UNIVERSAL_ANGER` | bool | **Ambiguous, not "definitely."** See below. |

### `UNIVERSAL_ANGER`

The rule does not decide whether anger "applies to" a player. It decides whether a neutral mob that has
been angered by *one* player turns hostile to *every* nearby player, instead of only the provoker. It's read in
`NeutralMob.isAngryAtAllPlayers`, `HurtByTargetGoal`, `ResetUniversalAngerTargetGoal`, `Piglin`, and
`PiglinAi`. In `HurtByTargetGoal`, the player in the check is the *attacker*.

So there are two ways to give it a per-player meaning:

- **By provoker:** "when *this player* angers a mob, the mob turns on everyone." That changes what
  happens to *other* players, so it breaks "subject only".
- **By target:** "when someone else angers a mob, it never turns on *this player*" (or always does). That
  only changes what happens to the kid, so it fits. The mob's target selection would need a hook that
  asks "may this mob be universally angry at this player?" That is a new decision point in the mob AI,
  not a swap of a rule read, so it's a bigger job than the rest of this list.

Neither is "definitely" until the by-target hook is looked at. I haven't read the full mob targeting code,
only the rule's read sites.

### Client-synced rules

`IMMEDIATE_RESPAWN`, `REDUCED_DEBUG_INFO`, and `LIMITED_CRAFTING` are sent to the client at login in the
login packet, with matching game-event packets sent when the rule changes. A per-player override means:

- Use the override when building that player's login packet (a wrapper on those three reads).
- When an override changes, send that player the matching packet.
- When the world rule is changed with `/gamerule`, the server's broadcast would reset everyone to the
  world value, so overrides must be re-sent afterwards. *(The broadcast lives in `MinecraftServer`; I
  haven't read it, so this is inferred from the rule showing up there.)*

These three share one mechanism, which is why they're grouped.

### Out of scope

Per-player difficulty (a possible later enhancement). `RAIDS` (dropped: not player-specific). `PVP` (pairwise; deferred). `SPAWN_PATROLS`,
`SPAWN_WANDERING_TRADERS`, `SPAWN_WARDENS`: not checked, and likely affect other players. Everything
else (time and weather advancing, random tick speed, spawn caps, TNT and explosion rules, fluid
conversion, command-block and spawner rules, minecart speed, block and mob drops, `MOB_GRIEFING`,
`SPAWN_MOBS` and its siblings) is world-level or affects other players.

## Decisions

- 2026-10-28 — v1 is `KEEP_INVENTORY`, `IMMEDIATE_RESPAWN`, `DROWNING_DAMAGE`, `FALL_DAMAGE`,
  `FIRE_DAMAGE`, `FREEZE_DAMAGE`, `SPAWN_PHANTOMS`, `SHOW_ADVANCEMENT_MESSAGES`, and `SHOW_DEATH_MESSAGES`.
- 2026-10-28 — `SEND_COMMAND_FEEDBACK`, `RESPAWN_RADIUS`, the two portal delays, and `REDUCED_DEBUG_INFO`
  are in v1 only if easy.
- 2026-10-28 — `LOCATOR_BAR` and `ENDER_PEARLS_VANISH_ON_DEATH` are "maybe".
- 2026-10-28 — The user's former "unsure" rules move to "maybe", except any that research shows aren't
  truly player-specific (see the `RAIDS` entry below).
- 2026-10-28 — `UNIVERSAL_ANGER` stays in "maybe" under the by-target reading only (a mob angered by
  someone else never turns on this player), accepting the extra mob-AI hook cost.
- 2026-10-28 — `RAIDS` is dropped as not player-specific.
- 2026-10-28 — The owner's `SHOW_DEATH_MESSAGES` override applies to their tamed animals' death messages.
- 2026-10-28 — The rule-change broadcast in `MinecraftServer` is read when the client-synced rules are
  built, not before.
- 2026-10-28 — The data model supports integer values as well as booleans in v1.

## Brainstorm variants

_No brainstorm yet._

## Related

- [Per-player game rule overrides](per-player-rule-overrides.md) — the mechanism.
- [Grave-mod compatibility](grave-mod-compatibility.md) — shares the death-time subject scope with
  `KEEP_INVENTORY`.

## Open questions

_None._
