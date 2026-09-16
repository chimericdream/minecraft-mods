# Git commits

- **This repo routinely has multiple unrelated efforts staged/in-progress at once** (different mods,
  scaffolding output, WIP from earlier in the session). Before running any `git commit`, always run
  `git status` (and `git diff --cached` if anything looks unfamiliar) right before committing and
  confirm the staged set matches only what was actually asked for — don't assume the index reflects
  just the current task. If unrelated files are already staged, commit by explicit pathspec (`git
  commit <path> <path> ...`) rather than a bare `git commit`, so the unrelated staged changes are left
  staged/untouched for their own commit later instead of being swept in.
