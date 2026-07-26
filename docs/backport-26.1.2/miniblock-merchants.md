# Backport: miniblock-merchants

| | |
|---|---|
| **Branch** | `backport/26.1.2/miniblock-merchants` |
| **Branch from** | `backport/26.1.2/chimeric-lib` (Wave 1) |
| **Wave** | 2 — parallel |
| **Payload** | `git diff c5f2cc4d..main -- miniblock-merchants/` — 6 files, +192 / −70 |
| **Risk** | **Trivial** |
| **Conflict-risk files** | **none** |
| **Depends on chimeric-lib** | `LootModifierHelper` (3.6) |
| **Gate** | compile only — the mod has no automated tests |

Read [README.md](README.md) §5 and §7 first.

---

## 1. Payload commits

```
e8aaffbe  refactor(chimeric-lib): add NeoForge LootModifierHelper (3.6 — consumer half)
41ab0adb  docs: add readmes and possible feature ideas
2b53182a  chore: document manual and automated test plans for each mod
```

---

## 2. Change inventory

### 3.6 — one line

`neoforge/.../registry/LootModifierRegistry.java` (+2/−2). shulker-stuff and miniblock-merchants had
diff-identical `LootModifierRegistry` classes whose only real content was verbose boilerplate:

```java
-import net.neoforged.neoforge.registries.NeoForgeRegistries;
+import com.chimericdream.lib.neoforge.loot.LootModifierHelper;

-public static DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
-    DeferredRegister.create(NeoForgeRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS, ModInfo.MOD_ID);
+public static DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
+    LootModifierHelper.createRegister(ModInfo.MOD_ID);
```

`VILLAGER_CONVERSION_ITEMS_MODIFIER` is unchanged. **Wave 1 dependency** — `LootModifierHelper` lives
in chimeric-lib's `neoforge` source set.

If Wave 1 did not deliver `LootModifierHelper`, skip this change entirely (keep the existing
`DeferredRegister.create` call) and say so. It is pure deduplication; nothing depends on it.

### Docs

`README.md` (+3/−1), `TEST_PLAN.md` (+120), `POTENTIAL_FEATURES.md` (+66).

---

## 3. Known adaptations

None. The 26.2 port did not touch any miniblock-merchants source file.

`miniblock-merchants/common/build.gradle` declares a custom `sourceSets` block — it is Wave 0's file
and the payload does not change it; leave it alone.

---

## 4. Procedure

```bash
git checkout -b backport/26.1.2/miniblock-merchants backport/26.1.2/chimeric-lib

git diff --binary c5f2cc4d..main -- miniblock-merchants/ > /tmp/mm.patch
git apply --3way /tmp/mm.patch

git checkout backport/26.1.2/chimeric-lib -- \
    miniblock-merchants/build.gradle miniblock-merchants/gradle.properties

./gradlew :miniblock-merchants:common:build :miniblock-merchants:fabric:build :miniblock-merchants:neoforge:build
```

Single commit: `refactor(miniblock-merchants): adopt LootModifierHelper + add docs (3.6)`.

---

## 5. Done criteria

- [ ] `:miniblock-merchants:{common,fabric,neoforge}:build` green.
- [ ] `LootModifierRegistry` does not import `NeoForgeRegistries`.
- [ ] Manual check (NeoForge): convert a villager and confirm the loot modifier still fires — this is
      the only behavior the changed line can break.
- [ ] The three docs exist and contain no `26.2` references.
