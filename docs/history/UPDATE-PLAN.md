# Minecraft Mod Monorepo — Update Plan

Two independent steps, done in order:

1. **Migrate from Yarn to Mojang mappings** (staying on MC 1.21.10)
2. **Update to Minecraft 26.2** (already on Mojang mappings, no mapping concerns)

Splitting them makes each step reversible and keeps the diff reviewable.

---

## Step 1: Yarn → Mojang Mappings (on MC 1.21.10)

### Branch

```bash
git checkout -b update/mojang-mappings
```

### 1a. Switch mappings in build.gradle

**File**: `build.gradle` (root), ~line 83 — inside the `dependencies {}` block

Replace:

```gradle
mappings loom.layered {
    it.mappings("net.fabricmc:yarn:$rootProject.yarn_mappings:v2")
    it.mappings("dev.architectury:yarn-mappings-patch-neoforge:$rootProject.yarn_mappings_patch_neoforge_version")
}
```

With:

```gradle
mappings loom.officialMojangMappings()
```

**Optional — Parchment** adds parameter names and Javadocs on top of Mojang mappings (Mojang mappings alone have
neither):

```gradle
mappings loom.layered() {
    officialMojangMappings()
    parchment("org.parchmentmc.data:parchment-${project.parchment_mappings}@zip")
}
```

Start with plain `officialMojangMappings()` and add Parchment later if desired.
Check [parchmentmc.org](https://parchmentmc.org) for the version compatible with 1.21.10.

**License note**: Mojang mappings are more restrictive than Yarn — the mappings themselves cannot be redistributed, but
compiled JARs are fine to distribute. Loom handles this boundary automatically.

### 1b. Update gradle.properties

**File**: `gradle.properties` (root)

Remove:

```properties
yarn_mappings=1.21.10+build.2
yarn_mappings_patch_neoforge_version=1.21+build.6
```

No other version changes needed in this step.

### 1c. Run migrateMappings for each mod

Loom's `migrateMappings` task rewrites Java source files in-place, renaming Yarn identifiers to their Mojang
equivalents. The `--mappings` argument specifies what the *current* (source) mappings are.

**Start with chimeric-lib** (all other mods depend on it):

```bash
./gradlew :chimeric-lib:common:migrateMappings --mappings "net.minecraft:mappings:1.21.10"
```

Then each active mod:

```bash
./gradlew :archaeology-tweaks:common:migrateMappings --mappings "net.minecraft:mappings:1.21.10"
./gradlew :artificial-heart:common:migrateMappings --mappings "net.minecraft:mappings:1.21.10"
./gradlew :athenaeum:common:migrateMappings --mappings "net.minecraft:mappings:1.21.10"
./gradlew :banner-tweaks:common:migrateMappings --mappings "net.minecraft:mappings:1.21.10"
./gradlew :beacon-conduit-tweaks:common:migrateMappings --mappings "net.minecraft:mappings:1.21.10"
./gradlew :enchantment-numbers-fix:common:migrateMappings --mappings "net.minecraft:mappings:1.21.10"
./gradlew :flat-bedrock:common:migrateMappings --mappings "net.minecraft:mappings:1.21.10"
./gradlew :hopper-xtreme:common:migrateMappings --mappings "net.minecraft:mappings:1.21.10"
./gradlew :houdini-block:common:migrateMappings --mappings "net.minecraft:mappings:1.21.10"
./gradlew :minekea:common:migrateMappings --mappings "net.minecraft:mappings:1.21.10"
./gradlew :miniblock-merchants:common:migrateMappings --mappings "net.minecraft:mappings:1.21.10"
./gradlew :sponj:common:migrateMappings --mappings "net.minecraft:mappings:1.21.10"
./gradlew :villager-tweaks:common:migrateMappings --mappings "net.minecraft:mappings:1.21.10"
```

The `fabric/` and `neoforge/` subprojects have minimal Java code — review them manually for any Yarn-named calls not
covered by this task.

### 1d. Update AccessWidener files

AccessWidener files use Yarn-mapped names and are **not** updated by `migrateMappings` — manual work required.

Find all of them:

```bash
find . -name "*.accesswidener" -not -path "*/build/*"
```

Known example (`archaeology-tweaks/common/src/main/resources/archtweaks.accesswidener`):

```
# These Yarn names must be replaced with Mojang equivalents:
accessible field net/minecraft/block/entity/BrushableBlockEntity item ...
accessible field net/minecraft/block/entity/BrushableBlockEntity lootTable ...
accessible method net/minecraft/item/BrushItem getHitResult ...
accessible method net/minecraft/item/BrushItem addDustParticles ...
```

Use a Minecraft mappings browser (e.g., [mappings.cephx.dev](https://mappings.cephx.dev/)) to look up correct Mojang
class/field/method names.

### 1e. Verify Mixin descriptors

Mixin `method = "..."` strings contain Yarn-mapped method names and JVM descriptors referencing Yarn class names.
`migrateMappings` should handle most of these, but manually verify every `*Mixin.java`:

```bash
find . -name "*Mixin.java" -not -path "*/build/*"
```

Pay particular attention to:

- `chimeric-lib/common/src/.../mixin/ShearsItemMixin.java`
- `banner-tweaks/common/src/.../mixin/LoomScreenHandlerMixin.java`

### 1f. Build and iterate

```bash
./gradlew :chimeric-lib:build
# Fix any errors, then:
./gradlew build
```

Since the MC version hasn't changed, all compilation errors at this stage are purely mapping-name issues —
straightforward to fix.

### 1g. Migrate commented-out mods

Once all 14 active mods build cleanly, repeat steps 1c–1f for the 7 disabled mods (enable one at a time in
`settings.gradle`, migrate, fix, re-disable):

- blacklight, cobblicious, hang-from-slabs, jdcrafte, pannotia-companion, playgrounds, shulker-stuff

### Merge

Merge `update/mojang-mappings` into `main` before starting Step 2.

---

## Step 2: Update to Minecraft 26.2

With Mojang mappings already in place, this step is purely about API changes and dependency updates — no mapping
migration work.

### Branch

```bash
git checkout -b update/mc-26.2
```

### 2a. Look up correct versions for 26.2

Before editing anything, find the current versions at fabricmc.net, neoforged.net, and modrinth:

- Fabric Loader
- Fabric API
- NeoForge
- Architectury API
- Loom
- YACL (currently `3.8.0+1.21.9`)
- Mod Menu
- Patchouli (used by hopper-xtreme and minekea — tends to lag MC releases, check early)

### 2b. Update gradle.properties

```properties
loom_version=<latest>
minecraft_version=26.2
minecraft_compatibility=26.2
architectury_api_version=<latest for 26.2>
fabric_loader_version=<latest>
fabric_api_version=<latest for 26.2>
neoforge_version=<latest for 26.2>
yacl_version=<latest for 26.2>
mod_menu_version=<latest for 26.2>
patchouli_version=<latest for 26.2>
```

### 2c. Build and fix API breakage

```bash
./gradlew :chimeric-lib:build
# Fix errors, then:
./gradlew build
```

The 1.21 → 26.x jump is large. Expect:

- Removed or renamed Minecraft API methods (beyond mapping names)
- Changed method signatures
- New required overrides or removed abstract methods
- Recipe/data format changes

Fix one mod at a time: chimeric-lib first, then archaeology-tweaks and banner-tweaks (most complex), then the rest
alphabetically.

Community resources: [Fabric wiki migration guides](https://fabricmc.net/wiki), NeoForge changelog, and mod-dev Discord
servers will be essential for tracking what changed in vanilla APIs between versions.

### 2d. Re-run data generation

If any mods use `runDatagen`, re-run it to regenerate JSON files (recipes, loot tables, etc.) for 26.2 formats.

---

## Key Files

| File                            | Step 1                               | Step 2                      |
|---------------------------------|--------------------------------------|-----------------------------|
| `build.gradle` (root, ~line 83) | Switch to `officialMojangMappings()` | No change                   |
| `gradle.properties`             | Remove `yarn_mappings*`              | Update all version pins     |
| `*/common/src/**/*.java`        | Renamed by `migrateMappings`         | Fix API breakage            |
| `*/*.accesswidener`             | Manual Yarn→Mojang rename            | Possibly update class names |
| `**/mixin/**Mixin.java`         | Verify `method =` descriptors        | Fix any broken targets      |

## Suggested Mod Order (for both steps)

1. chimeric-lib
2. archaeology-tweaks (complex — brushable block entity, access wideners)
3. banner-tweaks (mixin-heavy)
4. Remaining 11 active mods alphabetically
5. 7 disabled mods last
