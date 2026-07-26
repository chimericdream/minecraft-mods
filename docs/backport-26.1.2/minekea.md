# Backport: minekea

| | |
|---|---|
| **Branch** | `backport/26.1.2/minekea` |
| **Branch from** | `backport/26.1.2/chimeric-lib` (Wave 1) |
| **Wave** | 2 — parallel |
| **Payload** | `git diff c5f2cc4d..main -- minekea/` — 4,035 files… of which **24 are Java** |
| **Risk** | **High** — but concentrated in ~6 files |
| **Conflict-risk files** | 8 (see §4) |
| **Depends on chimeric-lib** | `AbstractWrenchItem`, `ContainerOpenersCounters`, `ImplementedInventory` (1.6), `InventoryScreenHandler` |
| **Gate** | `./gradlew :minekea:fabric:runGameTest` (3 tests) + full datagen regen |

Read [README.md](README.md) §5 and §7 first.

## 0. The file count is misleading — read this before you start

Of 4,035 changed files:

- **3,993** are `minekea/common/src/main/generated/**` — datagen output. **Do not port these.**
  3,840 of them are a single removed trailing newline; roughly 26 have real content changes, all of
  which are consequences of the Java changes below. **Regenerate them instead** (§5).
- **11** are `minekea/demo-world/**` — a standalone Python generator plus its outputs. Version-independent.
- **24** are Java. This is the actual work.
- The rest: `build.gradle`, `gradle.properties` (Wave 0's), `fabric.mod.json`, three docs.

So: extract the payload **excluding** `generated/`, do the Java work, then run datagen and commit
whatever it produces.

---

## 1. Payload commits

Two distinct workstreams that barely interact:

**A. Code (6 commits):**
```
d460136c  refactor(minekea): armoire renders armor directly instead of 4 entities per block
37f1508a  fix(minekea): glass jar renderer crashed on any fluid but honey/milk
063819c5  fix(minekea): guard OpenShutterHalfBlock against a missing parent shutter
ac7a0b8f  fix(minekea): glass jar Container contract + hopper fill/full behavior   (1.1)
f9f3cc5f  fix(minekea): stop GlassJar item-entity cache leak + drop a datagen registry lookup (1.4)
a10f4cce  fix(minekea): glass jar bottling, hand threading, server-side mutation   (2.2, 2.9, 2.10)
3defb97e  refactor(minekea): replace oshi tuples with records + ItemStorageBlock fixes (4.6, 4.8)
c392de43  refactor(chimeric-lib): ContainerOpenersCounters factory                  (3.5 — consumer half)
8acc6af7  refactor(chimeric-lib): AbstractWrenchItem                                (3.4 — consumer half)
09fdcaf6  fix(minekea): data generation
```

**B. demo-world (20 commits, `e1ee7a0a` … `750a327b`).** A full redesign of the showcase generator.
Self-contained Python + generated artifacts; see §6.

---

## 2. Code changes — glass jar (the largest and most valuable cluster)

`GlassJarBlockEntity.java` is +244/−113 and carries four merged fixes. Take it as a unit.

**1.1a — the hopper NPE.** `GlassJarBlockEntity` implemented `ImplementedInventory` but returned
`null` from `getItems()`, so every inherited `Container` default (`getContainerSize`, `getItem`,
`setItem`, `removeItem`, `clearContent`) NPE'd the instant a vanilla hopper found the jar via
`instanceof Container`. Back it with a real `NonNullList`, and implement the contract for real —
reserve cascade into the active slot on `removeItem`, lossless extract/putback guard.

**1.1b — let hoppers fill past the top stack.** A one-slot container reads "full" once its slot hits
64, and a pushing hopper short-circuits on `isInventoryFull` *before* it ever calls `canPlaceItem` —
so items past the first 64 could never be inserted into the jar's compressed reserve by automation.
Present the jar as a **two-slot** `Container`: slot 0 is the real active slot; slot 1 is a virtual,
always-empty overflow input that routes insertions into the reserve via `tryInsert` and yields
nothing on extraction (invisible to pulling hoppers and to rendering).

**1.1c — make a full jar read as full (perf).** The two-slot design left slot 1 always empty, so a
pushing hopper next to a completely full jar never tripped `isInventoryFull` and re-probed
`canPlaceItem` (tag lookup + capacity math) every cooldown. When storage is at capacity,
`getItem(OVERFLOW_SLOT)` now reports a phantom full stack so the hopper stops at `isInventoryFull`'s
cheap early-out. The phantom is inert: never a valid merge target, and `canTakeItem` blocks the slot
from extraction so it cannot be pulled or duped.

**2.2 — `getBottle()` had an unreachable branch *and* a dead honey branch.** Two consecutive
`!hasFluid()` checks, the second unreachable. Worse, the honey branch compared the stored `Fluid`
against `ModFluids.HONEY_FLUID` — the `RegistrySupplier` *holding* it, not the fluid. Java permits
`==` between a class and an unrelated interface, so it compiled and was always false: a jar of honey
could never be bottled. Rewritten as an explicit if/else returning `null` (without draining) for
fluids with no bottled form.

**2.9 / 2.10** — hand threading and server-side mutation. See `CODE-REVIEW-PLAN.md`.

**1.4 — the client memory leak.** `GlassJarItemEntityCache` (+40/−5) was keyed on `ItemStack`, which
has no value-based `equals`/`hashCode`, so it was effectively identity-keyed. Item stacks are
recreated constantly (sync, GUI copies), so nearly every lookup missed, inserted a new entry, and
nothing was ever evicted — an unbounded per-frame client leak. Re-key on the components that actually
drive the jar's render state (`CUSTOM_DATA`, `ENTITY_DATA`, `CUSTOM_NAME` — all value-based) and
bound the map with LRU eviction at 256 entries.

Also: replace `VanillaRegistries.createLookup()` (rebuilds the entire vanilla registry set on every
cache miss, and cannot see modded registry contents) with the level's `registryAccess()` in the jar's
item-stack deserialization paths.

**Renderer crash.** `GlassJarBlockEntityRenderer`'s `getFluidColor`/`getFluidTexture` routed through
`getAttributes()`, which only handled honey and milk and threw `IllegalArgumentException` for
anything else. That runs in the render path, and the block accepts any fluid (`canAcceptFluid`
returns true for an empty jar; it fills from any bucket or bottle including water and lava) — so a
normal water or lava jar **hard-crashed the client on render**. Both platform renderers
(`FabricGlassJarBlockEntityRenderer`, `NeoForgeGlassJarBlockEntityRenderer`, +28/−11 each) now handle
water, lava, milk and honey explicitly and fall back to rendering as water for anything else, never
throwing. Water uses `block/water_still` tinted `0x3F76E4`; lava uses `block/lava_still` untinted.

---

## 3. Code changes — everything else

**Armoire armor rendering (`d460136c`).** Chestplates and leggings used to be displayed by equipping
invisible, small, marker armor stands placed inside the block — four entities per armoire. They are
now rendered directly in the BER.

- `ArmoireBlockEntityRenderer.java` (+101/−1): bakes `ArmorModelSet<ArmorStandArmorModel>` from
  `ModelLayers.ARMOR_STAND_SMALL_ARMOR`, builds an `EquipmentLayerRenderer` from
  `ctx.entityRenderer().equipmentAssets` and the `AtlasIds.ARMOR_TRIMS` atlas, and submits chest/legs
  layers under the exact transform `LivingEntityRenderer` applied to the old stands
  (`STAND_Y_OFFSET = 0.78125`, per-facing x/z offsets, `Axis.YP.rotationDegrees(180 - yaw)`,
  `scale(-1,-1,1)`, `translate(0,-1.501,0)`).
- `ArmoireBlockEntityRenderState.java` (+11): new `StandArmorData` record.
- `ArmoireBlockEntity.java` (+31/−156): drops the stand-spawning code; keeps legacy-stand discard.
- `minekea.accesswidener`: drops `ArmorStand setSmall`/`setMarker`; adds
  `accessible field net/minecraft/client/renderer/entity/EntityRenderDispatcher equipmentAssets`.

> **Risk note, resolved.** This looks like the scariest file in the backport because it uses
> `SubmitNodeCollector`. It is not: **the render-feature overhaul landed in MC 26.1, not 26.2** —
> `SubmitNodeCollector` is already used on the `26.1.2` branch by nine files including this very
> renderer. Verified. What still needs checking on 26.1.2 is the narrower armor API:
> `ArmorModelSet.bake`, `ModelLayers.ARMOR_STAND_SMALL_ARMOR`, `EquipmentLayerRenderer.renderLayers`
> (arity), `AtlasIds.ARMOR_TRIMS`, `Minecraft.getAtlasManager()`, and
> `EntityRenderDispatcher.equipmentAssets`. Use the `javap` recipe in [README.md](README.md) §5.
> If `renderLayers`' signature differs, adapt the call; the transform math is version-independent.

**`OpenShutterHalfBlock` orphan guard (`063819c5`, +16).** An open-shutter half only exists flanking
its parent `ShutterBlock`. If orphaned — a `/setblock`, world edit, the demo world, or a half-broken
shutter — `useWithoutItem` and `playerWillDestroy` cycled `OPEN` / read `WATERLOGGED` on whatever
non-shutter block sat where the parent should be, which throws and crashes. Both paths now bail out
when the centre block is not a shutter, so an orphaned half no-ops. `ArmoireBlock.java` gets the
same treatment (+11).

**4.6 — oshi tuples → records (`3defb97e`).** `CompressedBlocks` and `DyedBlocks` modeled their block
tables with `oshi.util.tuples` (`Pair`/`Triplet`/`Quartet` — the *hardware-info* library's tuple
classes) read via opaque `data.getA().getB()/getC()/getD()` chains. Replaced with named domain
records: `CompressedEntry`/`ColumnEntry`/`MinekeaEntry` in `CompressedBlocks`, `DyedEntry` in
`DyedBlocks`, accessed by field name. **Pure 1:1 mechanical transform** — every value and list order
is preserved. Version-independent, and confirmed still applicable: `oshi.util.tuples` is imported on
`26.1.2` in `CompressedBlocks`, `DyedBlocks`, and `ArmoireBlockEntity`.

**4.8 — `ItemStorageBlock` fixes** (+5/−2), same commit.

**3.5 — `ContainerOpenersCounters` adoption.** `CrateBlockEntity` (+15/−28) and
`MinekeaBarrelBlockEntity` (+15/−27) drop their hand-rolled anonymous counters. **Wave 1 dependency.**

**3.4 — `WrenchItem`** shrinks 108 lines to a thin `AbstractWrenchItem` subclass. **Wave 1 dependency.**

**1.6 consumer — `ShelfBlockEntity`** (+7/−1): `tryInsert` compared the returned remainder against the
input with `ItemStack.matches`, but the default `tryInsert` can mutate and return that same instance
on a partial merge, so the comparison always read equal and the insert sound never played. Snapshot
the incoming count; treat a smaller remainder as "something was inserted".

**Tests** — three new GameTests in a new `minekea/fabric/src/gametest/` source set (+ its
`fabric.mod.json`):

| Test | Covers |
|---|---|
| `GlassJarContainerGameTest` (+221) | the 1.1 Container contract, overflow slot, phantom-full behavior |
| `GlassJarInteractionGameTest` (+136) | bottling (2.2), hand threading (2.9) |
| `OpenShutterHalfBlockGameTest` (+84) | the orphaned-half no-crash path and the normal open-then-close cycle |

---

## 4. Conflict-risk files

Eight files were touched by both the 26.2 port and the payload.

| File | Port did | Do this |
|---|---|---|
| `block/building/beams/Beams.java` | copper → `weathering()` accessors | Payload's **real** change is **one line**: a texture id (`purpur_pillar` → `purpur_pillar_top`/`_side`). The other 177 lines are CRLF. Apply the one line; touch nothing else. |
| `block/building/covers/Covers.java` | same | Same — **one real line**. |
| `block/building/compressed/CompressedBlocks.java` | same | Real work. Apply the records refactor (§3, 4.6) over 26.1.2's **flat** copper constants (`Blocks.EXPOSED_CUT_COPPER`, not `.weathering().exposed()`). Do not let the patch reintroduce 26.2 accessors. |
| `block/containers/GlassJarBlock.java` | renames only | Take payload; fix any 26.2 symbol via §5's map. |
| `entity/block/containers/GlassJarBlockEntity.java` | **one line**: `EntityType.byString(id)` → `Optional.ofNullable(Identifier.tryParse(id)).flatMap(BuiltInRegistries.ENTITY_TYPE::getOptional)` | Reverse it back to `EntityType.byString(id)`. Everything else in this file is payload. `getStringOr`/`getIntOr` **exist on 26.1.2** — verified, this file already uses them there. |
| `client/render/block/GlassJarBlockEntityRenderer.java` | renames only | Take payload (+3 lines). |
| `common/src/main/resources/minekea.accesswidener` | added a `TextureSlot create` entry | Payload edits a *different* region (ArmorStand → `equipmentAssets`). Apply the payload hunk; **do not** add the `TextureSlot` entry — it is 26.2-only and `TextureSlot.create` is public on 26.1.2. |
| `fabric/.../data/ModDataGenerator.java` | `this::valueLookupBuilder` → `this::builder` (×3) | ⚠ **Keep 26.1.2's `valueLookupBuilder`.** And the payload's only change to this file is the 8-line `DATA_COMPONENT_INITIALIZERS` bind at the top of `buildRecipes()` — that is a **26.2-only workaround**. `BuiltInRegistries.DATA_COMPONENT_INITIALIZERS` does not exist on 26.1.2. **Drop the entire hunk, including the `BuiltInRegistries` import.** |

Also expect `TagAppender<T>` → `TagAppender<T, T>` and `.add(X.builtInRegistryHolder().key())` →
`.add(X)` anywhere the payload touches datagen tag code.

---

## 5. Regenerating the datagen output

**Do not port `minekea/common/src/main/generated/**`.** After the Java changes compile:

```bash
./gradlew :minekea:fabric:runDatagen
```

(The `datagen` run config is defined in `minekea/fabric/build.gradle`.) Then:

```bash
git status --short minekea/common/src/main/generated | head -50
```

Expect a small, explainable diff — the beam/cover texture-id fix, and anything the records refactor
shifted. **If you get thousands of changed files, stop**: it means Wave 0's `.gitattributes`
renormalization did not land, or datagen ran with different line-ending settings. Fix that first.

If datagen throws, it is *not* the 26.2 "Components not bound yet" problem — that is 26.2-only and is
exactly why the bind hunk is dropped in §4. Diagnose normally.

---

## 6. demo-world

`minekea/demo-world/` is a self-contained Python showcase generator: `generate_layout.py`,
`extract_jar_contents.py`, and their outputs (`demo_build.mcfunction`, `demo_layout_manifest.csv`,
`LAYOUT.md`, `layout_stats.json`, `layout_regions.txt`, `block_inventory.txt`,
`glass_jar_contents.csv`, `README.md`, `.gitignore`).

20 payload commits reshaped it: a by-material grouping with a rising staircase, flat colour-gradient
regions, fixed block-family bands per wood/stone set, a clear-and-reset step, a smooth-sandstone
floor, filled glass jars with real mob NBT, and a tool item-frame showcase wall.

**It is Python and mcfunction — no Minecraft API.** Copy all 11 files verbatim from `main`. Per
`CLAUDE.md`, the generated artifacts are **never hand-edited**; regenerate instead:

```bash
cd minekea/demo-world && python generate_layout.py
```

The generator hard-codes block ids from minekea's registry. If the 26.1.2 branch has a different
block set than `main` (it should not — no blocks were added or removed in the payload), the generator
will surface it. Verify the output is byte-identical to what you copied; if not, commit the
regenerated version and say so.

⚠ `demo_build.mcfunction` references vanilla ids and block states. Spot-check that nothing in it is
26.2-only — in particular the copper block ids, which changed *representation* in code but not in
data. Data ids (`minecraft:exposed_cut_copper`) are stable across both versions.

---

## 7. Procedure

```bash
git checkout -b backport/26.1.2/minekea backport/26.1.2/chimeric-lib

# Everything EXCEPT generated output
git diff --binary c5f2cc4d..main -- minekea/ ':!minekea/common/src/main/generated' > /tmp/mk.patch
git apply --3way /tmp/mk.patch

git checkout backport/26.1.2/chimeric-lib -- minekea/build.gradle minekea/gradle.properties
```

Then work §4's eight files, then:

```bash
./gradlew :minekea:common:build :minekea:fabric:build :minekea:neoforge:build
./gradlew :minekea:fabric:runDatagen
./gradlew :minekea:fabric:runGameTest
```

Suggested commit split:

1. `fix(minekea): glass jar Container contract, bottling, cache leak (1.1, 1.4, 2.2, 2.9, 2.10)`
2. `fix(minekea): glass jar renderer handles every fluid`
3. `refactor(minekea): armoire renders armor directly instead of spawning stands`
4. `fix(minekea): guard OpenShutterHalfBlock and ArmoireBlock against a missing parent`
5. `refactor(minekea): oshi tuples → records; adopt ContainerOpenersCounters and AbstractWrenchItem (3.4, 3.5, 4.6, 4.8)`
6. `test(minekea): fabric gametest source set + 3 GameTests`
7. `chore(minekea): regenerate datagen output`
8. `feat(minekea): demo-world by-material redesign` (all 11 demo-world files)
9. `docs(minekea): README, TEST_PLAN, POTENTIAL_FEATURES`

---

## 8. Done criteria

- [ ] `:minekea:{common,fabric,neoforge}:build` green.
- [ ] `:minekea:fabric:runGameTest` — 3 tests green.
- [ ] `:minekea:fabric:runDatagen` produces a **small, explainable** diff.
- [ ] `git grep -n 'weathering()\|DATA_COMPONENT_INITIALIZERS\|\.builtInRegistryHolder().key()\|EntityTypes\.' -- 'minekea/**'`
      returns nothing.
- [ ] `ModDataGenerator.buildRecipes()` has **no** component-binding block.
- [ ] `minekea.accesswidener` has `equipmentAssets`, no `ArmorStand setSmall/setMarker`, and no
      `TextureSlot create`.
- [ ] Visual check on the armoire: put a chestplate and leggings in one, confirm they render in place
      and that no armor-stand entities are spawned (`/kill @e[type=armor_stand]` should find none).
      The `mc-visual-smoke-test` skill covers this workflow, but its code samples are 26.2 — adapt.
- [ ] Visual check on the glass jar: fill one with water and one with lava, confirm neither crashes
      the client.
- [ ] `demo-world/` regenerates cleanly.
