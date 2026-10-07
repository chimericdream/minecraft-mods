# Planning & reference docs index

- [docs/NEOFORGE.md](../../NEOFORGE.md) / [docs/FABRIC.md](../../FABRIC.md) — confirmed loader-specific
  runtime/build gotchas (mixins, renderer registration, etc.).
- [docs/TESTING.md](../../TESTING.md) — how tests are wired and run (JUnit bootstrap, GameTest harness,
  testFixtures).
- [docs/history/DEPENDENCY-PLAN.md](../../history/DEPENDENCY-PLAN.md) — how chimeric-lib came to be wired
  as an in-build project dependency (no publish loop). Finished; kept for the rationale.
- [docs/history/CROSS-MOD-PATTERN-AUDIT.md](../../history/CROSS-MOD-PATTERN-AUDIT.md) (+ `-BUGS`) — the
  2026-09 audit of how mixins, datagen, rendering, tests, and registration differ across mods; open
  findings are GitHub Issues labeled `tech-debt`. Useful as a record of what was already consistent.
- [docs/MC-26.2-NOTES.md](../../MC-26.2-NOTES.md) — MC 26.2 port gotchas: datagen component binding,
  API renames, reading decompiled vanilla source, the shutdown-watchdog false crash, and
  build/datagen/GameTest tasks that hang after finishing.
- [docs/BLOCK-MIGRATION.md](../../BLOCK-MIGRATION.md) — non-breaking block/item deprecation & rename
  across both loaders (no DataFixerUpper).
- [docs/history/UPDATE-PLAN.md](../../history/UPDATE-PLAN.md) — the Yarn→Mojang + MC 26.2 update runbook
  (migration complete; kept for reference).
- [docs/FEATURE-WORK.md](../../FEATURE-WORK.md) — how feature ideas are captured, brainstormed, voted on,
  and refined until ready to build, for every mod.
- Per-mod `TEST_PLAN.md` (testing plans) and `ideas/` folders (feature ideas; start at `ideas/README.md`).
- [opus/HANDOFF.md](../../../opus/HANDOFF.md) — status of the new Opus (Markdown in-game books) mod: what is
  tested, what has never been compiled, and the first steps for the next session. Delete when done.
  [opus/AGENT-HANDOFF.md](../../../opus/AGENT-HANDOFF.md) is the agent-oriented companion (architecture,
  invariants, confirmed API sources, sandbox gotchas).

## Where open work is tracked

Open work lives in [GitHub Issues](https://github.com/chimericdream/minecraft-mods/issues), not in root
Markdown files. Labels: `build` (tooling/CI), `ideas` (feature-ideas pipeline), `meta`, plus the per-mod
labels and `P1`–`P3`. Don't add new ALL_CAPS planning files at the repo root; file an issue, or put a
finished design in `docs/history/`.
