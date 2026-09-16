# Conventions

- **Line endings**: LF everywhere, enforced by the root `.gitattributes` (`* text=auto eol=lf`).
- **demo-world (minekea)**: `minekea/demo-world/` is a deterministic showcase generator. Generated
  files (`demo_build.mcfunction`, manifests) are produced by `generate_layout.py` /
  `extract_jar_contents.py` — **never hand-edit them**; regenerate. See `minekea/demo-world/README.md`.
- **Container block-entity inventories**: pick the abstraction by capability, don't mix arbitrarily.
  - A block entity that should support **loot tables** (chest/barrel-like) extends
    `RandomizableContainerBlockEntity` and honors `trySaveLootTable`/`tryLoadLootTable` in its
    save/load. Don't also implement `ImplementedInventory` on these — its defaults are dead weight and
    it's easy to bypass the loot-table plumbing.
  - `ImplementedInventory` (chimeric-lib) is for inventories that are **not** loot-table-capable —
    typically item-backed or purpose-built containers (glass jar, shelf, display case, the hopper
    filter item). Back them with a `NonNullList.withSize(...)`.
  - `setItems` must preserve the fixed slot count: copy in place
    (`for i: items.set(i, incoming.get(i))`), never `clear()+addAll` on a fixed-size `NonNullList`
    (that can throw or let the size drift). Same fixed-size rule as `ImplementedInventory.clearContent`.
