# Backport: shulker-stuff

| | |
|---|---|
| **Branch** | `backport/26.1.2/shulker-stuff` |
| **Branch from** | `backport/26.1.2/chimeric-lib` (Wave 1) |
| **Wave** | 2 — parallel |
| **Payload** | `git diff c5f2cc4d..main -- shulker-stuff/` — 10 files, +450 / −115 |
| **Risk** | Medium |
| **Conflict-risk files** | **none** — the 26.2 port touched only this mod's `gradle.properties` |
| **Depends on chimeric-lib** | `ContainerOpenersCounters` (3.5), `LootModifierHelper` (3.6) |
| **Gate** | `./gradlew :shulker-stuff:fabric:runGameTest` — 5 tests |

Read [README.md](README.md) §5 and §7 first.

---

## 1. Payload commits

```
e084103e / 3f0958bd  fix(shulker-stuff): dye station viewer counting and output bookkeeping (2.5, 2.6, 2.7)
c392de43  refactor(chimeric-lib): ContainerOpenersCounters factory  (3.5 — consumer half)
e8aaffbe  refactor(chimeric-lib): NeoForge LootModifierHelper       (3.6 — consumer half)
f6f82add  docs+fix: inventory-abstraction convention + DyeStation.setItems (3.8)
41ab0adb / 2b53182a  docs + test plan
```

---

## 2. Change inventory

### 2.5 — the opener counter tested the wrong menu class

`ContainerOpenersCounter.isOwnContainer` tested `player.containerMenu instanceof ChestMenu` —
copy-pasted from the barrel. The dye station opens a `DyeStationScreenHandler`, so `recheckOpeners`
**never counted a single legitimate viewer**. Test for the real menu class and compare its container
to this block entity.

This is resolved by adopting chimeric-lib's `ContainerOpenersCounters.create(...)`, whose factory
takes the menu class as a **required** parameter precisely so this cannot be copy-pasted wrong again.
`DyeStationBlockEntity.java` goes +18/−30 as its ~40-line anonymous counter disappears.

### 2.6 — unbalanced menu open/close

The constructor issues `startOpen` but nothing issued `stopOpen`, so the opener counter never saw the
viewer leave. `DyeStationScreenHandler` gains:

```java
@Override
public void removed(Player player) {
    super.removed(player);
    this.inventory.stopOpen(player);
}
```

### 2.7 — shift-clicking the result did nothing

`quickMoveStack` used `invSlot < this.inventory.getContainerSize()` as the station/player boundary,
but the **result slot sits after the station's slots** and is backed by `output`, not `inventory`.
The result index therefore fell into the "player slot" branch, which then found no legal target — so
shift-clicking the crafted shulker box was a no-op.

The fix introduces three named constants and branches on them explicitly:

```java
public static final int STATION_SLOT_COUNT = DyeStationBlockEntity.INVENTORY_SIZE;  // 0..6
public static final int OUTPUT_SLOT_INDEX  = STATION_SLOT_COUNT;                    // the result
public static final int FIRST_PLAYER_SLOT  = OUTPUT_SLOT_INDEX + 1;
```

Also in this file: the output slot gains a `this::refreshOutput` callback so the result recomputes
from current inputs instead of relying on a `slots.get(6)` index that was already off by one.
`DyeStationScreenHandler.java` is +75/−10 — the largest single change in this mod.

### 3.8 — `DyeStationBlockEntity.setItems`

Was `clear()` + `addAll` on a `final`, fixed-size `NonNullList.withSize(...)`, which can throw and
lets the size drift off `INVENTORY_SIZE`. Copy in place (pad or truncate to our size) instead.

This is the concrete instance of the rule Wave 0 adds to `CLAUDE.md`:

> `setItems` must preserve the fixed slot count: copy in place
> (`for i: items.set(i, incoming.get(i))`), never `clear()+addAll` on a fixed-size `NonNullList`.

The broader normalization of every block entity onto that rule (`CrateBlockEntity`'s
`ImplementedInventory`-on-a-`Randomizable` mix, the crate/barrel loot-table gap) was explicitly left
as follow-up on `main`. **Do not attempt it here** — backport the one `setItems` fix only.

### 3.6 — `LootModifierRegistry`

`neoforge/.../LootModifierRegistry.java` (+2/−2): use `LootModifierHelper.createRegister(ModInfo.MOD_ID)`
instead of `DeferredRegister.create(NeoForgeRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS, ModInfo.MOD_ID)`,
dropping the `NeoForgeRegistries` import. **Wave 1 dependency.**

### Tests

`fabric/src/gametest/java/.../DyeStationGameTest.java` (+168) plus its
`gametest/resources/fabric.mod.json` (+25). Five tests covering the viewer-count, open/close balance,
and shift-click routing fixes. Verified green on `main` after the 3.8 fix.

### Docs

`README.md` (+70), `TEST_PLAN.md` (+126), `POTENTIAL_FEATURES.md` (+61).

---

## 3. Known adaptations

None expected. The 26.2 port did not touch a single shulker-stuff source file — only its
`gradle.properties`, which is Wave 0's. If the payload patch does not apply cleanly here, something
went wrong upstream; re-check that you branched off `backport/26.1.2/chimeric-lib`.

The one thing to verify at compile time is that chimeric-lib's `ContainerOpenersCounters.create(...)`
and `LootModifierHelper.createRegister(...)` exist with the signatures the payload calls.

---

## 4. Procedure

```bash
git checkout -b backport/26.1.2/shulker-stuff backport/26.1.2/chimeric-lib

git diff --binary c5f2cc4d..main -- shulker-stuff/ > /tmp/ss.patch
git apply --3way /tmp/ss.patch

git checkout backport/26.1.2/chimeric-lib -- shulker-stuff/build.gradle shulker-stuff/gradle.properties

./gradlew :shulker-stuff:common:build :shulker-stuff:fabric:build :shulker-stuff:neoforge:build
./gradlew :shulker-stuff:fabric:runGameTest
```

Suggested commit split:

1. `fix(shulker-stuff): dye station viewer counting, open/close balance, result shift-click (2.5, 2.6, 2.7)`
2. `fix(shulker-stuff): DyeStationBlockEntity.setItems preserves the fixed slot count (3.8)`
3. `refactor(shulker-stuff): adopt ContainerOpenersCounters and LootModifierHelper (3.5, 3.6)`
4. `test(shulker-stuff): DyeStationGameTest`
5. `docs(shulker-stuff): README, TEST_PLAN, POTENTIAL_FEATURES`

---

## 5. Done criteria

- [ ] `:shulker-stuff:{common,fabric,neoforge}:build` green.
- [ ] `:shulker-stuff:fabric:runGameTest` — 5 tests green.
- [ ] `DyeStationBlockEntity` has no hand-rolled anonymous `ContainerOpenersCounter`.
- [ ] `LootModifierRegistry` does not import `NeoForgeRegistries`.
- [ ] `DyeStationScreenHandler` defines `STATION_SLOT_COUNT` / `OUTPUT_SLOT_INDEX` / `FIRST_PLAYER_SLOT`
      and overrides `removed(Player)`.
- [ ] Manual check: open the dye station, craft a dyed shulker box, **shift-click the result** — it
      should move to your inventory.
