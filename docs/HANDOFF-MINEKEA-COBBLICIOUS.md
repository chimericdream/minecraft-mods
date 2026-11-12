# Handoff: Minekea to Cobblicious migration

Date: 2026-11-12. Status: step 1 is done and committed on `main`. Step 2 (Forwarding Address) has not started.

## Goal

Move some blocks from Minekea to Cobblicious (and later to But What About). Step 1 is to stop Minekea from
registering the moved blocks when Cobblicious is loaded. Step 2 is to use
[Forwarding Address](../forwarding-address/README.md) to replace old `minekea:` blocks and items in existing
worlds with the `cobblicious:` ones.

## Step 1: done

Minekea skips these when Cobblicious is loaded:

- Basalt, Cracked, Crimson, Mossy, Warped Basalt Bricks, and Warped Nether Bricks. Each has stairs, slab, and wall.
- Chiseled Basalt Bricks.
- Warped Wart (item, block, and crop).

Decisions made with the user:

- Derived blocks stay (beams, covers, vertical stairs and slabs, compressed blocks, warped nether brick
  bookshelf). They use the Cobblicious block as ingredient.
- Static generated data uses dual load conditions (`fabric:load_conditions` and `neoforge:conditions`).

### Where the code is

| File | Purpose |
| --- | --- |
| `minekea/common/.../compat/CobbliciousCompat.java` | `isActive()`, the `replacements()` map (Minekea id to Cobblicious id), lazy `block(id)` and `item(id)` |
| `minekea/common/.../block/building/BuildingBlocks.java` | `brick(...)` helper registers a block, or returns the Cobblicious reference |
| `minekea/common/.../block/building/BasaltBrickFamilies.java`, `crop/ModCrops.java`, `block/containers/GlassJarBlock.java` | Gate the families and wart, and allow the Cobblicious wart in glass jars |
| `minekea/fabric/.../data/CobbliciousDataCompat.java` | Datagen wrapper: adds conditions, swaps ingredients for tags, makes tag entries optional, writes `*_cobblicious` unpack recipes |
| `minekea/fabric/.../data/ModDataGenerator.java` | Wraps every provider. Generates the `minekea:cobblicious_swap/*` item tags |
| `fabric.mod.json`, `neoforge.mods.toml` | Soft dependency on `cobblicious`, loaded first |

`CobbliciousCompat.isActive()` is always false during datagen (`fabric-api.datagen` property). Datagen
always generates the Minekea blocks. The generated JSON turns itself off at run time.

### Verified

- Fabric dev game with both mods: registries and tags correct, no recipe, loot table, or tag errors.
- `bun run lint` passes.
- NeoForge compiles.

## Open items

1. **NeoForge runtime is untested.** The `neoforge:conditions` format came from source, not from a game run.
   Test with Cobblicious loaded and not loaded. See `minekea/TEST_PLAN.md` item 16.
2. **Datagen deletes 81 textures** (compressed cinnabar and sulfur) as "removed stale". I restored them with
   `git checkout` each time. Probably not caused by this work. Check on a clean `main`. If it is a bug, fix
   it before the next datagen run.
3. **Opus guide pages still describe the moved blocks:** `06-basalt-bricks.md`, `07-warped-nether-bricks.md`,
   `06-crops-and-fluids/01-warped-wart.md`. They are not gated.
4. **Client assets for removed blocks** (blockstates, models, lang) stay in the jar. They are harmless.
5. **No Cobblicious version check.** The NeoForge range is `[1.0.0,)`. Cobblicious must have the matching ids.
6. `forwarding-address/VERIFY_STANDALONE.md` is untracked and was not part of this work. It was left alone.

## Step 2: Forwarding Address (next)

- Read `forwarding-address/README.md` and `docs/BLOCK-MIGRATION.md` first.
- API: `Migrations.block(old).to(new).register()`.
- Use `CobbliciousCompat.replacements()` as the source list of old and new ids. The wart has two Minekea ids
  (`crops/warped_wart` item, `crops/warped_wart/block`) that map to one Cobblicious id (`warped_wart`).
  Check whether the Cobblicious wart crop block has a different id from its item.
- Only register migrations when Cobblicious is loaded, for the same reason the blocks are skipped.
- Migrate placed blocks and items in inventories. The glass jar can hold Warped Wart. Check that the stored
  item converts too.
- Add a GameTest for conversion (see the Hopper X-Treme reference in `docs/BLOCK-MIGRATION.md`).
- Then repeat the pattern for But What About.

## Reminders

- Mod version bump rule: a feature is a minor bump. Minekea is now `10.4.0-beta.0`.
- Write prose in ASD-STE100. Use the `mc-text-cleanup` skill after changes to docs or CHANGELOG.
- Gradle needs Java 25 (`JAVA_HOME` to the mise temurin-25 install).
- Use LF line endings. `bun run lint` fails on CRLF.
