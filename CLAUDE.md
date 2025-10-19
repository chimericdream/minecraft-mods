# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

### Building and Development
- `bun run build` - Build all mods using Gradle and create modpacks
- `./gradlew build` - Build all mods using Gradle only
- `./gradlew clean` - Clean all build artifacts
- `bun run build:modpacks` - Create modpack distributions in `build/modpacks/`

### Project Management
- `bun run update:settingsgradle` - Update settings.gradle with project list
- `bun run update:projectlist` - Update the project list configuration
- `bun run copy:accesswideners` - Copy access widener files across projects
- `bun run update:patchoulibooks` - Update Patchouli documentation books

## Architecture

This is a multi-mod repository using Gradle with Architectury for cross-platform Minecraft mod development. Each mod supports both Fabric and NeoForge mod loaders.

### Repository Structure
- **Multi-project Gradle build**: Root `build.gradle` defines common configuration for all mods
- **Architectury pattern**: Each mod has three subprojects:
  - `common/` - Shared code between platforms
  - `fabric/` - Fabric-specific implementation
  - `neoforge/` - NeoForge-specific implementation
- **ChimericLib dependency**: Most mods depend on `chimeric-lib` which provides shared utilities
- **Project list management**: Active projects are controlled via `settings.gradle` projectList array

### Key Configuration Files
- `gradle.properties` - Global Minecraft version, mod loader versions, and dependency versions
- `settings.gradle` - Defines which projects are included in the build (many are commented out)
- `package.json` - Build scripts using Bun for automation tasks
- `project-list.json` - Used by build scripts to manage active projects

### Currently Active Mods
Based on `settings.gradle`, only these mods are currently enabled:
- `archaeology-tweaks`
- `banner-tweaks`
- `chimeric-lib` (core library)

All other mods are commented out in the project list.

### Platform-Specific Notes
- **Java 21** target compatibility
- **Minecraft 1.21.5** with Yarn mappings
- **Fabric API** and **NeoForge** as mod loader dependencies
- **Yet Another Config Lib (YACL)** for configuration screens
- **Architectury API** for cross-platform compatibility

### Build Process
1. Clean and prepare build (copy access wideners, update Patchouli books)
2. Gradle builds all enabled mod projects
3. Create modpack distributions copying JAR files to `build/modpacks/fabric/` and `build/modpacks/neoforge/`
4. Teardown reverts temporary changes to fabric.mod.json files

### Development Workflow
- Most mods are disabled by default in `settings.gradle`
- To work on a specific mod, uncomment it in the projectList array
- Each mod follows the standard Architectury pattern with common/fabric/neoforge subprojects
- ChimericLib must be built first as other mods depend on it

## Minecraft Asset Reference

When the user asks to check for asset changes between Minecraft versions, use the minecraft-assets repository as a reference:

**Repository**: https://github.com/InventivetalentDev/minecraft-assets

- Every Minecraft version is available as a tag in this repository
- Compare tags to identify changes in vanilla assets between versions
- Common changes include texture file renames, model structure updates, and recipe format changes

**Example**: Between Minecraft 1.21.4 and 1.21.5, creaking heart texture files were renamed:
- `minecraft:block/creaking_heart_active` → `minecraft:block/creaking_heart_awake`
- `minecraft:block/creaking_heart_top_active` → `minecraft:block/creaking_heart_top_awake`

**Note**: Only check this repository when explicitly asked by the user. Do not proactively check it during routine version updates.