# Versioning & releases

- **Before cutting any release (tagging a beta or final version), run `bun run datagen`** scoped
  to the mod being released (e.g. `bun run datagen --mods=<mod_id>`) and commit any resulting
  changes under `<mod>/*/src/main/generated`. This is a required safety check — released jars must
  ship data generated from the code at the tagged commit, not from whatever the generated files
  happened to contain from an earlier run. See [datagen commands](../commands/dev-workflow.md) and the
  gradle.properties flags rule there.
- **Day-to-day commits between releases do not get their own dated changelog entry or release
  version number.** Nothing accumulating under `### Unreleased changes` is published anywhere until a
  release is explicitly cut. What *does* need to stay current between releases is `mod_version` itself
  — see the next two bullets — since it's what tells you, at any commit, what the next release would
  be if cut right now.
- **Official releases are tagged in git** (e.g. `chimericlib/26.2-6.0.0`, `minekea/26.2-10.0.0`,
  `chimericlib/3.1.0-beta.1` — see `git tag --list`). A pre-release (`-beta.x`) tag counts as a real
  release just as much as a final one — the distinction that matters is tagged vs. untagged, not
  beta vs. final. A mod's current `mod_version` therefore reflects one of these states:
  - **At the tagged commit itself**: the exact released version, matching the tag (e.g. `6.0.0`, or
    `3.1.0-beta.1`).
  - **Continuing pre-release iterations of a target whose most recent tag was itself `-beta.x`**: the
    same `x.y.z`, suffixed `-beta.<x+1>` (e.g. after tagging `3.1.0-beta.1`, `mod_version` becomes
    `3.1.0-beta.2`). The target version doesn't change here — the prior tag already committed to
    `x.y.z` as the eventual release, this just continues iterating toward it.
  - **Otherwise, mid-cycle after a final tag (or after the target escalates — see below)**: the next
    anticipated pre-release version, chosen by what has actually accumulated under `### Unreleased
    changes` so far:
    - Only bug fixes so far → a patch bump: `x.y.(z+1)-beta.0`.
    - Any feature present → a minor bump: `x.(y+1).0-beta.0`.
  - **This target can escalate mid-cycle.** If `mod_version` is currently a patch-level pre-release
    (`x.y.(z+1)-beta.0`) from fixes only, and a feature then gets committed, re-target it up to the
    minor-level pre-release (`x.(y+1).0-beta.0`) instead — e.g. `6.0.0` → (first fix) `6.0.1-beta.0` →
    (later feature) `6.1.0-beta.0`. Once escalated to minor for a cycle, further fixes or features in
    that same cycle don't downgrade it back to patch.
- **When asked to "cut a release" with no other qualifier, that means a final release.** If
  `mod_version` is currently `a.b.c-beta.x`, bump it straight to `a.b.c` (update `mod_version`, retitle
  the changelog heading, tag `a.b.c`) — do not tag the beta first and promote it in a separate step.
  Only cut an actual beta tag (`-beta.x`) when the user explicitly asks for a beta/pre-release.
- **Check `mod_version` every time you touch `CHANGELOG.md`, not on a separate cadence.** Whenever a
  commit adds an entry under `### Unreleased changes` (see the changelog-update rule below), also check
  whether `gradle.properties`' `mod_version` already reflects the correct next pre-release version per
  the rule above. If it does, no action is needed. If not, bump it in the same commit — using the
  patch/minor/escalation logic above, based on what's now in `### Unreleased changes` taken as a whole
  (not just the entry you're adding). Don't increment the pre-release number for any other reason —
  e.g. never bump per-commit or per-session once `mod_version` already matches the correct target.
- **Changelog structure follows the same split.** Each mod's `CHANGELOG.md` accumulates all untagged
  work under a single `### Unreleased changes` heading at the top (with the usual `#### New
  Features`/`#### Bug Fixes`/`#### Changes` subheadings) — not a new dated/versioned heading per
  commit or session. When a release is cut (beta or final), `### Unreleased changes` is renamed to a
  dated `### <mc_version> - <version>` heading (matching the git tag) and a fresh, empty `###
  Unreleased changes` starts collecting the next round.
- **Group related Unreleased entries instead of appending each as its own top-level bullet.** When a
  single work session adds several related items in the same category (e.g. a handful of new trim
  materials, or a batch of set-bonus effects tied to those materials), nest them under one descriptive
  bullet rather than writing N separate top-level bullets — e.g. "Added new armor trim materials:"
  followed by an indented list of the materials, and a separate "Wearing a full set of armor trimmed
  with the same material grants a bonus ..." bullet with its own nested list of bonuses. Still keep the
  `#### New Features`/`#### Bug Fixes`/`#### Changes` top-level structure — this is about grouping
  *within* those sections, not replacing them. Unrelated one-off changes still just get their own
  top-level bullet.
- **Changelog/README tone**: player-facing docs (changelogs, READMEs) must be concise and
  non-technical — the audience is Minecraft players, not programmers. Editing test: for each
  sentence, if removing it still conveys the information accurately, delete it. **Exception:
  chimeric-lib** — it's a shared library consumed by other mods, so its changelog/README audience
  is developers; stay concise but technical detail (API names, method signatures, behavior) is
  appropriate there.
- **Before committing, check whether the affected mod's `CHANGELOG.md` and/or `README.md` need
  updating.** A changelog entry belongs under that mod's `### Unreleased changes` heading (see
  above) whenever the commit changes player-visible behavior (new feature, bug fix, balance/behavior
  change) — a pure internal refactor with no behavior change (e.g. hand-written files replaced by
  equivalent datagen output) does not need one. A README update belongs alongside any change to
  something the README documents (a feature list, supported versions, setup steps, public API
  surface for chimeric-lib, etc.). Skip either file when nothing it covers actually changed — don't
  add an entry just to have one. **Whenever this adds a `CHANGELOG.md` entry, also run the
  `mod_version` check** from the versioning rules above in the same commit.
