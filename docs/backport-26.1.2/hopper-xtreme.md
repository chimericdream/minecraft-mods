# Backport: hopper-xtreme

| | |
|---|---|
| **Branch** | `backport/26.1.2/hopper-xtreme` |
| **Branch from** | `backport/26.1.2/chimeric-lib` (Wave 1) |
| **Wave** | 2 — parallel |
| **Payload** | `git diff c5f2cc4d..main -- hopper-xtreme/` — 29 files, +1,900 / −3,100 |
| **Risk** | **High** — the largest refactor in the payload, on the mod's core transfer logic |
| **Conflict-risk files** | **none** — the 26.2 port did not touch a single hopper-xtreme source file |
| **Depends on chimeric-lib** | `AbstractWrenchItem` |
| **Gate** | `./gradlew :hopper-xtreme:fabric:runGameTest` — **20 tests must be green** |

Read [README.md](README.md) §5 and §7 first.

The 26.2 port changed only `hopper-xtreme/gradle.properties` in this mod — no Java. Verified. That
means **the payload patch should apply cleanly**, and any compile error you see afterwards is a real
26.1.2 API gap, not a merge artifact.

---

## 1. Payload commits

```
496e148d  fix(hopper-xtreme): correct filtered-hopper menu slot geometry            (1.2, 1.3)
54fc6a6f  fix(hopper-xtreme): guard canExtract's cast, skip the filter slot in isFull (2.6, 2.11, 2.12)
8acc6af7  refactor(chimeric-lib): extract shared AbstractWrenchItem                  (3.4 — consumer half)
1b8d84e5  refactor(hopper-xtreme): simplify HopperItemFilterItem.use + FilterSlot.mayPlace (4.4)
a96de329  docs(hopper-xtreme): correct FilterSlot.mayPlace comment
9d09bbf1  docs(hopper-xtreme): WIP design for 3.1 BE base-class extraction
bc0359e7  refactor(hopper-xtreme): extract AbstractXtremeHopperBlockEntity base      (3.1)
084eb51c  docs(hopper-xtreme): mark 3.1 BE extraction applied
1b6eaa76  fix(hopper-xtreme): drop multi-hupper's isFilter==slot5 extract gate
04bfc976  docs(hopper-xtreme): document the deferred step-4 block/screen collapse
78a23bc9  feat(hopper-xtreme): persist multi-hopper round-robin cursor to NBT
41ab0adb / 2b53182a / 3aaa3bb2  docs + test-plan
```

**Apply them roughly in this order.** Unlike the other mods, this payload has real internal
sequencing: the bugfixes (`496e148d`, `54fc6a6f`) land in six duplicated copies, then `bc0359e7`
folds those six copies into one base class, then `1b6eaa76` and `78a23bc9` build on the base class.
Applying the whole diff in one shot also works (the end state is the same), but if you hit trouble,
staging in this order lets you run the gametests between steps.

---

## 2. Change inventory

### 2a. The 3.1 base-class extraction — the centerpiece

Six `*BlockEntity` classes were ~80% identical copies of a vanilla-hopper fork, ~3,700 lines total.
They collapse to:

```
AbstractXtremeHopperBlockEntity                    (+623)  shared tick/insert/extract/transfer/
  ├─ AbstractSingleFacingXtremeHopperBlockEntity   (+92)   filter/load-save, parameterized by hooks:
  │    ├─ XtremeHopperBlockEntity                  (−512)  storageSlotCount / extractSide /
  │    ├─ XtremeHupperBlockEntity                  (−517)  inputBlockYOffset / levelYOffset /
  │    └─ GlazedHopperBlockEntity                  (−519)  pushOutput
  └─ AbstractMultiXtremeHopperBlockEntity          (+214)
       ├─ AbstractDownMultiXtremeHopperBlockEntity (+65)
       │    ├─ XtremeMultiHopperBlockEntity        (−598)
       │    └─ GlazedMultiHopperBlockEntity        (−625)
       └─ XtremeMultiHupperBlockEntity             (−586)
```

Output strategy (insert into a facing container vs. drop in front) and targeting (single `FACING` vs.
round-robin over connected sides) live in the two intermediates. Each leaf is now its identity
constants plus a handful of hook overrides. Net **−3,250 lines**.

**Two per-variant quirks were preserved verbatim — keep them that way:**

- the hupper / multi-hupper `UP` pull geometry (`SUCK_AABB`, the ±0.5 / 0.0 Y offsets);
- the multi-hupper's `canExtract` gate — preserved via an overridable `passesExtractFilter` hook
  rather than silently "fixed". (It is then removed on purpose by `1b6eaa76`; see 2c.)

`HopperVariantBlock.java` gains 2 lines. Block classes and screen handlers were deliberately **not**
collapsed — that is "step 4", documented as deferred in `REFACTOR-3.1-PLAN.md`. Do not attempt it.

### 2b. Bug fixes folded into the base class

| Item | Bug | Fix |
|---|---|---|
| **1.2 / 1.3** | The filter slot is the last container slot, but the server BE hides it from the `Container` API (`getContainerSize()` returns the storage-only count) while the client builds the menu over a dummy `SimpleContainer` that does not. Deriving geometry from `getContainerSize()` disagreed across sides. `NonFilterSlot` read the filter at `getContainerSize()-1` — on the server a real storage slot, so an ordinary item placed there was treated as the filter and the GUI rejected every insert. `quickMoveStack` used `getContainerSize()+1` as the hopper/player boundary: 6 server-side, 7 client-side, so shift-click routing desynced. | Pass the filter-slot index explicitly; use the fixed hopper-slot count for the boundary. Centralized in the base class as `getContainerSize()` (the slot right after the storage slots), not a hard-coded 5 or 1. |
| **2.11** | `canExtract` cast its `hopper` argument straight to the mod's own BE, but the public entry point `extract(Level, Hopper)` accepts any vanilla `Hopper` — a hopper minecart driven through it threw `ClassCastException`. Present in all six copies. | `instanceof` pattern, matching the sibling `canInsert`. Foreign hoppers default to vanilla pull-from-above semantics. |
| **2.12** | `isFull()` iterated the backing list, so it counted the filter slot as storage. | Iterate `getContainerSize()`. |
| **2.6** | Menu open/close balance. | Via chimeric-lib's `InventoryScreenHandler`. |

Screen-handler files touched: `FilterSlot` (+5/−1), `NonFilterSlot` (+5/−2),
`FilteredHopperScreenHandler` (+24/−7), `FilteredGlazedHopperScreenHandler` (+20/−3),
`GlazedHopperScreenHandler` (+10), `HopperItemFilterScreenHandler` (+10).

### 2c. Behavior changes (not pure refactors)

**`1b6eaa76` — drop the multi-hupper's `isFilter == (slot == 5)` extract gate.**
`XtremeMultiHupperBlockEntity` was the only variant gating `canExtract` this way, so a non-filtered
multi-hupper refused to pull anything but the filter item out of a source's slot 5 (and refused the
filter item from any other slot). Dead code from an older filter design. Removing the override lets
it inherit the standard `passesExtractFilter` (unfiltered → extract everything), matching the other
five variants. The non-filtered hoppers/huppers are deprecated anyway.

**`78a23bc9` — persist the round-robin cursor.** The multi-hopper/hupper `lastDirection` cursor was
memory-only, so unload/reload reset it to the vertical direction: a hopper that had just handed off
to one side would repeat that side after reloading instead of continuing the rotation. Override
`load`/`saveAdditional` in `AbstractMultiXtremeHopperBlockEntity` to round-trip it via
`Direction.CODEC`, falling back to `verticalDirection()` when absent (fresh placement / old saves).

**`1b8d84e5` (4.4) — simplify `HopperItemFilterItem.use`.** Redundant nested client checks: an outer
`player.level() != null && !player.level().isClientSide()` (where the `world` param *is*
`player.level()`) wrapping an inner `if (!world.isClientSide())` around `sendOverlayMessage`.
Collapse to a single `!world.isClientSide()` gate. Also simplifies `FilterSlot.mayPlace`.

**`8acc6af7` (3.4) — `WrenchItem` shrinks from 111 lines to 2.** It becomes a thin subclass of
chimeric-lib's `AbstractWrenchItem`, supplying only `ITEM_ID` and registration properties. The
registry call site (`REGISTRY_HELPER.registerItem(ITEM_ID, WrenchItem::new)`) and the mod's tooltip
are unchanged. **Requires Wave 1 to have landed.**

### 2d. Tests

Three new GameTests, plus 3 lines registering them in `fabric/src/main/resources/fabric.mod.json`:

| Test | Covers |
|---|---|
| `ExtractGuardTest` (+124) | 2.11 — a foreign `Hopper` no longer throws |
| `FilterSlotGeometryTest` (+105) | 1.2/1.3 — server-side filter-slot read over a real BE, and the client-geometry quick-move boundary over the dummy container |
| `MultiHopperPersistenceTest` (+53) | `78a23bc9` — advance the cursor to NORTH, round-trip through NBT, assert reload resumes at SOUTH |

⚠ **hopper-xtreme's gametests live in `fabric/src/main/java/.../fabric/test/`, not in a
`src/gametest` source set** — unlike every other mod. That is pre-existing on `26.1.2` (it already
has `DeprecatedBlockConversionTest`, `PreventFilterExtractionTest`, `SixSlotTransferTest`,
`TransferSpeedTest` there). **Keep that layout.** `TEST_PLAN.md` notes migrating to the isolated
pattern as future work — do not do it here.

`ExtractGuardTest` is the mod's one file using a 26.2-only symbol: `EntityTypes.X`. Reverse it to
`EntityType.X` (`net.minecraft.world.entity.EntityType`).

### 2e. Docs

`README.md` (+86/−1), `TEST_PLAN.md` (+133), `POTENTIAL_FEATURES.md` (+59), `REFACTOR-3.1-PLAN.md`
(+166). Copy verbatim; sweep for `26.2` and correct. `REFACTOR-3.1-PLAN.md` documents the deferred
step-4 collapse and is referenced from `AbstractXtremeHopperBlockEntity`'s javadoc — keep both.

---

## 3. Known adaptations

Only one confirmed: `EntityTypes` → `EntityType` in `ExtractGuardTest` (§2d).

Everything else should compile as-is. Symbols verified present on `26.1.2`:
`getStringOr` (already used by all six hopper BEs), `ValueInput`/`ValueOutput`, `Direction.CODEC`.

If the base-class extraction hits an unexpected API gap, check with
`git grep '<Symbol>' 26.1.2 -- 'hopper-xtreme/**'` before changing anything — the six pre-refactor
copies on `26.1.2` contain essentially every API the base class needs.

---

## 4. Procedure

```bash
git checkout -b backport/26.1.2/hopper-xtreme backport/26.1.2/chimeric-lib

git diff --binary c5f2cc4d..main -- hopper-xtreme/ > /tmp/hx.patch
git apply --3way /tmp/hx.patch

git checkout backport/26.1.2/chimeric-lib -- \
    hopper-xtreme/build.gradle hopper-xtreme/gradle.properties   # Wave 0 owns these

./gradlew :hopper-xtreme:common:build :hopper-xtreme:fabric:build :hopper-xtreme:neoforge:build
./gradlew :hopper-xtreme:fabric:runGameTest
```

**Run the gametests before and after.** On `26.1.2` before the patch you should get 17 green; after,
20. If the count is wrong, the new tests are not registered in `fabric.mod.json` — check that hunk.

If `--3way` produces a mess (unlikely given zero conflict files), the fallback is to cherry-pick the
commits from §1 in order:

```bash
git cherry-pick -x 496e148d 54fc6a6f 1b8d84e5 a96de329 9d09bbf1 bc0359e7 084eb51c 1b6eaa76 04bfc976 78a23bc9
```

Several of those commits touch other mods; use `git cherry-pick -n` and reset the out-of-scope paths.

---

## 5. Done criteria

- [ ] `:hopper-xtreme:{common,fabric,neoforge}:build` green.
- [ ] `:hopper-xtreme:fabric:runGameTest` — **20 tests green.**
- [ ] The six leaf BEs are thin: none should exceed ~120 lines.
- [ ] `git grep -n 'EntityTypes\.\|BlockEntityTypes\.' -- 'hopper-xtreme/**'` returns nothing.
- [ ] `WrenchItem.java` is a ~2-line subclass of `AbstractWrenchItem`.
- [ ] `XtremeMultiHupperBlockEntity` no longer overrides `passesExtractFilter`.
- [ ] Gametests still live under `fabric/src/main/java/.../fabric/test/`, not `src/gametest`.
- [ ] Manual sanity check worth doing: place a multi-hopper, let it hand off to one side, reload the
      world, confirm it continues the rotation rather than repeating.
