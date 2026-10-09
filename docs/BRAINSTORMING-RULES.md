# Brainstorming rules

Rules for proposing features in **any** mod in this repo. They apply on top of each mod's identity
statement (in `<mod>/ideas/README.md`), which covers what fits one particular mod. These rules cover
what's never or always acceptable anywhere.

This list grows over time. Only the user adds, changes, or removes a rule. Agents can suggest one when a
pattern keeps coming up, but it doesn't count until the user approves it.

## How the rules are used

- `ideas-brainstorm` copies every rule into the session's `prompt.md`, so every brainstormer sees them.
- `ideas-shortlist` cuts any idea that breaks a rule, citing the rule's ID (e.g. "breaks N1") in
  "What was cut and why".
- When refining an idea file, check it against the rules again before asking the user to mark it Ready.
- An idea that only works if it breaks a rule is a question for the user, not something to quietly
  reshape or drop.

Each rule has a stable ID. Never renumber: retired rules keep their ID, with a strikethrough and the
reason.

## Never

- **N1. Never change a player's existing builds or world without an opt-out.** No seasonal recoloring,
  weather that alters blocks, or anything else that changes what a player built or placed, unless the
  player can turn it off.
  *Why:* a player's build is theirs. A mod that repaints it without asking makes them afraid to
  install it. *(Added 2026-09-29)*
- **N2. Never add chores.** Mechanics that make players maintain things (decay, rot, natural build-up,
  repair) must be opt-in or so slow they rarely matter.
  *Why:* upkeep punishes decorative builds and turns a feature into a tax. *(Added 2026-09-29)*
- **N3. Never duplicate what vanilla already does.** If vanilla already has the block, mechanic, or
  particle, don't propose it again. Extending or varying it is fine.
  *Why:* duplicates confuse players and add maintenance for nothing. *(Added 2026-09-29)*

## Always

- **A1. Always prefer deepening what the mod already does.** Rank ideas that build on the mod's
  existing systems above ones that spread it into another mod's territory.
  *Why:* depth keeps each mod coherent, and sideways growth turns a small mod into a worse copy of a
  bigger one. *(Added 2026-09-29)*
- **A2. Always make balance-affecting mechanics configurable.** If a feature changes gameplay balance
  (difficulty, economy, progression, mob behavior), it needs a config option to turn it off or tune it.
  *Why:* servers and modpacks balance things differently, and a hard-coded change can rule a mod out
  of a pack. *(Added 2026-09-29)*
- **A3. Always keep accessibility in mind.** A feature shouldn't depend on one sense, one input method,
  or one level of ability. Anything that blocks or distorts the view, relies on color or sound alone,
  or needs precise timing or input needs an alternative or a setting to reduce it.
  *Why:* a mod should be playable by everyone who installs it, and it is far cheaper to design for
  this up front than to patch it in later. *(Added 2026-10-02)*
- **A4. Always consider whether a feature or value should be configurable.** For every feature,
  number, and toggle an idea introduces, say whether it should be a config option. Fundamental values
  that define what the thing *is* (e.g. the tick cooldown of Hopper X-Treme's tiered hoppers) can stay
  fixed. Values that are a matter of taste or play style (e.g. the settings in Villager Tweaks) should
  be configurable. When an idea leaves something fixed, note why. A2 still applies on top: balance-affecting
  mechanics must be configurable no matter what.
  *Why:* players, servers, and modpacks differ, and deciding up front what is tunable is cheaper than
  adding options after complaints. Not everything should be a knob, though. Too many options make a mod
  harder to understand and test. *(Added 2026-10-09)*
