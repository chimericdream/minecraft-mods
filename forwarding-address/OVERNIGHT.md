# Overnight report (delete after reading)

Branch `forwarding-address`, off `main`. Nothing pushed. Stage 0 and Stage 1 are done; Stage 2 and the
registration spike were not started, as agreed.

## State

* `./gradlew :forwarding-address:fabric:test` — 29 unit tests pass.
* `./gradlew :forwarding-address:fabric:runGameTest` — 20 GameTests pass.
* `./gradlew :forwarding-address:fabric:build :forwarding-address:neoforge:build` — both build; the
  Fabric jar contains no test code. `bun run lint` and `bun run verify:gametests` are clean.
* **NeoForge was compiled but never run.** The chunk-load, tooltip, and creative-tab hooks there are
  untested; `TEST_PLAN.md` has the manual checklist.

## What I decided without you (all logged in the idea file's Decisions)

* Chunk detection uses each loader's *post-load* event, not Architectury's `LOAD_DATA` (fires too early).
* A block migration implies a migration for its item; an explicit item migration wins.
* Declaring migrations is load-order independent: early declarations are queued. A GameTest failed
  intermittently on exactly this, so it was a real bug, not a test quirk.
* Duplicate declarations are logged and ignored rather than throwing.
* Version set to `1.0.0-beta.0` (the template gives `1.0.0`).

## Things to look at

* **Every container in every loaded chunk is queued for an item scan** once any migration exists
  (block migrations imply item migrations). Each costs one slot of the per-tick budget. Fine for normal
  worlds; worth a look if you expect huge chunk counts. A cheaper option is to scan containers only when
  an *explicit* item migration exists.
* `scripts/update-settingsgradle.ts` uses a stale template: running `init-mod.sh` reverted
  `settings.gradle`'s `enabled_platforms` logic and maven URLs. I restored the file and added the project
  line by hand. The script template (`scripts/settings.gradle.tpl`) needs the same update or the next
  scaffold will do it again.
* I wrote several files with Python on Windows, which produced CRLF and failed `bun run lint`. Fixed.
  Not a repo problem, but worth knowing if you see `dos2unix` hints.
* The GameTest log shows a Yggdrasil/SSL error (`PKIX path building failed`). It's the game's auth
  check failing on this machine, not related to the mod, and the tests pass regardless.

## Not done

* Hopper X-Treme migration onto this mod (separate backlog item).
* Stage 2: JSON loader, stub registration, duplicate guard, dump command, and the prototype spike.
* The manual Hopper X-Treme / NeoForge / dedicated-server checks.
