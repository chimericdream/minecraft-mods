# Compatibility repro scaffold

> Source: user request, 2026-11-03 · Status: **Exploring** · Effort **L** · Scope: repo-level (tooling, not a mod feature)

## Description

When a bug report says "crashes with mod X", the slow part is not the fix. It is building a place where
the bug can happen: finding the right versions of the other mods, the right loader, and a clean setup. This
idea automates only that part.

It has **two separate tasks**:

| | Task 1: **Scaffold** (this idea) | Task 2: **Debug** (not automated) |
|---|---|---|
| Who starts it | The user, with a local command | The user, when the user decides to |
| What it does | Reads the issue, finds the other mods, downloads them, sets up the Docker environment | Starts the environment, reproduces the bug, finds the cause, fixes our side |
| Runs third-party code? | **Never** | Yes, but only inside the container, and only when the user starts it |
| Who does the work | Scripts, plus a local agent step to read the issue (no API key needed) | The user, with or without a local agent (no API key needed) |
| Ends with | A ready environment and a short summary. Then it **stops**. | Whatever the user decides |

Task 1 stops before anything runs. The user looks at what it prepared and chooses whether to run it.

**Out of scope:** running or debugging the reproduction automatically, writing fixes, opening PRs, posting
to issues without the user's approval, fixing third-party mods, and bugs that need no other mod.

### Why this split is safer

Downloading a jar only copies bytes. Danger starts when the jar *runs*. In this design, the automated task
never starts the game, never loads a jar, and never builds one. So reading an issue cannot cause
third-party code to run on the user's computer. The only code that runs in Task 1 is this repo's own
scripts. Third-party code runs only in Task 2, in a container, after a person says go.

### Worked example: issue #141

"Crash on load with MrCrayfish's Furniture Mod: Refurbished" (`bug`, `NeoForge`, `needs verification`,
`compatibility`). It has a crash log, exact versions, and a guess at the cause. Task 1 should produce this:

1. **Read.** From the body and the crash log: Minecraft 26.2, NeoForge, Log All the Things! 1.1.0,
   Furniture Mod: Refurbished 1.0.25, plus Architectury and YACL as dependencies. The stack trace points
   at `refurbished_furniture` and `lavalogged`.
2. **Check.** A script validates that data (see [Trust boundary](#trust-boundary)).
3. **Download.** Furniture Mod: Refurbished 1.0.25 for NeoForge 26.2, and its dependencies. Check each
   hash.
4. **Set up.** A NeoForge 26.2 server image, a `mods/` folder with the jars, and our mod at the reported
   version (and, as a second option, the current `main`).
5. **Summarize.** A `SCAFFOLD.md` with the versions, the symptom to look for
   (`Cannot get property ... lavalogged` on `mangrove_lattice_fence`), and the exact command to start it.
6. **Stop.**

Then the user runs `bun run compat:up 141`, watches the crash (or not), and uses a local agent to dig in.

## Task 1 pipeline

| # | Stage | Output | Model needed? |
|---|---|---|---|
| 0 | Start: the user runs `bun run compat:scaffold <issue#>`, or the `compat-scaffold` skill | A run for one issue | No |
| 1 | Read the issue, comments, and attached logs | `triage.json`: our mod, loader, MC version, other mods with versions, crash signature | Yes (a local agent) |
| 2 | **Schema check** | Pass, or stop with a reason | No |
| 3 | Download and verify the other mods and their dependencies | Jars plus a lock file with URLs and hashes | No (script) |
| 4 | Set up the environment: server install, `mods/` folder, our mod, run scripts | An image definition (`Dockerfile`, compose file) and a folder for the run | No (script) |
| 5 | Write `SCAFFOLD.md` | The summary | Light (writing) |
| 6 | Stop | Nothing runs | — |

Stages 0, 2, 3, 4 are plain `bun` scripts under `scripts/compat/`. They do the same thing every time and
are testable. Only stages 1 and 5 use a model.

### What the environment contains

Prefer a **real server install** (the NeoForge or Fabric server launcher plus a `mods/` folder) over this
repo's Gradle `runServer`. It matches what the reporter did, and it does not need this repo's dev setup to
reproduce a player's crash. Our mod comes in as a jar, in two forms:

- **As reported:** the released jar at the reported version, if it is downloadable. Otherwise built from the
  tag (`<mod>/<mc>-<ver>`).
- **Current:** built from the current `main`, to find out whether the bug is already fixed.

Building our own jar means running this repo's Gradle build. That is trusted code, but build it inside
the container image anyway, so the host stays clean. A spike must confirm this works for both loaders.

### Sources

- **Modrinth only** for now. Its API is public. Filter versions by game version and loader, and verify
  the hash it returns.
- A mod that is not on Modrinth is listed in `SCAFFOLD.md` as **missing**, with the reason. The user can
  add the jar by hand. Add CurseForge support when this happens often enough.
- Resolve dependencies of the other mod too. Pin every version to the one in the report, not to "latest".
- Never commit a downloaded jar. Never attach one to an issue or a PR. Keep them in a local cache
  outside the repo, keyed by hash.

## Safety

### Trust boundary

The reporter controls the issue text, and so indirectly controls which mods get fetched. Other mod
authors control the jars. Treat both as hostile.

- Stage 1 only *reads text*. It cannot run commands or download files. Its output is a small data file.
- Stage 2 is a plain script with no model. It checks `triage.json` against a strict schema:
  - Mod identifiers match a fixed pattern, such as a Modrinth slug (`^[a-z0-9_-]{1,64}$`). Versions match
    a version pattern. Nothing else passes.
  - A limit on the number of mods (for example 10).
  - Failure stops the run with a message that says why.
- Downloads use only the Modrinth API and CDN hosts, only over HTTPS, with a size limit, and are checked
  against the hash before they are kept.
- A prompt injection in an issue can at worst choose a different valid Modrinth mod. In Task 1, that mod
  is downloaded and never run.
- `SCAFFOLD.md` contains issue text. Treat it as untrusted when a local agent later reads it.

### Rules for the environment (it will run third-party code in Task 2)

The generated container setup must follow these. They are part of the template, not an option.

- Rootless Docker, or Docker Desktop's Linux VM. Never `--privileged`. No Docker socket inside. All Linux
  capabilities dropped. `no-new-privileges`. Default seccomp profile or stricter.
- **No bind mounts of the user's home or of the repo.** Input comes by copy at build time. Output (logs
  and reports) goes to a named volume or leaves with `docker cp`. If the user wants to edit our source
  during debugging, that is a deliberate choice, covered in the open questions.
- Read-only root filesystem, with a size-limited scratch area. Hard limits on CPU, memory, process count,
  and wall-clock time. The container is deleted after use (`--rm`).
- **Network egress is off by default.** A server crash reproduction needs no network after the jars are in.
  Version 1 has **no network at all** and no way to turn it on. See [Network access](#network-access).
- No secrets inside: no GitHub token, no API keys, no personal credentials.

### Network access

Version 1 starts with the bare minimum: **the container has no network.** The scaffold step downloads
everything first, so `compat:up` needs nothing from the internet. The container runs with Docker's
`--network none`.

Only add network access if real use shows a mod that needs it. When that happens, use a **proxy
container**, not firewall rules:

- The game container joins an internal Docker network that has no route to the internet.
- A small proxy container sits on that network and on a normal one. It forwards requests only to hosts on
  an allowlist (for example `api.modrinth.com` and `cdn.modrinth.com`) and refuses all others.
- The cloud metadata address (`169.254.169.254`), the local network, and private address ranges are always
  blocked, even if a host name on the allowlist resolves to one.
- Requests and refusals are logged, so the user can see what a mod tried to reach.
- The user turns the proxy on per run and edits the allowlist by hand. No default entries.

Firewall rules match IP addresses, not host names, and CDN addresses change. That makes them fragile.
This design needs a check on Docker Desktop with WSL2 before it is built. Do that check only if the
need appears.

### Known limits

A container shares the host kernel. It is a strong boundary, not an unbreakable one. Mitigations: keep
Docker updated, run rootless, and where available add a sandboxed runtime such as gVisor. This is why
Task 2 stays a human decision.

### Go/no-go gate

This idea has one top priority: **no unreviewed third-party code may run just because an issue was read.** If the
checks below cannot be met, stop and drop the idea. The spike must show:

1. Task 1 runs end to end for #141 **without starting the game, loading a jar, or building a jar** on the
   host. A test confirms this (for example, by running Task 1 in a container with no JVM for
   mods and checking that no mod process starts).
2. The schema check rejects bad input: a slug with `;`, `../`, a URL, or a newline; too many mods; a
   version string with shell characters.
3. The download step refuses a file whose hash does not match, a redirect to another host, and a file
   over the size limit.
4. The generated container setup has the rules above: no privileged mode, no socket, no host mounts, no
   token, and egress off by default.
5. A deliberately hostile test jar (it tries to read `~`, the environment, and the network on start)
   cannot reach anything outside the container, and the attempt shows in the logs.
6. Resource limits stop a run that uses too much memory or time.

Checks 1 to 3 gate the automated Task 1. Checks 4 to 6 gate the environment template that Task 2 uses.

## Task 2: what the user gets (not automated)

The scaffold should make manual debugging fast. Task 1 provides:

- `bun run compat:up <issue#>`: start the server from the prepared image.
- `bun run compat:shell <issue#>`: open a shell in the container.
- `bun run compat:deploy <issue#> [jar]`: copy a rebuilt jar of our mod into the container's `mods/` folder
  and restart the server. (Working name. The exact form is decided during implementation.)
- `bun run compat:down <issue#>`: stop and delete the container and its volumes.
- Logs saved to a named volume, with a command to copy them out.
- A cache of decompiled sources for the other mods (see the `mc-source-decompile` skill), run inside the
  container.
- `SCAFFOLD.md` with the versions, the expected symptom, and what is missing.

A local agent (for example Claude Code in this repo, using the user's own login) can then read
`SCAFFOLD.md`, run those commands, and help find the cause. No API key is needed for that. A later idea can
add a `compat-debug` skill for it. This idea does not.

## Phases

1. **Spike and gate (M).** Prove the scaffold by hand on #141 for NeoForge: download the jars, build the
   image, start the server, see the crash. Repeat on Fabric with any Modrinth mod. Pass checks 1 to 6.
   Decide real-server install versus Gradle `runServer`. Everything else depends on this.
2. **Download script (S/M).** `scripts/compat/fetch-mod.ts`: Modrinth lookup (loader as a parameter),
   dependency resolve, hash check, cache. Includes the schema check and its tests.
3. **Environment generator (M).** The image definition, run scripts, and `compat:up` / `shell` / `down`,
   for both loaders.
4. **Skill `compat-scaffold` (S/M).** Stage 1 and stage 5: read the issue and write `SCAFFOLD.md`. Run
   locally on one issue, using a local agent.
5. **Later (not planned here).** A `compat-debug` skill, a compat matrix of tested mod pairs, CurseForge
   support, a draft PR with a fix.

## Decisions

- 2026-11-03 — ~~Build locally first, then run on a GitHub runner.~~ Superseded: see the "Local only" entry.
- 2026-11-03 — Fetch from Modrinth only. Add CurseForge when a needed mod is not on Modrinth.
- 2026-11-03 — Support both NeoForge and Fabric in version 1. The spike starts with NeoForge (#141).
- 2026-11-03 — ~~Trigger label name.~~ Superseded: no label trigger for now.
- 2026-11-03 — Isolation is a top-priority requirement. If it cannot be done safely, do not build this at
  all.
- 2026-11-03 — **Premise change.** The automated task only reads the issue, downloads the mods, sets up
  the Docker environment, and stops. It does not reproduce, debug, fix, or open PRs. Reproducing and
  debugging are a separate manual task, started by the user, which can use a local agent with no API key.
  This replaces the earlier "report only" version and the earlier "agent runs everything in the
  container" design.
- 2026-11-03 — ~~$5 cost cap.~~ Superseded: dropped with the runner.
- 2026-11-03 — **Local only.** The user runs this alone, on the user's own machine. No GitHub runner, no
  label trigger, no cost cap, and no API key. Reading the issue is done by a local agent. The scaffold
  step downloads the mods and sets up the container. The `up` step only starts it.
- 2026-11-03 — Version 1 is server-only. Client tests (virtual display, rendering bugs) are later.
- 2026-11-03 — Version 1 has no network in the container (`--network none`). If a need appears, add network
  access through an allowlisting proxy container. Start with the bare minimum and expand only when needed.
- 2026-11-03 — Scaffold does all the downloading. `compat:up` downloads nothing.
- 2026-11-03 — Debugging runs in a real server install, not in the IDE, so changes to our source reach the
  container as a rebuilt jar copied in by hand. The scaffold provides a mechanism for this (a
  `compat:deploy` command is the working idea). The container gets no bind mount of the repo. The
  details are worked out during implementation.

## Related

- [`mc-source-decompile`](../.claude/skills/mc-source-decompile/SKILL.md) skill: used in Task 2 to read
  other mods' code.
- [`mc-visual-smoke-test`](../.claude/skills/mc-visual-smoke-test/SKILL.md) skill: a possible client check
  later.
- [Testing](../docs/TESTING.md): the JUnit and GameTest harness that holds regression tests after a fix.
- `bun run dashboard` already maps open issues to mods. Stage 1 can reuse that mapping.

## Open questions

_None right now._ Questions that come up during the spike go here.
