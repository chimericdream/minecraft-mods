---
name: mc-cut-release
description: Step-by-step procedure for cutting a tagged release (beta or final) of one mod in this monorepo — set mod_version, retitle the CHANGELOG heading, run scoped datagen, commit, and tag. Use when the user asks to "cut a release", "tag a release", "release <mod>", "cut a beta", or "publish <mod>". Do NOT use for "bump" / "bump the version" - that only means editing mod_version in gradle.properties.
---

# Cutting a release

The detailed bump/changelog rules live in
[docs/agents/releases/versioning.md](../../../docs/agents/releases/versioning.md) — this skill is the
ordered checklist that ties them together so no step gets skipped or done out of order. If this skill
and that doc ever disagree, the doc wins; update this skill to match rather than trusting stale memory
of it.

## 0. Confirm scope before touching anything

- **Is this actually a release request?** The word "bump" (e.g. "bump opus to the next beta") means
  *only* update `mod_version` in `gradle.properties` (per the next-pre-release rules in
  versioning.md) - no dated changelog heading, no datagen run, no release commit, no tag. Run this
  skill only for explicit release language: "cut a release", "tag", "release <mod>", "cut a beta",
  "publish". If it's ambiguous, ask.

- **Which mod?** One project folder = one release; releases are never cross-mod.
- **Final or beta?** "Cut a release" with no qualifier means **final** — bump straight to `a.b.c` even
  from a `-beta.x` state, don't tag the beta first. Only cut a `-beta.x` tag if the user explicitly asks
  for a beta/pre-release.
- Run `bun run status --mods=<mod>` to see the mod's current `mod_version` and whether it actually has
  unreleased changes. If `### Unreleased changes` is empty, stop and confirm with the user — there is
  nothing to release.

## 1. Determine the target version

Read the mod's `### Unreleased changes` section in `CHANGELOG.md` and classify what's in it:

- Only bug fixes → patch bump (`x.y.(z+1)`).
- Any new feature → minor bump (`x.(y+1).0`).
- A patch-level target can escalate to minor mid-cycle if a feature lands later — never the reverse.

Then apply the tagged-vs-untagged state from `mod_version` itself (full rules and worked examples in
versioning.md):

- Already sitting on a `-beta.x` pre-release of the version you're targeting → for a beta release, bump
  to `-beta.(x+1)`; for a final release, drop the suffix entirely (`a.b.c`).
- Sitting on a final-tagged version already → compute the next patch/minor target per the bullets above,
  suffixed `-beta.0` for a beta release or bare for a final one.

## 2. Run datagen scoped to this mod

```
bun run datagen --mods=<mod>
```

This is a **required safety check**, not optional — a tagged release must ship data generated from the
code at that commit, not a stale earlier run. Check `git status` for changes under
`<mod>/*/src/main/generated` and stage them alongside the release commit.

## 3. Update `gradle.properties`

Set `mod_version` to the target from step 1.

## 4. Update `CHANGELOG.md`

Rename `### Unreleased changes` to `### <minecraft_compat> - <mod_version>` (matching the tag you'll
create in step 6, e.g. `### 26.2 - 6.1.0`), then insert a fresh, empty `### Unreleased changes` heading
above it for the next cycle.

Player-facing changelog prose must stay concise and non-technical (chimeric-lib is the one technical
exception — see versioning.md).

## 5. Update `README.md` — always, on every release

Always bump the version badge at the top (`Version: x` and the `version-x-` shields.io URL, with `-`
escaped as `--`) to the new `mod_version`, and also update anything else the README documents that
changed (feature list, supported versions, setup steps, public API surface). Never skip this step.

## 6. Commit

Run `git status` first (this repo routinely has other unrelated work staged/in progress) and commit by
**explicit pathspec** — the mod's `gradle.properties`, `CHANGELOG.md`, `README.md` if touched, and any
`src/main/generated` changes from step 2 — never a bare `git commit`.

## 7. Tag — confirm with the user before this step

Tag format matches the folder name and the changelog heading, e.g. `sponj/26.2-6.1.0`,
`chimericlib/26.2-6.0.0`, `villager-tweaks/26.2-6.2.0-beta.1`:

```
git tag <project-folder>/<minecraft_compat>-<mod_version>
```

**Tagging (and especially pushing a tag) is hard to reverse and affects shared state — always show the
user the exact tag name and ask before running `git tag`, and never push tags without a separate,
explicit go-ahead**, even if they approved the commit in the same breath. This repo's CI (`build.yml`)
runs on every push but does not publish anything on tag push.

## 8. Publish to CurseForge (only for mods with `curseforge_project_id`) — confirm first

Uploading is outward-facing and can't be undone, so always ask before the real run. Check out the tagged
commit, then dry-run and show the user the output:

```
./gradlew :<mod>:fabric:publishCurseforge :<mod>:neoforge:publishCurseforge -Ppublish_dry_run
```

The real run is the same command without `-Ppublish_dry_run` (needs `curseforge_token` in
`~/.gradle/gradle.properties`). Setup details are in the "CurseForge publishing" bullet of
[versioning.md](../../../docs/agents/releases/versioning.md). Modrinth is not set up.
