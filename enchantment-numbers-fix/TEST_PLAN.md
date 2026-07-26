# Test Plan — Enchantment Numbers Fix

This mod is a purely client-side, cosmetic tweak: `ENFEnchantmentMixin` intercepts the enchantment
level name lookup and `RomanNumeralUtil` converts levels above 10 (where vanilla falls back to decimal
digits) into Roman numerals. There is no world state, no server behavior, and no content. That makes
this the one mod in the suite whose core logic is best covered by **plain JVM unit tests** rather than
GameTests.

## Test conventions

* **Unit tests**: pure-JVM JUnit tests belong in `common/src/test/java/...` (a `test` source set will
  need to be added to the Gradle config; none of the mods currently have one). `RomanNumeralUtil` has
  no Minecraft imports, so it can be tested without bootstrapping the game.
* **GameTests**: follow the Hopper X-Treme pattern — test classes in
  `fabric/src/main/java/com/chimericdream/enchantnumfix/fabric/test/`, registered under the
  `fabric-gametest` entrypoint in `fabric/src/main/resources/fabric.mod.json`. However, because this
  mod only changes *client text rendering*, server-side GameTests can only verify "does not crash /
  does not alter data"; the visible behavior needs manual or client-side checks.

## Manual test plan

Setup: launch a Fabric client with the mod (`./gradlew :enchantment-numbers-fix:fabric:runClient`),
then repeat the smoke pass on NeoForge.

1. **Vanilla range unchanged (1–10)**
   * `/give @s diamond_sword[enchantments={"minecraft:sharpness":5}]` → tooltip reads "Sharpness V".
   * Repeat with level 10 → "Sharpness X". Levels ≤ 10 must be identical to vanilla.
2. **Extended range (11+)**
   * `/give @s diamond_sword[enchantments={"minecraft:sharpness":15}]` → "Sharpness XV", not "Sharpness 15".
   * Spot-check tricky numerals: 14 (XIV), 19 (XIX), 40 (XL), 49 (XLIX), 90 (XC), 255 (CCLV),
     and the max NBT level 32767 (should render as a long-but-correct numeral, without crashing or
     overflowing the tooltip in an ugly way — decide whether to cap; see Open questions).
3. **All display surfaces**
   * Item tooltip on gear, enchanted book tooltip, anvil result preview, enchanting-table hover
     tooltips (levels there are ≤ 3, so just confirm no regression), and grindstone screen.
4. **Level 1 suppression rule**
   * Vanilla omits the numeral for single-level enchantments at level I (e.g. "Mending", not
     "Mending I"). Confirm the mod does not accidentally add or remove numerals for those.
5. **Localization / fallback**
   * Switch client language to something non-English; enchantment names should translate while the
     numeral behavior stays correct.
6. **Absence-of-server safety**
   * Join a vanilla (or mod-less) server with the mod installed client-side. Tooltips should render
     with Roman numerals and nothing should desync — this validates the "client-only, safe to
     add/remove" claim in the README.

## Recommended automated tests

### Unit tests (highest value here)

`RomanNumeralUtilTest` (pure JUnit, `common` source set):

* **Known values**: 1→I, 4→IV, 9→IX, 14→XIV, 40→XL, 90→XC, 400→CD, 900→CM, 1994→MCMXCIV,
  3999→MMMCMXCIX.
* **Values above standard Roman range**: whatever convention the util uses for ≥ 4000 (repeated Ms
  or capping) — pin it with an explicit test so the behavior is intentional, not accidental.
* **Boundary/garbage inputs**: 0, negative numbers, `Integer.MAX_VALUE`, and 32767 (the max
  enchantment level reachable via commands). The test should document the intended behavior
  (exception vs. fallback string) rather than leaving it undefined.
* **Round-trip sanity** (if the util has a parse direction) or a property-style loop: for
  1..1000, the generated numeral contains only `IVXLCDM` and never four identical consecutive
  symbols except where the chosen convention allows it.

### Mixin-level tests

* **`ENFEnchantmentMixin` behavior test**: a GameTest (or a client test via the Fabric client
  gametest API, `fabric-client-gametest-api-v1`) that constructs an `Enchantment` component/name for
  levels 5, 10, 11, and 15 and asserts the produced `Component` string equals the vanilla value for
  ≤ 10 and the Roman numeral for > 10. If the mixin targets the name-building method directly, this
  can be run headlessly on the server side by calling `Enchantment.getFullname(...)` (or its current
  equivalent) and inspecting the resulting text — no rendering required.
* **Translation-key passthrough**: assert that levels with existing lang keys
  (`enchantment.level.1` … `enchantment.level.10`) still resolve via translation, so resource packs
  that override those keys keep working.

### Smoke GameTest

* One trivial GameTest that gives a mock player an over-enchanted item and ticks once, asserting no
  crash — guards against the mixin breaking on a Minecraft update even though the interesting
  assertions live in the unit/mixin tests above.

## ChimericLib helper opportunities

* A **component-text assertion helper** (`assertComponentEquals(Component, String)` that flattens
  styled/translatable components to plain strings) would be reused by Banner Tweaks (map marker
  names) and any mod asserting tooltip text.

## Open questions

* What is the intended output for levels ≥ 4000 and for level 32767? Worth deciding before writing
  the boundary unit tests so the tests encode the decision.
