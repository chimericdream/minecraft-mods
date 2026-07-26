# Backport: artificial-heart

| | |
|---|---|
| **Branch** | `backport/26.1.2/artificial-heart` |
| **Branch from** | `backport/26.1.2/chimeric-lib` (Wave 1) |
| **Wave** | 2 — parallel |
| **Payload** | `git diff c5f2cc4d..main -- artificial-heart/` — 5 files, +199 / −0 (docs) + build files |
| **Risk** | **Trivial — documentation only** |
| **Conflict-risk files** | **none** |
| **Depends on chimeric-lib** | nothing beyond the build wiring |
| **Gate** | compile only |

Read [README.md](README.md) §5 and §7 first.

> Batch candidate: this mod, `athenaeum`, `beacon-conduit-tweaks`, `flat-bedrock`, `banner-tweaks`,
> and `archaeology-tweaks` are collectively ~15 minutes of work. One agent, one branch, is reasonable.

---

## 1. Payload commits

```
41ab0adb  docs: add readmes and possible feature ideas
2b53182a  chore: document manual and automated test plans for each mod
```

Plus `b98aa703` / `a73f6b92` / `b1e8709a`, which touch only `build.gradle` and `gradle.properties`
— **Wave 0 already did those.** Drop any such hunk.

---

## 2. Change inventory

**No source changes.** The 26.2 port did not touch this mod's Java, and neither does the payload.

Three new documentation files:

| File | Size | Content |
|---|---|---|
| `README.md` | +44 | What the mod does, how to use it |
| `TEST_PLAN.md` | +103 | Manual and automated test plan (nothing automated exists yet) |
| `POTENTIAL_FEATURES.md` | +52 | Feature backlog |

---

## 3. Known adaptations

**Sweep the docs for version claims.** These files were written against `main`, so they may state
Minecraft `26.2`, chimeric-lib `6.0.0-alpha.0`, or Architectury `21.0.4`. On this branch those are
`26.1.2`, `5.0.0-alpha.x`, and `20.0.7` respectively (confirm against the values Wave 0 wrote into
the root `gradle.properties`).

`TEST_PLAN.md` may also reference the GameTest harness. This mod has no tests; if the plan describes
a `src/gametest` source set that does not exist here, keep it as the *plan* — that is what the file
is for — but make sure it does not read as though the tests already exist.

No other adaptation. Nothing else to do.

---

## 4. Procedure

```bash
git checkout -b backport/26.1.2/artificial-heart backport/26.1.2/chimeric-lib

git diff --binary c5f2cc4d..main -- artificial-heart/ > /tmp/ah.patch
git apply --3way /tmp/ah.patch

git checkout backport/26.1.2/chimeric-lib -- \
    artificial-heart/build.gradle artificial-heart/gradle.properties

./gradlew :artificial-heart:common:build :artificial-heart:fabric:build :artificial-heart:neoforge:build
```

Single commit: `docs(artificial-heart): add README, TEST_PLAN, POTENTIAL_FEATURES`.

---

## 5. Done criteria

- [ ] `:artificial-heart:{common,fabric,neoforge}:build` green.
- [ ] `git diff --stat backport/26.1.2/chimeric-lib..HEAD -- 'artificial-heart/**/*.java'` is **empty**.
- [ ] The three docs exist and reference 26.1.2-era versions, not 26.2.
