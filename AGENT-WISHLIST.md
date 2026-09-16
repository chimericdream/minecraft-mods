# Agent wishlist

Things that would make coding agents (Claude Code included) more productive in this repo. Written from
the agent's side — these are asks *of you*, not a task list for the codebase itself. Prune entries once
they're addressed.

## 1. The datagen jar-path env vars are global, not per-worktree

`chimericdream.datagen.minecraft-jar-path` and `<modid>.datagen.resource-path` are set at the OS/user
level and get read by `chimeric-lib`'s `JarAccess`/`TextureGenerator`. When more than one
worktree/branch is in play on this machine (e.g. `main` vs. `minecraft-mods-26.1.2`), whichever one set
them last silently wins for every other worktree's datagen runs — and a missing/renamed texture under
the wrong path fails **silently** (a logged warning, not an error), so a datagen re-run can look clean
while quietly producing zero output for a renamed asset. This already caused a real shipped bug
(minekea's compressed Purpur Pillar missing a side texture after 26.1.2→26.2).

**Ask**: if it's easy, make these resolvable per-worktree (e.g. derived from the checkout path instead
of a fixed global default) rather than a single machine-wide value — it removes a whole class of
silent-failure risk when working across branches/worktrees in parallel.
