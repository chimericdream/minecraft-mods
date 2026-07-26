# Backport: athenaeum

| | |
|---|---|
| **Branch** | `backport/26.1.2/athenaeum` |
| **Branch from** | `backport/26.1.2/chimeric-lib` (Wave 1) |
| **Wave** | 2 — parallel |
| **Payload** | `git diff c5f2cc4d..main -- athenaeum/` — 5 files, +164 / −1 (docs) + build files |
| **Risk** | **Trivial — documentation only** |
| **Conflict-risk files** | **none** |
| **Depends on chimeric-lib** | nothing beyond the build wiring |
| **Gate** | compile only |

Read [README.md](README.md) §5 and §7 first.

> Batch candidate with `artificial-heart`, `beacon-conduit-tweaks`, `flat-bedrock`, `banner-tweaks`,
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
| `README.md` | +2/−1 | small edit to the existing README |
| `TEST_PLAN.md` | +102 | new — manual and automated test plan |
| `POTENTIAL_FEATURES.md` | +60 | new — feature backlog |

---

## 3. Known adaptations

**Sweep the docs for version claims** — they were written against `main` and may cite Minecraft
`26.2`, chimeric-lib `6.0.0-alpha.0`, or Architectury `21.0.4`. On this branch: `26.1.2`,
`5.0.0-alpha.x`, `20.0.7` (confirm against what Wave 0 wrote into the root `gradle.properties`).

athenaeum uses Patchouli books (`bun run update:patchoulibooks` is part of the build lifecycle). If
`TEST_PLAN.md` describes book-related testing, check that the Patchouli version it assumes matches
26.1.2's.

---

## 4. Procedure

```bash
git checkout -b backport/26.1.2/athenaeum backport/26.1.2/chimeric-lib

git diff --binary c5f2cc4d..main -- athenaeum/ > /tmp/ath.patch
git apply --3way /tmp/ath.patch

git checkout backport/26.1.2/chimeric-lib -- athenaeum/build.gradle athenaeum/gradle.properties

./gradlew :athenaeum:common:build :athenaeum:fabric:build :athenaeum:neoforge:build
```

Single commit: `docs(athenaeum): add TEST_PLAN + POTENTIAL_FEATURES, tidy README`.

---

## 5. Done criteria

- [ ] `:athenaeum:{common,fabric,neoforge}:build` green.
- [ ] `git diff --stat backport/26.1.2/chimeric-lib..HEAD -- 'athenaeum/**/*.java'` is **empty**.
- [ ] The docs reference 26.1.2-era versions, not 26.2.
- [ ] `bun run update:patchoulibooks` still succeeds.
