# AGENTS.md

This file provides guidance to coding agents (including Claude Code, claude.ai/code) when working with
code in this repository.
Detailed guidance lives in the linked docs below — open a category only when the current task touches
it; each category links one level deeper to the specific doc for that topic.

## Snapshot (verify against `gradle.properties` before relying on exact versions)

- **Minecraft**: `26.2` (`minecraft_version` / `minecraft_compatibility` in `gradle.properties`)
- **Mappings**: official Mojang names — the Yarn→Mojang migration is **complete**. There is no
  `mappings` block or `yarn_mappings` property anywhere; the code builds directly against Minecraft's
  shipped 26.2 names (e.g. the identifier class is `net.minecraft.resources.Identifier`, **not** Yarn's
  `net.minecraft.util.Identifier` and **not** `ResourceLocation`).
- **Java**: **25** (`sourceCompatibility`/`targetCompatibility = VERSION_25`, `options.release = 25` in root `build.gradle`) — *not* 21.
- **Loaders / libs** (from `gradle.properties`): Fabric Loader `0.19.3`, Fabric API `0.154.2+26.2`,
  NeoForge `26.2.0.15-beta`, Architectury API `21.0.4`, YACL `3.9.5+26.2`, Mod Menu `20.0.1`,
  Kotlin-for-Forge `6.3.0`, Loom `1.17-SNAPSHOT`, chimeric-lib `26.2-6.0.0`.
- **Loom plugin**: `dev.architectury.loom-no-remap`; shadow via `com.gradleup.shadow`.

## Documentation map

- [Architecture](docs/agents/architecture.md) — monorepo layout, Architectury pattern, chimeric-lib
  wiring
- [Commands](docs/agents/commands.md) — build, datagen, testing, and project-management scripts
- [Gotchas & troubleshooting](docs/agents/gotchas.md) — build failures, MC 26.2 port issues,
  platform-specific (Fabric/NeoForge) bugs, rendering quirks
- [Conventions](docs/agents/conventions.md) — code style, container-inventory pattern, scaffolding new
  mods
- [Releases](docs/agents/releases.md) — versioning/changelog rules, git commit hygiene
- [Reference](docs/agents/reference.md) — planning docs index, external Minecraft asset reference
