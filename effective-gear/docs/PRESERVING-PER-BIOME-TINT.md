# Preserving: per-biome tint capture (design doc, not implemented)

Design notes for a possible follow-up to the **Preserving** enchantment: instead of always locking a
mined leaf block to vanilla's fixed default color (the currently shipped behavior — see
`PreservingHelper`, `EG$LeavesBlockMixin`, `EG$BlockMixin`, `PreservingBlockColors`), capture the
*specific* biome tint the leaves had at the moment they were broken (e.g. "Oak (Taiga tint)" vs. plain
"Oak"), and preserve that exact color instead. Nothing below is implemented — this is a plan to
implement from, written up because the design is nontrivial enough to lose track of between sessions.

## Why the current design can't just add a string field

The shipped implementation stores its state as a single blockstate boolean, `PreservingHelper.PRESERVED`,
carried from item to block via the vanilla `DataComponents.BLOCK_STATE` component
(`BlockItemStateProperties`) and read back by `PreservingBlockColors`' `BlockTintSource`. Blockstate
properties are the natural fit for a boolean, but they can't hold a biome identifier:

- Blockstate properties must be fully enumerable at `createBlockStateDefinition` time (every possible
  value becomes a real palette entry). Biomes are open-ended — vanilla ships ~60, datapacks and other
  mods add more — so there's no fixed `Property<Biome>` we could register.
- A custom **item** data component (e.g. storing a biome `ResourceLocation` string) works fine up until
  the item is placed — but the moment `BlockItem.place()` runs, that item and its component map are
  gone. Whatever needs to survive *at that world position* afterward has to live in one of exactly two
  places: blockstate (finite, ruled out above) or a **block entity** (arbitrary NBT/components, no
  enumeration limit). There is no third "per-position custom data" mechanism in vanilla — this was the
  point that led to reverting the first attempt at this feature (see git history around the Preserving
  enchantment's initial commit).

So capturing an open-ended biome tint requires a block entity. The rest of this doc assumes that.

## Performance: why a non-ticking block entity is fine here

This was the objection that killed the first attempt at this design, so it's worth being explicit about
why it doesn't actually apply:

- A block entity only costs anything for positions where one actually **exists**. `EntityBlock.newBlockEntity(pos, state)`
  can return `null` per call — for ordinary (non-preserved) leaves it always would, so the vast majority
  of leaves in a world pay nothing extra beyond one boolean blockstate check.
- **No tick-loop cost.** MC only adds a block entity to a chunk's ticking list if `getTicker(...)`
  returns non-null. This block entity never needs to tick (its data is set once, at placement, and read
  only by the render-side tint source), so it's never iterated by the server's per-tick block-entity
  loop at all — it just sits in the chunk's position→BE map.
- **Lookup cost** (`level.getBlockEntity(pos)`) is an O(1) hashmap get scoped to that chunk, unaffected
  by how many other preserved leaves exist elsewhere in the world.
- **Memory**: each instance holds one `int` (frozen color) and optionally one biome key/name — a few
  hundred bytes each. Thousands of them (e.g. a world full of custom-tree builds) is still only a few
  hundred KB, and only *loaded* chunks hold live instances.
- The real (still small) cost is **chunk (de)serialization and initial network sync**: block entities
  are written into their chunk's NBT on save/load and bundled into the initial chunk packet sent to a
  client. More BEs per chunk means marginally bigger NBT/packets, but this is a one-time per-chunk-load
  cost, not a per-tick one — negligible even at "several thousand across a world" scale, and only
  noticeable at absurd densities (thousands crammed into a handful of chunks).

Conclusion: this is *not* the kind of block entity that has a bad reputation (hoppers, etc., which cost
real time every tick across every loaded chunk). A ticker-less BE that just stores a captured color is
close to free.

## Proposed architecture

Keep the existing `PreservingHelper.PRESERVED` boolean blockstate property as a cheap per-instance gate
— it's still the right tool for "does this specific leaf position need a block entity at all," and lets
the tint source and `newBlockEntity` both skip any real work for the common (non-preserved) case with a
single boolean check.

### 1. New block entity: `PreservedLeavesBlockEntity`

`com.chimericdream.effectivegear.block.entity.PreservedLeavesBlockEntity extends BlockEntity`, storing:

- `int foliageColor` — the frozen, already-blended render color (see "Capturing the correct color"
  below for why this must be a precomputed int, not a live biome reference).
- Optionally, `ResourceKey<Biome> sourceBiome` (or just its `ResourceLocation`) — needed only if the
  tooltip/display-name flavor text ("Oak (Taiga tint)") is wanted; the tint source itself only needs
  `foliageColor`.

Save/load via the repo's usual `ValueOutput`/`ValueInput` idiom (`TagValueOutput`, `putInt`/`getIntOr`,
`putString`/`getStringOr` for the biome location) — same pattern as e.g. minekea's block entities. On
load, if `sourceBiome`'s location doesn't resolve in the current registry (biome removed by an
uninstalled datapack/mod, or world loaded without the mod that added it), fall back gracefully — treat
it as absent rather than throwing, and still trust the stored `foliageColor` for rendering.

### 2. Registering the block entity type

New `com.chimericdream.effectivegear.block.EGBlockEntities` (mirrors `ModEnchantments`'s shape): a single
`BlockEntityType<PreservedLeavesBlockEntity>` valid for all five species
(`PreservingHelper.getPreservableLeaves()`), registered the same way the repo registers other block
entity types (`ModRegistryHelper`, chimeric-lib). The first attempt at this feature needed an
access-widener entry to construct/use `BlockEntityType` in this way — re-check whether that's still
necessary once the actual vanilla builder API is confirmed; don't assume it's the same requirement
without re-verifying against current decompiled source (see the `mc-source-decompile` skill).

### 3. Making `LeavesBlock` an `EntityBlock`

`LeavesBlock` is vanilla and doesn't implement `EntityBlock`. Soft-implement it via Mixin's
`@Implements(@Interface(iface = EntityBlock.class, prefix = "eg$"))` on a new `EG$LeavesBlockMixin`
addition (or a dedicated mixin) providing:

```java
@Override
public BlockEntity eg$newBlockEntity(BlockPos pos, BlockState state) {
    if (!state.getValue(PreservingHelper.PRESERVED)) {
        return null;
    }
    return new PreservedLeavesBlockEntity(pos, state);
}
```

This is invoked for *every* leaf placement (vanilla calls `newBlockEntity` unconditionally for any
`EntityBlock`-implementing block), which is exactly why the `PRESERVED` boolean check needs to stay —
it's what keeps the non-preserved, overwhelmingly common case at "one boolean read, return null."

`getTicker(...)` should simply return `null` — this block entity never ticks (see performance section).

### 4. Capturing the color (and biome) at break time

The existing `EG$BlockMixin#eg$tagPreservedLeafDrops` already identifies the moment a preserved leaf is
mined (tool-aware `getDrops` overload, only reached from player mining, not explosions) and currently
just sets the `PRESERVED` boolean via `BLOCK_STATE`. It would need to additionally:

1. Compute the color exactly as the renderer would have shown it — call the **same**
   `BiomeColors.getAverageFoliageColor(level, pos)` helper `PreservingBlockColors` already uses for
   ordinary leaves, not a raw single-position `level.getBiome(pos).value().getFoliageColor()` read. The
   averaged/blended helper matches what the player actually saw on screen near biome borders; a raw
   single-biome read does not, and this distinction is easy to miss (the first, reverted attempt at this
   feature used the raw single-biome read).
2. Attach that captured data to the dropped stack via `DataComponents.BLOCK_ENTITY_DATA`
   (`BlockItem.setBlockEntityData(stack, EGBlockEntities.PRESERVED_LEAVES, tagValueOutput)`), alongside
   the existing `BLOCK_STATE` component that flips `PRESERVED` to `true`.

At placement, vanilla's own `BlockItem.place()` path both applies the `BLOCK_STATE` component (flipping
`PRESERVED`) *and*, because the block now implements `EntityBlock` and a block entity gets created for
that position, loads the `BLOCK_ENTITY_DATA` component's NBT into the freshly constructed
`PreservedLeavesBlockEntity` — the same mechanism vanilla uses for e.g. banner patterns or a named
shulker box surviving a break-and-replace.

### 5. Tint source lookup

`PreservingBlockColors`' `colorInWorld` changes from "return a fixed constant" to "look up the captured
color":

```java
@Override
public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
    if (state.getValue(PreservingHelper.PRESERVED)) {
        if (level.getBlockEntity(pos) instanceof PreservedLeavesBlockEntity preserved) {
            return preserved.getFoliageColor();
        }
        return FoliageColor.FOLIAGE_DEFAULT; // defensive fallback, shouldn't normally happen
    }
    return BiomeColors.getAverageFoliageColor(level, pos);
}
```

The defensive fallback matters because `BlockAndTintGetter` implementations used for rendering (e.g. a
`RenderChunkRegion`) aren't guaranteed to have every block entity loaded at the instant a tint is
sampled; falling back to the default color is safer than crashing or returning garbage.

### 6. Tooltip

The shipped tooltip line lives on `BlockItemStateProperties` (`EG$BlockItemStatePropertiesMixin`, which
only sees blockstate-shaped data, not block entity data) — it can still show a generic "Color Preserved"
line by checking `PRESERVED`, but showing the *specific* biome name (e.g. "Taiga tint") needs a
different hook, since that data lives in `BLOCK_ENTITY_DATA`, not `BLOCK_STATE`. Needs research into
whichever vanilla mechanism surfaces a tooltip line from an item's attached block-entity data (something
in this space already exists for named/decorated vanilla containers) — not yet confirmed which class
that is or whether it composes cleanly with the existing `BlockItemStateProperties` tooltip line. Flagged
as an open question below rather than designed further here.

### 7. Recipes, piston movement, and other edges that likely need no new work

- The five `strip_preserved_*_leaves.json` shapeless recipes (already shipped) need **no changes** —
  ordinary shapeless recipes always produce a fresh static output stack regardless of the input's
  components/block-entity data, so they already strip all of this away.
- Piston movement of a block with a block entity is already handled generically by vanilla for any
  `EntityBlock` — should carry over without new code, but is worth an explicit smoke test once
  implemented (moving a preserved leaf block with a piston, confirming the captured color survives the
  move).
- Block removal already tears down the associated block entity generically (`Level.removeBlockEntity`)
  whenever the block is removed — no custom cleanup path needed.

## Open questions

- Exact tooltip mechanism for surfacing block-entity-derived text on an item (see §6) — needs its own
  research pass before implementation.
- Display/flavor-text naming scheme: is "Oak (Taiga tint)" a custom item name override, a tooltip-only
  addition (keeping the item's real display name plain "Oak Leaves"), or something else? Affects
  whether `sourceBiome` needs a translatable biome-name lookup or just an internal identifier.
- Whether the access-widener entry the first (reverted) attempt needed for `BlockEntityType` is still
  required — re-verify against current decompiled source rather than assuming.
- Whether capturing `sourceBiome` at all is worth the extra stored field and tooltip work, versus
  shipping just the captured `foliageColor` (still solves the original ask — "keeps the tint it had when
  broken" — without the naming/tooltip complexity in §6).

## Status

Not implemented. Recorded in `POTENTIAL_FEATURES.md` under the 2026-08-25 entry. The currently shipped
Preserving enchantment uses the simpler fixed-default-color design (blockstate boolean only, no block
entity) described in this doc's introduction.
