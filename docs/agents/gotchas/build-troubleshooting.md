# Build troubleshooting

## Gradle: `PKIX path validation failed` on this machine

Avast's "Web/Mail Shield" intercepts HTTPS and re-signs it with a root CA that lives in the **Windows
certificate store** but not in any JDK's `cacerts`. `curl` works, every JVM fails, and Gradle swallows
the handshake error and reports it as a bogus resolution miss:

> Plugin [id: '...'] was not found in any of the following sources

Only `--debug` reveals the real cause (`Starting handshake` → `Shutdown connection`, and
`SSLHandshakeException: PKIX path validation failed`). Fix by pointing the daemon at the Windows store,
which already trusts that root:

```properties
org.gradle.jvmargs = -Xmx2G -Djavax.net.ssl.trustStoreType=WINDOWS-ROOT
```

The mod build doesn't hit this because its dependencies are already in `~/.gradle/caches` — it bites
**new** Gradle builds that must download something. `tools/mod-status-plugin/gradle.properties` carries
the flag for that reason. Don't chase repository URLs or plugin versions before ruling this out.

## Build/datagen/GameTest/run tasks that finish and then hang

Any `./gradlew build`/`runGameTest`/NeoForge `run*` task (datagen or otherwise) can finish all of its
real work and then hang indefinitely instead of exiting the JVM (unlike the client-side shutdown
watchdog crash, this does **not** self-terminate). **Check status every ~10 minutes, not every
20-30+** — the vast majority of these tasks finish well under that, so "still running" past ~10 minutes
means hung, not slow. Poll the expected *output* (log tail, generated-file timestamps, whether the
artifact already exists), not just whether the process reports completion. Full write-up (root cause,
confirmed instances, how to find + kill the stuck `java.exe`): [MC 26.2 notes](../../MC-26.2-NOTES.md).

### Run-task watchdog (auto-kill of hung `run*` JVMs)

`gradle/run-watchdog.gradle` (applied from the root `build.gradle`) watches every `run*` JavaExec task's
output for `Closing FML Loader` / `Clearing ModLoader` (NeoForge) or `All N required tests passed`
(Fabric `runGameTest`). If the JVM is still alive 30s later it kills the JVM tree (including Architectury's
`TransformerRuntime`) and counts the task as successful; a run that exits non-zero on its own, or a test
run killed without the "tests passed" line, still fails. Opt out with `-Pwatchdog=false`; tune with
`-Pwatchdog.graceSeconds=N`. Its `[watchdog]` messages go to the Gradle console, not the `logs/` files.
Each run JVM is tagged with `-Dwatchdog.task=<task path>` and the kill timer finds its JVM by that exact
token. Don't go back to matching by directory or "the only java child": in a parallel build that killed a
different mod's `runGameTest`.

### Slow Gradle startup: configuration-on-demand and the configuration cache

Loom's per-project `afterEvaluate` costs ~1-2s x ~130 projects, so configuring everything took 2-2.5 min
before any task started. `org.gradle.configureondemand=true` (in `gradle.properties`) configures only the
projects a qualified task path needs (`:mod:fabric:runClient` ~10s). Unqualified tasks (`build`,
`runData` with no path) still configure everything. Things that make this safe — keep them:

- `build.gradle` applies Loom first to the mods named in the requested tasks. Loom registers its
  `jarManifestService_<project NAME>` build service via `registerIfAbsent`, so the first project of each name
  (`common`/`fabric`/`neoforge`) supplies the params for every mod. If that is an unevaluated project, every
  `jar` task fails with "Cannot get MinecraftProvider before it has been setup".
- Each `:fabric`/`:neoforge` evaluates after its `:common` sibling (`beforeEvaluate { evaluationDependsOn }`),
  otherwise "accessWidenerPath is final and cannot be changed any further".
- `-Dorg.gradle.configureondemand=false` is the escape hatch if a new configure-order error appears.

The Gradle **configuration cache** stays off: `--configuration-cache` works for compile-style tasks (~1-2s
startup on a hit) but run/jar tasks fail inside Loom (`JarManifestService`) and Architectury
(`prepareArchitecturyTransformer` reads `Task.project`). `task-logs.gradle` and `run-watchdog.gradle` are
already written as build services so they are not the blocker. Don't make closures in those services read
`private` members — Gradle's generated subclass breaks that silently and the watchdog stops firing.
`~/.gradle/gradle.properties` overrides the project's `org.gradle.jvmargs`, so repeat `-Xmx8G` there.

### Confirming a hung `run*` JVM is really done (before killing it)

The run watchdog above handles most hangs, but if you have to judge one by hand: take `jstack <pid>` of the
forked JVM. If every thread is idle (`ForkJoinPool` / `ThreadPoolExecutor` workers parked in
`LinkedBlockingQueue.take()`, `DestroyJavaVM` present and waiting) and nothing is executing generation
logic, the work is done and it will never exit on its own (some dependency starts non-daemon executors and
never shuts them down). Verify the expected output landed (the file or directory exists, or `git status`
shows the expected diff) before killing. Gradle then reports the task FAILED (`non-zero exit value -1`) purely
from the forced kill; re-run that one task for a clean confirmation. A leftover hung JVM can also cause
"Failed to modify jar manifest" on the next build; killing it fixes that.

Fabric's `runDatagen` and NeoForge's `runData` share a clean-the-generated-directory step scoped per mod, not
per platform, so running Fabric's task after NeoForge's wipes NeoForge's output. Run NeoForge's datagen
**last**, one mod at a time, immediately before committing. See also the Fabric/NeoForge datagen race in
[MC 26.2 notes](../../MC-26.2-NOTES.md).

## Datagen reads machine-global environment variables: verify them first

chimeric-lib's `JarAccess` / `TextureGenerator` read `chimericdream.datagen.minecraft-jar-path` and
`<modid>.datagen.resource-path` from the **OS/user environment**, not per repo, branch or worktree. They can
silently point at another checkout or another Minecraft version's jar. `TextureGenerator` then logs a warning
and produces nothing instead of failing; this once shipped minekea's compressed Purpur Pillar with a missing
side texture (`purpur_pillar.png` on 26.1.2, `purpur_pillar_side.png` on 26.2). These variables are set per
computer, so check them on each machine you work from.

- Before trusting any `bun run datagen` / `runDatagen` output, check both variables point at *this* worktree's
  resource path and *this* branch's Minecraft jar.
- To override for one command only, use `env 'name=value' ...` in bash (the dotted names break plain
  `VAR=value`) rather than changing the global value.
- Datagen keeps a gitignored content-hash cache at `<mod>/common/src/main/generated/.cache/`. If you revert
  generated output with `git checkout` but leave the cache, the next correct run sees matching hashes, skips
  rewriting, and leaves the stale file while reporting success. Delete `.cache` (safe, regenerable) after any
  such revert, or diff against HEAD afterward. The same stale cache made minekea's model provider skip every
  write during the 26.1.2 backport.
