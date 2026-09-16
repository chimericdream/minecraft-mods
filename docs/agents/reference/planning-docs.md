# Planning & reference docs index

- [docs/NEOFORGE.md](../../NEOFORGE.md) / [docs/FABRIC.md](../../FABRIC.md) — confirmed loader-specific
  runtime/build gotchas (mixins, renderer registration, etc.).
- [docs/TESTING.md](../../TESTING.md) — how tests are wired and run (JUnit bootstrap, GameTest harness,
  testFixtures).
- [DEPENDENCY-PLAN.md](../../../DEPENDENCY-PLAN.md) (repo root) — how chimeric-lib is wired as an
  in-build project dependency (no publish loop) and the remaining monorepo build-structure
  improvements.
- [docs/MC-26.2-NOTES.md](../../MC-26.2-NOTES.md) — MC 26.2 port gotchas: datagen component binding,
  API renames, reading decompiled vanilla source, the shutdown-watchdog false crash, and
  build/datagen/GameTest tasks that hang after finishing.
- [docs/BLOCK-MIGRATION.md](../../BLOCK-MIGRATION.md) — non-breaking block/item deprecation & rename
  across both loaders (no DataFixerUpper).
- [CODE-REVIEW-PLAN.md](../../../CODE-REVIEW-PLAN.md) (repo root) — phased code-review plan. Phase 1
  (critical bugs) done on unmerged `fix/*` branches; Phase 2+ not started.
- [UPDATE-PLAN.md](../../../UPDATE-PLAN.md) (repo root) — the Yarn→Mojang + MC 26.2 update runbook
  (migration now complete).
- Per-mod `TEST_PLAN.md` and `POTENTIAL_FEATURES.md` — testing plans and feature backlogs.
