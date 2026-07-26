# Backport: houdini-block

| | |
|---|---|
| **Branch** | `backport/26.1.2/houdini-block` |
| **Branch from** | `backport/26.1.2/chimeric-lib` (Wave 1) |
| **Wave** | 2 — parallel |
| **Payload** | `git diff c5f2cc4d..main -- houdini-block/` — 10 files, +350 / −100 |
| **Risk** | Medium — one API to verify (§3) |
| **Conflict-risk files** | **none** |
| **Depends on chimeric-lib** | nothing beyond the build wiring |
| **Gate** | `./gradlew :houdini-block:fabric:runGameTest` (`HoudiniBlockDropGameTest`) |

Read [README.md](README.md) §5 and §7 first.

---

## 1. Payload commits

```
1beaea9c / 9006fddb  fix(houdini-block): stop spawning ghost items and blocks on the client (2.8)
d23ae3c5             fix(houdini-block): drop the loot table, keep the manual spawn (double-drop)
41ab0adb / 2b53182a  docs + test plan
```

---

## 2. Change inventory

### 2.8 — client-side ghost items and blocks

`HoudiniBlock.replaceWithBlockInHand` and `playerWillDestroy` both called `world.addFreshEntity` and
`setBlockAndUpdate` with **no `isClientSide` guard**. A client running those paths spawned its own
copy of the dropped Houdini Block — which popped out of existence the moment the server synced — and
swapped the block locally before the server said so.

`HoudiniBlock.java` (+21/−7) adds guards at three levels, deliberately belt-and-braces:

- `replaceWithBlockInHand`: particles stay unguarded (they are a level event, fired on both sides by
  design); the item spawn and the block swap move inside `if (!world.isClientSide())`.
- `playerWillDestroy`: same split — particles outside, item spawn and the waterlogged→water
  replacement inside the guard.
- `spawnHoudiniBlockItem`: an early `if (world.isClientSide()) return;` even though both callers
  already guard, with a comment saying why.

Keep all three guards and the comments. The redundancy is intentional.

### The double-drop bug (found while testing 2.8)

The block hands itself back manually in `playerWillDestroy` and `replaceWithBlockInHand`, **and** it
also carried a loot table: `Properties.ofFullCopy(Blocks.STONE)` inherits stone's derived-name drops,
which resolve to `data/houdiniblock/loot_table/blocks/houdini_block.json`. A real player breaking it
with the correct tool therefore got the loot-table drop *on top of* the manual spawn — **two Houdini
Blocks for one**.

Two changes:

- `blocks/ModBlocks.java` (+4): add `.noLootTable()` to the block properties, with the comment
  explaining that `ofFullCopy` is what pulled the drops in.
- **Delete** `houdini-block/common/src/main/resources/data/houdiniblock/loot_table/blocks/houdini_block.json`
  (−21).

### Tests

`fabric/src/gametest/java/.../HoudiniBlockDropGameTest.java` (+119) plus its
`gametest/resources/fabric.mod.json` (+24). This is the regression gate for the double-drop — it
asserts exactly one item entity after a survival break.

### Docs

`README.md` (+45), `TEST_PLAN.md` (+109), `POTENTIAL_FEATURES.md` (+50).

---

## 3. Known adaptation — verify `noLootTable()`

`BlockBehaviour.Properties.noLootTable()` is **not used anywhere on the `26.1.2` branch** (verified:
zero hits), so its existence there is unconfirmed. Check before assuming:

```bash
JAR=~/.gradle/caches/fabric-loom/minecraftMaven/net/minecraft/minecraft-merged-deobf/26.1.2/minecraft-merged-deobf-26.1.2.jar
unzip -o "$JAR" 'net/minecraft/world/level/block/state/BlockBehaviour$Properties.class' -d /tmp/mc
javap -p '/tmp/mc/net/minecraft/world/level/block/state/BlockBehaviour$Properties.class' | grep -i loot
```

It has existed under this name since 1.20 and is very likely present. If it is **not**, the
equivalent on that version is `.lootFrom(...)` pointing at an empty table, or keeping the JSON file
but replacing its contents with an empty pool. Either satisfies the requirement — the invariant to
preserve is **"exactly one Houdini Block drops when a player breaks it with the correct tool."** The
gametest enforces that regardless of how you get there; report which approach you used.

Nothing else in this mod needs adaptation. The 26.2 port did not touch any houdini-block source file.

---

## 4. Procedure

```bash
git checkout -b backport/26.1.2/houdini-block backport/26.1.2/chimeric-lib

git diff --binary c5f2cc4d..main -- houdini-block/ > /tmp/hb.patch
git apply --3way /tmp/hb.patch

git checkout backport/26.1.2/chimeric-lib -- houdini-block/build.gradle houdini-block/gradle.properties

./gradlew :houdini-block:common:build :houdini-block:fabric:build :houdini-block:neoforge:build
./gradlew :houdini-block:fabric:runGameTest
```

Suggested commit split:

1. `fix(houdini-block): stop client ghost items and blocks (2.8)`
2. `fix(houdini-block): drop the loot table, keep the manual spawn`
3. `test(houdini-block): HoudiniBlockDropGameTest`
4. `docs(houdini-block): README, TEST_PLAN, POTENTIAL_FEATURES`

---

## 5. Done criteria

- [ ] `:houdini-block:{common,fabric,neoforge}:build` green.
- [ ] `:houdini-block:fabric:runGameTest` green.
- [ ] `data/houdiniblock/loot_table/blocks/houdini_block.json` is gone (or provably drops nothing).
- [ ] All three `isClientSide` guards from §2 present, with their comments.
- [ ] Manual check in survival: break a Houdini Block with a pickaxe → **exactly one** drops.
- [ ] Manual check in multiplayer or with a dedicated server: right-click a Houdini Block with a
      block in hand → no ghost item flashes and disappears.
