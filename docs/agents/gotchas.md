# Gotchas & troubleshooting

Known build failures, MC 26.2 port issues, and platform-specific bugs. Check here before assuming
something you just hit is a new bug — it may already be documented with a root cause and fix.

- [Access wideners](gotchas/access-wideners.md) — why they must never be committed outside `common`
- [Build troubleshooting](gotchas/build-troubleshooting.md) — PKIX TLS, tasks that hang after finishing
- [MC 26.2 port notes](../MC-26.2-NOTES.md) — datagen component binding, API renames, shutdown-watchdog false crash
- [NeoForge gotchas](../NEOFORGE.md) / [Fabric gotchas](../FABRIC.md) — confirmed loader-specific runtime/build issues
- [Rendering: z-fighting on coplanar surfaces](gotchas/rendering.md)
