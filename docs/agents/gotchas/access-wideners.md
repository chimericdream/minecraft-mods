# Access wideners are build-time-only outside `common`

Each mod that needs one has a **canonical, always-committed** `<mod_id>.accesswidener` in
`<mod>/common/src/main/resources/`, wired into Loom via `accessWidenerPath` in **both**
`common/build.gradle` and `fabric/build.gradle` (the fabric block reads it off the common project,
e.g. `accessWidenerPath = project(":minekea:common").loom.accessWidenerPath`). That wiring alone is
enough for IDE/dev compilation — nothing else is required to edit or build a mod day-to-day.

`fabric.mod.json`'s `"accessWidener"` field and a **copy** of the file into
`<mod>/fabric/src/main/resources/` are added **only transiently**, by `bun run build`'s `prebuild` step
(`copy:accesswideners`), and removed again by `postbuild` (`teardown:build` /
`scripts/revert-fabricmodjson.ts`) once the build finishes. This copy is needed for the *packaged
runtime jar* (Fabric reads the AW path from `fabric.mod.json`, not from Loom's dev-time config), but
having it declared in both `common` and `fabric` at once — as it would be if these were left
committed — causes "duplicate accessWidener" errors in the IDE. That's why the scripts add it right
before a full build and strip it right after, instead of just committing it once.

**Practical implication**: `<mod>/fabric/src/main/resources/<mod_id>.accesswidener` and a
`fabric.mod.json` with an `"accessWidener"` line should **never** be sitting in a commit. If you see
either — e.g. because `bun run copy:accesswideners` was run by hand while debugging (its normal
callers are `prebuild`/`bun run build`, not something you'd invoke standalone) and the resulting files
got swept up in a `git add` — delete the copied `.accesswidener` file and remove the `"accessWidener"`
line from `fabric.mod.json` before committing. This is easy to forget since nothing about it looks
wrong at a glance (the mod still compiles and builds fine either way).
