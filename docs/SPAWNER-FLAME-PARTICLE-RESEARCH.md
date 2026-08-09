# Mob spawner flame particle research (MC 26.2)

Research notes on where vanilla renders the flame/smoke particles on an active mob spawner
cage (`minecraft:spawner`), for adapting to a custom block. Decompiled from
`minecraft-merged-deobf-26.2.jar` per [[mc-26-2-source-decompile-workflow]] (Vineflower on the
cached binary jar — the 26.2 sources jar in the loom cache is empty). No code in this repo was
changed for this research; see the "How to adapt" section below for the actual implementation
plan.

## TL;DR

The flames are **not** part of the block model and **not** drawn by a `BlockEntityRenderer`.
They're plain particles spawned every client tick from `BaseSpawner.clientTick(...)`, which is
invoked by the block entity's client-side ticker (the same `getTicker`/`createTickerHelper`
pattern already used by this repo's block entities, e.g. `CrateBlock`).

```java
// net/minecraft/world/level/BaseSpawner.java
public void clientTick(final Level level, final BlockPos pos) {
   if (!this.isNearPlayer(level, pos)) {
      this.oSpin = this.spin;
   } else if (this.displayEntity != null) {
      RandomSource random = level.getRandom();
      double xP = pos.getX() + random.nextDouble();
      double yP = pos.getY() + random.nextDouble();
      double zP = pos.getZ() + random.nextDouble();
      level.addParticle(ParticleTypes.SMOKE, xP, yP, zP, 0.0, 0.0, 0.0);
      level.addParticle(ParticleTypes.FLAME, xP, yP, zP, 0.0, 0.0, 0.0);
      if (this.spawnDelay > 0) {
         this.spawnDelay--;
      }

      this.oSpin = this.spin;
      this.spin = (this.spin + 1000.0F / (this.spawnDelay + 200.0F)) % 360.0;
   }
}
```

Key points from this method:

- **One `SMOKE` + one `FLAME` particle per client tick** (20/sec), each at the *same* random
  point inside the block's unit cube (`pos + random.nextDouble()` on all three axes — i.e.
  uniform random position in `[pos, pos+1)³`, freshly rolled every tick).
- **Zero velocity** on both particles (`0.0, 0.0, 0.0` for motion) — the drift you see in-game is
  purely each particle type's own physics (smoke/flame both have inherent upward drift/gravity
  built into their client-side `Particle` implementation, not anything the spawner code sets).
- Gated on **`isNearPlayer(level, pos)`** — `level.hasNearbyAlivePlayer(...)` within
  `requiredPlayerRange` (16 blocks by default). No player nearby → no particles, ever (this is
  also why an unloaded/far-away spawner looks "dark", not because of any separate on/off model
  state).
- Further gated on **`this.displayEntity != null`** — the spinning mob preview must have resolved
  (lazily created via `getOrCreateDisplayEntity`, which needs valid `SpawnData`). A spawner with
  no valid entity to spawn shows no flames either.
- The `spin`/`oSpin` bookkeeping in the same method drives the rotating mob-silhouette model
  (used by `SpawnerRenderer`) — unrelated to the particles themselves, just co-located because
  both are driven by the same per-tick hook.

## Full call chain

| Layer | File (decompiled path under `net/minecraft/...`) | Role |
|---|---|---|
| Particle emission | `world/level/BaseSpawner.java` → `clientTick` | Actual `level.addParticle(...)` calls (shown above) |
| Block entity | `world/level/block/entity/SpawnerBlockEntity.java` | `clientTick(level, pos, state, entity)` static method just delegates to `entity.spawner.clientTick(level, pos)`; also builds the anonymous `BaseSpawner` field with the two overrides needed for saving/broadcasting |
| Ticker wiring | `world/level/block/SpawnerBlock.java` → `getTicker` | `createTickerHelper(type, BlockEntityTypes.MOB_SPAWNER, level.isClientSide() ? SpawnerBlockEntity::clientTick : SpawnerBlockEntity::serverTick)` — standard `BaseEntityBlock` ticker split, identical shape to what `CrateBlock.getTicker` already does in this repo |
| Model/mob renderer (separate concern) | `client/renderer/blockentity/SpawnerRenderer.java` | Only draws the rotating semi-transparent mob preview inside the cage via `entityRenderer.submit(...)`; does **not** touch particles at all |
| Render state | `client/renderer/blockentity/state/SpawnerRenderState.java` | Holds `displayEntity`/`spin`/`scale` extracted each frame for the renderer above |
| Block class (server-side spawn logic, not relevant to visuals) | `world/level/block/SpawnerBlock.java` | Also handles XP drop on break |

So there are two entirely independent visual systems people conflate when they say "the spawner
render code":
1. **Flame + smoke particles** — `BaseSpawner.clientTick`, a plain particle spawn, no renderer
   class involved at all.
2. **Spinning mob silhouette** — `SpawnerRenderer` / `SpawnerRenderState`, a real
   `BlockEntityRenderer` that renders an actual entity model rotating inside the cage.

If the goal is just "particles coming off an active block," only #1 matters — it's ~10 lines and
has no dependency on entities, models, or a `BlockEntityRenderer` at all.

## How to adapt for a custom block

Since this is pure `Level.addParticle(...)` from a client ticker, the pattern transplants
directly onto any custom `BlockEntity` without needing a renderer:

1. Add an "active" condition to check each tick (vanilla uses nearby-player + valid spawn data;
   a custom block would probably use its own state, e.g. a blockstate property or an NBT flag on
   the block entity).
2. In the block entity's static `clientTick(Level, BlockPos, BlockState, YourBlockEntity)` method
   (same shape as `SpawnerBlockEntity.clientTick`), when active, call
   `level.addParticle(<YourParticleType>, xP, yP, zP, dx, dy, dz)` with a randomized position
   inside (or around) the block, same as the `pos.getX() + random.nextDouble()` pattern above.
3. Wire it into `getTicker` via `createTickerHelper(type, YOUR_BLOCK_ENTITY_TYPE, level.isClientSide() ? YourBlockEntity::clientTick : YourBlockEntity::serverTick)`
   — this repo already uses this exact helper for other block entities (e.g.
   `minekea`'s `CrateBlock.getTicker`), so no new plumbing pattern is needed.
4. Pick a particle type from `net.minecraft.core.particles.ParticleTypes` (or register a custom
   one) instead of `SMOKE`/`FLAME`.

No mod in this repo currently calls `Level.addParticle` from a block-entity ticker — the vanilla
`BaseSpawner`/`SpawnerBlock` pair above is the cleanest reference to copy the shape from.

## Open questions for Bill (non-blocking)

- What block and what particle type did you have in mind? (Affects whether "one particle at a
  random point in the cell every tick" is the right emission pattern, or whether something more
  directional/edge-anchored fits better — e.g. corner sparks vs. spawner's uniform-random cage
  glow.)
- Should the "active" condition mirror the spawner's nearby-player gate, or is this block active
  based on its own state (powered, has fuel, mid-operation, etc.)?
