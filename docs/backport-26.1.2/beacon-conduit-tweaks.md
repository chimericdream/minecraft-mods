# Backport: beacon-conduit-tweaks

| | |
|---|---|
| **Branch** | `backport/26.1.2/beacon-conduit-tweaks` |
| **Branch from** | `backport/26.1.2/chimeric-lib` (Wave 1) |
| **Wave** | 2 — parallel |
| **Payload** | `git diff c5f2cc4d..main -- beacon-conduit-tweaks/` — 5 files, +216 / −4 (docs) + build files |
| **Risk** | **Trivial — documentation only** |
| **Conflict-risk files** | **none** |
| **Depends on chimeric-lib** | nothing beyond the build wiring |
| **Gate** | compile only |

Read [README.md](README.md) §5 and §7 first.

> Batch candidate with `artificial-heart`, `athenaeum`, `flat-bedrock`, `banner-tweaks`,
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
| `README.md` | +55/−4 | substantial rewrite — the largest doc change of the four docs-only mods |
| `TEST_PLAN.md` | +110 | new — manual and automated test plan |
| `POTENTIAL_FEATURES.md` | +51 | new — feature backlog |

The README rewrite is worth reading rather than blindly applying: it documents the mod's config
surface and behavior. Make sure every claim it makes is true of the 26.1.2 build.

---

## 3. Known adaptations

**Sweep the docs for version claims** — written against `main`, so they may cite Minecraft `26.2`,
chimeric-lib `6.0.0-alpha.0`, or Architectury `21.0.4`. On this branch: `26.1.2`, `5.0.0-alpha.x`,
`20.0.7` (confirm against what Wave 0 wrote into the root `gradle.properties`).

Beacon and conduit mechanics changed across Minecraft versions. If the rewritten README describes
vanilla behavior that differs on 26.1.2 (beacon range tiers, conduit activation radius, the block
tags that count toward a beacon base), correct it. The `26.1.2` source is the authority — read the
mixins in `beacon-conduit-tweaks/common/src/main/java/` and describe what they actually do.

---

## 4. Procedure

```bash
git checkout -b backport/26.1.2/beacon-conduit-tweaks backport/26.1.2/chimeric-lib

git diff --binary c5f2cc4d..main -- beacon-conduit-tweaks/ > /tmp/bct.patch
git apply --3way /tmp/bct.patch

git checkout backport/26.1.2/chimeric-lib -- \
    beacon-conduit-tweaks/build.gradle beacon-conduit-tweaks/gradle.properties

./gradlew :beacon-conduit-tweaks:common:build :beacon-conduit-tweaks:fabric:build :beacon-conduit-tweaks:neoforge:build
```

Single commit: `docs(beacon-conduit-tweaks): rewrite README, add TEST_PLAN + POTENTIAL_FEATURES`.

---

## 5. Done criteria

- [ ] `:beacon-conduit-tweaks:{common,fabric,neoforge}:build` green.
- [ ] `git diff --stat backport/26.1.2/chimeric-lib..HEAD -- 'beacon-conduit-tweaks/**/*.java'` is **empty**.
- [ ] The README's behavioral claims match what the 26.1.2 mixins actually do.
- [ ] The docs reference 26.1.2-era versions, not 26.2.
