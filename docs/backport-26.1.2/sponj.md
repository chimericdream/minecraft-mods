# Backport: sponj

| | |
|---|---|
| **Branch** | `backport/26.1.2/sponj` |
| **Branch from** | `backport/26.1.2/chimeric-lib` (Wave 1) |
| **Wave** | 2 — parallel |
| **Payload** | `git diff c5f2cc4d..main -- sponj/` — 15 files, +570 / −350 |
| **Risk** | Medium |
| **Conflict-risk files** | 2 (`SponjBlock.java`, `LavaSponjBlock.java`) — **both trivially resolved, see §3** |
| **Depends on chimeric-lib** | `BlockUtils` (moved there in Wave 1) |
| **Gate** | `./gradlew :sponj:fabric:runGameTest` (`SponjAbsorptionRangeGameTest`) |

Read [README.md](README.md) §5 and §7 first.

---

## 1. Payload commits

```
b72f4be6  fix(sponj): absorb over the intended radius, not its square root   (2.1 — SUPERSEDED)
04f128e9  fix(sponj): cap the connected-sponj count explicitly instead of by distance  (2.1 — final)
6a317eb0  fix(sponj): bound absorption by an explicit connected-sponj cap (#59)
d0ed03cc  refactor(sponj): merge sponge blocks + move BlockUtils to chimeric-lib (3.3, 3.9)
41ab0adb / 2b53182a  docs + test plan
```

⚠ **`b72f4be6` and `04f128e9` are a fix and its partial revert.** Do not apply them as two steps —
apply the net diff. §2a explains why.

---

## 2. Change inventory

### 2a. Item 2.1 — the absorption bound (read this carefully)

The first attempt (`b72f4be6`) observed that `BlockUtils.isWithinDistance` compared `distSqr` (a
*squared* distance) against a raw limit, so the connected-sponj flood fill stopped at
`sqrt(32) ≈ 5.7` blocks instead of 32, and "fixed" the units.

The follow-up (`04f128e9`) reverted that: **the small reach was deliberate load-bounding, not a units
bug.** A sponj's clear radius is `6 + 3*(n-1)` and its block budget is `64 * n`, both scaling with the
connected count `n` — so a large sponj wall at full 32-block reach would clear tens of thousands of
liquid blocks in one tick and lag the server. The `distSqr`-vs-32 comparison was an obscure way of
bounding that.

**The shipped behavior** caps the connected-sponj count explicitly rather than relying on an
accidental distance quirk. Backport the final state, and keep the comment explaining why the bound
exists — the next reader will otherwise "fix" it again.

### 2b. Items 3.3 / 3.9 — the block hierarchy merge

The four sponge blocks were ~95% identical. Two new bases in sponj:

| New file | Contains |
|---|---|
| `blocks/AbstractSponjBlock.java` (+146) | the dry-sponge absorb / flood-fill logic, parameterized by fluid tag, dry↔wet block pair, connected-block list, break-particle fluid state, and whether replaceable "washable" blocks are cleared (water only) |
| `blocks/AbstractWetSponjBlock.java` (+82) | the wet-sponge dry-out + drip-particle logic, parameterized by dry block, drip particle, and the per-fluid "should dry out" predicate |

The four leaves shrink accordingly: `SponjBlock` (+24/−90), `LavaSponjBlock` (+24/−77),
`WetSponjBlock` (+18/−58), `WetLavaSponjBlock` (+13/−51). `ModBlocks.java` gains 17 lines.

**3.9 — `sponj/common/.../BlockUtils.java` is deleted (−70)** and its contents move to
`chimeric-lib/common/.../blocks/BlockUtils.java`. The chimeric-lib half lands in Wave 1; this branch
deletes the local copy and updates imports. **If Wave 1 did not land `BlockUtils`, stop and report
it** — do not keep a duplicate.

### 2c. Tests

`fabric/src/gametest/java/.../SponjAbsorptionRangeGameTest.java` (+130) plus its
`gametest/resources/fabric.mod.json` (+24). This is the regression gate for 2a — it is the reason the
revert-of-a-revert is safe. Do not drop it.

### 2d. Docs

`README.md` (+3/−2), `TEST_PLAN.md` (+114), `POTENTIAL_FEATURES.md` (+52).

---

## 3. Conflict-risk files — both are non-events

The 26.2 port touched `SponjBlock.java` and `LavaSponjBlock.java` for exactly one reason:
`net.minecraft.util.Tuple` was removed in 26.2, so the flood-fill queue was rewritten from
`Tuple<BlockPos, Integer>` / `new Tuple<>(a,b)` / `.getA()` / `.getB()` to
`com.mojang.datafixers.util.Pair` / `Pair.of(a,b)` / `.getFirst()` / `.getSecond()`.

**No reversal is needed.** `com.mojang.datafixers.util.Pair` is a DataFixerUpper class and is on the
Minecraft classpath in 26.1.2 exactly as it is in 26.2. Keep the `Pair` form the payload's
`AbstractSponjBlock` uses.

(Reversing to `Tuple` would also work, since `net.minecraft.util.Tuple` still exists on 26.1.2 — but
it would gratuitously diverge from `main` and make the next forward-port harder. Use `Pair`.)

Both files are largely rewritten by the payload anyway (their logic moves to `AbstractSponjBlock`),
so a `--3way` conflict here should be resolved by **taking the payload version wholesale**.

---

## 4. Procedure

```bash
git checkout -b backport/26.1.2/sponj backport/26.1.2/chimeric-lib

git diff --binary c5f2cc4d..main -- sponj/ > /tmp/sponj.patch
git apply --3way /tmp/sponj.patch

git checkout backport/26.1.2/chimeric-lib -- sponj/build.gradle sponj/gradle.properties

./gradlew :sponj:common:build :sponj:fabric:build :sponj:neoforge:build
./gradlew :sponj:fabric:runGameTest
```

Suggested commit split:

1. `fix(sponj): bound absorption by an explicit connected-sponj cap (2.1)`
2. `refactor(sponj): merge sponge blocks behind AbstractSponjBlock/AbstractWetSponjBlock (3.3)`
3. `refactor(sponj): move BlockUtils to chimeric-lib (3.9)`
4. `test(sponj): SponjAbsorptionRangeGameTest`
5. `docs(sponj): README, TEST_PLAN, POTENTIAL_FEATURES`

---

## 5. Done criteria

- [ ] `:sponj:{common,fabric,neoforge}:build` green.
- [ ] `:sponj:fabric:runGameTest` green.
- [ ] `sponj/common/src/main/java/com/chimericdream/sponj/BlockUtils.java` **does not exist**;
      all references point at `com.chimericdream.lib.blocks.BlockUtils`.
- [ ] `git grep -c 'net.minecraft.util.Tuple' -- 'sponj/**'` returns nothing (the payload uses `Pair`).
- [ ] The connected-sponj cap has a comment explaining it is a deliberate load bound, not a bug.
- [ ] Manual check: a 3+ block sponj wall does not stall the server tick when placed next to a large
      water body.
