# Backport: chimeric-lib

| | |
|---|---|
| **Branch** | `backport/26.1.2/chimeric-lib` |
| **Branch from** | `backport/26.1.2/shared-build` (Wave 0) |
| **Wave** | 1 — **serial, blocks all of Wave 2** |
| **Payload** | `git diff c5f2cc4d..main -- chimeric-lib/` — 41 files, +1,860 / −270 |
| **Risk** | **High** — new public API that six other mods compile against |
| **Conflict-risk files** | 4 (see §3) |
| **Gate** | `:chimeric-lib:fabric:test` (~30 unit tests) + `:chimeric-lib:fabric:runGameTest` (5 tests) |

Read [README.md](README.md) §5 (reverse API map) and §7 (procedure) first.

Build files (`chimeric-lib/build.gradle`, `common/build.gradle`, `fabric/build.gradle`,
`gradle.properties`) are **Wave 0's**. Drop any hunk touching them.

---

## 1. Payload commits

```
41ab0adb  docs: add readmes and possible feature ideas
2b53182a  chore: document manual and automated test plans for each mod
e5233d82  test(chimericlib): first trivial unit test and repo setup
e69d6e8d  test(chimericlib): unit tests for pure helpers per TEST_PLAN
60a8169b  test(chimericlib): publish BootstrapMinecraft as a shared testFixtures variant
3aaa3bb2  test(chimericlib): add isolated fabric GameTests + shared GameTest helpers
5315faa9  fix(chimeric-lib): merge partial stacks by item+components, not by count      (1.6)
8d7fa89a  fix(chimeric-lib): lazy texture fallback and balanced menu open/close         (2.4, 2.6)
875eaa24  refactor(chimeric-lib): unify screen handlers + ColorHelpers/inventory hygiene (3.2, 3.7, 4.7)
5f70e69b  refactor(chimeric-lib): address PR review nits
d0ed03cc  refactor(sponj): merge sponge blocks + move BlockUtils to chimeric-lib        (3.9 — lib half)
8acc6af7  refactor(chimeric-lib): extract shared AbstractWrenchItem                     (3.4)
e8aaffbe  refactor(chimeric-lib): add NeoForge LootModifierHelper                       (3.6)
c392de43  refactor(chimeric-lib): extract ContainerOpenersCounters factory              (3.5)
```

The `(N.M)` tags reference `CODE-REVIEW-PLAN.md`, which Wave 0 copies to the repo root. Read the
relevant item before touching each file — it carries the reasoning the commit message compresses.

---

## 2. Change inventory

### 2a. Bug fixes (backport the behavior exactly)

**`inventories/ImplementedInventory.java`** (+14/−2) — two fixes:

- **1.6, the important one.** `isMatchingPartialStack` used `ItemStack.matches`, which compares
  *counts* as well as item+components. Two otherwise-identical partial stacks were therefore only
  mergeable when their counts happened to be equal, so every `tryInsert` consumer — minekea shelves,
  armoires, glass jars, the block painter, the hopper filter inventory — silently failed to merge
  partial stacks. Use `isSameItemSameComponents`.
- **4.7.** `clearContent` must preserve the fixed slot count of a `NonNullList.withSize(...)` —
  clear in place, never `clear()` on a fixed-size list.

**`blocks/BlockConfig.java`** (+10/−1) — **2.4.** `getTexture()` used `Map.getOrDefault(k, fallback)`,
and Java evaluates the `fallback` argument unconditionally. A config with an explicit texture but no
ingredient therefore threw `IllegalStateException: No default ingredient set` from a fallback it
never needed. Look the texture up first; derive from the ingredient only when absent.

**Menu open/close balance (2.6)** — landed as part of the screen-handler unification below:
`InventoryScreenHandler` pairs `removed()` with `stopOpen()` so viewer counts stay balanced.

### 2b. New shared API — this is what Wave 2 depends on

| New file | Item | What it is | Consumed by |
|---|---|---|---|
| `screen/InventoryScreenHandler.java` (+124) | 3.2 | Base for fixed-grid container menus: layout, `quickMoveStack`, `removed()`/`stopOpen()`. `SimpleInventoryScreenHandler` (9 col) and `DoubleWideInventoryScreenHandler` (18 col) collapse to thin subclasses (each −82 lines) differing only in column count and the derived player-inventory x-offset. Player/hotbar Y offsets derive from `ROW_HEIGHT`. | shulker-stuff, hopper-xtreme, minekea |
| `inventories/ContainerOpenersCounters.java` (+101) | 3.5 | Factory replacing three hand-rolled ~40-line anonymous `ContainerOpenersCounter`s. Takes the menu class as a **required** parameter and confirms ownership by comparing that menu's container to the block entity — this is what fixes shulker-stuff's 2.5 (the dye station's counter tested `instanceof ChestMenu`, copy-pasted from the barrel, so it never counted a real viewer). | minekea (`CrateBlockEntity`, `MinekeaBarrelBlockEntity`), shulker-stuff (`DyeStationBlockEntity`) |
| `item/AbstractWrenchItem.java` (+122) | 3.4 | `tryPlacing`/`tryFacing`/`tryAxes`/`trySlab`/`useOn`, previously duplicated byte-for-byte between minekea and hopper-xtreme. Constructor takes `Item.Properties`; each mod keeps a thin subclass supplying only `ITEM_ID` and registration properties, so registry call sites and per-mod tooltips are unchanged. | minekea, hopper-xtreme |
| `blocks/BlockUtils.java` (+86) | 3.9 | Moved wholesale out of `sponj/.../BlockUtils.java` (which the sponj branch deletes). | sponj |
| `neoforge/loot/LootModifierHelper.java` (+34) | 3.6 | `createRegister(modId)` wrapping `DeferredRegister.create(NeoForgeRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS, modId)`. | shulker-stuff, miniblock-merchants |

**⚠ Coordination:** these five files must exist and compile before any Wave 2 branch starts. If one
cannot be backported, say so loudly — the affected Wave 2 mods must then keep their local copies.

### 2c. `colors/ColorHelpers.java` — see §3, this is a conflict file

Payload changes (4.7 + review nits), all version-independent in *intent*:

- Palette arrays (`WHITE`, `LIGHT_GRAY`, … 16 of them) become `private static final`, handed out only
  as defensive copies through a new `getTints(String)` accessor. Callers could previously mutate
  shared palette state.
- `getTint(int, int[])` gains a lower bound: `if (tintIndex < 0 || tintIndex >= variants.length)`.
- `RGB.toInt()` delegates to `getColor()` instead of re-deriving via `ARGB.color(0, r, g, b)`.

### 2d. Test infrastructure

**`common/src/testFixtures/`** — published as a `testFixtures` variant on `components.java` so
downstream mods reuse rather than copy:

- `testkit/BootstrapMinecraft.java` (+57) — JUnit base running `SharedConstants.tryDetectVersion()`
  + `Bootstrap.bootStrap()`. **⚠ Adaptation required — see §3.**
- `testkit/gametest/GameTestContainers.java` (+63), `GameTestEntities.java` (+46),
  `GameTestMenus.java` (+34) — reusable GameTest helpers.

**`fabric/src/test/`** — 8 JUnit classes, ~30 tests, for pure helpers:
`BlockConfigTest`, `ColorHelpersTest`, `ImplementedInventoryTest`, `InventoryUtilsTest`,
`ItemHelpersTest`, `TextureUtilsTest`, `TextHelpersTest`, `ToolTest`, `DirectionUtilsTest`.

`ImplementedInventoryTest` and `ColorHelpersTest` are the regression gates for 1.6 and 4.7 — they
are the reason those fixes are safe. Do not drop them.

**`fabric/src/gametest/`** — 5 GameTests plus throwaway fixture content
(`TestContainerBlock`, `TestContainerBlockEntity`, `TestFixtures`, `TestLootModifier`,
`ChimericLibTestEntrypoint`) and `resources/fabric.mod.json`:

| Test | Covers |
|---|---|
| `RegisterableBlockGameTest` | `ModRegistryHelper` registers the block/item/BE trio under expected ids |
| `ImplementedInventoryGameTest` | NBT save/load round-trip + a vanilla hopper feeding the fixture container |
| `ScreenHandlerGameTest` | Simple/DoubleWide slot layout + shift-click routing both directions |
| `SimpleSeatEntityGameTest` | sit / dismount / auto-despawn with no leaked entities |
| `LootTableModifierGameTest` | injects into the targeted table only, then rolls it |

The whole `gametest` source set is created by `fabricApi.configureTests` (already present in
`26.1.2`'s root `build.gradle` — verified) and never ships.

### 2e. Docs

`README.md` (+47), `TEST_PLAN.md` (+176), `POTENTIAL_FEATURES.md` (+124). Copy verbatim, then sweep
for `26.2` / Java-25-specific claims and correct them to 26.1.2.

---

## 3. Conflict-risk files and their adaptations

Four files were touched by both the 26.2 port and the payload.

### `colors/ColorHelpers.java` — **must be hand-merged**

The 26.2 port rewrote `getDye`, `getWool`, and the other color switches from flat constants to
`ColorCollection` accessors:

```java
// on main (26.2):
case "white" -> Items.DYE.white();
case "light_gray" -> Items.DYE.lightGray();

// what you must write on 26.1.2:
case "white" -> Items.WHITE_DYE;
case "light_gray" -> Items.LIGHT_GRAY_DYE;
```

The payload does **not** change those switch bodies — it changes the palette arrays, adds `getTints`,
and bounds `getTint`. So: take the payload's changes, leave every existing 26.1.2 switch body alone.
`git apply --3way` will likely handle this on its own since the edits are in disjoint regions; verify
by grepping for `.white()` afterwards.

Full 26.2→26.1.2 color reverse map is in [README.md](README.md) §5. It covers `Blocks.WOOL`,
`CARPET`, `CONCRETE`, `CONCRETE_POWDER`, `STAINED_GLASS`, `STAINED_GLASS_PANE`, `GLAZED_TERRACOTTA`,
`DYED_TERRACOTTA`, `BANNER`, `WALL_BANNER`, `BED`, `DYED_SHULKER_BOX`, `DYED_CANDLE`,
`DYED_CANDLE_CAKE`, and `Items.DYE`.

### `inventories/ImplementedInventory.java`

The 26.2 port added `org.jspecify.annotations.NonNull` to `getItem`, `removeItem`, and
`removeItemNoUpdate`. **jspecify is already on the 26.1.2 classpath** — verified, it is used by
`archaeology-tweaks/.../SuspiciousRootedDirtBlock.java` and chimeric-lib's own
`DoubleWideInventoryScreen`/`SimpleInventoryScreen`. So you may keep the annotations or drop them;
either compiles. Prefer **dropping** them, since they are a port artifact rather than payload and
keeping them makes the file diverge from the rest of the 26.1.2 branch's style for no reason.

The payload's actual changes here (`isMatchingPartialStack`, `clearContent`) are unrelated to the
annotations and should apply cleanly.

### `screen/SimpleInventoryScreenHandler.java`, `screen/DoubleWideInventoryScreenHandler.java`

The port touched both, but the payload guts them (−82 lines each) down to thin subclasses of the new
`InventoryScreenHandler`. The port's edits are therefore moot. If the patch conflicts, **take the
payload version wholesale**, then compile — any 26.2 symbol that survives will surface immediately
and is covered by §5's reverse map.

### `testkit/BootstrapMinecraft.java` — **new file, requires a deletion**

The `main` version bakes data components, which is a **26.2-only** requirement:

```java
HolderLookup.Provider provider = VanillaRegistries.createLookup();
BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.build(provider)
    .forEach(pending -> pending.apply());
```

On 26.1.2, item components are bound at construction, not lazily during a server reload, and
`BuiltInRegistries.DATA_COMPONENT_INITIALIZERS` does not exist (verified: zero hits on the `26.1.2`
tree). **Delete those three lines**, the `VanillaRegistries` / `HolderLookup` / `BuiltInRegistries`
imports, and the `baked` guard's justification comment. Keep the class, the `@BeforeAll`, the
idempotence guard, and the rest of the javadoc — `SharedConstants.tryDetectVersion()` +
`Bootstrap.bootStrap()` is still exactly what is needed.

Rewrite the paragraph beginning *"As of MC 26.2 an item's data components are data-driven…"* rather
than deleting it silently; replace it with a one-line note that the bootstrap populates the static
registries so headless `ItemStack` construction works.

If `:chimeric-lib:fabric:test` then fails with *"Components not bound yet"*, that would mean 26.1.2
shares the lazy-binding behavior after all — in that case restore the bake and find the 26.1.2
equivalent. Expected outcome: it passes without it.

---

## 4. Procedure

```bash
git checkout -b backport/26.1.2/chimeric-lib backport/26.1.2/shared-build

git diff --binary c5f2cc4d..main -- chimeric-lib/ > /tmp/lib.patch
git apply --3way /tmp/lib.patch     # or --reject, then hand-merge

# drop any build-file hunks that slipped through
git checkout backport/26.1.2/shared-build -- \
    chimeric-lib/build.gradle chimeric-lib/gradle.properties \
    chimeric-lib/common/build.gradle chimeric-lib/fabric/build.gradle \
    chimeric-lib/neoforge/build.gradle
```

Then work §3's four files, then:

```bash
./gradlew :chimeric-lib:common:build :chimeric-lib:fabric:build :chimeric-lib:neoforge:build
./gradlew :chimeric-lib:fabric:test
./gradlew :chimeric-lib:fabric:runGameTest
```

Suggested commit split, so the wave stays bisectable:

1. `fix(chimeric-lib): partial-stack merge + fixed-size clearContent (1.6, 4.7)`
2. `fix(chimeric-lib): lazy texture fallback in BlockConfig (2.4)`
3. `refactor(chimeric-lib): unify screen handlers behind InventoryScreenHandler (3.2, 2.6)`
4. `refactor(chimeric-lib): ColorHelpers palette immutability + getTint bounds (4.7)`
5. `refactor(chimeric-lib): add AbstractWrenchItem, ContainerOpenersCounters, BlockUtils, LootModifierHelper (3.3–3.6, 3.9)`
6. `test(chimeric-lib): testFixtures + JUnit suites + fabric GameTests`
7. `docs(chimeric-lib): README, TEST_PLAN, POTENTIAL_FEATURES`

---

## 5. Done criteria

- [ ] `:chimeric-lib:{common,fabric,neoforge}:build` green.
- [ ] `:chimeric-lib:fabric:test` — ~30 tests pass.
- [ ] `:chimeric-lib:fabric:runGameTest` — 5 tests pass.
- [ ] All five new API classes exist and are public:
      `screen/InventoryScreenHandler`, `inventories/ContainerOpenersCounters`,
      `item/AbstractWrenchItem`, `blocks/BlockUtils`, `neoforge/loot/LootModifierHelper`.
- [ ] `git grep -n 'DATA_COMPONENT_INITIALIZERS\|\.white()\|\.lightGray()\|weathering()' -- 'chimeric-lib/**'`
      returns nothing.
- [ ] `testFixtures` variant resolves from a consumer: pick any mod and confirm
      `testImplementation(testFixtures(project(":chimeric-lib:common")))` compiles.
- [ ] Report to the orchestrator that Wave 2 is unblocked, naming any API that did **not** make it.
