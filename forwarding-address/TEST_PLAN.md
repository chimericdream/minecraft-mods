# Test Plan — Forwarding Address

Forwarding Address converts placed blocks and held items from retired IDs to their replacements. It has
no content of its own, so its tests use throwaway fixture blocks and items registered only by a test
mod. The GameTest source set is Fabric-only (see `docs/TESTING.md`), so the conversion logic is covered
on Fabric and NeoForge gets a manual pass. The loader hooks are deliberately thin to keep that pass short.

## Automated coverage (keep green)

Run: `./gradlew :forwarding-address:fabric:test` and `./gradlew :forwarding-address:fabric:runGameTest`.

### Unit tests (`fabric/src/test`)

* **`ConversionQueueTest`** — the per-tick budget: never more than the budget per drain, leftovers finish
  next drain, a budget spans chunks, a chunk reported twice is scanned once, a chunk with nothing to
  convert costs no budget, an unloaded chunk is dropped, scans per drain are bounded, levels are
  independent, clearing a level forgets its work.
* **`MigrationRegistryTest`** — IDs resolve to registered blocks and items, a block migration implies its
  item migration, an explicit item migration wins, a missing target or old block is skipped, duplicate
  declarations are ignored, declaring after the first lookup still takes effect.
* **`PropertyCopierTest`** — matching properties copy by name and type, unshared properties are ignored,
  values the target doesn't allow keep the target's default.
* **`ApiBoundaryTest`** — the `api` package imports nothing from the rest of the mod (so it can be split
  out later), and the builder hands tidy records to the backend.
* **`ForwardingSettingsTest`** — the default cap and its clamping.

### GameTests (`fabric/src/gametest`, test mod `forwardingaddress_test`)

* **`BlockConversionGameTest`** — plain block keeps facing and waterlogging; unmigrated and
  missing-target blocks are left alone; a container keeps its contents and drops nothing; a hidden
  trailing slot survives; different block-entity types convert through the mapper; a block-entity block
  whose target has none is skipped with contents intact.
* **`ItemConversionGameTest`** — stacks keep count and components; unmigrated and missing-target stacks
  are untouched; a block's item converts with the block; containers in the world convert; a player's
  inventory, armor, offhand, and ender chest convert; a dropped item converts when added; retired items get a tooltip and
  are flagged for creative-tab hiding.
* **`ChunkQueueGameTest`** — a drain never exceeds its budget and every block converts eventually; a
  chunk with nothing to convert is harmless; the real server tick converts a block in a reported chunk.

## Manual checklist

Needs a real world and a person. Do it on **both Fabric and NeoForge**, since the GameTests are Fabric-only.

* [ ] **Chunk load hook (NeoForge).** In a dev world with a test migration declared, place an old block,
      save and quit, reload: the block converts within a few seconds. Repeat on Fabric.
* [ ] **Login and inventory (both loaders).** Put old items in the inventory, armor slots, offhand, and
      ender chest; log out and in: all convert.
* [ ] **Item frames and dropped items (both loaders).** Reload a world with an old item in an item frame
      and one on the ground: both convert.
* [ ] **Tooltip and creative tab (both loaders).** An old item shows "Retired. It will be replaced by ..."
      in its tooltip and is not in any creative tab or the search tab.
* [ ] **Hopper X-Treme `filtered_*` hoppers.** Once Hopper X-Treme declares its migration through this mod,
      load a world with filled `filtered_*` hoppers: contents and the filter survive, nothing drops.
      Until then this is not testable, since Hopper X-Treme keeps its own conversion.
* [ ] **Dedicated server.** Start a server with the mod and a world containing old blocks: no client
      classes load, and blocks convert. Join with a client that has the mod.
* [ ] **Large backlog.** Load a world with a very large number of old blocks in one chunk area and
      confirm there is no long tick; lower `maxConversionsPerTick` in
      `config/forwardingaddress/settings.json` and confirm conversion takes proportionally longer.
* [ ] **Bad settings file.** Put invalid JSON in `settings.json`: the game starts, logs an error, uses the
      default, and leaves the file as it was.

## Not covered yet (Stage 2)

JSON-declared migrations, stub registration for retired IDs, the duplicate guard, and the
`/forwardingaddress dump` command. Their tests are planned with the work.
