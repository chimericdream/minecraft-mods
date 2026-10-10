# Flat Bedrock — Ideas

Feature ideas for this mod, from first thought to ready to build. The process is described in
[`docs/FEATURE-WORK.md`](../../docs/FEATURE-WORK.md).

## Identity

> A **predictable floor (and roof)**. It should stay a worldgen tweak — small, boring in the best way, and server-side only.

## Active ideas

No active ideas yet.

## Inbox

Folded in from the old backlog in [`brainstorms/2026-08-13-legacy/`](brainstorms/2026-08-13-legacy/), de-duplicated:

- Configurable bedrock thickness (1–5 layers).
- Independent floor and roof toggles, plus a "no roof" option that removes the nether ceiling bedrock.
- A configurable filler layer (deepslate, obsidian, blackstone) behind the flat bedrock, so the old
  transition band isn't empty.
- Per-dimension settings, with a wildcard rule for modded dimensions that use vanilla-style bedrock.
- Datapack-driven rules, so packs can target custom dimension types.
- `/flatbedrock retrogen`: an opt-in, radius-limited command (with confirmation and a backup warning) to
  flatten chunks that were generated before the mod was installed.
- A dry-run chunk scan that reports how many generated chunks still have non-flat bedrock.
- Check, and document, how mods and datapacks that place features in the old bedrock band (Y -60 to -64)
  behave with it.
- Check, and document, behavior on superflat and amplified world presets.

Non-goals from the old notes: no new blocks, no client requirement, no bedrock-breaking mechanics.

## Archive

_Empty._
