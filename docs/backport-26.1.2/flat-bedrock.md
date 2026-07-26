# Backport: flat-bedrock

| | |
|---|---|
| **Branch** | `backport/26.1.2/flat-bedrock` |
| **Branch from** | `backport/26.1.2/chimeric-lib` (Wave 1) |
| **Wave** | 2 — parallel |
| **Payload** | `git diff c5f2cc4d..main -- flat-bedrock/` — 5 files, +178 / −0 (docs) + build files |
| **Risk** | **Trivial — documentation only** |
| **Conflict-risk files** | **none** |
| **Depends on chimeric-lib** | nothing beyond the build wiring |
| **Gate** | compile only |

Read [README.md](README.md) §5 and §7 first.

> Batch candidate with `artificial-heart`, `athenaeum`, `beacon-conduit-tweaks`, `banner-tweaks`,
> `archaeology-tweaks`.

---

## 1. Payload commits

```
41ab0adb  docs: add readmes and possible feature ideas
2b53182a  chore: document manual and automated test plans for each mod
```

Plus `b98aa703` / `a73f6b92` / `b1e8709a`, which touch only build files — **Wave 0 already did those.**

---

## 2. Change inventory

**No source changes.** Neither the 26.2 port nor the payload touched this mod's Java.

| File | Size | Content |
|---|---|---|
| `README.md` | +43 | new |
| `TEST_PLAN.md` | +90 | new — manual and automated test plan |
| `POTENTIAL_FEATURES.md` | +45 | new — feature backlog |

---

## 3. Known adaptations

**Sweep the docs for version claims** — written against `main`, so they may cite Minecraft `26.2`,
chimeric-lib `6.0.0-alpha.0`, or Architectury `21.0.4`. On this branch: `26.1.2`, `5.0.0-alpha.x`,
`20.0.7` (confirm against what Wave 0 wrote into the root `gradle.properties`).

flat-bedrock is worldgen-adjacent. If `TEST_PLAN.md` names specific Y levels, dimension heights, or
`minecraft:bedrock` placement rules, check them against 26.1.2 rather than assuming 26.2 values
carried over — worldgen constants are exactly the sort of thing that shifts between versions.

---

## 4. Procedure

```bash
git checkout -b backport/26.1.2/flat-bedrock backport/26.1.2/chimeric-lib

git diff --binary c5f2cc4d..main -- flat-bedrock/ > /tmp/fb.patch
git apply --3way /tmp/fb.patch

git checkout backport/26.1.2/chimeric-lib -- flat-bedrock/build.gradle flat-bedrock/gradle.properties

./gradlew :flat-bedrock:common:build :flat-bedrock:fabric:build :flat-bedrock:neoforge:build
```

Single commit: `docs(flat-bedrock): add README, TEST_PLAN, POTENTIAL_FEATURES`.

---

## 5. Done criteria

- [ ] `:flat-bedrock:{common,fabric,neoforge}:build` green.
- [ ] `git diff --stat backport/26.1.2/chimeric-lib..HEAD -- 'flat-bedrock/**/*.java'` is **empty**.
- [ ] Any Y levels / worldgen constants in the docs match 26.1.2.
- [ ] The docs reference 26.1.2-era versions, not 26.2.
