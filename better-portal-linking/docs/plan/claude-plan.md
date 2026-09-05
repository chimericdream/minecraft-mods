# Better Portal Linking — "portal address blocks"

## Context

`better-portal-linking` is a scaffolded-but-empty mod (committed as `b8a2731e`, already in `settings.gradle`
and `project-list.json`). It currently does nothing: no mixins, no config, no tags — just the standard
`common`/`fabric`/`neoforge` template.

The problem it exists to solve: vanilla nether-portal linking picks the *nearest* portal to
`entryCoords × dimensionScale`, and players have no way to influence the pairing. Building two portals
close together in the Nether makes them fight over the same Overworld destination, and there is no way to
say "this portal goes to the mine, that one goes to the base."

This change gives players an explicit, in-world control: put marker blocks at the four **diagonal corner
positions** of a portal's obsidian frame (positions vanilla leaves empty), and portals whose corner blocks
match get preferentially linked. Portals with no marker blocks behave exactly as they do today.

**Intended outcome:** a player who decorates two portal frames with matching corners always travels
between those two portals, and a player who has never heard of this mod sees no behavior change at all.

### Decisions already made (confirmed with the user)

| Decision | Choice |
|---|---|
| Link persistence | **Stateless.** Re-score on every transit. No `SavedData`, no invalidation. Scoring is deterministic, so the same portal wins every time until the blocks change. |
| Final tiebreak | **Deterministic**, not random. Sort tied candidates by block position, take the first. Stable across transits and reproducible in tests. |
| Tag default contents | **Curated set**: `#minecraft:concrete`, `#minecraft:terracotta`, `#minecraft:glazed_terracotta` (all three verified present in the 26.2 jar) — a 49-block address alphabet. Deliberately **no wool**: it is flammable, and address blocks sit directly against a portal frame where stray fire is likely. |
| Config | **Minimal YACL config** following the villager-tweaks pattern: `enableAddressLinking` (default `true`), `logLinkingDecisions` (default `false`). |

---

## Vanilla 26.2 call chain (verified via `javap` against `minecraft-merged-deobf-26.2.jar`)

This was established from bytecode, not guessed. Implementers should still decompile for full bodies
(see Wave 0), but these signatures and constants are confirmed:

```
Entity portal tick
  → net.minecraft.world.entity.PortalProcessor#getPortalDestination(ServerLevel, Entity)
    → NetherPortalBlock#getPortalDestination(ServerLevel currentLevel, Entity entity, BlockPos entryPortalPos)
        destLevel = currentLevel.dimension()==NETHER ? OVERWORLD : NETHER
        isNether  = destLevel.dimension()==NETHER
        scale     = DimensionType.getTeleportationScale(currentLevel.dimensionType(), destLevel.dimensionType())
        scaledPos = destBorder.clampToBounds(entity.getX()*scale, entity.getY(), entity.getZ()*scale)   // <-- "entry coords / 8"
      → NetherPortalBlock#getExitPortal(destLevel, entity, entryPortalPos, scaledPos, isNether, border)   [private]
        → PortalForcer#findClosestPortalPosition(scaledPos, isNether, border) : Optional<BlockPos>       // <-- INJECTION POINT
          ├─ present → BlockUtil.getLargestRectangleAround(foundPos, horizAxis, 21, Axis.Y, 21, pred) → FoundRectangle
          └─ empty   → PortalForcer#createPortal(scaledPos, axis)  (vanilla builds a new portal)
```

`PortalForcer#findClosestPortalPosition` in full (decompiled from bytecode):

```java
public Optional<BlockPos> findClosestPortalPosition(BlockPos target, boolean isNether, WorldBorder border) {
    PoiManager poi = this.level.getPoiManager();
    int radius = isNether ? 16 : 128;                                  // NETHER_PORTAL_RADIUS / OVERWORLD_PORTAL_RADIUS
    poi.ensureLoadedAndValid(this.level, target, radius);
    return poi.getInSquare(h -> h.is(PoiTypes.NETHER_PORTAL), target, radius, PoiManager.Occupancy.ANY)
        .map(PoiRecord::getPos)
        .filter(border::isWithinBounds)
        .filter(p -> this.level.getBlockState(p).hasProperty(BlockStateProperties.HORIZONTAL_AXIS))  // stale-POI guard
        .min(Comparator.comparingDouble((BlockPos p) -> p.distSqr(target))
                       .thenComparingInt(BlockPos::getY));
}
```

**Key consequences for the design:**

- `findClosestPortalPosition` is the *only* place an existing exit portal is selected, and it is called
  exactly once per transit. Injecting at its `HEAD` (cancellable) is a complete and minimal hook.
- It knows nothing about the entry portal, so entry context must be threaded in from
  `NetherPortalBlock#getPortalDestination`.
- The POI index stores **one record per portal block**, so a 2×3 portal yields 6 candidate positions.
  Candidates must be grouped into distinct portals before scoring.
- `target` (the `scaledPos` argument) already *is* "entry coords ÷ 8", clamped to the world border — so the
  spec's distance tiebreak is simply `distSqr(target)`.
- **No access widener entries are needed.** `BlockUtil.getLargestRectangleAround`, `ServerLevel#getPoiManager`,
  `PoiRecord#getPos`, `PoiTypes.NETHER_PORTAL`, and `NetherPortalBlock.AXIS` are all public.
  `better-portal-linking/common/src/main/resources/betterportallinking.accesswidener` stays header-only.

---

## Algorithm

**Address extraction** (given a portal block position):

1. Read the block state; require `NetherPortalBlock.AXIS` (X or Z). `right = Direction.get(POSITIVE, axis)`.
2. `rect = BlockUtil.getLargestRectangleAround(pos, axis, 21, Direction.Axis.Y, 21, p -> level.getBlockState(p) == state)`
   — reuse vanilla so our frame is byte-for-byte the same rectangle `getExitPortal` will use.
   Gives `minCorner` (interior bottom-left), `axis1Size` = width `w`, `axis2Size` = height `h`.
3. The four diagonal frame corners:
   ```
   bottomLeft  = minCorner.relative(right, -1).below()
   bottomRight = minCorner.relative(right,  w).below()
   topLeft     = minCorner.relative(right, -1).above(h)
   topRight    = minCorner.relative(right,  w).above(h)
   ```
4. Address = the multiset of those four blocks that are in `betterportallinking:portal_address_blocks`.
   Air and untagged blocks are simply absent. Order is irrelevant; duplicates are kept.

**Scoring:** multiset intersection size — for each distinct block, `min(countEntry, countCandidate)`, summed.
Both sides have ≤4 elements, so a naive "copy the candidate list and remove one match per entry element"
loop is correct and fast enough.

**Selection**, inside the `findClosestPortalPosition` hook:

1. If no entry address was recorded, or it is empty → return without cancelling (pure vanilla).
2. Gather candidates exactly as vanilla does (same radius, same POI predicate, same border and stale-POI
   filters), then **group by portal**: key on `rect.minCorner`. For each portal keep the representative
   portal-block position that vanilla itself would have returned — i.e. `min` by
   `distSqr(target)` then `getY()`. Skip POI positions already inside an already-resolved rectangle so each
   portal's rectangle is computed once.
3. Score every distinct portal.
4. If the best score is `0` → return without cancelling (pure vanilla fallback).
5. Ties on score → lowest `representativePos.distSqr(target)`.
6. Still tied → lowest `representativePos.asLong()` (deterministic).
7. `cir.setReturnValue(Optional.of(representativePos))`.

**Entry-context lifetime:** set at `getPortalDestination` HEAD, **consumed and cleared** at
`findClosestPortalPosition` HEAD (take-once), and defensively cleared again at `getPortalDestination` RETURN.
Take-once is the important property: a stale address can never leak into an unrelated portal search even if
an exception unwinds the stack. A `ThreadLocal` is the carrier (portal handling is server-thread only, but
the `ThreadLocal` costs nothing and removes the question).

---

## Files

New package root: `better-portal-linking/common/src/main/java/com/chimericdream/betterportallinking/`

| File | Purpose |
|---|---|
| `tag/ModTags.java` | `TagKey<Block> PORTAL_ADDRESS_BLOCKS`. Mirror `villager-tweaks/common/.../tag/ModTags.java` — `TagKey.create(BuiltInRegistries.BLOCK.key(), Identifier.fromNamespaceAndPath(ModInfo.MOD_ID, "portal_address_blocks"))`. Note `net.minecraft.resources.Identifier`, **not** `ResourceLocation`. |
| `portal/PortalAddress.java` | Immutable multiset of ≤4 `Block`s. `isEmpty()`, `score(PortalAddress other)`, `toString()` for logging. |
| `portal/PortalFrame.java` | Level-independent frame resolution: takes a `Predicate<BlockPos> isPortal` + start pos + axis, returns `FoundRectangle`, the 4 corner positions, and the interior positions. **Keep it free of `Level`** so it is unit-testable. |
| `portal/PortalAddressLinker.java` | Level-facing façade: candidate gathering, grouping, scoring, selection. Split the pure scoring/tiebreak step into a static method taking a `List<Candidate>` so it too is unit-testable. |
| `portal/EntryPortalContext.java` | `ThreadLocal<PortalAddress>` with `set` / `take` (get+clear) / `clear`. |
| `mixin/BPLNetherPortalBlockMixin.java` | `@Mixin(NetherPortalBlock.class)`; `@Inject(method = "getPortalDestination", at = @At("HEAD"))` sets context, `@At("RETURN")` clears it. Injector methods named `bpl$...` per repo convention. |
| `mixin/BPLPortalForcerMixin.java` | `@Mixin(PortalForcer.class)`; `@Shadow @Final private ServerLevel level;`; `@Inject(method = "findClosestPortalPosition", at = @At("HEAD"), cancellable = true)`. |
| `config/BetterPortalLinkingConfig.java` | YACL. Copy the shape of `villager-tweaks/common/.../config/VillagerTweaksConfig.java`: `@SerialEntry` fields, nested `Defaults`, `ConfigClassHandler` writing `betterportallinking.json5`, `load()`, `configScreen(Screen)`. |

Modified:

| File | Change |
|---|---|
| `common/.../BetterPortalLinkingMod.java` | Add `BetterPortalLinkingConfig.load();` as the first statement of `init()` (matches `VillagerTweaksMod`). |
| `common/src/main/resources/betterportallinking.mixins.json` | Add both mixin simple names to the `mixins` array. `client` stays empty. Do **not** add a `refmap` key — this repo uses `dev.architectury.loom-no-remap`. |
| `fabric/src/main/resources/fabric.mod.json` | Add the `"modmenu"` entrypoint → `com.chimericdream.betterportallinking.fabric.config.ModMenuIntegration`. Also fix the template leftover `contact.sources` (currently points at `fabric-example-mod`). |
| `CHANGELOG.md` | Add a `#### New Features` bullet under the existing `### Unreleased changes`. **Do not** bump `mod_version` or add a dated heading — nothing is being released. |
| `README.md` | Fill in the empty "Current Features" section. Player-facing tone: concise, non-technical. |

New resources:

| File | Contents |
|---|---|
| `common/src/main/resources/data/betterportallinking/tags/block/portal_address_blocks.json` | `{ "replace": false, "values": ["#minecraft:concrete", "#minecraft:terracotta", "#minecraft:glazed_terracotta"] }` — note the MC 26.2 **singular** `tags/block/` path. No flammable blocks: address blocks touch the portal frame, so wool and similar are excluded on purpose. |
| `common/src/main/resources/assets/betterportallinking/lang/en_us.json` | `text.config.title`, `text.config.section.general`, and `text.config.option.<name>` + `.desc` for both options. |
| `fabric/src/main/java/com/chimericdream/betterportallinking/fabric/config/ModMenuIntegration.java` | `implements ModMenuApi`, returns `BetterPortalLinkingConfig::configScreen`. Mirror `villager-tweaks/fabric/.../fabric/config/ModMenuIntegration.java`. |

**Nothing goes in `neoforge/`.** Both loaders already register `betterportallinking.mixins.json`
(`fabric.mod.json` `mixins` array + `neoforge.mods.toml` `[[mixins]]`), and the mixins target vanilla
classes only, so the repo's common-mixin default applies — no platform split, no `@ExpectPlatform`.

**Hygiene (from `CLAUDE.md`):** `fabric/src/main/resources/betterportallinking.accesswidener` and an
`"accessWidener"` line in `fabric.mod.json` must **never** appear in a commit. If `bun run copy:accesswideners`
runs during debugging, delete the copy and strip the line before committing.

---

## Execution: sub-agent waves

### Wave 0 — source cache (1 Sonnet agent, blocking)

Invoke the **`mc-source-decompile`** skill to decompile and cache into `.sources/minecraft/26.2/` at minimum:
`net/minecraft/world/level/portal/PortalForcer`, `PortalShape`, `TeleportTransition`;
`net/minecraft/world/level/block/NetherPortalBlock`; `net/minecraft/world/entity/PortalProcessor`;
`net/minecraft/util/BlockUtil`; `net/minecraft/world/entity/ai/village/poi/PoiManager` + `PoiRecord` + `PoiTypes`.
The current cache has **no** `world/level/portal/` directory at all. Report the resolved paths — every later
wave reads from there rather than re-deriving from bytecode.

### Wave 1 — three Sonnet agents in parallel (disjoint file sets)

- **1A — Config.** `config/BetterPortalLinkingConfig.java`, `assets/.../lang/en_us.json`,
  `fabric/.../fabric/config/ModMenuIntegration.java`, the `modmenu` entrypoint in `fabric.mod.json`, and the
  `BetterPortalLinkingConfig.load();` line in `BetterPortalLinkingMod.java`. Reference villager-tweaks
  throughout. Verify with `./gradlew :better-portal-linking:build`.
- **1B — Tag.** `data/betterportallinking/tags/block/portal_address_blocks.json` and `tag/ModTags.java`.
  Confirm each referenced vanilla tag exists in the 26.2 jar before referencing it (all three were verified
  during planning; re-confirm rather than trust). Keep flammable blocks out of the default set.
- **1C — Core logic.** `portal/PortalAddress.java`, `portal/PortalFrame.java`, `portal/PortalAddressLinker.java`,
  `portal/EntryPortalContext.java`. **Requirement: the scoring, tiebreak, and corner-derivation logic must
  not reference `Level`** — they take a `Predicate<BlockPos>` / `Function<BlockPos, Block>` — so Wave 3 can
  unit-test them without a running server.

### Wave 2 — mixins (1 Sonnet agent, after 1B + 1C)

`mixin/BPLNetherPortalBlockMixin.java`, `mixin/BPLPortalForcerMixin.java`, and the `mixins.json` registration.
Keep both mixins thin — all real work delegates to `PortalAddressLinker`. Both must compile against the
decompiled source from Wave 0, not against guessed signatures. Verify with a full
`./gradlew :better-portal-linking:build` (both loaders).

### Wave 3 — tests (1 Sonnet agent)

JUnit in `better-portal-linking/fabric/src/test/java/...`, extending `BootstrapMinecraft` from
chimeric-lib's testFixtures (already wired by the root `build.gradle`; see `docs/TESTING.md`). Cover:

- corner derivation for both axes, and for non-2×3 sizes (3×4, 21-wide edge case);
- multiset scoring: no overlap → 0; partial; duplicates on one side only; identical addresses → 4;
- tiebreak ordering: equal score → nearer wins; equal score *and* distance → deterministic position order,
  asserted as an exact expected `BlockPos`;
- empty-entry-address and all-zero-score paths both return "no override".

*Stretch, only if it works cleanly:* a GameTest in `fabric/src/gametest/` that places two portals in one
dimension via `setBlock` and calls `PortalAddressLinker` directly with a synthetic entry address. Cross-
dimension linking cannot be GameTested, and POI registration from `setBlock` may not populate — if it
doesn't, drop it and say so rather than weakening the test.

### Wave 4 — docs (1 Sonnet agent)

`README.md` "Current Features", `CHANGELOG.md` under `### Unreleased changes`, and a line or two in
`POTENTIAL_FEATURES.md` for follow-ups (e.g. placing address blocks on auto-created portals, a
`/portallink debug` command). Player-facing tone: concise and non-technical, per `CLAUDE.md`.

### Wave 5 — review (1 **Opus** agent, final)

Read every changed file and check:

1. Algorithm matches the spec above clause by clause — especially "air ignored", "duplicates allowed",
   "order irrelevant", the all-zero fallback, and both tiebreak levels.
2. Vanilla parity in the non-address case: an entry portal with no tagged corners must produce **exactly**
   vanilla's result. Confirm the hook returns without cancelling on every such path.
3. Entry-context cannot leak: verify the take-once semantics hold if `findClosestPortalPosition` is never
   reached, or throws.
4. Candidate grouping is correct — one score per physical portal, not one per POI record.
5. Repo conventions: LF line endings; `net.minecraft.resources.Identifier`; singular `tags/block/`;
   no `refmap` key; `bpl$` injector naming; mod-word-prefixed mixin class names.
6. Release hygiene: `mod_version` still `1.0.0`, no dated changelog heading, no committed
   `fabric/src/main/resources/betterportallinking.accesswidener`, no `"accessWidener"` line in
   `fabric.mod.json`, `betterportallinking.accesswidener` still header-only.
7. `./gradlew :better-portal-linking:build` and `./gradlew :better-portal-linking:fabric:test` both pass.

---

## Verification

**Automated**

```
./gradlew :better-portal-linking:build              # both loaders compile, mixins apply
./gradlew :better-portal-linking:fabric:test        # Wave 3 unit tests
```

Mixin config has `injectors.defaultRequire: 1`, so a mis-targeted injector fails the build rather than
silently no-op'ing — a clean build is real evidence the hooks bound.

**Manual in-game** (the only way to exercise the cross-dimension path end to end):

1. Overworld: build portals **A** and **B** ~40 blocks apart. Give A four red-concrete corners, B four
   blue-concrete corners. Leave a third portal **C** with bare corners.
2. Nether: build portal **X** with four red-concrete corners and portal **Y** with four blue-concrete corners,
   placed so that **Y is the nearer of the two** to A's scaled coordinates. Vanilla would send A → Y.
3. Walk into A → expect to arrive at **X** (address wins over distance). Walk into B → expect **Y**.
4. Walk into C → expect vanilla behavior (nearest portal), unchanged.
5. Break one red-concrete corner on X and re-enter A → score drops from 4 to 3; if that puts X below Y, the
   destination changes immediately. This is the visible proof that linking is stateless.
6. Repeat one transit several times and confirm the destination never varies (deterministic tiebreak).
7. Set `enableAddressLinking: false` in `betterportallinking.json5`, restart, and confirm step 3 reverts to
   vanilla behavior.

Enable `logLinkingDecisions` during manual testing — it should log the entry address, each candidate's
address and score, and the winner, which makes every step above self-diagnosing.
