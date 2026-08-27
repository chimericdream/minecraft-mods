# How loot chest contents are generated (MC 26.2)

Research notes on the vanilla mechanism that fills a structure chest (e.g. an End City chest) with
items the first time a player opens it. Based on decompiling the MC 26.2 merged-deobf jar (see
`.claude/skills/mc-source-decompile/`); the relevant classes are cached at
`.sources/minecraft/26.2/net/minecraft/...`:

- `world/level/storage/loot/LootTable.java`
- `world/level/storage/loot/LootPool.java`
- `world/level/storage/loot/LootContext.java`
- `world/level/storage/loot/LootParams.java`
- `world/RandomizableContainer.java`
- `world/level/block/entity/RandomizableContainerBlockEntity.java`
- `world/level/block/entity/ChestBlockEntity.java`

## 1. The chest is *tagged* with a loot table when it's placed (worldgen)

When a structure (e.g. an End City) generates, its chest block entities are placed with a
`minecraft:container_loot` data component already baked into the structure's NBT — for example
`{"loot_table": "minecraft:chests/end_city_treasure", "seed": 0}`. When the block entity loads,
`RandomizableContainerBlockEntity.applyImplicitComponents()` reads that component and stashes it:

```java
// RandomizableContainerBlockEntity.java:93-100
protected void applyImplicitComponents(final DataComponentGetter components) {
   super.applyImplicitComponents(components);
   SeededContainerLoot loot = (SeededContainerLoot) components.get(DataComponents.CONTAINER_LOOT);
   if (loot != null) {
      this.lootTable = loot.lootTable();
      this.lootTableSeed = loot.seed();
   }
}
```

At this point the chest is **empty** — it just remembers *which* loot table (a
`ResourceKey<LootTable>`, e.g. `minecraft:chests/end_city_treasure`) and a seed. Nothing has been
rolled yet.

## 2. Contents are generated lazily, on first access

`ChestBlockEntity` extends `RandomizableContainerBlockEntity`, which overrides basically every
inventory-reading method (`getItem`, `isEmpty`, `createMenu`, etc.) to call `unpackLootTable(...)`
first:

```java
// RandomizableContainerBlockEntity.java:80-83
public AbstractContainerMenu createMenu(final int containerId, final Inventory inventory, final Player player) {
   if (this.canOpen(player)) {
      this.unpackLootTable(inventory.player);   // rolls loot right when the GUI opens
      return this.createMenu(containerId, inventory);
```

`unpackLootTable` (default method on the `RandomizableContainer` interface,
`RandomizableContainer.java:74-92`) does the actual work:

```java
default void unpackLootTable(@Nullable final Player player) {
   ...
   LootTable lootTable = level.getServer().reloadableRegistries().getLootTable(lootTableKey);
   ...
   this.setLootTable(null);   // clear the marker so it only ever rolls once
   LootParams.Builder params = new LootParams.Builder((ServerLevel) level)
       .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(worldPosition));
   if (player != null) {
      params.withLuck(player.getLuck()).withParameter(LootContextParams.THIS_ENTITY, player);
   }
   lootTable.fill(this, params.create(LootContextParamSets.CHEST), this.getLootTableSeed());
}
```

This is the "generate on open" behavior: loot generation is deferred until the first real access,
and the `lootTable` field is nulled immediately afterward so a second open never re-rolls.

## 3. `LootTable.fill()` does the actual rolling (`LootTable.java:146-166`)

- Builds a `LootContext` from the params (origin, luck, seed, etc.)
- Calls `getRandomItems(context)`, which walks every `LootPool` in the table and calls
  `pool.addRandomItems(...)`
- Picks which of the container's empty slots to use (`getAvailableSlots`, shuffled)
- Splits/shuffles stacks so they don't look suspiciously uniform (`shuffleAndSplitItems`)
- Places each rolled `ItemStack` into a random available slot

## 4. `LootPool.addRandomItems()` is where weighted item selection happens (`LootPool.java:96-105`)

- Checks the pool's `conditions` (e.g. "only if raining")
- Computes `rolls + bonusRolls * luck` — how many times to draw
- Each draw (`addRandomItem`, line 63-94) expands all entries, sums their weights (adjusted by
  luck), and picks one via `random.nextInt(totalWeight)` — a standard weighted-random draw
- The winning entry's `createItemStack` runs its own functions (set count, enchant, etc.)

## Summary of the call chain (End City example)

Structure loads chest with `container_loot` component
→ `RandomizableContainerBlockEntity.applyImplicitComponents` stores the loot table key
→ player opens chest → `createMenu` → `unpackLootTable`
→ `LootTable.fill` → `LootPool.addRandomItems` (weighted picks)
→ items land in shuffled slots.
