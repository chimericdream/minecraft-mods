# Backport: villager-tweaks

| | |
|---|---|
| **Branch** | `backport/26.1.2/villager-tweaks` |
| **Branch from** | `backport/26.1.2/chimeric-lib` (Wave 1) |
| **Wave** | 2 — parallel |
| **Payload** | `git diff c5f2cc4d..main -- villager-tweaks/` — 8 files, +410 / −75 |
| **Risk** | Low |
| **Conflict-risk files** | **none** |
| **Depends on chimeric-lib** | nothing beyond the build wiring |
| **Gate** | `./gradlew :villager-tweaks:fabric:runGameTest` (`GlobalReputationGameTest`) |

Read [README.md](README.md) §5 and §7 first.

---

## 1. Payload commits

```
fb0abdaa / 1a75ea74  fix(villager-tweaks): read global reputation when bad reputation is on (2.3)
41ab0adb / 2b53182a  docs + test plan
```

---

## 2. Change inventory

### 2.3 — global reputation was written but never read

`VTVillagerEntityMixin.injected` (the `getPlayerReputation` HEAD injection) computed the
`GLOBAL_UUID` substitution and then **returned early whenever `enableBadReputation` was set — which
is its default.** Writes still went to `GLOBAL_UUID` via `onReputationEventFrom`, so with both
options enabled the mod wrote reputation under one key and read it from another, and global
reputation silently did nothing.

The rewrite (`VTVillagerEntityMixin.java`, +13/−3) separates the two concerns that were tangled
together:

```java
// Neither tweak is on: let vanilla read the player's own gossip, all types included.
if (!config.enableGlobalReputation && config.enableBadReputation) {
    return;
}

// vt_overrideSettingGossip files every reputation event under GLOBAL_UUID, so reads have to
// look there too — otherwise global reputation is written but never read back.
UUID playerId = config.enableGlobalReputation ? GLOBAL_UUID : player.getUUID();

// Vanilla counts every gossip type; disabling bad reputation just drops the negative ones.
Predicate<GossipType> gossipTypes = config.enableBadReputation
    ? (t) -> true
    : (t) -> t != GossipType.MINOR_NEGATIVE && t != GossipType.MAJOR_NEGATIVE;

cir.setReturnValue(this.gossips.getReputation(playerId, gossipTypes));
```

Note the two axes are now independent: **which UUID to read** (global vs. per-player) and **which
gossip types to count** (all vs. non-negative). The old code conflated them. Adds
`import java.util.function.Predicate;`.

Keep the three comments — they are what stops the next reader from re-collapsing the branches.

### Tests

`fabric/src/gametest/java/.../GlobalReputationGameTest.java` (+117) plus its
`gametest/resources/fabric.mod.json` (+24). Exercises the four config combinations.

### Docs

`README.md` (+68/−4), `TEST_PLAN.md` (+127), `POTENTIAL_FEATURES.md` (+64).

---

## 3. Known adaptation

**One symbol.** `GlobalReputationGameTest` uses the 26.2-only `EntityTypes.VILLAGER`. Reverse it:

```java
// main (26.2):
import net.minecraft.world.entity.EntityTypes;
... EntityTypes.VILLAGER ...

// 26.1.2:
import net.minecraft.world.entity.EntityType;
... EntityType.VILLAGER ...
```

`net.minecraft.world.entity.EntityType` on 26.1.2 holds both the generic type and the static
instances; the 26.2 port split the instances out into `EntityTypes`.

Nothing else. The 26.2 port's only change to this mod's source was a one-line rename in
`VTZombieVillagerEntityMixin.java`, which the payload does not touch.

---

## 4. Procedure

```bash
git checkout -b backport/26.1.2/villager-tweaks backport/26.1.2/chimeric-lib

git diff --binary c5f2cc4d..main -- villager-tweaks/ > /tmp/vt.patch
git apply --3way /tmp/vt.patch

git checkout backport/26.1.2/chimeric-lib -- villager-tweaks/build.gradle villager-tweaks/gradle.properties

# fix EntityTypes -> EntityType in the gametest, then:
./gradlew :villager-tweaks:common:build :villager-tweaks:fabric:build :villager-tweaks:neoforge:build
./gradlew :villager-tweaks:fabric:runGameTest
```

Suggested commit split:

1. `fix(villager-tweaks): read global reputation when bad reputation is on (2.3)`
2. `test(villager-tweaks): GlobalReputationGameTest`
3. `docs(villager-tweaks): README, TEST_PLAN, POTENTIAL_FEATURES`

---

## 5. Done criteria

- [ ] `:villager-tweaks:{common,fabric,neoforge}:build` green.
- [ ] `:villager-tweaks:fabric:runGameTest` green.
- [ ] `git grep -n 'EntityTypes' -- 'villager-tweaks/**'` returns nothing.
- [ ] Manual check: enable both `enableGlobalReputation` and `enableBadReputation`, trade with one
      villager to build reputation, then check a **different** villager reflects it.
