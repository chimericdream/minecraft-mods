# Backport: banner-tweaks

| | |
|---|---|
| **Branch** | `backport/26.1.2/banner-tweaks` |
| **Branch from** | `backport/26.1.2/chimeric-lib` (Wave 1) |
| **Wave** | 2 — parallel |
| **Payload** | `git diff c5f2cc4d..main -- banner-tweaks/` — 6 files, +221 / −2 |
| **Risk** | **Trivial** — documentation only, zero behavior change |
| **Conflict-risk files** | **none** |
| **Depends on chimeric-lib** | nothing beyond the build wiring |
| **Gate** | compile only |

Read [README.md](README.md) §5 and §7 first.

> Can be batched with `archaeology-tweaks`, `artificial-heart`, `athenaeum`,
> `beacon-conduit-tweaks`, and `flat-bedrock` on one branch.

---

## 1. Payload commits

```
3a14927f  docs(banner-tweaks): document why MapStateMixin keeps @Overwrite (4.3)
41ab0adb  docs: add readmes and possible feature ideas
2b53182a  chore: document manual and automated test plans for each mod
```

---

## 2. Change inventory

### 4.3 — a documented exception, not a code change

Item 4.3 of `CODE-REVIEW-PLAN.md` asks to convert `@Overwrite` mixins to `@Inject`/`@ModifyArg` where
practical, and otherwise *"note in each file why overwrite was needed if kept"*.

`MapStateMixin` overwrites `addDecoration` and `toggleBanner` to fix
[MC-144406](https://bugs.mojang.com/browse/MC-144406) (banner markers off the edge of a map are
dropped instead of clamped to the border). The fix is **distributed through each method** — the
out-of-bounds clamping (`f/g < -64 .. >= 64`), the `removeDecoration`-vs-keep decisions, the
`PLAYER_OFF_MAP`/`PLAYER_OFF_LIMITS` substitution — all interleaved with shared decoration-tracking
bookkeeping. There is no stable injection point that expresses the corrected behavior, so the methods
stay reimplemented wholesale.

The payload therefore **adds only javadoc** (+22/−2): a `Why @Overwrite rather than @Inject/@ModifyArg`
paragraph on each method, an explicit statement of the consequence (conflicts with any other mod that
overwrites `MapItemSavedData#addDecoration` / `toggleBanner`, accepted as the cost), and a sharper
`@reason` string.

**No behavior changes. No API surface touched.** This is the exact opposite of what
`enchantment-numbers-fix` did for the same review item, and the contrast is the point — record why
the two mods diverged.

### Docs

`README.md` (+46), `TEST_PLAN.md` (+106), `POTENTIAL_FEATURES.md` (+47).

---

## 3. Known adaptations

None. The 26.2 port did not touch any banner-tweaks source file, and the payload adds only comments.

One thing to check while you are here: the javadoc references vanilla's coordinate constants (`-64`,
`64`) and decoration types. If 26.1.2's `MapItemSavedData` differs, the *existing* `26.1.2` mixin body
already reflects that — the comments describe the method, so make sure they still describe what the
26.1.2 body actually does. Adjust the wording if not; do not change the code.

---

## 4. Procedure

```bash
git checkout -b backport/26.1.2/banner-tweaks backport/26.1.2/chimeric-lib

git diff --binary c5f2cc4d..main -- banner-tweaks/ > /tmp/bt.patch
git apply --3way /tmp/bt.patch

git checkout backport/26.1.2/chimeric-lib -- banner-tweaks/build.gradle banner-tweaks/gradle.properties

./gradlew :banner-tweaks:common:build :banner-tweaks:fabric:build :banner-tweaks:neoforge:build
```

Single commit: `docs(banner-tweaks): document why MapStateMixin keeps @Overwrite + README/TEST_PLAN/POTENTIAL_FEATURES`.

---

## 5. Done criteria

- [ ] `:banner-tweaks:{common,fabric,neoforge}:build` green.
- [ ] `git diff --stat backport/26.1.2/chimeric-lib..HEAD -- 'banner-tweaks/**/*.java'` shows
      **comment lines only** — no logic changed.
- [ ] Both `@Overwrite` methods carry the "why" paragraph and the updated `@reason`.
- [ ] The three docs exist and contain no `26.2` references.
