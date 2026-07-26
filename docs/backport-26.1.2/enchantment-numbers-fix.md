# Backport: enchantment-numbers-fix

| | |
|---|---|
| **Branch** | `backport/26.1.2/enchantment-numbers-fix` |
| **Branch from** | `backport/26.1.2/chimeric-lib` (Wave 1) |
| **Wave** | 2 — parallel |
| **Payload** | `git diff c5f2cc4d..main -- enchantment-numbers-fix/` — 7 files, +205 / −25 |
| **Risk** | Low, with **one real verification step** (§3) |
| **Conflict-risk files** | **none** |
| **Depends on chimeric-lib** | nothing beyond the build wiring |
| **Gate** | manual — the mod has no automated tests |

Read [README.md](README.md) §5 and §7 first.

---

## 1. Payload commits

```
0b7f6508  fix(enchantment-numbers-fix): convert getFullname @Overwrite to @Redirect
          + guard RomanNumeralUtil                                        (4.3, 4.5)
41ab0adb / 2b53182a  docs + test plan
```

---

## 2. Change inventory

### 4.3 — `@Overwrite` → `@Redirect`

`ENFEnchantmentMixin` used `@Overwrite` on `Enchantment.getFullname` — a verbatim copy of vanilla
with a single line changed (the level suffix). `@Overwrite` is maximally incompatible: **any** other
mod touching `getFullname` clashes with it.

The replacement redirects only the one internal `Component.translatable(String)` call and substitutes
a literal Roman-numeral component:

```java
@Mixin(Enchantment.class)
public abstract class ENFEnchantmentMixin {
    @Redirect(
        method = "getFullname",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;"
        )
    )
    private static MutableComponent enchantnumfix$romanLevel(String translationKey, Holder<Enchantment> enchantment, int level) {
        return Component.literal(RomanNumeralUtil.toRoman(level));
    }
}
```

Vanilla keeps ownership of the description copy, the curse/gray styling, and the
`level != 1 || maxLevel != 1` guard that decides whether a suffix is appended at all — so this now
composes with other enchantment-tooltip mods. Net −25/+22 and six now-unused imports removed
(`ChatFormatting`, `CommonComponents`, `ComponentUtils`, `Style`, `EnchantmentTags`, `Overwrite`).

Keep the class javadoc explaining *why* the redirect is preferable — it is the durable part.

### 4.5 — `RomanNumeralUtil.toRoman` guard

```java
// Classic Roman numerals only represent 1..3999. Anything outside that — including 0 or
// negatives, which other mods' /enchant commands can produce — has no numeral, and for
// number < 1 map.floorKey(number) returns null and NPEs on unboxing. Fall back to the
// Arabic value in those cases.
if (number < 1 || number > 3999) {
    return String.valueOf(number);
}
```

`map.floorKey(number)` returns `null` for `number < 1` and the method unboxes it to `int` — an NPE on
any level ≤ 0, which other mods' `/enchant` commands can produce. +8 lines.

### Docs

`README.md` (+41), `TEST_PLAN.md` (+91), `POTENTIAL_FEATURES.md` (+44).

---

## 3. The verification step — the `@Redirect` target

**This is the only real risk in this mod.** The redirect depends on vanilla `Enchantment.getFullname`
containing exactly one `Component.translatable(String)` call, at the level suffix. That is true in
26.2. Confirm it on 26.1.2 before trusting it:

```bash
JAR=~/.gradle/caches/fabric-loom/minecraftMaven/net/minecraft/minecraft-merged-deobf/26.1.2/minecraft-merged-deobf-26.1.2.jar
unzip -o "$JAR" 'net/minecraft/world/item/enchantment/Enchantment.class' -d /tmp/mc
javap -c -p /tmp/mc/net/minecraft/world/item/enchantment/Enchantment.class \
  | sed -n '/getFullname/,/^$/p' | grep -n 'translatable\|Method'
```

Three possible outcomes:

| Outcome | Action |
|---|---|
| Exactly one `Component.translatable(String)` call | Backport as written. |
| More than one such call | Add `ordinal = N` to the `@At` to disambiguate. Pick the ordinal that corresponds to the level suffix, not the description. |
| Vanilla 26.1.2 builds the suffix differently (e.g. no `translatable` call at all) | The redirect cannot work. **Keep the existing `@Overwrite` on this branch** and backport only the 4.5 `RomanNumeralUtil` guard. Say so explicitly in your report — do not silently ship a mixin that does not apply. |

A mixin that fails to apply is not a compile error. **Verify at runtime**, not just by building:
launch the client, enchant something to level II+ and confirm the tooltip shows a Roman numeral.

---

## 4. Procedure

```bash
git checkout -b backport/26.1.2/enchantment-numbers-fix backport/26.1.2/chimeric-lib

git diff --binary c5f2cc4d..main -- enchantment-numbers-fix/ > /tmp/enf.patch
git apply --3way /tmp/enf.patch

git checkout backport/26.1.2/chimeric-lib -- \
    enchantment-numbers-fix/build.gradle enchantment-numbers-fix/gradle.properties

./gradlew :enchantment-numbers-fix:common:build :enchantment-numbers-fix:fabric:build :enchantment-numbers-fix:neoforge:build
```

Then do §3's verification and a client run.

Suggested commit split:

1. `fix(enchantment-numbers-fix): guard RomanNumeralUtil against out-of-range levels (4.5)`
2. `refactor(enchantment-numbers-fix): convert getFullname @Overwrite to @Redirect (4.3)`
3. `docs(enchantment-numbers-fix): README, TEST_PLAN, POTENTIAL_FEATURES`

Splitting them this way means that if §3 rules out the redirect, you simply skip commit 2.

---

## 5. Done criteria

- [ ] `:enchantment-numbers-fix:{common,fabric,neoforge}:build` green.
- [ ] §3 verification done, with the outcome stated in your report.
- [ ] **Runtime check:** enchant an item to level III, confirm the tooltip reads `III` not `3`.
- [ ] **Runtime check:** `/enchant @s minecraft:sharpness 0` (or any mod producing level ≤ 0) does not
      crash — it should render the Arabic value.
- [ ] No `@Overwrite` remains in `ENFEnchantmentMixin` **unless** §3 forced it, in which case the file
      carries a comment saying why (matching what banner-tweaks does for `MapStateMixin`).
