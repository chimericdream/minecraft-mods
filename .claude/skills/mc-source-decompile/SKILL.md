---
name: mc-source-decompile
description: Cache and reuse decompiled Minecraft / mod-dependency source instead of re-decompiling every time. Use whenever you need to read Minecraft's or a dependency mod's actual implementation code (a Vineflower decompile, not just a binary jar) — check `.sources/` first, decompile only on a cache miss, then save the result there for next time.
---

# Minecraft / dependency source decompile cache

Decompiling the same jar repeatedly wastes time. This repo keeps a persistent, gitignored cache of
decompiled source at `.sources/` in the repo root, keyed by artifact and version. Always check the
cache before decompiling anything, and always populate it after a decompile so future work (this
session or a later one) is instant.

## Layout

```
.sources/
  minecraft/{mc_version}/...        e.g. .sources/minecraft/26.2/net/minecraft/...
  {modid}/{loader}/{version}/...    e.g. .sources/chimericlib/fabric/26.2-6.0.0/...
```

- `{mc_version}` matches `minecraft_version` in `gradle.properties` (see `CLAUDE.md` "Snapshot").
- For mods, `{loader}` is `fabric` or `neoforge` (or `common` if you decompiled loader-agnostic
  classes only); `{version}` is the dependency's own version string as declared in `gradle.properties`
  / the mod's build file — not the Minecraft version.
- Mirror the original package path under the version directory (`net/minecraft/...`,
  `com/chimericdream/...`) so it reads like a normal source tree and multiple lookups merge cleanly.
- `.sources/` is listed in `.gitignore` — never commit decompiled output.

## Procedure

1. **Check the cache first.** Before doing any decompilation, check whether the class(es) you need
   already exist under `.sources/minecraft/{version}/...` or `.sources/{modid}/{loader}/{version}/...`.
   If they're there, just read them — done.
2. **On a cache miss, locate the binary jar.**
   - Minecraft itself (MC 26.2 and likely later): the Loom-cached merged/deobfuscated jar, e.g.
     `~/.gradle/caches/fabric-loom/minecraftMaven/net/minecraft/minecraft-merged-deobf/{version}/minecraft-merged-deobf-{version}.jar`.
     As of 26.2 the Loom-generated `-sources.jar` is an empty placeholder — don't bother looking for
     it, go straight to decompiling the binary jar. Verify the exact path/version format still matches
     before relying on it; Loom's cache layout has shifted across versions.
   - A mod dependency: the Gradle module cache, e.g.
     `~/.gradle/caches/modules-2/files-2.1/{group}/{artifact}/{version}/{hash}/{artifact}-{version}.jar`
     (use `find ~/.gradle/caches/modules-2/files-2.1 -iname "*{artifact}*"` to locate it — the hash
     segment is content-addressed and can't be predicted). Prefer a `-sources.jar` in that same
     directory if one was actually resolved (non-empty) — skip decompiling entirely and copy/extract it
     into `.sources/` instead.
3. **Decompile with Vineflower.**
   - If Vineflower isn't already available locally, download it from Maven Central. On this machine,
     plain `curl` in Git Bash has failed with exit 35 for Maven Central before — use PowerShell
     `Invoke-WebRequest` instead if that happens.
   - Extract only the classes you actually need from the jar first (`unzip <jar> '<path/to/Class*.class>' -d <tmpdir>`)
     rather than decompiling the whole jar — Minecraft's merged jar in particular is huge.
   - Run `java -jar vineflower.jar <extracted-classes-dir> <outdir>`.
   - If you only need method/field signatures, not implementation, `javap -p` on the extracted
     `.class` file is faster than a full decompile — but still worth caching the real decompile if you
     expect to come back to this class's implementation.
4. **Save into the cache.** Move/copy the decompiled `.java` output into the matching
   `.sources/minecraft/{version}/...` or `.sources/{modid}/{loader}/{version}/...` path, preserving the
   package structure, before moving on. Don't leave decompiled output only in a scratch/tmp dir.
5. **Use it.** Read the cached `.java` files with the Read tool like any other source file.

## Notes

- ChimericLib is currently an **in-build `project()` dependency** in this repo (see `CLAUDE.md` /
  `DEPENDENCY-PLAN.md`), so its real source is already on disk under `chimeric-lib/` — never decompile
  it. The `.sources/chimericlib/...` path in the example above is for the hypothetical purposes only.
- This cache is local-machine convenience only (gitignored, not reproducible-build input) — if a
  version's decompile looks stale or wrong, delete that version's subdirectory and redo it rather than
  trying to patch it in place.
