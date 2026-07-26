# Backport: archaeology-tweaks

| | |
|---|---|
| **Branch** | `backport/26.1.2/archaeology-tweaks` |
| **Branch from** | `backport/26.1.2/chimeric-lib` (Wave 1) |
| **Wave** | 2 — parallel |
| **Payload** | `git diff c5f2cc4d..main -- archaeology-tweaks/` — 6 files, +212 / −1 |
| **Risk** | **Trivial** |
| **Conflict-risk files** | 1 (`ATBrushableBlockEntity.java`) — nominal, see §3 |
| **Depends on chimeric-lib** | nothing beyond the build wiring |
| **Gate** | compile only — the mod has no automated tests |

Read [README.md](README.md) §5 and §7 first.

> This mod, together with `artificial-heart`, `athenaeum`, `banner-tweaks`,
> `beacon-conduit-tweaks`, and `flat-bedrock`, is small enough that one agent can reasonably do all
> six on a single branch. Doing them separately is also fine.

---

## 1. Payload commits

```
1dc06c1b  chore(archaeology-tweaks): remove dead local in ATBrushableBlockEntity (4.2)
41ab0adb  docs: add readmes and possible feature ideas
2b53182a  chore: document manual and automated test plans for each mod
```

---

## 2. Change inventory

### 4.2 — one dead local

`ATBrushableBlockEntity.scheduledTick`:

```java
-            int k = 4;
             this.nextDustTime = world.getGameTime() + 4L;
```

`int k = 4;` was never read — leftover from porting vanilla's `BrushableBlockEntity`, where the `4` is
inlined as the `4L` delay literal on the next line. That is the **entire** code change in this mod:
one deleted line.

### Docs

`README.md` (+52), `TEST_PLAN.md` (+106), `POTENTIAL_FEATURES.md` (+53). Copy verbatim, then sweep for
`26.2` / Java-25 claims and correct them to 26.1.2.

---

## 3. Conflict-risk file

`ATBrushableBlockEntity.java` was touched by the 26.2 port, but in two places unrelated to the
payload hunk:

- `import net.minecraft.advancements.CriteriaTriggers` → `net.minecraft.advancements.triggers.CriteriaTriggers`
- `EntityType.ITEM` → `EntityTypes.ITEM` (two usages in `spawnItem`)

The payload's deletion is at line ~159, well away from both. **Leave 26.1.2's imports and
`EntityType.ITEM` usages exactly as they are** — do not let the patch drag the port's renames along.
If `--3way` behaves, this file needs no manual work at all; verify with:

```bash
git grep -n 'EntityTypes\.\|advancements.triggers' -- 'archaeology-tweaks/**'   # must be empty
```

---

## 4. Procedure

```bash
git checkout -b backport/26.1.2/archaeology-tweaks backport/26.1.2/chimeric-lib

git diff --binary c5f2cc4d..main -- archaeology-tweaks/ > /tmp/at.patch
git apply --3way /tmp/at.patch

git checkout backport/26.1.2/chimeric-lib -- \
    archaeology-tweaks/build.gradle archaeology-tweaks/gradle.properties

./gradlew :archaeology-tweaks:common:build :archaeology-tweaks:fabric:build :archaeology-tweaks:neoforge:build
```

Single commit is fine: `chore(archaeology-tweaks): remove dead local + add README/TEST_PLAN/POTENTIAL_FEATURES`.

---

## 5. Done criteria

- [ ] `:archaeology-tweaks:{common,fabric,neoforge}:build` green.
- [ ] `git grep -n 'EntityTypes\.\|advancements.triggers' -- 'archaeology-tweaks/**'` is empty.
- [ ] `int k = 4;` is gone from `ATBrushableBlockEntity`.
- [ ] The three docs exist and contain no `26.2` references.
