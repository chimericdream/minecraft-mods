# Forwarding Address saved-data mixins

Forwarding Address renames retired block and item IDs in saved data before the game decodes it. It does
this with mixins into Minecraft internals, so re-check them on every Minecraft update.

- `SimpleRegionStorageMixin` targets `SimpleRegionStorage.upgradeChunkTag` at `HEAD`. It must be here and
  not in `DataFixTypes`: `upgradeChunkTag` returns early without calling the data fixer when the data is
  already at the current version, which is exactly what a mod update (as opposed to a Minecraft update)
  leaves behind.
- `DataFixTypesMixin` targets `DataFixTypes.update` at `RETURN`, for `PLAYER` data only. Player data always
  goes through the fixer, and handling only `PLAYER` there avoids renaming region data twice.
- The hooks run on worker threads, so `MigrationRegistry.rewriter()` and `IdRewriter` must stay
  thread-safe and must not touch the world.
- Both method descriptors are written out in the mixins (`defaultRequire` is 1), so a signature change
  fails at startup rather than silently doing nothing.
- `SavedDataRewriteGameTest` is the check: it writes a real chunk with an unregistered ID and loads it.
  If it fails after an update, look at these two methods first.
