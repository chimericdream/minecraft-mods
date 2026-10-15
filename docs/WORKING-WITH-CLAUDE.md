# Working with Claude in this repo — what helps, what hurts

_Drafted 2026-10-15 from what I observed in this repo (setup files, memory index, scripts, worktrees) while
working on the overnight tasks. Nothing here is a complaint: the repo is already unusually agent-friendly
(layered `docs/agents/`, skills, verification scripts). These are the next-percent improvements, plus the
anti-patterns to watch for. Each item has a rough **effort** (you) and **payoff**._

## Part 1 — Things you can do to make me more effective

### A. Tighten the feedback loop (largest payoff)

| # | Idea | Effort | Payoff |
| --- | --- | --- | --- |
| 1 | **A single "is this change OK?" command.** e.g. `bun run verify -- --mods=<mod>` = scoped build + JUnit + `verify:gametests` + datagen-drift check (`git diff --exit-code` on `*/src/main/generated`) + metadata lint. Right now those are separate commands I have to remember and sequence; one command with one exit code makes "done" unambiguous. | M | High |
| 2 | **Fast-path build for a single mod**. Document (and script) the quickest compile-only check (`./gradlew :<mod>:fabric:compileJava`?) vs. the full `bun run build` which runs `clean` first. A 20-second check vs. multi-minute full build changes how often I verify. | S | High |
| 3 | **Make the gametest/client-test runs headless-by-default with a one-line result summary** (pass/fail + failing test names, not 2000 lines). Shutdown hangs are documented in memory; a wrapper that kills the process when the result file is written removes that class of waste. | M | High |
| 4 | **Metadata lint in CI** (fails on `fabric-example-mod`, `fabricmc.net` homepage, missing `logoFile`, mismatched `mod_id`). I found ~35 stale/placeholder URLs that nothing caught. Checks like this are free guardrails for me and you. | S | Medium |
| 5 | **Datagen drift check in CI** (`datagen` then `git diff --exit-code`). Your own release rule says generated data must match code; let CI enforce it, instead of relying on remembering. | M | Medium |
| 6 | **Per-worktree datagen/env paths** (the existing open item): `chimericdream.datagen.*` env vars are machine-global, so I must verify them every run. Reading them from a git-ignored `local.properties` in each worktree, defaulting to the worktree's own paths, eliminates a recurring trap. | M | High |

### B. Permissions & autonomy (this matters most for "overnight" sessions like this one)

- **Pre-decide the AFK envelope** in a checked-in `docs/AFK-MODE.md` (or in `AGENTS.md`): _may_ create branches,
  commit on a topic branch, run builds/tests/datagen/screenshots, write docs; _may not_ push, tag, publish, delete
  branches/worktrees, touch `main` directly. Then I don't have to guess whether to commit tonight's work, and I can
  stop worrying about whether being thorough is overstepping. (Tonight I defaulted to the cautious end: wrote
  files, committed nothing.)
- **Expand the shared allowlist** (`.claude/settings.json` currently allows gradlew, bun run, read-only git).
  Run `/fewer-permission-prompts` after a few sessions. Candidates: `git add/commit` scoped paths on topic
  branches, `gh issue/pr view|list`, `bun run` anything, `Read`/`Glob`/`Grep` in `.sources/`, PowerShell
  read-only cmdlets. An AFK session stalls on the first unapproved prompt; allowlist gaps are the #1 reason
  overnight runs end early.
- **`gh` auth in the agent's environment**, so I can read/triage issues (`#138`, `#139` are referenced in commits)
  without asking.
- **Notifications**: configure `PushNotification`/a Stop hook so a finished or blocked overnight run pings you
  rather than sitting silent.

### C. Context hygiene (keeps me sharp and fast)

1. **Prune the memory index.** `MEMORY.md` has ~50 one-liners loaded into *every* session. Several are
   explicitly `SUPERSEDED`, several describe finished one-off work (backport rounds, "Code review plan July 2026"),
   and at least one is already **wrong**: it says `AGENT-WISHLIST.md` exists at the repo root; it doesn't (the file
   was deleted). Stale memory is worse than none because I act on it. Suggest: move finished-project memories into
   an archive folder (not indexed), delete superseded ones, and keep the index to durable preferences and
   non-obvious gotchas. Target: ≤ 25 lines.
2. **Prefer repo docs over memory for repo facts** (you already noted this in a memory). Where memory and docs
   duplicate, delete the memory. Facts that live in the repo travel to every clone/worktree/CI; memory doesn't.
3. **One-line "last verified" dates** at the top of docs/gotchas pages (versions, "this still reproduces?").
   Lets me judge staleness without re-deriving.
4. **Delete or archive dead docs.** `docs/history/` is fine; any finished plans left outside it
   cost me tokens whenever I search.
5. **Clean leftovers**: `.claude/worktrees/` is 3.3 GB with one old worktree (`fix-issue-138`, branch
   `worktree-fix-issue-138`) and my greps of the repo land in it (duplicate hits). Prune finished worktrees
   (`git worktree remove`), or add `.claude/worktrees` to a search-ignore (`.ignore`/`.rgignore`).
6. **Keep `CLAUDE.md` thin and `AGENTS.md` as the router** (as now). Add a short **"Definition of done"** section:
   which verification applies to which kind of change (logic → JUnit/GameTest; rendering → visual smoke test;
   data → datagen + drift; release → mc-cut-release). It's the single most useful paragraph for me.

### D. Task-shaping habits (how to ask)

- **State the deliverable and the acceptance check**: "done = `bun run verify -- --mods=hopper-xtreme` green and
  a screenshot of X". Overnight tasks especially: _what should exist in the morning?_ (Tonight's three tasks were
  clear; the open choice was where to put the output — a stated default location like `docs/` would help.)
- **Separate "research/plan" from "implement"** in the prompt, and say whether implementing is wanted. I assumed
  plan-only for the CurseForge task because you said "draft a plan"; one phrase removes the ambiguity.
- **Name the constraint behind a preference** once (you do this well: "LF line endings", "concise, non-technical
  changelogs"). I save the *why* and apply it to cases you didn't foresee.
- **Link the issue/number** instead of describing from memory; I can read the issue text myself.
- **Say when you want options vs. a decision.** My default is a recommendation; tell me "give me 3 and let me pick"
  when that's what you want.
- **Batch independent tasks in one message** (you did) — I can parallelize research; but give an order of
  importance in case something blocks.

### E. Build/tooling quality-of-life (Windows-specific)

- **Python isn't installed** (`python3` → Microsoft Store stub); `.claude/settings.json` allowlists
  `python generate_layout.py` / `extract_jar_contents.py`. Either install Python or convert those to Bun scripts
  (everything else in `scripts/` is Bun). A tool I can't run silently wastes a turn.
- **Pick one shell story.** I have both PowerShell and Git Bash; docs mix POSIX (`./gradlew`) and Windows paths.
  A line in `AGENTS.md` — "use the Bash tool for all repo scripts; PowerShell only for Windows-specific tasks" —
  prevents quoting/escaping detours.
- **`.gitattributes` + a pre-commit hook** that rejects CRLF in text files (memory: all text files must be LF). A
  hook beats a memory note: it catches me *and* tools like editors.
- **Gradle daemon/JVM hygiene**: the Avast TLS issue is documented; also consider a tiny `bun run doctor` that
  checks JDK 25, trust-store flag, stale env vars, free disk, orphan `java.exe` — one command, one answer.
- **Orphaned `java.exe` after runs** (documented hang). A `bun run kill-mc` helper that identifies and kills only
  Minecraft/Gradle test JVMs would make that routine instead of a judgment call each time.

### F. Release automation (ties into the CurseForge plan)

- When tag-push = publish, the safety rails (environment approval, dry-run) become the thing that lets me do
  "release prep" end-to-end without you worrying. Until then, `mc-cut-release` stops at the tag for good reason.
- A `bun run status` column for "unreleased changes + tests green + datagen clean" gives me a one-glance release
  readiness check across 33 mods.

### G. Structure that makes repetitive work cheaper

- **Template skills for recurring chores** I currently re-derive: "port mod to next MC version" (the
  UPDATE-PLAN is a good seed), "add a block set to minekea," "add an advancement." Each is a skill with a
  checklist and a reference commit. (You've done this for releases/screenshots/brainstorming.)
- **A `bun run new:mod` / scaffold check**: `init-mod.sh` exists; add a post-scaffold verify so a new mod can't be
  half-wired (settings.gradle, project-list, gametest entrypoints, README stub, TEST_PLAN stub).
- **Golden-file/Snapshot tests for datagen output** so refactors of shared code (ChimericLib) show a diff across
  all consumers in one run — the CODE-REVIEW "hopper BE dedup"–type refactors become safe to do overnight.

## Part 2 — The inverse: things that make me *less* effective

(Not blame — these are patterns I've seen cost time in this and similar repos. Useful as a checklist when a
session goes sideways.)

1. **Stale or contradictory sources of truth.** Memory says X, docs say Y, code says Z. I'll often pick the first
   one I read. (Example above: `AGENT-WISHLIST.md`. Another: `publish-packages.yml` still sets Java 21 while docs and
   `build.gradle` say 25. Another: the "Java 21" ghost in older notes.)
2. **Hidden state outside the repo.** Machine-global env vars, a datagen path pointing at a different worktree's
   jar, a Gradle JVM flag only in `~/.gradle/gradle.properties`. Anything that makes the same command behave
   differently by machine is something I cannot see or reason about from the repo.
3. **Approval prompts on routine commands when you're away.** Each unapproved call blocks the whole run.
4. **Unverifiable "done."** Without a command that proves success, I fall back to "it compiles," which this repo's
   own history shows isn't enough for rendering, mixins, or NeoForge-vs-Fabric differences.
5. **Slow, flaky, or hang-prone feedback** (long clean builds, runs that never exit, datagen races between
   Fabric and NeoForge on shared `generated/` output). Each costs minutes and tempts me to skip the check.
6. **Concurrent heavy work on one checkout.** Two Gradle builds / a run-task plus a build at the same time fight for
   locks and the shared `generated/` dir. Separate worktrees (as you do) are the fix; same-tree parallelism is the
   problem.
7. **Large, unrelated, pre-staged changes** in the index. Already in your rules; it's still the most common way
   to produce a commit that contains something nobody intended.
8. **Ambiguous scope words** ("clean up," "improve," "look into") with no stopping condition. I'll either
   under-deliver (stop at analysis) or over-deliver (rewrite adjacent code). A stopping condition fixes both.
9. **Knowledge that lives only in your head.** E.g., which CurseForge project is which, which mods are "real" vs
   placeholders, which are Fabric-only in practice. Put it in a table in the repo (`project-list.json` could
   carry `status`, `curseforge_id`, `modrinth_id`, `audience`).
10. **Too much always-loaded context.** A 50-line memory index + long root docs mean less room for the task and
    more chance a stale line steers me. Concise, current, and linked beats comprehensive.
11. **Changing the requirement mid-run without saying so**, particularly after I've started a long autonomous
    chunk. A quick "scratch that, do X instead" is fine; silence is where wasted overnight runs come from.
12. **Mixed-language/mixed-shell tooling** where I must guess (Python stubs that don't run, POSIX one-liners in
    PowerShell). Small friction, many repeats.
13. **Docs that describe intent but not the check.** "Always run datagen before release" is a memory test;
    a failing CI step is a guarantee.
14. **Assuming my training knowledge of Minecraft modding APIs is current.** The 26.x line changed names
    (Yarn→Mojang, `Identifier`, rendering `submit*` APIs). You've handled this brilliantly with `.sources/` and
    API-rename notes; the failure mode to avoid is *removing* those or letting them go stale.

## Part 3 — Things *I* should do better (self-notes)

- Re-verify memory claims against the repo before relying on them (caught the wishlist case this way).
- In AFK mode, declare my assumptions at the top of the deliverable (done here: plans only, no commits).
- Prefer a runnable check over a reasoned claim; when I can't run one, say so plainly and mark it **VERIFY**.
- Keep deliverables short enough to review over coffee: summary first, details after.
- Don't create planning files you'll never read: each doc from tonight has an "open questions" section so a
  5-minute review can turn it into decisions.

## Suggested 1-hour starter set (if you only do a few)

1. Prune `MEMORY.md` to the durable ~25 lines; delete the wrong `AGENT-WISHLIST` entry. (15 min)
2. Add the AFK envelope paragraph + "Definition of done" to `AGENTS.md`. (15 min)
3. Run `/fewer-permission-prompts`; add `gh` read commands. (10 min)
4. `git worktree remove` the finished `fix-issue-138` worktree (3.3 GB) after checking it has no unmerged
   work. (5 min)
5. Install Python or tell me to port the two `.py` helpers to Bun. (5 min)
6. Ask me to build `bun run verify` (item A1) — the biggest single force-multiplier. (a session)
