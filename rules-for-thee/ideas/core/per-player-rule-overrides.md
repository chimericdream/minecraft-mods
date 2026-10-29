# Per-player game rule overrides

> Source: user's original request, 2026-10-28 · Effort **L** · Status: **Building** (branch `rules-for-thee-ideas`; v1 implemented, pending manual verification in `rules-for-thee/TEST_PLAN.md`)

## Description

Let a server operator override a game rule for specific players. Everyone without an override sees the
world's normal value.

**Motivating example:** the server runs on hard with `keepInventory` off. The operator's young children
play on it, and only they should keep their inventory on death. Adults die and drop everything as usual.

**Shape:**

- **Server-side only.** Vanilla clients can join. Rule state is shown through chat and commands, never a
  custom client UI.
- **Individual overrides and profiles both.** A player can have overrides set directly on them, and/or be
  a member of a named **profile** (e.g. `kids` = `{ keepInventory: true }`). Resolution order for a rule
  read on behalf of a player: individual override, then profile, then the world's value.
- **Either direction.** An override can loosen or tighten a rule. Lenient is the expected common case.
- **Subject only.** An override only changes what happens to or for that player. It never changes what
  happens to other players nearby.
- **Pinned.** Overrides are independent of `/gamerule`. Changing the world value later doesn't touch them.
- **Commands only.** Managed by operator commands (`/rulesforthee ...`) and persisted in world saved
  data. No config screen.
- **Death handling:** v1 works with vanilla death handling. Compatibility with "grave" mods is a goal
  right after (see Open questions), with Serilum's *Your Items Are Safe* as a required target.

**Which rules:** see [supported rules](supported-rules.md) for the catalog, checked against 26.2's rule
registry. Only rules that are about the player (damage taken, death, respawn, the player's own HUD or
movement) qualify. World-level rules stay global.

**Out of scope:** per-player *difficulty* (reduced damage, easier mobs). Possibly a later enhancement,
not part of v1. Also out of scope: any config screen and any custom client UI.

## Decisions

- 2026-10-28 — Server-side only; vanilla clients must be able to join.
- 2026-10-28 — Support both individual per-player overrides and named profiles.
- 2026-10-28 — Overrides can be more lenient or stricter than the world value.
- 2026-10-28 — Overrides affect only the player in question, never others nearby.
- 2026-10-28 — Overrides stay pinned when the world's `/gamerule` value changes.
- 2026-10-28 — Per-player difficulty is not in v1; maybe a later enhancement.
- 2026-10-28 — No config screen; commands plus saved world data.
- 2026-10-28 — v1 targets vanilla death handling; grave-mod compatibility follows, with Serilum's
  *Your Items Are Safe* as a personal requirement.
- 2026-10-28 — One profile per player in v1, but the data model must not block several profiles later.
- 2026-10-28 — Offline players are targeted by name and stored by UUID.
- 2026-10-28 — Commands are op-only (gamemaster level, like `/gamerule`).
- 2026-10-28 — Players are told in chat the first time they log in after their effective rules changed,
  and can also run a command to see them.
- 2026-10-28 — Grave-mod compatibility is a research task: [grave-mod compatibility](grave-mod-compatibility.md).
- 2026-10-28 — Integrations are optional; active only when the other mod is installed, never a hard dependency.
- 2026-10-28 — If a player's effective rules change while they're online, tell them immediately; if
  offline, tell them at next login.
- 2026-10-28 — Profiles are stored as an ordered list per player and read through a single "effective
  rules" lookup, but v1 limits the list to 0 or 1 entries. Multiple profiles (priority order, etc.) are
  deferred.
- 2026-10-28 — v1 ships on top of a general lookup mechanism. The rule list is in
  [supported rules](supported-rules.md): `keepInventory`, immediate respawn, and the drowning, fall, fire
  and freeze damage rules, phantom spawning, and advancement and death messages. This replaces an
  earlier POC list.
- 2026-10-28 — Rule values are typed: the data model, storage, and commands support booleans and
  integers from v1.

## Build notes

- **Effort: L.** Re-estimated now that scope is fixed. It adds no entity, rendering, or UI, but it needs
  saved data with typed values, a command tree (profiles, players, offline lookup), a notification
  system, about ten vanilla hook sites, and a packet-resend path for client-synced rules. The rubric's XL
  ("touches many other features") applies to the full rule catalog; v1 is bounded by the rule list in
  [supported rules](supported-rules.md). Rough shape: the core (storage, lookup, commands, notifications) is
  M, most rules are S each, and `SPAWN_PHANTOMS` and the client-synced rules are M each.
- **Dependencies.** chimeric-lib already has `commands/` (`ChimericCommand`, `ChimericCommands`,
  `PlatformCommandArgumentTypes`) and a `mixin/` package, so command registration and mixins have a
  home. Saved data is not in chimeric-lib today; it would be mod-local unless a second mod needs it.
  Player-join and player-quit notification hooks are loader-specific (Fabric and NeoForge). Hook sites are
  vanilla only. Optional integrations (*Your Items Are Safe*) stay out of v1; see
  [grave-mod compatibility](grave-mod-compatibility.md).
- **Verification.** GameTests for `KEEP_INVENTORY` and the four damage rules (a mock player with and
  without an override, same world rule); JUnit for the effective-rules lookup and typed values; manual
  steps in the mod's `TEST_PLAN.md` for the client-synced rules (login packet and mid-session resend),
  phantoms, and chat notifications.
- **Implementation can decide:** the rule-change broadcast in `MinecraftServer` is read when the
  client-synced rules are built; the first-login-after-change tracking format.

## Brainstorm variants

_No brainstorm yet._

## Related

- [Supported rules](supported-rules.md) — the catalog of rules and their hooks.
- [Grave-mod compatibility](grave-mod-compatibility.md) — research on hooking *Your Items Are Safe* and
  similar mods.

## Open questions

_None that block the idea. Remaining unknowns live in [grave-mod compatibility](grave-mod-compatibility.md)._
